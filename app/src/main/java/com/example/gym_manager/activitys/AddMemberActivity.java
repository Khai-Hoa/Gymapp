package com.example.gym_manager.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityAddMemberBinding;
import com.example.gym_manager.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    private ActivityAddMemberBinding binding;
    private int mYear, mMonth, mDay;
    private String role;

    private Uri imgUri;
    private FirebaseAuth auth;
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
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(AddMemberActivity.this, MainActivity.class));
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (imgUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.labelBtn.setVisibility(View.GONE);
            binding.animationLoading.setVisibility(View.VISIBLE);

            // Get input data
            String name = binding.edtName.getText().toString().trim();
            String address = binding.edtAddress.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();
            String birthday = binding.edtBirthday.getText().toString().trim();
            String sex = binding.edtSex.getText().toString().trim();
            String height = binding.edtHeight.getText().toString().trim();
            String weight = binding.edtWeight.getText().toString().trim();
            String startDay = binding.edtStartDay.getText().toString().trim();

            // Convert monthsRegistered from text to integer
            final String numMonthsStr = binding.edtMonths.getText().toString().trim();
            final int monthsRegistered;
            try {
                monthsRegistered = Integer.parseInt(numMonthsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of months", Toast.LENGTH_SHORT).show();
                binding.labelBtn.setVisibility(View.VISIBLE);
                binding.animationLoading.setVisibility(View.GONE);
                return;
            }

            final double amountPaid = monthsRegistered * 200000.0; // Calculate amount paid

            // Determine role
            final String role;
            if (binding.radioTrainer.isChecked()) {
                role = "Huấn luyện viên";
            } else if (binding.radioPerson.isChecked()) {
                role = "Học viên";
            } else {
                role = "";
            }

            // Calculate end date and active status
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(startDay));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid start date", Toast.LENGTH_SHORT).show();
                binding.labelBtn.setVisibility(View.VISIBLE);
                binding.animationLoading.setVisibility(View.GONE);
                return;
            }
            calendar.add(Calendar.MONTH, monthsRegistered);
            final String endDate = sdf.format(calendar.getTime());
            final boolean isActive = calendar.getTime().after(Calendar.getInstance().getTime());

            // Upload image and save member data
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("images").child(imgUri.getLastPathSegment());
            imageRef.putFile(imgUri)
                    .addOnSuccessListener(task -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Member member = new Member(
                                uri.toString(),
                                name,
                                address,
                                phone,
                                birthday,
                                sex,
                                height,
                                weight,
                                startDay,
                                monthsRegistered,
                                isActive,
                                role,
                                amountPaid, // Số tiền đã trả
                                endDate // Ngày kết thúc
                        );

                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            FirebaseDatabase.getInstance().getReference("members")
                                    .child(currentUser.getUid())
                                    .child(phone)
                                    .setValue(member)
                                    .addOnSuccessListener(aVoid -> {
                                        clearInput();
                                        binding.labelBtn.setVisibility(View.VISIBLE);
                                        binding.animationLoading.setVisibility(View.GONE);
                                        Toast.makeText(AddMemberActivity.this, "Member added successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        binding.labelBtn.setVisibility(View.VISIBLE);
                                        binding.animationLoading.setVisibility(View.GONE);
                                        Toast.makeText(AddMemberActivity.this, "Failed to add member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }).addOnFailureListener(error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show()));
        });

        binding.imageAvatar.setOnClickListener(v -> openGallery());

        binding.edtStartDay.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddMemberActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                binding.edtStartDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    private void clearInput() {
        binding.imageAvatar.setImageResource(R.drawable.imag_user_default);
        binding.edtName.setText("");
        binding.edtAddress.setText("");
        binding.edtPhone.setText("");
        binding.edtBirthday.setText("");
        binding.edtSex.setText("");
        binding.edtHeight.setText("");
        binding.edtWeight.setText("");
        binding.edtStartDay.setText("");
        binding.edtMonths.setText("");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }
}
