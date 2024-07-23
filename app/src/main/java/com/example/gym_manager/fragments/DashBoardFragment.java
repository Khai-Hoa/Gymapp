package com.example.gym_manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gym_manager.R;
import com.example.gym_manager.activitys.MainActivity;
import com.example.gym_manager.activitys.ManagerDeviceActivity;
import com.example.gym_manager.activitys.ManagerEmployeeActivity;
import com.example.gym_manager.activitys.ShopActivity;
import com.example.gym_manager.adapter.ProductAdapter;
import com.example.gym_manager.databinding.FragmentDashBoardBinding;
import com.example.gym_manager.model.Member;
import com.example.gym_manager.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DashBoardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentDashBoardBinding binding;
    private FirebaseAuth auth;
    private NavController navController;
    private ProductAdapter productAdapter;
    private List<Product> mListProduct = new ArrayList<>();

    private List<Member> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashBoardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        iNit(view);


        fetchMemberFromFirebase();
        binding.iconGroup.setOnClickListener(v -> {
            navController.navigate(R.id.memberFragment);
        });
        binding.menuShop.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), ShopActivity.class));
        });
        binding.menuItemManagerEmployee.setOnClickListener(v->{
                startActivity(new Intent(requireActivity(), ManagerEmployeeActivity.class));
        });

        binding.itemManagerMember.setOnClickListener(v->{
            navController.navigate(R.id.memberFragment);

        });
        binding.itemManagerDevice.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), ManagerDeviceActivity.class));

        });
        loadProductFromFirebase();

    }

    private void loadProductFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("product_shop")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListProduct.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                Product product = productSnapshot.getValue(Product.class);
                                if (product != null) {
                                    mListProduct.add(product);
                                }
                            }
                            productAdapter = new ProductAdapter(mListProduct);
                            binding.recyclerview.setAdapter(productAdapter);
                            binding.swiplayout.setRefreshing(false);
                        } else {
                            Toast.makeText(requireContext(), "Chưa có sản phẩm nào trên server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swiplayout.setRefreshing(false);

                    }
                });
    }

    private void fetchMemberFromFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase.getInstance().getReference("members")
                    .child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mList.clear();
                            if (snapshot.exists()) {
                                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                                    Member member = memberSnapshot.getValue(Member.class);
                                    if (member != null) {
                                        mList.add(member);

                                    }
                                }


                            }
                            binding.badge.setNumber(mList.size());
                            binding.swiplayout.setRefreshing(false);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.swiplayout.setRefreshing(false);

                        }
                    });
        }


    }

    private void iNit(View view) {
        navController = Navigation.findNavController(view);
        binding.swiplayout.setOnRefreshListener(this);
        auth = FirebaseAuth.getInstance();

        binding.recyclerview.setHasFixedSize(true);
        binding.swiplayout.setOnRefreshListener(this);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onRefresh() {
        fetchMemberFromFirebase();
        loadProductFromFirebase();


    }
}