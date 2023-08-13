package com.rizal.antrianprinting.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;
import com.rizal.antrianprinting.utils.Responses;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBookingActivity extends AppCompatActivity {

    TextView txtNamaDesigner, txtWaktuBooking, txtWaktuSelesai, txtJenisLayanan, txtTglPesanan, txtHandphone, txtStatus;
    ImageView backButton;
    Button btnCancel;
    ProgressDialog progressDialog;
    MobileService mobileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booking);

        backButton = findViewById(R.id.img_kembali_detail);
        txtNamaDesigner = findViewById(R.id.textNamaDesigner);
        txtWaktuBooking = findViewById(R.id.textWaktuBooking);
        txtWaktuSelesai = findViewById(R.id.textWaktuSelesai);
        txtJenisLayanan = findViewById(R.id.textJenisLayanan);
        txtTglPesanan = findViewById(R.id.textTanggalPesanan);
        txtHandphone = findViewById(R.id.textNoHandphone);
        txtStatus = findViewById(R.id.textStatus);
        btnCancel = findViewById(R.id.btnCancelBooking);

        mobileService = ApiUtils.MobileService(getApplicationContext());

        Intent intentExtra = getIntent();

        int id = intentExtra.getExtras().getInt("id");
        String namaDesign = intentExtra.getExtras().getString("nama_designer");
        String jenisLayanan = intentExtra.getExtras().getString("jenis_layanan");
        String waktuBooking = intentExtra.getExtras().getString("jam_booking");
        String waktuSelesai = intentExtra.getExtras().getString("jam_selesai");
        String tglPesanan = intentExtra.getExtras().getString("tgl_pesanan");
        String statusPesanan = intentExtra.getExtras().getString("status");

        txtNamaDesigner.setText(namaDesign);
        txtWaktuBooking.setText(waktuBooking);
        txtWaktuSelesai.setText(waktuSelesai);
        txtJenisLayanan.setText(jenisLayanan);
        txtTglPesanan.setText(tglPesanan);

        if (statusPesanan.equals("Batal") || statusPesanan.equals("Berhasil")) {
            txtStatus.setTextColor(getResources().getColor(R.color.red_pink));

            btnCancel.setTextColor(getResources().getColor(R.color.white));
            btnCancel.setBackgroundColor(getResources().getColor(R.color.black_sub));
            btnCancel.setEnabled(false);
            btnCancel.setClickable(false);
        }

        txtStatus.setText(statusPesanan);

        backButton.setOnClickListener( v -> backActivity());
        btnCancel.setOnClickListener(v -> cancelAntrian(id));
    }

    private void cancelAntrian(int id) {
        showDialogCancel(id);
    }

    private void showDialogCancel(int id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Konfirmasi")
                .setMessage("Apa anda yakin ingin membatalkan antrian?");
        alertDialogBuilder
                .setIcon(R.drawable.ic_logout)
                .setCancelable(false)
                .setPositiveButton("Yakin", (dialogInterface, i) -> {
                    prosesCancelAntrian(id);
                })
                .setNegativeButton("Kembali", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        alertDialog.show();
    }

    private void prosesCancelAntrian(int id) {
        showProgressDialog();

        mobileService.cancelantrian(id).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Responses body = response.body();
                Log.d("CancelAntrianResponse", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;

                    if (!body.isStatus() && body.getCode() != 200) {
                        Log.d("CancelAntrianStatus", "onResponse: " + body.isStatus());
                        if (body.getCode() == 400) {
                            dismissProgressDialog();
                            Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, silahkan coba lagi!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        handlePushNotification();
                        dismissProgressDialog();

                        Preferences.setAntrian(getApplicationContext(), null);
                        Preferences.setAntrianFlagging(getApplicationContext(), null);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                } else {
                    dismissProgressDialog();
                    Log.d("FailureCancelAntrian", "onFailure: " + response.errorBody());
                    Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                dismissProgressDialog();
                Log.d("FailureCancelAntrian", "onFailure: " + t.getMessage());

                Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePushNotification() {
        HashMap<String,  String> map = new HashMap<>();
        map.put("title", "Notifikasi Antrian");
        map.put("message", "Sayang sekali waktu kamu sudah habis, Antrian otomatis dibatalkan!");

        mobileService.pushNotification(map).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Responses body = response.body();

                if (body != null) {
                    Log.d("Notifikasi", "body: " + body.getData());
                    if (body.isStatus()) {
                        if (body.getCode() != 400) {
                            Log.d("Notifikasi", "Notifikasi Sukses: " + body.getMessage());
                        } else {
                            Log.d("Notifikasi", "Notifikasi Failed: " + body.getCode());
                        }
                    }
                } else {
                    Log.d("Notifikasi", "Notifikasi Response: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Log.d("Notifikasi", "onFailure: " + t.getMessage());
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(DetailBookingActivity.this);
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

    private void backActivity() {
        startActivity(new Intent(this, RiwayatBookingActivity.class));
        finish();
    }
}