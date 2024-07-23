package com.example.gym_manager.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityMainBinding;
import com.example.gym_manager.listener.FabController;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, FabController {
    private ActivityMainBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));

        navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        binding.bottomNavigationView.setOnItemSelectedListener(this);


        binding.fab.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, AddMemberActivity.class));
        });




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.dashBoardFragment){
            navController.navigate(R.id.dashBoardFragment);
            return true;
        }
        else if(item.getItemId() == R.id.memberFragment){
            navController.navigate(R.id.memberFragment);
            return true;
        }
        else if(item.getItemId() == R.id.reportFragment){
            navController.navigate(R.id.reportFragment);
            return true;
        }
        else if(item.getItemId() == R.id.accountFragment){
            navController.navigate(R.id.accountFragment);
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

    @Override
    public void collapseFab() {
        binding.fab.shrink();
    }

    @Override
    public void expandFab() {
        binding.fab.extend();

    }
}