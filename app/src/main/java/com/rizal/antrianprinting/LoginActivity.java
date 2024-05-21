package com.rizal.antrianprinting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.rizal.antrianprinting.base.BaseActivity;
import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;
import com.rizal.antrianprinting.utils.Responses;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    Button btnMasuk, btnGoogle;
    TextInputEditText etEmail, etPassword;
    MobileService mobileService;
    ProgressDialog progressDialog;
    TextView txtDaftar, title_sheet, message_sheet;
    View bottom_sheet;
    ImageView close_sheet, icon_sheet;
    BottomSheetDialog bottomSheetDialog;
    MaterialButton action_positif, action_negatif;

    boolean doubleBackToExitPress = false;

    private static final int REQUEST_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViewId();
        setOnClick();
    }

    private void setViewId() {
        mobileService = ApiUtils.MobileService(getApplicationContext());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        bottomSheetDialog = new BottomSheetDialog(LoginActivity.this, R.style.BottomSheetDialogTheme);

        bottom_sheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, findViewById(R.id.bottom_sheet_dialog_konfirmasi));
        close_sheet = bottom_sheet.findViewById(R.id.btn_sheet_close);
        icon_sheet = bottom_sheet.findViewById(R.id.img_sheet_icon);
        title_sheet = bottom_sheet.findViewById(R.id.view_bottom_tittle);
        message_sheet = bottom_sheet.findViewById(R.id.view_bottom_message);
        action_positif = bottom_sheet.findViewById(R.id.btn_bottom_action_positif);
        action_negatif = bottom_sheet.findViewById(R.id.btn_bottom_action_negatif);

        btnMasuk = findViewById(R.id.btn_masuk);
        btnGoogle = findViewById(R.id.btn_google_login);
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_pass_login);
        txtDaftar = findViewById(R.id.tv_daftar);
    }

    private void setOnClick() {
        btnGoogle.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQUEST_SIGN_IN);
        });

        btnMasuk.setOnClickListener(v -> prosesLogin());
        txtDaftar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }

    private void firebaseLoginWithGoogle(GoogleSignInAccount account) {
        Log.d("Login Firebase", "Proses firebaseLoginWithGoogle");

        if (account != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if ((firebaseUser != null ? firebaseUser.getPhoneNumber() : null) == null || firebaseUser.getEmail() == null) {
                            showBottomSheet("Informasi", "No Handphone atau email pada akun anda kosong.", R.drawable.ic_profil_user, "Mengerti", "Daftar Akun");
                            return;
                        }

                        String uid = firebaseUser.getUid();
                        String username = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                        String phone = firebaseUser.getPhoneNumber();

                        // Set when data is not registered in db local
                        User stafUser = new User();
                        stafUser.setUid(uid);
                        stafUser.setUsername(username);
                        stafUser.setNama_lengkap(username);
                        stafUser.setEmail_user(email);
                        stafUser.setPhone_user(phone);

                        // Mapping Check User
                        Map<String, String> mapCheck = new HashMap<>();
                        mapCheck.put("phone_user", phone);
                        mapCheck.put("email_user", email);

                        // Set when data is registered in db local
                        Map<String, String> mapUser = new HashMap<>();
                        mapUser.put("username", username);
                        mapUser.put("nama_lengkap", username);
                        mapUser.put("alamat", "");
                        mapUser.put("jenis_kelamin", "");
                        mapUser.put("tempat_lahir", "");
                        mapUser.put("tanggal_lahir", "");
                        mapUser.put("password", phone);
                        mapUser.put("conf_password", phone);
                        mapUser.put("phone_user", phone);
                        mapUser.put("email_user", email);

                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            Log.d("Login Google", "onSuccess: Akun dibuat");
                        }

                        mobileService.checkuser(mapCheck).enqueue(new Callback<Responses>() {
                            @Override
                            public void onResponse(Call<Responses> call, Response<Responses> response) {
                                Responses body = response.body();

                                if (body != null) {
                                    Log.d("Login Google", "body: " + body.getData());
                                    if (body.isStatus()) {
                                        if (body.getData() != null) {
                                            User user = new Gson().fromJson(new Gson().toJson(body.getData()), User.class);
                                            Preferences.setUser(getApplicationContext(), user);

                                            Log.d("Login Google", "Login Sukses: " + user.getUsername());

                                            Toast.makeText(LoginActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            mobileService.createuser(mapUser).enqueue(new Callback<Responses>() {
                                                @Override
                                                public void onResponse(Call<Responses> call, Response<Responses> response) {
                                                    Responses body = response.body();
                                                    assert body != null;
                                                    if (body.isStatus()) {
                                                        if (body.getData() != null) {
                                                            User user = new Gson().fromJson(new Gson().toJson(body.getData()), User.class);
                                                            Preferences.setUser(getApplicationContext(), user);

                                                            Log.d("Login Google", "Berhasil Daftarkan User: " + user.getUsername());
                                                            Toast.makeText(LoginActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                        showBottomSheet("Informasi", body.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
                                                    } else {
                                                        showBottomSheet("Informasi", body.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
                                                        Log.d("Login Google", "Gagal Daftarkan User: " + body.getCode());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Responses> call, Throwable t) {
                                                    showBottomSheet("Informasi", t.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
                                                    Log.d("Failure Login", "onFailure: " + t.getMessage());
                                                }
                                            });
                                        }
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                } else {
                                    Log.d("Failure Login", "body: " + response.errorBody());
                                    showBottomSheet("Informasi", response.message(), R.drawable.ic_profil_user, "Mengerti", "");
                                }
                            }

                            @Override
                            public void onFailure(Call<Responses> call, Throwable t) {
                                showBottomSheet("Informasi", t.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
                                Log.d("Failure Login", "onFailure: " + t.getMessage());
                            }
                        });

                    }).addOnFailureListener(e -> Log.d("Login Google", "onFailure: Login failed " + e.getMessage()));
        }
    }

    private void prosesLogin() {
        String inputEmail = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String inputPass = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(inputEmail)) {
            etEmail.setError("Kolom Email masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputPass)) {
            etPassword.setError("Kolom Password masih kosong");
            return;
        }

        if (inputEmail.isEmpty() && inputPass.isEmpty()) {
            showMessage("Kolom masih kosong, Silahkan lengkapi!");
            return;
        }

        showProgressDialog();

        Map<String, String> map = new HashMap<>();
        map.put("input", inputEmail);
        map.put("password", inputPass);

        mobileService.login(map).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, retrofit2.Response<Responses> response) {
                Responses body = response.body();
                Log.d("Login Response", "onResponse: " + response.message());
                if (body != null) {
                    if (response.isSuccessful()) {
                        User user = new Gson().fromJson(new Gson().toJson(body.getData()), User.class);
                        if (!body.isStatus() && body.getCode() != 200) {
                            dismissProgressDialog();

                            Log.d("Login Status", "onResponse: " + body.getMessage());
                            showMessage("Email atau password anda salah, coba lagi!");
                        } else {
                            requestFCMToken(user.getId()); // Request FCM Token User then update to table user

                            dismissProgressDialog();

                            Preferences.setUser(getApplicationContext(), user);
                            showMessage("Selamat datang, " + user.getNama_lengkap());

                            Preferences.setLoginFlag(getApplicationContext(), true);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        dismissProgressDialog();
                        Log.d("Failure Login", "onFailure: " + body.getMessage());

                        showMessage(body.getMessage());
                    }
                } else {
                    dismissProgressDialog();
                    showMessage("Terjadi kesalahan, coba lagi!");
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                dismissProgressDialog();

                Log.d("Failure Login", "onFailure: " + t.getMessage());
                showMessage("Terjadi kesalahan, Periksa koneksi anda");
            }
        });
    }

    private void requestFCMToken(int id) {
        HashMap<String, String> map = new HashMap<>();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();

                        map.put("id_user", String.valueOf(id));
                        map.put("fcm_token", token);

                        mobileService.updateFCMToken(map).enqueue(new Callback<Responses>() {
                            @Override
                            public void onResponse(Call<Responses> call, Response<Responses> response) {
                                Responses body = response.body();
                                if (body != null) {
                                    if (body.getCode() == 200) {
                                        Log.e("FCMToken", "Success to update fcm token");
                                    } else {
                                        Log.e("FCMToken", "Failed to update fcm token cause code is not 200");
                                    }
                                } else {
                                    Log.e("FCMToken", "Failed cause body response is null. " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<Responses> call, Throwable t) {
                                Log.e("FCMToken", "Failed to update fcm token");
                            }
                        });
                    } else {
                        Log.e("FCMToken", "Failed to update fcm token");
                    }
                });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN) {
            Log.d("Login Google", "Activity Result: Running");

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseLoginWithGoogle(account);
        } catch (ApiException e) {
            Log.d("Login Google", "signInResult:failed code=" + e.getStatusCode());
            firebaseLoginWithGoogle(null);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheet(String informasi, String message, int icon, String positifAction, String negatifAction) {
        bottomSheetDialog.setContentView(bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        icon_sheet.setImageDrawable(getResources().getDrawable(icon));
        title_sheet.setText(informasi);
        message_sheet.setText(message);

        action_positif.setText(positifAction);
        action_positif.setOnClickListener(view -> bottomSheetDialog.dismiss());

        action_negatif.setText(negatifAction);
        if (negatifAction != null) {
            action_negatif.setOnClickListener(view -> {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

                bottomSheetDialog.dismiss();
                finish();
            });
        } else {
            action_negatif.setVisibility(View.GONE);
        }

        close_sheet.setOnClickListener(view -> bottomSheetDialog.dismiss());
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