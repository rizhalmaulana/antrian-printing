package com.rizal.antrianprinting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.utils.Preferences;

public class ProfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView txtNama, txtEmail;
    ImageView ivBack;
    LinearLayout layoutEditProfil, layoutGantiPass;

    String namaUser = "";
    String emailUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        txtEmail = findViewById(R.id.text_email_profil);
        txtNama = findViewById(R.id.text_nama_profil);
        ivBack = findViewById(R.id.iv_back_profil);

        layoutEditProfil = findViewById(R.id.layout_edit_profil);
        layoutGantiPass = findViewById(R.id.layout_ganti_password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        checkUser();

        layoutEditProfil.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Fitur Dalam Pengerjaan", Toast.LENGTH_SHORT).show());
        layoutGantiPass.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Fitur Dalam Pengerjaan", Toast.LENGTH_SHORT).show());
        ivBack.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }

    private void checkUser() {
        if (firebaseAuth != null) {
            namaUser = firebaseUser.getDisplayName();
            emailUser = firebaseUser.getEmail();

        } else {
            User user = Preferences.getUser(getApplicationContext());

            if (user != null) {
                namaUser = user.getUsername();
                emailUser = user.getEmail_user();
            } else {
                namaUser = "User";
                emailUser = "user@gmail.com";
            }
        }

        txtNama.setText(namaUser);
        txtEmail.setText(emailUser);
    }
}