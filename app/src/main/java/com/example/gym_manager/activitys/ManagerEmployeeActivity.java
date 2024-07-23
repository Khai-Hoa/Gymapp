package com.example.gym_manager.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.adapter.EmployeeAdapter;
import com.example.gym_manager.databinding.ActivityManagerEmployeeBinding;
import com.example.gym_manager.model.Employee;
import com.example.gym_manager.model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerEmployeeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityManagerEmployeeBinding binding;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> mListEmployee = new ArrayList<>();
    private List<Employee> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));


        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.iconSearch.setOnClickListener(v->{
            binding.tvLabelsTitle.setVisibility(View.GONE);
            binding.searchEdt.setVisibility(View.VISIBLE);
            binding.iconSearch.setVisibility(View.GONE);
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
        binding.fab.setOnClickListener(v->{
            startActivity(new Intent(ManagerEmployeeActivity.this, AddEmployeeActivity.class));
        });

        iNit();
        loadEmployeeFromFirebase();

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                   binding.fab.shrink();
                } else if (dy < 0) {
                   binding.fab.extend();
                }
            }
        });
    }
    private void filterMembers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(mListEmployee);
        } else {
            for (Employee employee : mListEmployee) {
                if (employee.getName().toLowerCase().contains(query.toLowerCase().trim()) ||
                        employee.getPhone().contains(query)) {
                    filteredList.add(employee);
                }
            }
        }

        employeeAdapter.setMlistEmployee(filteredList);
        employeeAdapter.notifyDataSetChanged();

    }

    private void loadEmployeeFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("employees")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListEmployee.clear();
                        filteredList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot employeeSnapshot : snapshot.getChildren()){
                                Employee employee = employeeSnapshot.getValue(Employee.class);
                                if(employee != null){
                                    mListEmployee.add(employee);
                                    filteredList.add(employee);
                                }
                            }
                            employeeAdapter = new EmployeeAdapter(mListEmployee);
                            binding.recyclerview.setAdapter(employeeAdapter);
                            binding.swiplayout.setRefreshing(false);
                        }
                        else{
                            binding.swiplayout.setRefreshing(false);
                            Toast.makeText(ManagerEmployeeActivity.this, "Chưa có nhân viên nào trên server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swiplayout.setRefreshing(false);


                    }
                });

    }

    private void iNit() {
        filteredList = new ArrayList<>();
        binding.swiplayout.setOnRefreshListener(this);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onRefresh() {
        loadEmployeeFromFirebase();

    }
}