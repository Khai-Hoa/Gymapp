package com.example.gym_manager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.FragmentAddDeviceBinding;
import com.example.gym_manager.model.Device;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddDeviceFragment extends Fragment {

    private FragmentAddDeviceBinding binding;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.imageView.setImageURI(imageUri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddDeviceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddImage.setOnClickListener(v -> openGallery());

        binding.btnAddDevice.setOnClickListener(v -> {
            binding.btnAddDevice.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
            String device_id = binding.deviceId.getText().toString().trim();
            String device_name = binding.deviceName.getText().toString().trim();
            String device_price = binding.devicePrice.getText().toString().trim();
            String quantity = binding.deviceQuantity.getText().toString().trim();
            String dateBuy = binding.deviceDateBuy.getText().toString().trim();
            if (device_id.isEmpty() || device_name.isEmpty() || device_price.isEmpty() || quantity.isEmpty() || dateBuy.isEmpty() || (imageUri == null)) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin và ảnh", Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.GONE);
                binding.btnAddDevice.setVisibility(View.VISIBLE);
                return;
            }

            StorageReference imgeRef = FirebaseStorage.getInstance().getReference("image_device").child(imageUri.getLastPathSegment());
            imgeRef.putFile(imageUri)
                    .addOnSuccessListener(task -> {
                        imgeRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            Device device = new Device(
                                    device_id,
                                    uri.toString(),
                                    device_name,
                                    device_price,
                                    quantity,
                                    dateBuy,
                                    "",
                                    false,
                                    0.0,
                                    "",
                                    ""
                            );
                            FirebaseDatabase.getInstance().getReference("devices")
                                    .child(device_id)
                                    .setValue(device)
                                    .addOnSuccessListener(taskSuccess -> {
                                        clearInput();
                                        binding.progress.setVisibility(View.GONE);
                                        binding.btnAddDevice.setVisibility(View.VISIBLE);
                                        Toast.makeText(requireContext(), "Thêm thiết bị thành công", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(error -> {
                                        binding.progress.setVisibility(View.GONE);
                                        binding.btnAddDevice.setVisibility(View.VISIBLE);
                                        Log.e("Log", error.getMessage());
                                    });

                        });
                    })
                    .addOnFailureListener(error -> {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnAddDevice.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Load ảnh thất bại", Toast.LENGTH_SHORT).show();
                    });
        });

        // Thêm sự kiện nhấn cho ImageView với id là "back"
        binding.back.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút back
            requireActivity().onBackPressed();
        });
    }

    private void clearInput() {
        binding.deviceId.setText("");
        binding.deviceName.setText("");
        binding.devicePrice.setText("");
        binding.deviceQuantity.setText("");
        binding.deviceDateBuy.setText("");
        binding.imageView.setImageResource(R.drawable.noimage);
        imageUri = null;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        resultLauncher.launch(intent);
    }
}
