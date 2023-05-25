package com.example.logintest.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.templates.ToggleRangeTemplate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.logintest.databinding.ActivityRegisterBinding;
import com.example.logintest.message.MessageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Button RegisterButton;
    EditText userNameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    EditText emailEditText;

    ImageView userImage;

    int colorUser;

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RegisterButton = binding.buttonRegister;
        userNameEditText = binding.editTextName;
        passwordEditText = binding.editTextPassword;
        confirmPasswordEditText = binding.editTextConfirmPassword;
        emailEditText = binding.editTextEmail;
        userImage = binding.userImage;

        Random rnd = new Random();
        colorUser = Color.argb(255, rnd.nextInt(256)/2, rnd.nextInt(256)/2, rnd.nextInt(256)/2);
        userImage.setBackgroundColor(colorUser);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null){
                    Toast.makeText(RegisterActivity.this, "Add Profile Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                // Check if passwords match
                if (userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(confirmPassword)) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                                }
                                if (task.isSuccessful()) {
                                    // User account created successfully
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    usersRef.child(uid).child("email").setValue(email);
                                    usersRef.child(uid).child("username").setValue(userName);


                                    Toast.makeText(RegisterActivity.this, "Register succesfull", Toast.LENGTH_SHORT).show();
                                    uploadImage(uid, userName, email);
                                    /*Intent intent = new Intent(RegisterActivity.this, LoadingActivity.class);
                                        intent.putExtra("username", userName);
                                        intent.putExtra("uid", uid);
                                        intent.putExtra("LOGIN", true);
                                        startActivity(intent);*/
                                } else {
                                    Toast.makeText(RegisterActivity.this, String.valueOf(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        binding.changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
    }

    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                userImage.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String uid, String userName, String email)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "usersImages/"
                                    + uid);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(RegisterActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    finishAffinity();
                                    Intent intent = new Intent(RegisterActivity.this, MessageActivity.class);
                                    intent.putExtra("username", userName);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("email", email);
                                    //intent.putExtra("LOGIN", true);
                                    startActivity(intent);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(RegisterActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
}