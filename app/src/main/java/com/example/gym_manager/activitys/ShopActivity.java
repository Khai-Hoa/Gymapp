package com.example.gym_manager.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.adapter.ProductAdapter;
import com.example.gym_manager.databinding.ActivityShopBinding;
import com.example.gym_manager.listener.FabController;
import com.example.gym_manager.model.Member;
import com.example.gym_manager.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityShopBinding binding;
    private List<Product> mList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private List<Product> filteredList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));

        binding.fab.setOnClickListener(v->{
            startActivity(new Intent(ShopActivity.this, AddProductActivity.class));
        });
        iNit();
        loadProductFromFirebase();

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
        binding.btnBack.setOnClickListener(v->{
            onBackPressed();
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



    }

    private void filterMembers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(mList);
        } else {
            for (Product product : mList) {
                if (product.getProduct_name().toLowerCase().contains(query.toLowerCase().trim())) {
                    filteredList.add(product);
                }
                else{
                    binding.tvNoSearch.setVisibility(View.VISIBLE);
                }
            }
        }

        productAdapter.setProducts(filteredList);
        productAdapter.notifyDataSetChanged();

    }
    private void loadProductFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("product_shop")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mList.clear();
                        filteredList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot productSnapshot : snapshot.getChildren()){
                                Product product = productSnapshot.getValue(Product.class);
                                if(product != null){
                                    mList.add(product);
                                    filteredList.add(product);
                                }
                            }
                            productAdapter = new ProductAdapter(mList);
                            binding.recyclerview.setAdapter(productAdapter);
                            binding.swiplayout.setRefreshing(false);
                        }
                        else{
                            Toast.makeText(ShopActivity.this, "Chưa có sản phẩm nào trên server", Toast.LENGTH_SHORT).show();
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
        binding.recyclerview.setHasFixedSize(true);
        binding.swiplayout.setOnRefreshListener(this);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onRefresh() {
        loadProductFromFirebase();

    }
}