package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivitySignUpBinding;
import com.example.gym_manager.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        initalized();

        binding.btnSignup.setOnClickListener(v->{
            binding.labelBtn.setVisibility(View.GONE);
            binding.animationLoading.setVisibility(View.VISIBLE);

            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String confirmPass = binding.edtConfirmPassword.getText().toString().trim();
            if(email.isEmpty()){
                binding.animationLoading.setVisibility(View.GONE);
                binding.labelBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Vui lòng điền email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()){
                binding.animationLoading.setVisibility(View.GONE);
                binding.labelBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Vui lòng điền mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            if(confirmPass.isEmpty()){
                binding.animationLoading.setVisibility(View.GONE);
                binding.labelBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Vui lòng xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.equals(confirmPass)){
                User user = new User(email,password);
                signupAccount(user);
            }
            else{
                binding.animationLoading.setVisibility(View.GONE);
                binding.labelBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Mật khẩu không đúng vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
            }

        });
        binding.tvLogin.setOnClickListener(v->{
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        });
    }

    private void signupAccount(User user) {
        auth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        binding.animationLoading.setVisibility(View.GONE);
                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

                    }
                    else{
                        binding.animationLoading.setVisibility(View.GONE);
                        binding.labelBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Đăng ký thất bại! Vui lòng quay lại sau!", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void initalized() {
        auth = FirebaseAuth.getInstance();
    }
}