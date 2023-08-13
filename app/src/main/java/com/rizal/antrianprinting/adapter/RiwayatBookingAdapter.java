package com.rizal.antrianprinting.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.activity.DetailBookingActivity;
import com.rizal.antrianprinting.models.riwayat.RiwayatBookingItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RiwayatBookingAdapter extends RecyclerView.Adapter<RiwayatBookingAdapter.ViewHolder> {

    private Context context;
    ArrayList<RiwayatBookingItem> listRiwayatBooking;

    public RiwayatBookingAdapter(Context context, ArrayList<RiwayatBookingItem> riwayatItem) {
        this.context = context;
        this.listRiwayatBooking = riwayatItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RiwayatBookingItem itemBooking = getItemBooking().get(position);

        Picasso.get().load(R.drawable.background_booking)
                .centerCrop()
                .placeholder(R.drawable.background_booking)
                .fit()
                .into(holder.ivGambar);

        holder.txtTitle.setText(itemBooking.getNama_designer());
        holder.txtLayanan.setText(itemBooking.getJenis_layanan());
        holder.txtTglBooking.setText(itemBooking.getJam_booking());
        holder.txtTglSelesai.setText(itemBooking.getJam_selesai());

        holder.cvBooking.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DetailBookingActivity.class);

            intent.putExtra("id", getItemBooking().get(position).getId());
            intent.putExtra("nama_designer", getItemBooking().get(position).getNama_designer());
            intent.putExtra("jenis_layanan", getItemBooking().get(position).getJenis_layanan());
            intent.putExtra("jam_booking", getItemBooking().get(position).getJam_booking());
            intent.putExtra("jam_selesai", getItemBooking().get(position).getJam_selesai());
            intent.putExtra("tgl_pesanan", getItemBooking().get(position).getTgl_pesanan());
            intent.putExtra("handphone", getItemBooking().get(position).getNo_handphone());
            intent.putExtra("status", getItemBooking().get(position).getStatus());

            view.getContext().startActivity(intent);
        });

        holder.cvBooking.setOnLongClickListener(view -> {
            Toast.makeText(view.getContext(), "Status Anda " + itemBooking.getStatus(), Toast.LENGTH_SHORT).show();
            return true;
        });

        if (listRiwayatBooking.get(position).getStatus().equalsIgnoreCase("Batal")) {
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorCancel));
        } else if (listRiwayatBooking.get(position).getStatus().equalsIgnoreCase("Menunggu")) {
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
        } else {
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryDark));
        }

        holder.txtStatus.setText(listRiwayatBooking.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return listRiwayatBooking.size();
    }

    private ArrayList<RiwayatBookingItem> getItemBooking() {
        return listRiwayatBooking;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtLayanan, txtTglBooking, txtTglSelesai, txtStatus;
        ImageView ivGambar;
        CardView cvBooking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_title_booking);
            txtLayanan = itemView.findViewById(R.id.item_layanan_booking);
            txtTglBooking = itemView.findViewById(R.id.item_tanggal_booking);
            txtTglSelesai = itemView.findViewById(R.id.item_tanggal_selesai);
            txtStatus = itemView.findViewById(R.id.item_status_booking);
            ivGambar = itemView.findViewById(R.id.item_gambar_booking);
            cvBooking = itemView.findViewById(R.id.item_cv_booking);
        }
    }
}
