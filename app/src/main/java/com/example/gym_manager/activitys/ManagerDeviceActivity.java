package com.example.gym_manager.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityManagerDeviceBinding;
import com.google.android.material.navigation.NavigationBarView;

public class ManagerDeviceActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityManagerDeviceBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));

        navController = Navigation.findNavController(this, R.id.fragment_container_deviec);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        binding.bottomNavigationView.setOnItemSelectedListener(this);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.allDeviceFragment){
            navController.navigate(R.id.allDeviceFragment);
            return true;
        }
        else if(item.getItemId() == R.id.adddeviceFragment){
            navController.navigate(R.id.adddeviceFragment);
            return true;
        }
        else if(item.getItemId() == R.id.fixdeviceFragment){
            navController.navigate(R.id.fixdeviceFragment);
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if(!navController.popBackStack()){
            super.onBackPressed();
        }

    }

}