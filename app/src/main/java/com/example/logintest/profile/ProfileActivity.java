package com.example.logintest.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logintest.data.model.GlideApp;
import com.example.logintest.databinding.ActivityProfileBinding;
import com.example.logintest.message.MessageActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userImageView = binding.userImage;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference conic = storageRef.child("1conic.jpg");

        GlideApp.with(this /* context */)
                .load(conic)
                .into(userImageView);

        binding.changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangeProfilePictureActivity.class);
                //intent.putExtra("userName", current_user);
                startActivity(intent);
            }
        });
    }
}