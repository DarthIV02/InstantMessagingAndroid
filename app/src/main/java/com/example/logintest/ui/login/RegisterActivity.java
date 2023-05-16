package com.example.logintest.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.logintest.R;
import com.example.logintest.databinding.ActivityLoadingBinding;
import com.example.logintest.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}