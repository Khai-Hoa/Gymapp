package com.example.gym_manager.fragments;

import android.app.DatePickerDialog;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.database.Cart;
import com.example.gym_manager.database.CartDatabase;
import com.example.gym_manager.databinding.FragmentReportBinding;
import com.example.gym_manager.model.Device;
import com.example.gym_manager.model.Employee;
import com.example.gym_manager.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private FirebaseAuth auth;
    private CartDatabase cartDatabase;
    private String startDay;
    private String endDay;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat format = new DecimalFormat("#,###");
    private List<Member> mList = new ArrayList<>();
    private List<Employee> mListEmployee = new ArrayList<>();
    private List<Device> mListDevice = new ArrayList<>();
    private List<Device> mListDeviceDamaged = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit();
        binding.edtStartDate.setOnClickListener(v -> showDatePickerDialog(binding.edtStartDate));
        binding.edtEndDate.setOnClickListener(v -> showDatePickerDialog(binding.edtEndDate));

        binding.btnSearch.setOnClickListener(v -> {
            filterByCartDate(binding.edtStartDate.getText().toString().trim(), binding.edtEndDate.getText().toString().trim());
        });

        loadMemberFromFirebase();
        loadEmployeeFromFirebase();
        loadDeviceFromServer();
        loadDeviceDamaged();

        new Thread(() -> {
            List<Cart> cartList = cartDatabase.cartDao().getAllCart();
            double totalSum = calculateTotalSum(cartList);
            requireActivity().runOnUiThread(() -> binding.tvSum.setText(new StringBuilder().append(format.format(totalSum)).append(" VND")));
        }).start();
    }

    private void loadDeviceDamaged() {
        DatabaseReference deviceRef = FirebaseDatabase.getInstance().getReference("devices");
        Query query = deviceRef.orderByChild("fix").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListDeviceDamaged.clear();
                for (DataSnapshot damagedDevice : snapshot.getChildren()) {
                    Device device = damagedDevice.getValue(Device.class);
                    if (device != null) {
                        mListDeviceDamaged.add(device);
                    }
                }
                binding.tvCountDeviceHu.setText(String.valueOf(mListDeviceDamaged.size()));
                double totalMaintenanceCost = calculateTotalMaintenanceCost(mListDeviceDamaged);
                binding.tvMoneyFix.setText(new StringBuilder().append(format.format(totalMaintenanceCost)).append(" VND"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private double calculateTotalMaintenanceCost(List<Device> deviceList) {
        double totalCost = 0.0;
        for (Device device : deviceList) {
            totalCost += device.getMaintenanceCost(); // Assuming getMaintenanceCost() returns the cost as double
        }
        return totalCost;
    }

    private void filterByCartDate(String startDay, String endDay) {
        if (startDay.isEmpty() || endDay.isEmpty()) {
            Toast.makeText(requireContext(), "Bạn chưa chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date _startDay = dateFormat.parse(startDay);
            Date _endDay = dateFormat.parse(endDay);
            new Thread(() -> {
                List<Cart> filteredCartList = cartDatabase.cartDao().getCartsBetweenDates(_startDay, _endDay);
                double totalSum = calculateTotalSum(filteredCartList);
                requireActivity().runOnUiThread(() -> binding.tvSumDate.setText(new StringBuilder().append(format.format(totalSum)).append(" VND")));
            }).start();
        } catch (ParseException e) {
            Toast.makeText(requireContext(), "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                    textView.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private double calculateTotalSum(List<Cart> cartList) {
        double total = 0;
        for (Cart cart : cartList) {
            total += cart.getTotalPrice();
        }
        return total;
    }

    private void loadDeviceFromServer() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("devices")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListDevice.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                                Device device = deviceSnapshot.getValue(Device.class);
                                if (device != null) {
                                    mListDevice.add(device);
                                }
                            }
                            binding.tvCountDeviceHu.setText(String.valueOf(mListDevice.size()));
                        } else {
                            binding.tvCountDeviceHu.setText("0");
                        }
                        binding.swiplayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swiplayout.setRefreshing(false);
                    }
                });
    }

    private void loadEmployeeFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("employees")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListEmployee.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                                Employee employee = employeeSnapshot.getValue(Employee.class);
                                if (employee != null) {
                                    mListEmployee.add(employee);
                                }
                            }
                            binding.tvCountEmployee.setText(String.valueOf(mListEmployee.size()));
                        } else {
                            binding.tvCountEmployee.setText("0");
                        }
                        binding.swiplayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swiplayout.setRefreshing(false);
                    }
                });
    }

    private void loadMemberFromFirebase() {
        binding.swiplayout.setRefreshing(true);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase.getInstance().getReference("members")
                    .child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mList.clear();
                            double totalMemberRevenue = 0; // Variable to store total revenue
                            if (snapshot.exists()) {
                                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                                    Member member = memberSnapshot.getValue(Member.class);
                                    if (member != null) {
                                        mList.add(member);
                                        // Sử dụng phương thức hiện tại để lấy doanh thu
                                        totalMemberRevenue += member.getAmountPaid(); // Hoặc phương thức thực tế của bạn
                                    }
                                }
                                Log.d("ReportFragment", "Total Member Revenue: " + totalMemberRevenue);
                                binding.tvCountMember.setText(String.valueOf(mList.size()));
                                binding.tvSumDate.setText(new StringBuilder().append(format.format(totalMemberRevenue)).append(" VND")); // Update this line to show the total revenue
                            } else {
                                binding.tvCountMember.setText("0");
                                binding.tvSumDate.setText("0 VND"); // Set to 0 if no members
                            }
                            binding.swiplayout.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.swiplayout.setRefreshing(false);
                        }
                    });
        }
    }


    private void iNit() {
        cartDatabase = CartDatabase.getDatabase(requireContext());
        auth = FirebaseAuth.getInstance();
    }
}
