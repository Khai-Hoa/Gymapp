package com.example.gym_manager.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.adapter.CartAdapter;
import com.example.gym_manager.database.Cart;
import com.example.gym_manager.database.CartDatabase;
import com.example.gym_manager.databinding.ActivityHistorySellBinding;
import com.example.gym_manager.listener.OnCartItemDeletedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistorySellActivity extends AppCompatActivity implements OnCartItemDeletedListener {
    private ActivityHistorySellBinding binding;
    private CartAdapter adapter;
    private List<Cart> cartList;
    private CartDatabase cartDatabase;
    private String startDate, endDate ;
    private int mYear, mMonth, mDay, mHours, mMinute;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat format = new DecimalFormat("#,###");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistorySellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));


        iNit();
        new Thread(() -> {
            cartList = cartDatabase.cartDao().getAllCart();
            runOnUiThread(() -> {
                if (cartList.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.recyclerview.setVisibility(View.GONE);
                    binding.startDay.setVisibility(View.GONE);
                    binding.endDay.setVisibility(View.GONE);
                    binding.btnFind.setVisibility(View.GONE);

                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    binding.startDay.setVisibility(View.VISIBLE);
                    binding.endDay.setVisibility(View.VISIBLE);
                    binding.btnFind.setVisibility(View.VISIBLE);
                }
                adapter = new CartAdapter(cartList, this);
                binding.recyclerview.setAdapter(adapter);
            });
        }).start();

        new Thread(()->{
            cartList = cartDatabase.cartDao().getAllCart();
            adapter = new CartAdapter(cartList,this);
            binding.recyclerview.setAdapter(adapter);

        }).start();

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

        binding.fab.setOnClickListener(v->{
            AlertDialog.Builder builder =  new AlertDialog.Builder(this);
            builder.setMessage("Bạn có chắc muốn xóa?");
            builder.setPositiveButton("Có", (dialog, i) -> {

                new Thread(()->{
                    cartDatabase.cartDao().deleteAllCarts();
                    runOnUiThread(() -> {
                        cartList.clear();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });

                }).start();
            });
            builder.setNegativeButton("Không", (dialog, i) -> {
                dialog.dismiss();

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        binding.btnFind.setOnClickListener(v->{
            filterByCartDate();

        });

        binding.startDay.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistorySellActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                binding.startDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.endDay.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistorySellActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                binding.endDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.btnBack.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút quay lại
            finish();
        });

    }

    private void filterByCartDate() {
        startDate = binding.startDay.getText().toString().trim();
        endDate = binding.endDay.getText().toString().trim();
        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        } else {
            try {
                binding.layoutDoanhthu.setVisibility(View.VISIBLE);
                Date startDay = dateFormat.parse(startDate);
                Date endDay = dateFormat.parse(endDate);
                new Thread(() -> {
                    List<Cart> filteredCartList = cartDatabase.cartDao().getCartsBetweenDates(startDay, endDay);
                    runOnUiThread(() -> {
                        if (filteredCartList.isEmpty()) {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                            binding.recyclerview.setVisibility(View.GONE);
                        } else {
                            binding.tvEmpty.setVisibility(View.GONE);
                            binding.recyclerview.setVisibility(View.VISIBLE);
                        }
                        adapter = new CartAdapter(filteredCartList, this);
                        binding.recyclerview.setAdapter(adapter);

                        double totalSum = calculateTotalSum(filteredCartList);
                        binding.tvSum.setText(new StringBuilder().append(format.format(totalSum)).append("đ"));
                    });
                }).start();
            } catch (ParseException e) {
                binding.layoutDoanhthu.setVisibility(View.GONE);
                Toast.makeText(this, "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double calculateTotalSum(List<Cart> cartList) {

        double total = 0;
        for (Cart cart : cartList) {
            total += cart.getTotalPrice();
        }
        return total;

    }

    private void iNit() {
        cartDatabase = CartDatabase.getDatabase(this);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onCartItemDeleted(Cart cart) {
        runOnUiThread(() -> {
            cartList.remove(cart);
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            double totalSum = calculateTotalSum(cartList);
            binding.tvSum.setText(new StringBuilder().append(format.format(totalSum)).append("đ"));
        });
    }
}
