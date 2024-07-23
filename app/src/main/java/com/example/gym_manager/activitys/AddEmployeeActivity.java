package com.example.gym_manager.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityAddEmployeeBinding;
import com.example.gym_manager.model.Employee;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AddEmployeeActivity extends AppCompatActivity {
    private ActivityAddEmployeeBinding binding;
    private Uri imgUri;
    private boolean isUpdate = false; // Biến để kiểm tra cập nhật hay thêm mới
    private String id = null; // Biến lưu số điện thoại cũ để làm khóa khi cập nhật

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imgUri = result.getData().getData();
                    binding.imageAvatar.setImageURI(imgUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // Kiểm tra Intent có chứa thông tin nhân viên để cập nhật không
        Intent intent = getIntent();
        if (intent.hasExtra("id") && intent.hasExtra("url") && intent.hasExtra("name") && intent.hasExtra("address") && intent.hasExtra("phone") &&
                intent.hasExtra("role") && intent.hasExtra("salary")) {
            isUpdate = true;
            String _id = intent.getStringExtra("id");
            String avatar = intent.getStringExtra("url");
            String _getName = intent.getStringExtra("name");
            String _getAddress = intent.getStringExtra("address");
            String _getPhone = intent.getStringExtra("phone");
            String _getRole = intent.getStringExtra("role");
            String _getSalary = intent.getStringExtra("salary");

            id = _id; // Lưu số điện thoại cũ

            // Hiển thị thông tin nhân viên cần cập nhật
            binding.edtIdEmployee.setText(_id);
            binding.edtNameEmployee.setText(_getName);
            binding.edtAddressEmployee.setText(_getAddress);
            binding.edtPhoneEmployee.setText(_getPhone);
            binding.edtRoleEmployee.setText(_getRole);
            binding.edtSalaryEmployee.setText(_getSalary);
            Glide.with(this).load(avatar).into(binding.imageAvatar);
        }

        binding.btnSave.setOnClickListener(v -> {
            binding.tvBtn.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
            String employee_id = binding.edtIdEmployee.getText().toString().trim();
            String name_employee = binding.edtNameEmployee.getText().toString().trim();
            String address = binding.edtAddressEmployee.getText().toString().trim();
            String phone = binding.edtPhoneEmployee.getText().toString().trim();
            String role = binding.edtRoleEmployee.getText().toString().trim();
            String salary = binding.edtSalaryEmployee.getText().toString().trim();

            if (employee_id.isEmpty() || name_employee.isEmpty() || address.isEmpty() || phone.isEmpty() || role.isEmpty() || salary.isEmpty() || (imgUri == null && !isUpdate)) {
                binding.progress.setVisibility(View.GONE);
                binding.tvBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUpdate) {
                updateEmployee(id,name_employee, address, phone, role, salary);
            } else {
                addNewEmployee(employee_id,name_employee, address, phone, role, salary);
            }
        });

        binding.imageAvatar.setOnClickListener(v -> openGallery());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void addNewEmployee(String employee_id,String name, String address, String phone, String role, String salary) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images_employee").child(imgUri.getLastPathSegment());
        imageRef.putFile(imgUri)
                .addOnSuccessListener(task -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Employee employee = new Employee(
                                employee_id,
                                name,
                                address,
                                uri.toString(),
                                phone,
                                role,
                                salary
                        );
                        FirebaseDatabase.getInstance().getReference("employees")
                                .child(employee_id)
                                .setValue(employee)
                                .addOnSuccessListener(isTask -> {
                                    binding.progress.setVisibility(View.GONE);
                                    binding.tvBtn.setVisibility(View.VISIBLE);
                                    clearInputFields();
                                    Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(error -> {
                                    binding.progress.setVisibility(View.GONE);
                                    binding.tvBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(this, "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(error -> {
                    binding.progress.setVisibility(View.GONE);
                    binding.tvBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmployee(String employee_id,String name, String address, String phone, String role, String salary) {
        if (imgUri != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("images_employee").child(imgUri.getLastPathSegment());
            imageRef.putFile(imgUri)
                    .addOnSuccessListener(task -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateEmployeeInDatabase(employee_id,name, address, phone, role, salary, uri.toString());
                        });
                    })
                    .addOnFailureListener(error -> {
                        binding.progress.setVisibility(View.GONE);
                        binding.tvBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Nếu không cập nhật ảnh mới, giữ nguyên URL ảnh cũ
            String avatarUrl = getIntent().getStringExtra("url");
            updateEmployeeInDatabase(id,name, address, phone, role, salary, avatarUrl);
        }
    }

    private void updateEmployeeInDatabase(String empoyee_id,String name, String address, String phone, String role, String salary, String avatarUrl) {
        Employee employee = new Employee(
                empoyee_id,
                name,
                address,
                avatarUrl,
                phone,
                role,
                salary
        );
        FirebaseDatabase.getInstance().getReference("employees")
                .child(id) // Dùng số điện thoại cũ để cập nhật
                .setValue(employee)
                .addOnSuccessListener(isTask -> {
                    binding.progress.setVisibility(View.GONE);
                    binding.tvBtn.setVisibility(View.VISIBLE);
                    clearInputFields();
                    Toast.makeText(this, "Cập nhật nhân viên thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> {
                    binding.progress.setVisibility(View.GONE);
                    binding.tvBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Cập nhật nhân viên thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputFields() {
        binding.edtIdEmployee.setText("");
        binding.edtNameEmployee.setText("");
        binding.edtAddressEmployee.setText("");
        binding.edtPhoneEmployee.setText("");
        binding.edtRoleEmployee.setText("");
        binding.edtSalaryEmployee.setText("");
        binding.imageAvatar.setImageResource(R.drawable.imag_user_default);
        imgUri = null;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }
}
