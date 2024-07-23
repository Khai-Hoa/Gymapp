package com.example.gym_manager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gym_manager.R;
import com.example.gym_manager.activitys.AddEmployeeActivity;
import com.example.gym_manager.databinding.RowItemEmployeeBinding;
import com.example.gym_manager.model.Employee;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private List<Employee> mlistEmployee;
    Context context;

    public EmployeeAdapter(List<Employee> mlistEmployee) {
        this.mlistEmployee = mlistEmployee;
    }

    public void setMlistEmployee(List<Employee> mlistEmployee) {
        this.mlistEmployee = mlistEmployee;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemEmployeeBinding binding = RowItemEmployeeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {
        Employee employee = mlistEmployee.get(position);
        context = holder.itemView.getContext();
        holder.binding.tvName.setText(employee.getName());
        holder.binding.tvAddress.setText(employee.getAddress());
        holder.binding.tvPhone.setText(new StringBuilder("Phone: ").append(employee.getPhone()));
        holder.binding.tvRole.setText(new StringBuilder("Chức vụ: ").append(employee.getRole()));
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvSalary.setText(new StringBuilder("Lương: ").append(format.format(Double.parseDouble(employee.getSalary()))).append(" VND"));
        Glide.with(context)
                .load(employee.getUrl_avatar())
                .placeholder(R.drawable.imag_user_default)
                .into(holder.binding.imageAvatar);
        holder.binding.btnOption.setOnClickListener(v->{
            showPopupMenu(v, position,holder);
        });

    }

    private void showPopupMenu(View view, int position, ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item_employee, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, position,holder));
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item, int position,ViewHolder holder) {
       if(item.getItemId() == R.id.action_update){
           Intent intent = new Intent(context, AddEmployeeActivity.class);
           intent.putExtra("id",mlistEmployee.get(position).getEmployeeId());
           intent.putExtra("url",mlistEmployee.get(position).getUrl_avatar());
           intent.putExtra("name",mlistEmployee.get(position).getName());
           intent.putExtra("address",mlistEmployee.get(position).getAddress());
           intent.putExtra("phone",mlistEmployee.get(position).getPhone());
           intent.putExtra("role",mlistEmployee.get(position).getRole());
           intent.putExtra("salary",mlistEmployee.get(position).getSalary());
           context.startActivity(intent);

           return true;
       }
       else if(item.getItemId() == R.id.action_delete){
           deleteEmployee(position,holder);
           return true;
       }
        return false;
    }

    private void deleteEmployee(int position,ViewHolder holder) {


              String employee_id = mlistEmployee.get(position).getEmployeeId();
              FirebaseDatabase.getInstance().getReference("employees")
                      .child(employee_id)
                      .removeValue()
                      .addOnSuccessListener(aVoid -> {
                          mlistEmployee.remove(position);
                          notifyItemRemoved(position);
                          notifyItemRangeChanged(position, mlistEmployee.size());
                          Toast.makeText(context, "Xóa nhân viên thành công", Toast.LENGTH_SHORT).show();
                      })
                      .addOnFailureListener(e -> {
                          Toast.makeText(context, "Xóa nhân viên thất bại", Toast.LENGTH_SHORT).show();
                      });




    }

    @Override
    public int getItemCount() {
        return mlistEmployee.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemEmployeeBinding binding;

        public ViewHolder(@NonNull RowItemEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
