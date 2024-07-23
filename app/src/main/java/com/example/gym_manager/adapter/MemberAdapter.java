package com.example.gym_manager.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.activitys.SeenImageActivity;
import com.example.gym_manager.databinding.RowItemMemberBinding;
import com.example.gym_manager.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    List<Member> mList;
    Context context;
    private FirebaseAuth auth;

    public MemberAdapter(List<Member> mList) {

        this.mList = mList;
        auth = FirebaseAuth.getInstance();
    }
    public void setMemberList(List<Member> newList) {
        mList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemMemberBinding binding = RowItemMemberBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder holder, int position) {

        context = holder.itemView.getContext();
        Member member = mList.get(position);
        Glide.with(context)
                .load(member.getAvatar())
                .placeholder(R.drawable.imag_user_default)
                .into(holder.binding.imgAvatar);
        holder.binding.tvName.setText(member.getName());
        holder.binding.tvAddress.setText(new StringBuilder("Địa chỉ: ").append(member.getAddress()));
        holder.binding.tvPhone.setText(new StringBuilder("ĐT: ").append(member.getPhone()));
        holder.binding.tvHeight.setText(new StringBuilder("Chiều cao: ").append(member.getHeight()).append(" cm"));
        holder.binding.tvWeight.setText(new StringBuilder("Cân nặng: ").append(member.getWeight()).append(" kg"));
       holder.binding.tvRole.setText(member.getRole());
       if(member.getActive().equals(true)){
           holder.binding.tvStatus.setText(new StringBuilder("Trạng thái: ").append("Còn tập"));
       }
       else{
           holder.binding.tvStatus.setText(new StringBuilder("Trạng thái: ").append("Đã nghỉ"));
       }

       holder.binding.ivDelete.setOnClickListener(v->{
           showDialogDelete(member,holder);
       });

       holder.itemView.setOnClickListener(v->{
           Intent intent = new Intent(context, SeenImageActivity.class);
           intent.putExtra("image",member.getAvatar());
           context.startActivity(intent);
       });



    }

    private void showDialogDelete(Member member, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có chắc muốn xóa "+member.getName()+" không?");
        builder.setPositiveButton("Có", (dialog, i) -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null){
                FirebaseDatabase.getInstance().getReference("members")
                        .child(currentUser.getUid())
                        .child(member.getPhone())
                        .removeValue()
                        .addOnSuccessListener(aVoid->{
                            mList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(error->{
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                Toast.makeText(context, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Không", (dialog, i) -> {
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemMemberBinding binding;

        public ViewHolder(@NonNull RowItemMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
