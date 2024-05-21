package com.rizal.antrianprinting.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.rizal.antrianprinting.MainActivity;
import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.base.BaseActivity;
import com.rizal.antrianprinting.models.antrian.Antrian;
import com.rizal.antrianprinting.models.antrian.AntrianFlagging;
import com.rizal.antrianprinting.models.antrian.AntrianResponses;
import com.rizal.antrianprinting.models.designer.DesignerItem;
import com.rizal.antrianprinting.models.designer.DesignerResponse;
import com.rizal.antrianprinting.models.layanan.LayananItem;
import com.rizal.antrianprinting.models.layanan.LayananResponse;
import com.rizal.antrianprinting.models.user.User;
import com.rizal.antrianprinting.models.waktubooking.WaktuBookingItem;
import com.rizal.antrianprinting.models.waktubooking.WaktuBookingResponse;
import com.rizal.antrianprinting.models.waktuselesai.WaktuSelesaiItem;
import com.rizal.antrianprinting.models.waktuselesai.WaktuSelesaiResponse;
import com.rizal.antrianprinting.service.BookingWorker;
import com.rizal.antrianprinting.utils.ApiUtils;
import com.rizal.antrianprinting.utils.MobileService;
import com.rizal.antrianprinting.utils.Preferences;
import com.rizal.antrianprinting.utils.Responses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends BaseActivity {

    private final ArrayList<String> listWaktuBooking = new ArrayList<>();
    private final ArrayList<String> listWaktuSelesai = new ArrayList<>();
    private final ArrayList<String> listDesigner = new ArrayList<>();
    private final ArrayList<String> listLayanan = new ArrayList<>();

    LinearLayout layout_back;
    ImageView close_sheet, icon_sheet;
    AutoCompleteTextView pilih_jam_booking, pilih_jam_selesai, pilih_designer, pilih_layanan;
    TextInputEditText input_nomor_whatsapp;
    Button cek_antrian, input_tanggal_pesanan;
    ProgressDialog progress_dialog;
    MobileService mobile_service, fcm_service;
    View bottom_sheet;
    MaterialButton action_positif, action_negatif;
    BottomSheetDialog bottomSheetDialog;
    TextView title_sheet, message_sheet;
    User user;

    private Handler handler;
    private Runnable checkBookingRunnable;
    private static final long CHECK_INTERVAL = 60000; // 1 minute

    private String pick_tanggal = "";
    long scheduleTime = 0L;

    public static final String TAG = "BookingActivity";
    public static final String TOPIC = "all_user";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mobile_service = ApiUtils.MobileService(getApplicationContext());
        fcm_service = ApiUtils.FCMService(getApplicationContext());
        user = Preferences.getUser(getApplicationContext());

        bottomSheetDialog = new BottomSheetDialog(BookingActivity.this, R.style.BottomSheetDialogTheme);
        handler = new Handler();

        bottom_sheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, findViewById(R.id.bottom_sheet_dialog_konfirmasi));
        close_sheet = bottom_sheet.findViewById(R.id.btn_sheet_close);
        icon_sheet = bottom_sheet.findViewById(R.id.img_sheet_icon);
        title_sheet = bottom_sheet.findViewById(R.id.view_bottom_tittle);
        message_sheet = bottom_sheet.findViewById(R.id.view_bottom_message);
        action_positif = bottom_sheet.findViewById(R.id.btn_bottom_action_positif);
        action_negatif = bottom_sheet.findViewById(R.id.btn_bottom_action_negatif);

        layout_back = findViewById(R.id.lr_back_booking);
        pilih_jam_booking = findViewById(R.id.et_pilih_jam_booking);
        pilih_jam_selesai = findViewById(R.id.et_pilih_jam_selesai);
        pilih_designer = findViewById(R.id.et_pilih_designer);
        pilih_layanan = findViewById(R.id.et_pilih_pelayanan);
        input_tanggal_pesanan = findViewById(R.id.et_input_tanggal);
        input_nomor_whatsapp = findViewById(R.id.et_no_handphone);
        cek_antrian = findViewById(R.id.btn_ketersediaan_booking);

        setupDataAntrian();
        configureMessage();

        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        final MaterialDatePicker.Builder<Long> materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker();
        materialDatePickerBuilder.setTitleText("Pilih Tanggal");

        // material date picker Calendar constraints
        materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build());

        // now build the material date picker dialog
        final MaterialDatePicker<Long> materialDatePicker = materialDatePickerBuilder.build();

        pilih_jam_booking.setOnItemClickListener((adapterView, view, i, l) -> Log.d("Selected item", pilih_jam_booking.getText().toString()));
        pilih_jam_selesai.setOnItemClickListener((adapterView, view, i, l) -> Log.d("Selected item", pilih_jam_selesai.getText().toString()));
        pilih_designer.setOnItemClickListener((adapterView, view, i, l) -> Log.d("Selected item", pilih_designer.getText().toString()));
        pilih_layanan.setOnItemClickListener((adapterView, view, i, l) -> Log.d("Selected item", pilih_layanan.getText().toString()));

        input_tanggal_pesanan.setOnClickListener(view -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            String dateInString = materialDatePicker.getHeaderText();

            try {
                Date date = formatter.parse(dateInString);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");

                pick_tanggal = print.format(date != null ? date : materialDatePicker.getHeaderText());

            } catch (ParseException e) {
                Log.d(TAG, "err: " + e.getMessage());
                e.printStackTrace();
            }

            input_tanggal_pesanan.setText(materialDatePicker.getHeaderText());
        });

        layout_back.setOnClickListener(v -> {
            startActivity(new Intent(BookingActivity.this, MainActivity.class));
            finish();
        });

        cek_antrian.setOnClickListener(v -> cekAntrianTersedia());
    }

    private void cekAntrianTersedia() {
        String input_booking = pilih_jam_booking.getText().toString().trim();
        String input_selesai = pilih_jam_selesai.getText().toString().trim();
        String input_designer = pilih_designer.getText().toString().trim();
        String input_layanan = pilih_layanan.getText().toString().trim();
        String input_tanggal = pick_tanggal;
        String input_number = Objects.requireNonNull(input_nomor_whatsapp.getText()).toString().trim();

        if (TextUtils.isEmpty(input_booking)) {
            pilih_jam_booking.setError("Jam antrian harus diisi.");
            return;
        }

        if (TextUtils.isEmpty(input_selesai)) {
            pilih_jam_selesai.setError("Jam selesai harus diisi.");
            return;
        }

        if (TextUtils.isEmpty(input_designer)) {
            pilih_designer.setError("Pilih designer terlebih dahulu.");
            return;
        }

        if (TextUtils.isEmpty(input_layanan)) {
            pilih_layanan.setError("Pilih layanan terlebih dahulu.");
            return;
        }

        if (TextUtils.isEmpty(input_number)) {
            input_nomor_whatsapp.setError("Nomor handphone harus diisi.");
            return;
        }

        if (input_number.length() < 10) {
            input_nomor_whatsapp.setError("Batas minimal No Handphone 10 digit.");
            return;
        }

        if (input_number.length() > 13) {
            input_nomor_whatsapp.setError("Batas maksimal No Handphone 13 digit.");
            return;
        }

        if (input_tanggal.isEmpty() || TextUtils.isEmpty(input_tanggal)) {
            showMessage("Silahkan isi tanggal antrian anda.");
        }

        Map<String, String> mapInput = new HashMap<>();
        mapInput.put("id_user", String.valueOf(user.getId()));
        mapInput.put("nama_designer", input_designer);
        mapInput.put("jenis_layanan", input_layanan);
        mapInput.put("jam_booking", input_booking);
        mapInput.put("jam_selesai", input_selesai);
        mapInput.put("tgl_pesanan", input_tanggal);
        mapInput.put("no_handphone", input_number);

        showSubmitDialog();

        mobile_service.checkantrian(mapInput).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, retrofit2.Response<Responses> response) {
                Responses body = response.body();
                Log.d("Get Error Response", "onResponse: " + response.message());
                if (response.isSuccessful()) {
                    if (body != null) {
                        if (!body.isStatus() && body.getCode() != 200) {
                            showBottomSheet("Informasi", body.getMessage(), R.drawable.ic_profil_user, "Oke", "Kembali", body.getCode());
                        } else {
                            showBottomSheet("Informasi", body.getMessage(), R.drawable.ic_location, "Konfirmasi", "Batal", body.getCode());
                        }
                    }
                } else {
                    Log.d("Failure Cek Antrian", "onFailure: " + response.errorBody());
                    Toast.makeText(getApplicationContext(), "Gagal mendapatkan data ketersediaan antrian, coba lagi!", Toast.LENGTH_LONG).show();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                dismissProgressDialog();
                Log.d("Get error message", "onError: " + t.getMessage());

                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prosesSubmitAntrian() {
        bottomSheetDialog.dismiss();

        String input_booking = pilih_jam_booking.getText().toString().trim();
        String input_selesai = pilih_jam_selesai.getText().toString().trim();
        String input_designer = pilih_designer.getText().toString().trim();
        String input_layanan = pilih_layanan.getText().toString().trim();
        String input_tanggal = pick_tanggal;
        String input_number = Objects.requireNonNull(input_nomor_whatsapp.getText()).toString().trim();

        Map<String, String> map = new HashMap<>();
        map.put("id_user", String.valueOf(user.getId()));
        map.put("nama_designer", input_designer);
        map.put("jenis_layanan", input_layanan);
        map.put("jam_booking", input_booking);
        map.put("jam_selesai", input_selesai);
        map.put("tgl_pesanan", input_tanggal);
        map.put("no_handphone", input_number);

        showSubmitDialog();

        mobile_service.createantrian(map).enqueue(new Callback<AntrianResponses>() {
            @Override
            public void onResponse(Call<AntrianResponses> call, Response<AntrianResponses> response) {
                AntrianResponses body = response.body();
                Log.d("Get Error Response", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (!body.isStatus() && body.getCode() != 200) {
                        showBottomSheetBooking("Informasi", body.getMessage(), R.drawable.ic_profil_user, "Oke", "Kembali", body.getCode());
                    } else {
                        retrieveRiwayatBooking();
                        showBottomSheetBooking("Berhasil", body.getMessage(), R.drawable.ic_location, "Mengerti", "", body.getCode());
                    }
                } else {
                    Log.d("Failure Cek Antrian", "onFailure: " + response.errorBody());
                    Toast.makeText(getApplicationContext(), "Gagal booking antrian, coba lagi!", Toast.LENGTH_LONG).show();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<AntrianResponses> call, Throwable t) {
                dismissProgressDialog();
                Log.d("Get error message", "onError: " + t.getMessage());

                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveRiwayatBooking() {
        mobile_service.getantrian(user.getId()).enqueue(new Callback<AntrianResponses>() {
            @Override
            public void onResponse(Call<AntrianResponses> call, retrofit2.Response<AntrianResponses> response) {
                AntrianResponses body = response.body();
                Log.d("Get Id Antrian Response", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;

                    Antrian antrianResponse = new Gson().fromJson(new Gson().toJson(body.getData()), Antrian.class);
                    AntrianFlagging antrianFlagging = new Gson().fromJson(new Gson().toJson(body.getTime()), AntrianFlagging.class);

                    if (!body.isStatus() && body.getCode() != 200) {
                        Log.d("Get Antrian Status", "onResponse: " + body.isStatus());
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();

                        if (body.getCode() == 400) {
                            dismissProgressDialog();
                        }

                    } else {
                        Preferences.setAntrian(getApplicationContext(), antrianResponse);
                        Preferences.setAntrianFlagging(getApplicationContext(), antrianFlagging);

                        startScheduleAntrian(antrianFlagging);
                    }
                } else {
                    Log.d("Failure Get Antrian", "onFailure: " + response.errorBody());
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AntrianResponses> call, Throwable t) {
                Log.d("Failure Get Antrian", "onFailure: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDataAntrian() {
        showProgressDialog();
        initializeData();

        mobile_service.getjambooking().enqueue(new Callback<WaktuBookingResponse>() {
            @Override
            public void onResponse(Call<WaktuBookingResponse> call, Response<WaktuBookingResponse> response) {
                WaktuBookingResponse body = response.body();
                Log.d("Get response body", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (body.isStatus()) {
                        Log.d("Get data", "onResponseData : " + body.getData());
                        for (int i = 0; i < body.getData().size(); i++) {
                            final WaktuBookingItem item = new WaktuBookingItem();
                            item.setJam_booking(body.getData().get(i).getJam_booking());
                            listWaktuBooking.add(item.toString());
                        }
                        ArrayAdapter<String> adapterWaktuBooking = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_booking, listWaktuBooking);
                        pilih_jam_booking.setAdapter(adapterWaktuBooking);

                        setupDataSelesai();

                        Log.d("Adapter jam booking", "adapter: " + adapterWaktuBooking);
                    } else {
                        Log.d("Get response", "onResponse: " + response.errorBody());

                        pilih_jam_booking.setText(R.string.jam_antrian);
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan jam booking, coba lagi!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Get response", "onResponse: " + response.errorBody());

                    pilih_jam_booking.setText(R.string.jam_antrian);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<WaktuBookingResponse> call, Throwable t) {
                Log.d("Get error message", "onError: " + t.getMessage());

                pilih_jam_booking.setText(R.string.jam_antrian);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }
        });
    }

    private void setupDataSelesai() {
        mobile_service.getjamselesai().enqueue(new Callback<WaktuSelesaiResponse>() {
            @Override
            public void onResponse(Call<WaktuSelesaiResponse> call, Response<WaktuSelesaiResponse> response) {
                WaktuSelesaiResponse body = response.body();
                Log.d("Get response body", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (body.isStatus()) {
                        Log.d("Get data", "onResponseData : " + body.getData());
                        for (int i = 0; i < body.getData().size(); i++) {
                            final WaktuSelesaiItem item = new WaktuSelesaiItem();
                            item.setJam_selesai(body.getData().get(i).getJam_selesai());
                            listWaktuSelesai.add(item.toString());
                        }
                        ArrayAdapter<String> adapterWaktuSelesai = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_selesai, listWaktuSelesai);
                        pilih_jam_selesai.setAdapter(adapterWaktuSelesai);

                        setupDataDesigner();

                        Log.d("Adapter jam booking", "adapter: " + adapterWaktuSelesai);
                    } else {
                        Log.d("Get response", "onResponse: " + response.errorBody());

                        pilih_jam_selesai.setText(R.string.jam_selesai);
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan jam selesai, coba lagi!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Get response", "onResponse: " + response.errorBody());

                    pilih_jam_selesai.setText(R.string.jam_selesai);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WaktuSelesaiResponse> call, Throwable t) {
                Log.d("Get error message", "onError: " + t.getMessage());

                pilih_jam_selesai.setText(R.string.jam_selesai);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDataDesigner() {
        mobile_service.getdesigner().enqueue(new Callback<DesignerResponse>() {
            @Override
            public void onResponse(Call<DesignerResponse> call, Response<DesignerResponse> response) {
                DesignerResponse body = response.body();
                Log.d("Get response body", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (body.isStatus()) {
                        Log.d("Get data", "onResponseData : " + body.getData());

                        for (int i = 0; i < body.getData().size(); i++) {
                            final DesignerItem item = new DesignerItem();

                            item.setId(body.getData().get(i).getId());
                            item.setUsername(body.getData().get(i).getUsername());

                            listDesigner.add(item.toString());
                        }

                        ArrayAdapter<String> adapterDesigner = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_designer, listDesigner);
                        pilih_designer.setAdapter(adapterDesigner);

                        setupDataLayanan();
                    } else {
                        Log.d("Get response", "onResponse: " + response.errorBody());

                        pilih_designer.setText(R.string.pilih_designer);
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan designer, coba lagi!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Get response", "onResponse: " + response.errorBody());

                    pilih_designer.setText(R.string.pilih_designer);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DesignerResponse> call, Throwable t) {
                Log.d("Get error message", "onError: " + t.getMessage());

                pilih_designer.setText(R.string.pilih_designer);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDataLayanan() {
        mobile_service.getlayanan().enqueue(new Callback<LayananResponse>() {
            @Override
            public void onResponse(Call<LayananResponse> call, Response<LayananResponse> response) {
                LayananResponse body = response.body();
                Log.d("Get response body", "onResponse: " + response.errorBody());
                if (response.isSuccessful()) {
                    assert body != null;
                    if (body.isStatus()) {
                        Log.d("Get data", "onResponseData : " + body.getData());
                        for (int i = 0; i < body.getData().size(); i++) {
                            final LayananItem item = new LayananItem();

                            item.setNama_layanan(body.getData().get(i).getNama_layanan());
                            listLayanan.add(item.toString());
                        }
                        ArrayAdapter<String> adapterLayanan = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_layanan, listLayanan);
                        pilih_layanan.setAdapter(adapterLayanan);

                        Log.d("Adapter layanan", "adapter: " + adapterLayanan);
                    } else {
                        Log.d("Get response", "onResponse: " + response.errorBody());

                        pilih_layanan.setText(R.string.pilih_pelayanan);
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan layanan, coba lagi!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Get response", "onResponse: " + response.errorBody());

                    pilih_layanan.setText(R.string.pilih_pelayanan);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LayananResponse> call, Throwable t) {
                Log.d("Get error message", "onError: " + t.getMessage());

                pilih_layanan.setText(R.string.pilih_pelayanan);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, periksa koneksi anda!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startScheduleAntrian(AntrianFlagging antrianFlagging) {
        String jamReminder = antrianFlagging.getJam_reminder();
        SimpleDateFormat formatWaktu = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        Data itemAntrianId = new Data.Builder()
                .putInt("antrianId", antrianFlagging.getId_antrian())
                .build();

        try {
            // Parsing the booking time
            Calendar bookingCalendar = Calendar.getInstance();
            bookingCalendar.setTime(formatWaktu.parse(jamReminder));

            Calendar currentCalendar = Calendar.getInstance();
            bookingCalendar.set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BookingWorker.class)
                    .setInitialDelay(60, TimeUnit.MINUTES)
                    .setInputData(itemAntrianId)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);
            Toast.makeText(this, "Segera lakukan konfirmasi antrian!", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            Toast.makeText(this, "Terjadi kesalahan, invalid format waktu. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void configureMessage() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(authResult -> FirebaseMessaging.getInstance()
                            .subscribeToTopic("All"));
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);
        }
    }

    private void initializeData() {
        initializeWaktuBooking();
        initializeWaktuSelesai();
        initalizeDesigner();
        initalizeLayanan();
    }

    private void initializeWaktuBooking() {
        listWaktuBooking.clear();
        pilih_jam_booking.setText("");
    }

    private void initializeWaktuSelesai() {
        listWaktuSelesai.clear();
        pilih_jam_selesai.setText("");
    }

    private void initalizeDesigner() {
        listDesigner.clear();
        pilih_designer.setText("");
    }

    private void initalizeLayanan() {
        listLayanan.clear();
        pilih_layanan.setText("");
    }

    private void showProgressDialog() {
        progress_dialog = new ProgressDialog(BookingActivity.this);
        progress_dialog.show();
        progress_dialog.setContentView(R.layout.item_progress_bar);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void showSubmitDialog() {
        progress_dialog = new ProgressDialog(BookingActivity.this);
        progress_dialog.show();
        progress_dialog.setContentView(R.layout.item_submit_bar);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void dismissProgressDialog() {
        progress_dialog.dismiss();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheetBooking(String informasi, String message, int icon, String positifAction, String negatifAction, int code) {
        bottomSheetDialog.setContentView(bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        icon_sheet.setImageDrawable(getResources().getDrawable(icon));
        title_sheet.setText(informasi);
        message_sheet.setText(message);

        action_positif.setText(positifAction);
        action_negatif.setText(negatifAction);

        action_positif.setOnClickListener(view -> {
            if (code == 200) {
                bottomSheetDialog.dismiss();

                startActivity(new Intent(BookingActivity.this, RiwayatBookingActivity.class));
                finish();
            } else if (code == 100) {
                startActivity(new Intent(BookingActivity.this, RiwayatBookingActivity.class));
            } else {
                bottomSheetDialog.dismiss();
            }
        });

        if (negatifAction != null) {
            action_negatif.setOnClickListener(view -> {
                if (code == 200) {
                    bottomSheetDialog.dismiss();
                } else {
                    bottomSheetDialog.dismiss();

                    startActivity(new Intent(BookingActivity.this, MainActivity.class));
                    finish();
                }
            });
        } else {
            action_negatif.setVisibility(View.GONE);
        }

        close_sheet.setOnClickListener(view -> alertDialogCancel());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheet(String informasi, String message, int icon, String positifAction, String negatifAction, int code) {
        bottomSheetDialog.setContentView(bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        icon_sheet.setImageDrawable(getResources().getDrawable(icon));
        title_sheet.setText(informasi);
        message_sheet.setText(message);

        action_positif.setText(positifAction);
        action_negatif.setText(negatifAction);

        action_positif.setOnClickListener(view -> {
            if (code == 200) {
                bottomSheetDialog.dismiss();
                prosesSubmitAntrian();
            } else {
                bottomSheetDialog.dismiss();
            }
        });

        if (negatifAction != null) {
            action_negatif.setOnClickListener(view -> {
                if (code == 200) {
                    bottomSheetDialog.dismiss();
                } else {
                    bottomSheetDialog.dismiss();

                    startActivity(new Intent(BookingActivity.this, MainActivity.class));
                    finish();
                }
            });
        } else {
            action_negatif.setVisibility(View.GONE);
        }

        close_sheet.setOnClickListener(view -> alertDialogCancel());
    }

    private void alertDialogCancel() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Informasi")
                .setMessage("Apa anda yakin ingin membatalkan proses booking?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yakin", (dialogInterface, i) -> {
                    bottomSheetDialog.dismiss();

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("Kembali", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}