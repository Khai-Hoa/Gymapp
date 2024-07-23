package com.example.gym_manager.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gym_manager.R;
import com.example.gym_manager.activitys.MainActivity;
import com.example.gym_manager.activitys.ManagerDeviceActivity;
import com.example.gym_manager.adapter.DeviceAdapter;
import com.example.gym_manager.databinding.FragmentAllDeviceBinding;
import com.example.gym_manager.model.Device;
import com.example.gym_manager.model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AllDeviceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentAllDeviceBinding binding;
    private List<Device> mListDevice;
    private DeviceAdapter deviceAdapter;
    private List<Device> filteredList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding = FragmentAllDeviceBinding.inflate(inflater,container,false);
      return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit();
        loadDeviceFromServer();
        binding.iconSearch.setOnClickListener(v->{
            binding.tvLabelsTitle.setVisibility(View.GONE);
            binding.searchEdt.setVisibility(View.VISIBLE);
            binding.iconSearch.setVisibility(View.GONE);
        });
        binding.btnBack.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), MainActivity.class));
        });
        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                //no code
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                filterMembers(s.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    private void filterMembers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(mListDevice);
        } else {
            for (Device device : mListDevice) {
                if (device.getName().toLowerCase().contains(query.toLowerCase().trim())) {
                    filteredList.add(device);
                }
            }
        }

        deviceAdapter.setmListDevice(filteredList);
        deviceAdapter.notifyDataSetChanged();

    }

    private void loadDeviceFromServer() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("devices")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListDevice.clear();
                        filteredList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot deviceSnapshot : snapshot.getChildren()){
                                Device device = deviceSnapshot.getValue(Device.class);
                                if(device != null){
                                    mListDevice.add(device);
                                    filteredList.add(device);
                                }
                            }
                            deviceAdapter = new DeviceAdapter(mListDevice);
                            binding.recyclerview.setAdapter(deviceAdapter);
                            binding.swiplayout.setRefreshing(false);
                        }
                        else{
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swiplayout.setRefreshing(false);

                    }
                });

    }

    private void iNit() {
        mListDevice = new ArrayList<>();
        filteredList = new ArrayList<>();
        binding.swiplayout.setOnRefreshListener(this);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onRefresh() {
        loadDeviceFromServer();

    }
}