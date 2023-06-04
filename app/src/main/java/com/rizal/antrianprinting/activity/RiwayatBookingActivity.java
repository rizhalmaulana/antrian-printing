package com.rizal.antrianprinting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.adapter.RiwayatBookingAdapter;
import com.rizal.antrianprinting.models.riwayat.RiwayatBookingItem;
import com.rizal.antrianprinting.models.riwayat.RiwayatResponse;
import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatBookingActivity extends AppCompatActivity {

    private ArrayList<RiwayatBookingItem> listRiwayat = new ArrayList<>();
    private RecyclerView recyclerView_booking;

    LinearLayout layout_not_found, layout_list_antrian, layout_kembali;
    ImageView img_content_not_found;
    RiwayatBookingAdapter riwayatBookingAdapter;
    MobileService mobileService;
    ProgressDialog progress_dialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_booking);

        riwayatBookingAdapter = new RiwayatBookingAdapter(getApplicationContext(), listRiwayat);

        user = Preferences.getUser(getApplicationContext());
        mobileService = ApiUtils.MobileService(getApplicationContext());

        layout_not_found = findViewById(R.id.menu_no_content);
        layout_list_antrian = findViewById(R.id.menu_riwayat_booking);
        img_content_not_found = findViewById(R.id.iv_data_not_found);
        layout_kembali = findViewById(R.id.lr_back_riwayat);

        recyclerView_booking = findViewById(R.id.recycle_booking);
        recyclerView_booking.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutBooking = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView_booking.setLayoutManager(layoutBooking);

        layout_kembali.setOnClickListener(v -> {
            startActivity(new Intent(RiwayatBookingActivity.this, MainActivity.class));
            finish();
        });

        loadRiwayatBooking();
    }

    private void loadRiwayatBooking() {
        showProgressDialog();

        mobileService.getriwayat(user.getId()).enqueue(new Callback<RiwayatResponse>() {
            @Override
            public void onResponse(Call<RiwayatResponse> call, Response<RiwayatResponse> response) {
                initializeRiwayat();

                RiwayatResponse body = response.body();
                Log.d("Get response body", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (!body.isStatus()) {
                        Log.d("Get response", "onResponse: " + response.errorBody());

                        layout_not_found.setVisibility(View.GONE);
                        layout_list_antrian.setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan riwayat, coba lagi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Data onResponse Antrian", "Status data antrian: " + body.getData());
                        if (body.getData() != null) {

                            for (int i = 0; i < body.getData().size(); i++) {
                                final RiwayatBookingItem itemAntrian = new RiwayatBookingItem();

                                itemAntrian.setId(body.getData().get(i).getId());
                                itemAntrian.setId_user(body.getData().get(i).getId_user());
                                itemAntrian.setNama_designer(body.getData().get(i).getNama_designer());
                                itemAntrian.setJenis_layanan(body.getData().get(i).getJenis_layanan());
                                itemAntrian.setJam_booking(body.getData().get(i).getJam_booking());
                                itemAntrian.setJam_selesai(body.getData().get(i).getJam_selesai());
                                itemAntrian.setTgl_pesanan(body.getData().get(i).getTgl_pesanan());
                                itemAntrian.setNo_handphone(body.getData().get(i).getNo_handphone());
                                itemAntrian.setStatus(body.getData().get(i).getStatus());

                                listRiwayat.add(itemAntrian);
                            }

                            riwayatBookingAdapter = new RiwayatBookingAdapter(getApplicationContext(), listRiwayat);
                            recyclerView_booking.setAdapter(riwayatBookingAdapter);

                            layout_list_antrian.setVisibility(View.VISIBLE);
                            layout_not_found.setVisibility(View.GONE);
                        } else {
                            Log.d("Data onResponse Antrian", "Status data antrian: " + body.getData());

                            layout_not_found.setVisibility(View.VISIBLE);
                            layout_list_antrian.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), "Belum ada riwayat antrian, coba booking yuk!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.d("Get response", "onResponse: " + response.errorBody());

                    layout_not_found.setVisibility(View.VISIBLE);
                    layout_list_antrian.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<RiwayatResponse> call, Throwable t) {
                Log.d("onThrowable", "Status Antrian: " + t);

                layout_not_found.setVisibility(View.VISIBLE);
                layout_list_antrian.setVisibility(View.GONE);
            }
        });
    }

    private void initializeRiwayat() {
        listRiwayat = new ArrayList<>();
        riwayatBookingAdapter = new RiwayatBookingAdapter(getApplicationContext(), listRiwayat);

        recyclerView_booking.setAdapter(riwayatBookingAdapter);
        listRiwayat.clear();
    }

    private void showProgressDialog() {
        progress_dialog = new ProgressDialog(RiwayatBookingActivity.this);
        progress_dialog.show();
        progress_dialog.setContentView(R.layout.item_progress_bar);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void dismissProgressDialog() {
        progress_dialog.dismiss();
    }
}