package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivitySeenImageBinding;

public class SeenImageActivity extends AppCompatActivity {
    private ActivitySeenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        binding.btnBack.setOnClickListener(v->{
           startActivity(new Intent(SeenImageActivity.this,MainActivity.class));
        });


        String image = getIntent().getStringExtra("image");
        Glide.with(this)
                .load(image)
                .into(binding.imageView);


    }
}