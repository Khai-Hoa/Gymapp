package com.example.gym_manager.activitys;

import androidx.activity.result.ActivityResult;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityAddMemberBinding;
import com.example.gym_manager.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMemberActivity extends AppCompatActivity {

    private ActivityAddMemberBinding binding;
    private int mYear, mMonth, mDay, mHours, mMinute;
 private    String role;

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
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v->{
            startActivity(new Intent(AddMemberActivity.this,MainActivity.class));
            finish();
        });



        binding.btnSave.setOnClickListener(v->{
            binding.labelBtn.setVisibility(View.GONE);
            binding.animationLoading.setVisibility(View.VISIBLE);
            String name = binding.edtName.getText().toString().trim();
            String address = binding.edtAddress.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();
            String birthday = binding.edtBirthday.getText().toString().trim();
            String sex = binding.edtSex.getText().toString().trim();
            String height = binding.edtHight.getText().toString().trim();
            String weight = binding.edtWeight.getText().toString().trim();
            String firstDay = binding.edtFirstDay.getText().toString().trim();
            String lastDay = binding.edtLastDay.getText().toString().trim();
            if(binding.radioTrainer.isChecked()){
                role = "Huấn luyện viên";
            }
            else if(binding.radioPerson.isChecked()){
              role = "Học viên";
            }

            long days = calculateDays(firstDay, lastDay);
            double payment = days * 50;

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
                                firstDay,
                                lastDay,
                                true,
                                role,
                                payment
                        );

                        FirebaseUser currentUser = auth.getCurrentUser();
                        if(currentUser != null){
                            FirebaseDatabase.getInstance().getReference("members")
                                    .child(currentUser.getUid())
                                    .child(phone)
                                    .setValue(member)
                                    .addOnSuccessListener(

                                            aVoid ->{
                                                clearInput();
                                                binding.labelBtn.setVisibility(View.VISIBLE);
                                                binding.animationLoading.setVisibility(View.GONE);
                                                Toast.makeText(AddMemberActivity.this, "Member added successfully", Toast.LENGTH_SHORT).show();

                                            })

                                    .addOnFailureListener(e ->{
                                        binding.labelBtn.setVisibility(View.VISIBLE);
                                        binding.animationLoading.setVisibility(View.GONE);
                                        Toast.makeText(AddMemberActivity.this, "Failed to add member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                        }

                    }).addOnFailureListener(error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show()));
        });

        binding.imageAvatar.setOnClickListener(v -> openGallery());

        binding.edtFirstDay.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddMemberActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                binding.edtFirstDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.edtLastDay.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddMemberActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                binding.edtLastDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    private void clearInput(){
        binding.imageAvatar.setImageResource(R.drawable.imag_user_default);
        binding.edtName.setText("");
       binding.edtAddress.setText("");
       binding.edtPhone.setText("");
        binding.edtBirthday.setText("");
       binding.edtSex.setText("");
      binding.edtHight.setText("");
      binding.edtWeight.setText("");
       binding.edtFirstDay.setText("");
        binding.edtLastDay.setText("");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }

    private long calculateDays(String firstDay, String lastDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date firstDate = sdf.parse(firstDay);
            Date lastDate = sdf.parse(lastDay);
            if (firstDate != null && lastDate != null) {
                long diff = lastDate.getTime() - firstDate.getTime();
                return diff / (24 * 60 * 60 * 1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}