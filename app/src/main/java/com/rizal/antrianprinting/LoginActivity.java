package com.rizal.antrianprinting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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

public class LoginActivity extends BaseActivity {
    Button btnMasuk;
    TextInputEditText etEmail, etPassword;
    MobileService mobileService;
    ProgressDialog progressDialog;
    TextView txtDaftar;

    boolean doubleBackToExitPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViewId();
        setOnClick();
    }

    private void setViewId() {
        mobileService = ApiUtils.MobileService(getApplicationContext());

        btnMasuk = findViewById(R.id.btn_masuk);
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_pass_login);
        txtDaftar = findViewById(R.id.tv_daftar);
    }

    private void setOnClick() {
        btnMasuk.setOnClickListener(v -> prosesLogin());
        txtDaftar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
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
                Log.d("Login Response", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    User user = new Gson().fromJson(new Gson().toJson(body.getData()), User.class);
                    if (!body.isStatus() && body.getCode() != 200) {
                        dismissProgressDialog();

                        Log.d("Login Status", "onResponse: " + body.isStatus());
                        showMessage("Email atau password anda salah.");
                    } else {
                        dismissProgressDialog();

                        Preferences.setUser(getApplicationContext(), user);
                        showMessage("Selamat datang, " + user.getNama_lengkap());

                        Preferences.setLoginFlag(getApplicationContext(), true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    dismissProgressDialog();
                    Log.d("Failure Login", "onFailure: " + response.errorBody());

                    showMessage("Gagal login, Silahkan coba lagi.");
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
    public void onBackPressed() {
        if (doubleBackToExitPress){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPress = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPress = false, 2000);
    }
}