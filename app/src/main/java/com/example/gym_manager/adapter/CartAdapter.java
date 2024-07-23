package com.example.gym_manager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.activitys.HistorySellActivity;
import com.example.gym_manager.database.Cart;
import com.example.gym_manager.database.CartDatabase;
import com.example.gym_manager.databinding.RowItemHistorySellBinding;
import com.example.gym_manager.listener.OnCartItemDeletedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    List<Cart> mListCart;
    Context context;
    CartDatabase cartDatabase;
    private OnCartItemDeletedListener onCartItemDeletedListener;

    public CartAdapter(List<Cart> mListCart,OnCartItemDeletedListener listener) {
        this.mListCart = mListCart;
        this.onCartItemDeletedListener = listener;
        cartDatabase = CartDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemHistorySellBinding binding = RowItemHistorySellBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        Cart cart = mListCart.get(position);
        holder.binding.tvNameCustomer.setText(cart.getName_customer());
        holder.binding.tvPhone.setText(new StringBuilder("SĐT: ").append(cart.getPhone_customer()));
        holder.binding.tvAddress.setText(new StringBuilder("Địa chỉ: ").append(cart.getAddress_customer()));
        Glide.with(context)
                .load(cart.getUrl_product())
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageView);
        holder.binding.tvNameProduct.setText(cart.getProduct_name());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(cart.getPrice_product())).append("đ"));
        holder.binding.tvSum.setText(new StringBuilder("Giá: ").append(format.format(cart.getTotalPrice())).append("đ"));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(cart.getPurchaseTime());
        holder.binding.tvTime.setText(new StringBuilder("Thời gian: ").append(formattedDate));

        holder.binding.ivDelete.setOnClickListener(v->{
            openDialogDelete(cart,holder);
        });

    }

    private void openDialogDelete(Cart cart, ViewHolder holder) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setMessage("Bạn có chắc muốn xóa?");
        builder.setPositiveButton("Có", (dialog, i) -> {

            new Thread(()->{
                cartDatabase.cartDao().deleteCartItemById(cart.getId());
                if (onCartItemDeletedListener != null) {
                    onCartItemDeletedListener.onCartItemDeleted(cart);
                }
            }).start();
        });
        builder.setNegativeButton("Không", (dialog, i) -> {
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return mListCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemHistorySellBinding binding;

        public ViewHolder(@NonNull RowItemHistorySellBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
