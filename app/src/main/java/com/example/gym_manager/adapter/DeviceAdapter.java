package com.example.gym_manager.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.databinding.RowItemDeviceBinding;
import com.example.gym_manager.model.Device;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    List<Device> mListDevice;
    Context context;

    public DeviceAdapter(List<Device> mListDevice) {
        this.mListDevice = mListDevice;
    }

    public void setmListDevice(List<Device> mListDevice) {
        this.mListDevice = mListDevice;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemDeviceBinding binding = RowItemDeviceBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        Device device = mListDevice.get(position);
        Glide.with(context)
                .load(device.getUrl())
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageView);
        holder.binding.tvName.setText(device.getName());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(Double.parseDouble(device.getPrice()))).append("đ"));
        holder.binding.tvQuantity.setText(new StringBuilder("Số lượng: ").append(device.getQuantity()));
        holder.binding.tvDateBuy.setText(new StringBuilder("Ngày mua: ").append(device.getDateBuy()));
        if(device.getFix().equals(false)){
            holder.binding.tvDateFix.setVisibility(View.GONE);
            holder.binding.tvStatus.setVisibility(View.GONE);
        }
        else{
            holder.binding.tvDateFix.setVisibility(View.VISIBLE);
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
            holder.binding.tvStatus.setText(new StringBuilder("Trạng thái: ").append("Đã bảo trì"));
            holder.binding.tvDateFix.setText(new StringBuilder("Ngày bảo trì gần nhất: ").append(device.getDateFix()));
        }

        holder.binding.ivDelete.setOnClickListener(v->{
            openShowDialogDelete(device,holder);
        });

    }

    private void openShowDialogDelete(Device device, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có chắc muốn xóa?");
        builder.setPositiveButton("có", (dialog, i) -> {
            FirebaseDatabase.getInstance().getReference("devices")
                    .child(device.getId())
                    .removeValue()
                    .addOnSuccessListener(task->{
                        mListDevice.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(error->{
                        Log.e("Log",error.getMessage());
                        dialog.dismiss();

                    });

        });
        builder.setNegativeButton("Hủy", (dialog, i) -> {
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return mListDevice.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemDeviceBinding binding;

        public ViewHolder(@NonNull RowItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
