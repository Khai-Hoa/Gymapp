package com.example.gym_manager.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gym_manager.R;
import com.example.gym_manager.databinding.ActivityAddProductBinding;
import com.example.gym_manager.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    private ActivityAddProductBinding binding;
    private FirebaseAuth auth;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.imageview.setImageURI(imageUri);

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        iNit();

        binding.btnSave.setOnClickListener(v->{
            binding.progress.setVisibility(View.VISIBLE);
            binding.btnSave.setVisibility(View.GONE);
            String name_product = binding.edtNameProduct.getText().toString().trim();
            String price_product = binding.edtPriceProduct.getText().toString().trim();

            if(name_product.isEmpty() && price_product.isEmpty()){
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("image_product").child(imageUri.getLastPathSegment());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(task->{
                        imageRef.getDownloadUrl().addOnSuccessListener(uri->{
                            if(uri.toString().isEmpty()){
                                Toast.makeText(this, "chưa có hình ảnh", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("product_shop");
//                                HashMap<String,Object> hashMap = new HashMap<>();
//                                hashMap.put("product_name",name_product);
//                                hashMap.put("key",productRef.getKey());
//                                hashMap.put("product_price",Double.parseDouble(price_product));
//                                hashMap.put("url",uri.toString());
                                DatabaseReference newProductRef = productRef.push();
                                String productKey = newProductRef.getKey();
                                Product product = new Product(
                                        productKey,
                                        name_product,
                                        uri.toString(),
                                        Double.parseDouble(price_product)



                                );
                                FirebaseUser currentUser = auth.getCurrentUser();
                              if(currentUser != null){

                                 newProductRef.setValue(product)
                                         .addOnSuccessListener(taskVoid->{
                                             binding.progress.setVisibility(View.GONE);
                                             binding.btnSave.setVisibility(View.VISIBLE);
                                             clearInput();
                                             Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

                                         })
                                         .addOnFailureListener(error->{
                                             binding.progress.setVisibility(View.GONE);
                                             binding.btnSave.setVisibility(View.VISIBLE);
                                             Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                                         });
                              }
                            }
                        });
                    });
        });



        binding.btnAddImage.setOnClickListener(v->openGallery());



    }
    private void clearInput(){
        binding.edtNameProduct.setText("");
        binding.edtPriceProduct.setText("");
        binding.imageview.setImageResource(R.drawable.noimage);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}