package com.example.gym_manager.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.FragmentRepairDeviceBinding;
import com.example.gym_manager.model.Device;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RepairDeviceFragment extends Fragment {

    private FragmentRepairDeviceBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentRepairDeviceBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnCompelteFix.setOnClickListener(v->{
            String device_id = binding.deviceId.getText().toString().trim();
            String amount_money = binding.edtMoney.getText().toString().trim();
            String quantity = binding.tvQuantity.getText().toString().trim();


            int selectedStatusId = binding.layoutRadio.getCheckedRadioButtonId();
            RadioButton selectedStatus = view.findViewById(selectedStatusId);
            String status = (selectedStatus != null) ? selectedStatus.getText().toString() : "";

            if (TextUtils.isEmpty(device_id) || TextUtils.isEmpty(amount_money) || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(status)) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            updateDeviceMaintenance(device_id, amount_money, quantity, status);


        });
    }
    private void updateDeviceMaintenance(String deviceId, String maintenanceCost, String maintenanceQuantity, String maintenanceStatus) {
        FirebaseDatabase.getInstance().getReference("devices").child(deviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        device.setMaintenanceCost(Double.parseDouble(maintenanceCost));
                        device.setMaintenanceQuantity(maintenanceQuantity);
                        device.setMaintenanceStatus(maintenanceStatus);
                        device.setDateFix(getCurrentDate()); // Assume you have a method to get the current date
                        device.setFix(true);

                        FirebaseDatabase.getInstance().getReference("devices").child(deviceId).setValue(device)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Cập nhật bảo trì thành công", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Cập nhật bảo trì thất bại", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "Thiết bị không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

}