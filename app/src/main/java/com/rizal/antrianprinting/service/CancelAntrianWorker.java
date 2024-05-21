package com.rizal.antrianprinting.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;
import com.rizal.antrianprinting.utils.Responses;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelAntrianWorker extends Worker {
    public CancelAntrianWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        cancelAntrian();
        return Result.success();
    }

    private void cancelAntrian() {
        Integer itemAntrianId = getInputData().getInt("antrianId", 0);
        MobileService mobileService = ApiUtils.MobileService(getApplicationContext());

        mobileService.cancelantrian(itemAntrianId).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Responses body = response.body();
                if (response.isSuccessful()) {
                    assert body != null;

                    if (!body.isStatus() && body.getCode() != 200) {
                        if (body.getCode() == 400) {
                            Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, silahkan coba lagi!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Preferences.setAntrian(getApplicationContext(), null);
                        Preferences.setAntrianFlagging(getApplicationContext(), null);

                        Toast.makeText(getApplicationContext(), "Antrian dibatalkan, Terima kasih!", Toast.LENGTH_SHORT).show();
                        getApplicationContext().startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Antrian gagal untuk dibatalkan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
