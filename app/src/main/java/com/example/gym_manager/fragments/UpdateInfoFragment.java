package com.example.gym_manager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.activitys.SettingActivity;
import com.example.gym_manager.databinding.FragmentUpdateInfoBinding;
import com.example.gym_manager.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;


public class UpdateInfoFragment extends Fragment {

  private FragmentUpdateInfoBinding binding;
  private FirebaseAuth auth;
    private Uri imgUri;



    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imgUri = result.getData().getData();
                    binding.imgAvatar.setImageURI(imgUri);

                }
            }
    );
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     binding = FragmentUpdateInfoBinding.inflate(inflater,container,false);
     return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       iNit(view);



       binding.btnUpdate.setOnClickListener(v->{
           binding.progressBar.setVisibility(View.VISIBLE);
           String name_gym = binding.edtNameGym.getText().toString().trim();
           if(name_gym.isEmpty()){
               Toast.makeText(requireContext(), "Bạn chưa nhập tên", Toast.LENGTH_SHORT).show();
               binding.progressBar.setVisibility(View.GONE);
               return;
           }
           StorageReference imageRef = FirebaseStorage.getInstance().getReference("images_avatar").child(imgUri.getLastPathSegment());
           imageRef.putFile(imgUri)
                   .addOnSuccessListener(task->{
                       imageRef.getDownloadUrl()
                               .addOnSuccessListener(uri->{

                                   HashMap<String,Object> hashMap = new HashMap<>();
                                   hashMap.put("url_avatar",uri.toString());
                                   hashMap.put("gym_name",name_gym);
                                   FirebaseUser currentUser = auth.getCurrentUser();

                                   if(currentUser != null){
                                       FirebaseDatabase.getInstance().getReference("admin")
                                               .child(currentUser.getUid())
                                               .setValue(hashMap)
                                               .addOnSuccessListener(success->{
                                                   clearInput();
                                                   binding.progressBar.setVisibility(View.GONE);
                                                   Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                                               })
                                               .addOnFailureListener(error->{
                                                   binding.progressBar.setVisibility(View.GONE);
                                                   Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();

                                               });


                                   }




                               })
                               .addOnFailureListener(error->{

                               });

                   })
                   .addOnFailureListener(error->{

                   });


       });
       binding.imgAvatar.setOnClickListener(v->openGallery());
        binding.btnBack.setOnClickListener(v->{
          requireActivity().onBackPressed();
        });


    }

    private void iNit(View view) {
        auth = FirebaseAuth.getInstance();

    }

    private void clearInput(){
        binding.imgAvatar.setImageResource(R.drawable.imag_user_default);
        binding.edtNameGym.setText("");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }


}