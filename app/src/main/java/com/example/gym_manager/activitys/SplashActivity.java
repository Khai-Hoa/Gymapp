package com.example.gym_manager.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.util.Log;

import com.example.gym_manager.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;
    private FirebaseAuth auth;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "onCreate called");

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.tv1.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);

            // Điều hướng người dùng sau khi hiển thị splash screen
            if (currentUser != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // Đảm bảo rằng SplashActivity không còn trong back stack
        }, 3000); // Delay 3 giây
    }
}
