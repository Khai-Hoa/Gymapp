package com.example.gym_manager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.activitys.BuyOrderActivity;
import com.example.gym_manager.activitys.SeenImageActivity;
import com.example.gym_manager.databinding.RowItemProductBinding;
import com.example.gym_manager.model.Product;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    List<Product> mProducts;
    Context context;

    public ProductAdapter(List<Product> mProducts) {
        this.mProducts = mProducts;
    }

    public void setProducts(List<Product> mProducts) {
        this.mProducts = mProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemProductBinding binding = RowItemProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = mProducts.get(position);
        context = holder.itemView.getContext();
        Glide.with(context)
                .load(product.getUrl())
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageView);
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvName.setText(product.getProduct_name());
        holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(product.getProduct_price())).append("đ"));

        holder.binding.imageView.setOnClickListener(v->{
            Intent intent = new Intent(context, SeenImageActivity.class);
            intent.putExtra("image",product.getUrl());
            context.startActivity(intent);
        });

        holder.binding.ivDelete.setOnClickListener(v->{
            showDialogDelete(product,holder);
        });

        holder.binding.btnBuy.setOnClickListener(v->{
            Intent intent = new Intent(context, BuyOrderActivity.class);
            intent.putExtra("image",product.getUrl());
            intent.putExtra("name",product.getProduct_name());
            intent.putExtra("price",product.getProduct_price());
            context.startActivity(intent);
        });

    }

    private void showDialogDelete(Product product, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có chắc muốn xóa "+product.getProduct_name()+" không?");
        builder.setPositiveButton("Có", (dialog, i) -> {
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("product_shop").child(product.getProduct_id());
                String key = productRef.getKey();
            assert key != null;
            productRef

                        .removeValue()
                        .addOnSuccessListener(aVoid->{
                            mProducts.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(error->{
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        });


        });
        builder.setNegativeButton("Không", (dialog, i) -> {
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
}




    @Override
    public int getItemCount() {
        return mProducts.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        RowItemProductBinding binding;

        public ViewHolder(@NonNull RowItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
