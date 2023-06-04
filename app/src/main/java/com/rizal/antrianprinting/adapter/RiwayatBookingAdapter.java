package com.rizal.antrianprinting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rizal.antrianprinting.R;
import com.rizal.antrianprinting.models.riwayat.RiwayatBookingItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RiwayatBookingAdapter extends RecyclerView.Adapter<RiwayatBookingAdapter.ViewHolder> {

    ArrayList<RiwayatBookingItem> listRiwayatBooking;

    public RiwayatBookingAdapter(Context context, ArrayList<RiwayatBookingItem> riwayatItem) {
        this.listRiwayatBooking = riwayatItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_booking, parent, false);
        return new RiwayatBookingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(R.drawable.background_booking)
                .centerCrop()
                .placeholder(R.drawable.background_booking)
                .fit()
                .into(holder.ivGambar);

        holder.txtTitle.setText(listRiwayatBooking.get(position).getNama_designer());
        holder.txtLayanan.setText(listRiwayatBooking.get(position).getJenis_layanan());
        holder.txtTglBooking.setText(listRiwayatBooking.get(position).getJam_booking());
        holder.txtTglSelesai.setText(listRiwayatBooking.get(position).getJam_selesai());

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtLayanan, txtTglBooking, txtTglSelesai, txtStatus;
        ImageView ivGambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_title_booking);
            txtLayanan = itemView.findViewById(R.id.item_layanan_booking);
            txtTglBooking = itemView.findViewById(R.id.item_tanggal_booking);
            txtTglSelesai = itemView.findViewById(R.id.item_tanggal_selesai);
            txtStatus = itemView.findViewById(R.id.item_status_booking);
            ivGambar = itemView.findViewById(R.id.item_gambar_booking);
        }
    }
}
