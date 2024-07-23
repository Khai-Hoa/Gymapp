package com.example.gym_manager.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.database.Cart;
import com.example.gym_manager.database.CartDatabase;
import com.example.gym_manager.databinding.ActivityBuyOrderBinding;

import java.text.DecimalFormat;
import java.util.Date;

public class BuyOrderActivity extends AppCompatActivity {
    private ActivityBuyOrderBinding binding;
    private String name_product, url;
    private Double price_product;
    private CartDatabase cartDatabase;
    private int currentQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));

        cartDatabase = CartDatabase.getDatabase(this);
        DecimalFormat format = new DecimalFormat("#,###");

        name_product = getIntent().getStringExtra("name");
        binding.tvNameProduct.setText(new StringBuilder().append(name_product));
        url = getIntent().getStringExtra("image");
        if(url != null){
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.noimage).into(binding.imageView);

        }
        else{
            Log.d("Log","NO_IMAGE");
        }
        price_product = getIntent().getDoubleExtra("price",0.0);

        binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(price_product)).append("đ"));
        binding.tvQuantity.setText(new StringBuilder().append(1));

        binding.btnBack.setOnClickListener(v->{
            onBackPressed();
        });

        binding.ivMinus.setOnClickListener(v->{
            if(currentQuantity > 1){
                currentQuantity--;
                binding.tvQuantity.setText(new StringBuilder().append(currentQuantity));
                Double price_sum = currentQuantity * price_product;
                binding.tvSum.setText(new StringBuilder().append(format.format(price_sum)).append("đ"));
            }
            else{
                Toast.makeText(this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        binding.ivPlus.setOnClickListener(v->{
            currentQuantity++;
            binding.tvQuantity.setText(new StringBuilder().append(currentQuantity));
            Double price_sum = currentQuantity * price_product;
            binding.tvSum.setText(new StringBuilder().append(format.format(price_sum)).append("đ"));
        });

        binding.btnBuy.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
            String name_customer = binding.edtNameCustomer.getText().toString().trim();
            String phone = binding.edtPhoneCustomer.getText().toString().trim();
            String address = binding.edtAddressCustomer.getText().toString().trim();
            if(name_customer.isEmpty() && phone.isEmpty() && address.isEmpty()){
                Toast.makeText(this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);
                return;
            }
            Cart cart = new Cart(
                    name_product,
                    price_product,
                    url,
                    currentQuantity,
                    name_customer,
                    phone,
                    address,
                    currentQuantity * price_product,
                    new Date()

            );
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cartDatabase.cartDao().insertCart(cart);
                    runOnUiThread(() -> {
                        clearInput();
                        binding.tvBtn.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);
                        Toast.makeText(BuyOrderActivity.this, "Mua hàng thành công", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });









    }
    private void clearInput(){
        binding.edtNameCustomer.setText("");
        binding.edtPhoneCustomer.setText("");
        binding.edtAddressCustomer.setText("");
    }

}