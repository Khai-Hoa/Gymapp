package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityLoginBinding;
import com.example.gym_manager.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        initalized();

        binding.btnLogin.setOnClickListener(v->{
            binding.labelBtn.setVisibility(View.GONE);
            binding.animationLoading.setVisibility(View.VISIBLE);
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

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
            User user = new User(email,password);
            signinAccount(user);
        });

        binding.tvSignup.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        });




    }

    private void signinAccount(User user) {
        auth.signInWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        binding.animationLoading.setVisibility(View.GONE);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();

                    }
                    else{
                        binding.animationLoading.setVisibility(View.GONE);
                        binding.labelBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void initalized() {
        auth = FirebaseAuth.getInstance();


    }
}