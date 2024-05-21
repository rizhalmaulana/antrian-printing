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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
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
import retrofit2.Response;

public class RegistrationActivity extends BaseActivity {
    Button btnRegister;
    TextInputEditText etNama, etUsername, etEmail, etPass, etConfirmPass, etPhone;
    TextView txtLogin, title_sheet, message_sheet;
    ImageView close_sheet, icon_sheet;
    MobileService mobileService;
    BottomSheetDialog bottomSheetDialog;
    MaterialButton action_positif, action_negatif;
    ProgressDialog progressDialog;
    View bottom_sheet;

    boolean doubleBackToExitPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mobileService = ApiUtils.MobileService(getApplicationContext());

        bottomSheetDialog = new BottomSheetDialog(RegistrationActivity.this, R.style.BottomSheetDialogTheme);

        bottom_sheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, findViewById(R.id.bottom_sheet_dialog_konfirmasi));
        close_sheet = bottom_sheet.findViewById(R.id.btn_sheet_close);
        icon_sheet = bottom_sheet.findViewById(R.id.img_sheet_icon);
        title_sheet = bottom_sheet.findViewById(R.id.view_bottom_tittle);
        message_sheet = bottom_sheet.findViewById(R.id.view_bottom_message);
        action_positif = bottom_sheet.findViewById(R.id.btn_bottom_action_positif);
        action_negatif = bottom_sheet.findViewById(R.id.btn_bottom_action_negatif);

        txtLogin = findViewById(R.id.text_login_daftar);
        btnRegister = findViewById(R.id.btn_daftar);

        etNama = findViewById(R.id.et_nama_daftar);
        etUsername = findViewById(R.id.et_user_daftar);
        etEmail = findViewById(R.id.et_email_daftar);
        etPass = findViewById(R.id.et_pass_daftar);
        etConfirmPass = findViewById(R.id.et_confirm_pass_daftar);
        etPhone = findViewById(R.id.et_phone_daftar);

        btnRegister.setOnClickListener(v -> prosesDaftar());

        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void prosesDaftar() {
        String inputNama = Objects.requireNonNull(etNama.getText()).toString().trim();
        String inputUsername = Objects.requireNonNull(etUsername.getText()).toString().trim();
        String inputEmail = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String inputPass = Objects.requireNonNull(etPass.getText()).toString().trim();
        String inputConfirmPass = Objects.requireNonNull(etConfirmPass.getText()).toString().trim();
        String inputPhone = Objects.requireNonNull(etPhone.getText()).toString().trim();

        if (TextUtils.isEmpty(inputNama)) {
            etNama.setError("Kolom nama lengkap masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputUsername)) {
            etUsername.setError("Kolom username masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputEmail)) {
            etEmail.setError("Kolom email masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputPass)) {
            etPass.setError("Kolom password masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputConfirmPass)) {
            etConfirmPass.setError("Kolom konfirmasi password masih kosong");
            return;
        }

        if (TextUtils.isEmpty(inputPhone)) {
            etPhone.setError("Kolom nomor handphone masih kosong");
            return;
        }

        if (!inputPass.equals(inputConfirmPass)) {
            showMessage("Password yang dimasukkan tidak sama!");
            return;
        }

        if (inputEmail.isEmpty() && inputPass.isEmpty()) {
            showMessage("Kolom masih kosong, Silahkan lengkapi!");
            return;
        }

        showProgressDialog();

        Map<String, String> mapRegister = new HashMap<>();
        mapRegister.put("username", inputUsername);
        mapRegister.put("nama_lengkap", inputNama);
        mapRegister.put("alamat", "");
        mapRegister.put("jenis_kelamin", "");
        mapRegister.put("tempat_lahir", "");
        mapRegister.put("tanggal_lahir", "");
        mapRegister.put("password", inputPass);
        mapRegister.put("conf_password", inputConfirmPass);
        mapRegister.put("phone_user", inputPhone);
        mapRegister.put("email_user", inputEmail);

        mobileService.createuser(mapRegister).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Responses body = response.body();

                if (body != null) {
                    Log.d("Registration", "body: " + body.getData());
                    if (body.isStatus()) {
                        Log.d("Registration", "body: " + body.getData());
                        if (body.getData() != null) {
                            dismissProgressDialog();

                            User user = new Gson().fromJson(new Gson().toJson(body.getData()), User.class);
                            Preferences.setUser(getApplicationContext(), user);

                            Preferences.setUser(getApplicationContext(), user);
                            showMessage("Selamat datang, " + user.getNama_lengkap());

                            Preferences.setLoginFlag(getApplicationContext(), true);

                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        dismissProgressDialog();

                        Log.d("Registration", "body: " + body.getMessage());
                        showBottomSheet("Informasi", "Terjadi kesalahan, " + body.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
                    }
                } else {
                    dismissProgressDialog();

                    Log.d("Registration", "body: " + response.errorBody());
                    showBottomSheet("Informasi", "Terjadi kesalahan, " + response.message(), R.drawable.ic_profil_user, "Mengerti", "");
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                dismissProgressDialog();

                Log.d("Registration", "body: " + t.getMessage());
                showBottomSheet("Informasi", "Gagal melakukan registrasi, " + t.getMessage(), R.drawable.ic_profil_user, "Mengerti", "");
            }
        });
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
            action_negatif.setOnClickListener(view -> bottomSheetDialog.dismiss());
        } else {
            action_negatif.setVisibility(View.GONE);
        }

        close_sheet.setOnClickListener(view -> bottomSheetDialog.dismiss());
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(RegistrationActivity.this);
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