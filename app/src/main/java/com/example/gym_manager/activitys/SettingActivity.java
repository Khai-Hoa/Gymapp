package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivitySettingBinding;
import com.example.gym_manager.fragments.ReplacePasswordFragment;
import com.example.gym_manager.fragments.UpdateInfoFragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        auth = FirebaseAuth.getInstance();
        binding.btnLogout.setOnClickListener(v->{
            auth.signOut();
            startActivity(new Intent(SettingActivity.this, LoginActivity.class));
            finish();
        });

        binding.itemInfo.setOnClickListener(v->{
            showFragment(new UpdateInfoFragment());
        });
        binding.itemReplacePass.setOnClickListener(v->{

            showFragment(new ReplacePasswordFragment());
        });

        binding.btnBack.setOnClickListener(v->{

            onBackPressed();
        });



    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container_setting,fragment);
        fragmentTransaction.commit();
        binding.btnLogout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        binding.btnLogout.setVisibility(View.VISIBLE);

    }
}