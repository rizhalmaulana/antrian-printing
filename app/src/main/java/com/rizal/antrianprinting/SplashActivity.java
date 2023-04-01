package com.rizal.antrianprinting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.utils.Preferences;

public class SplashActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        user = Preferences.getUser(getApplicationContext());

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(() -> {
                    if (user != null) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    finish();
                });
            }
        }).start();
    }
}