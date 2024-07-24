package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.gym_manager.R;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import androidx.core.content.ContextCompat;

import com.example.gym_manager.databinding.ActivityForgotPassBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    ActivityForgotPassBinding binding;
    Integer defaultColor;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int color = ContextCompat.getColor(this, R.color.white);
        getWindow().setStatusBarColor(color);
        defaultColor = ContextCompat.getColor(getApplicationContext(), R.color.green);
        setButtonColor(defaultColor);

        auth = FirebaseAuth.getInstance();

        binding.edtEmail.addTextChangedListener(loginTextWatcher);
        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
            finish();
        });

        binding.btnGetPass.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = binding.edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.edtEmail.setError("Vui lòng nhập email");
            } else {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                binding.progressBar.setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar.make(binding.layoutMain, "Vui lòng kiểm tra email để lấy mật khẩu.", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green));
                                snackbar.show();
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar.make(binding.layoutMain, "Lỗi! Vui lòng thử lại sau.", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.red));
                                snackbar.show();
                            }
                        });
            }

        });


    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //kiem tra nguoi dung co nhap khong
            String email = binding.edtEmail.getText().toString().trim();
            if (email.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.btnGetPass.setEnabled(true);
                //thay mau cho button login
                int loginColor = ContextCompat.getColor(getApplicationContext(), R.color.bg_splash);
                setButtonColor(loginColor);

            } else {
                binding.btnGetPass.setEnabled(false);
                setButtonColor(defaultColor);
            }

        }
    };

    private void setButtonColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(25);
        binding.btnGetPass.setElevation(8);
        drawable.setColor(color);
        binding.btnGetPass.setBackground(drawable);
        binding.btnGetPass.setPadding(10, 10, 10, 10);
    }
}