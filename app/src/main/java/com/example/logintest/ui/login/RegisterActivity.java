package com.example.logintest.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logintest.R;
import com.example.logintest.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Button RegisterButton;
    EditText userNameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    EditText emailEditText;

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


        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                                }
                                if (task.isSuccessful()) {
                                    // User account created successfully
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    usersRef.child(uid).child("email").setValue(email);
                                    usersRef.child(uid).child("username").setValue(userName);

                                    Toast.makeText(RegisterActivity.this, "Register succesfull", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "fallooooooo", Toast.LENGTH_SHORT).show();
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }
}