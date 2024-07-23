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

import com.example.gym_manager.activitys.SettingActivity;
import com.example.gym_manager.databinding.FragmentReplacePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ReplacePasswordFragment extends Fragment {

  private FragmentReplacePasswordBinding binding;
  private FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentReplacePasswordBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v->{

            startActivity(new Intent(requireActivity(), SettingActivity.class));
        });

        binding.btnUpdate.setOnClickListener(v->{
            binding.progressBar.setVisibility(View.VISIBLE);
            String old_pass = binding.edtOldPass.getText().toString().trim();
            String newPass = binding.edtNewPass.getText().toString().trim();
            String cofirmNewPass = binding.edtConfirmNewPass.getText().toString().trim();

            if(old_pass.isEmpty() && newPass.isEmpty() && cofirmNewPass.isEmpty()){
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cofirmNewPass.equals(newPass)) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Mật khẩu không giống nhau", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), old_pass);
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                currentUser.updatePassword(newPass)
                                        .addOnCompleteListener(taskUpdate -> {
                                            binding.progressBar.setVisibility(View.GONE);
                                            if (taskUpdate.isSuccessful()) {
                                                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(), "Cập nhật thất bại: " + taskUpdate.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Xác thực thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}