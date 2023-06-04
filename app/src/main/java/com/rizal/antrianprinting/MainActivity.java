package com.rizal.antrianprinting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.rizal.antrianprinting.activity.BookingActivity;
import com.rizal.antrianprinting.activity.InformasiActivity;
import com.rizal.antrianprinting.activity.ProfilActivity;
import com.rizal.antrianprinting.activity.RiwayatBookingActivity;
import com.rizal.antrianprinting.base.BaseActivity;
import com.rizal.antrianprinting.models.antrian.Antrian;
import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;
import com.rizal.antrianprinting.utils.Responses;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends BaseActivity {

    boolean doubleBackToExitPress = false;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    TextView jam_booking, jam_pelayanan;
    LinearLayout lr_booking, lr_riwayat, lr_informasi, lr_profil, lr_logout;
    SwipeRefreshLayout lr_refresh;
    MobileService mobileService;
    ProgressDialog progressDialog;

    User user;
    Antrian antrian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewId();
        setUpData();
        setOnClick();
    }

    private void setViewId() {
        mobileService = ApiUtils.MobileService(getApplicationContext());

        //Inisialisasi Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        user = Preferences.getUser(getApplicationContext());
        antrian = Preferences.getAntrian(getApplicationContext());

        if (user == null) {
            Toast.makeText(this, "Silahkan login terlebih dahulu.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        jam_booking = findViewById(R.id.tv_home_jam_booking);
        jam_pelayanan = findViewById(R.id.tv_home_jam_pelayanan);

        lr_booking = findViewById(R.id.lr_booking);
        lr_riwayat = findViewById(R.id.lr_riwayat);
        lr_informasi = findViewById(R.id.lr_informasi);
        lr_profil = findViewById(R.id.lr_profil);
        lr_logout = findViewById(R.id.lr_logout);
        lr_refresh = findViewById(R.id.layout_refresh_utama);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String deviceToken = task.getResult();
                        Log.d("Firebase Device Token", "token: " + deviceToken);
                    }
                });

        Intent intent = getIntent();
        String clickAction = intent.getStringExtra("click_action");

        if (clickAction != null) {
            if (clickAction.equals("OPEN_ACTIVITY_1")) {
                Intent activity1 = new Intent(this, RiwayatBookingActivity.class);
                startActivity(activity1);
            }
        }
    }

    private void setOnClick() {
        lr_booking.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
        lr_riwayat.setOnClickListener(v -> startActivity(new Intent(this, RiwayatBookingActivity.class)));
        lr_informasi.setOnClickListener(v -> startActivity(new Intent(this, InformasiActivity.class)));
        lr_profil.setOnClickListener(v -> startActivity(new Intent(this, ProfilActivity.class)));

        lr_refresh.setOnRefreshListener(this::setUpData);
        lr_logout.setOnClickListener(v -> showDialogLogout());
    }

    private void setUpData() {
        showProgressDialog();

        mobileService.getantrian(user.getId()).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, retrofit2.Response<Responses> response) {
                Responses body = response.body();
                Log.d("Get Id Antrian Response", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    Antrian antrianResponse = new Gson().fromJson(new Gson().toJson(body.getData()), Antrian.class);
                    if (!body.isStatus() && body.getCode() != 200) {
                        Log.d("Get Antrian Status", "onResponse: " + body.isStatus());
                        if (body.getCode() == 400) {
                            dismissProgressDialog();

                            jam_booking.setText(R.string.tidak_ada_antrian);
                            jam_pelayanan.setText(R.string.tidak_ada_antrian);

                            Preferences.setBookingFlag(getApplicationContext(), false);
                            lr_refresh.setRefreshing(false);
                        }
                    } else {
                        dismissProgressDialog();

                        jam_booking.setText(antrianResponse.getJam_booking());
                        jam_pelayanan.setText(antrianResponse.getJam_selesai());

                        Preferences.setBookingFlag(getApplicationContext(), true);
                        lr_refresh.setRefreshing(false);
                    }
                } else {
                    dismissProgressDialog();
                    Preferences.setBookingFlag(getApplicationContext(), false);

                    Log.d("Failure Get Antrian", "onFailure: " + response.errorBody());

                    jam_booking.setText(R.string.tidak_ada_antrian);
                    jam_pelayanan.setText(R.string.tidak_ada_antrian);
                    lr_refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                dismissProgressDialog();
                Preferences.setBookingFlag(getApplicationContext(), false);

                Log.d("Failure Get Antrian", "onFailure: " + t.getMessage());

                showMessage("Terjadi kesalahan, Periksa koneksi anda");
                lr_refresh.setRefreshing(false);
            }
        });
    }

    private void showDialogLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Konfirmasi")
                .setMessage("Apa anda yakin ingin keluar?");
        alertDialogBuilder
                .setIcon(R.drawable.ic_logout)
                .setCancelable(false)
                .setPositiveButton("Keluar", (dialogInterface, i) -> {
                    signOutAccount();

                    Preferences.setUser(getApplicationContext(), null);
                    Preferences.setLoginFlag(getApplicationContext(), false);

                    Intent keluar = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(keluar);
                    this.finish();
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        alertDialog.show();
    }

    private void signOutAccount() {
        googleSignInClient.signOut()
                .addOnSuccessListener(this, aVoid -> Log.d("Logout Account", "Sign out success"))
                .addOnFailureListener(this, e -> Log.e("Logout Account", "Sign out failed", e));
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.item_progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPress) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPress = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPress = false, 2000);
    }
}