package com.example.gym_manager.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gym_manager.R;
import com.example.gym_manager.activitys.HistorySellActivity;
import com.example.gym_manager.activitys.SettingActivity;
import com.example.gym_manager.databinding.FragmentAccountBinding;


public class AccountFragment extends Fragment {

   private FragmentAccountBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding = FragmentAccountBinding.inflate(inflater,container,false);
      return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.fab.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), SettingActivity.class));
        });
        binding.btnBack.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });

        binding.historySell.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), HistorySellActivity.class));

        });
    }
}