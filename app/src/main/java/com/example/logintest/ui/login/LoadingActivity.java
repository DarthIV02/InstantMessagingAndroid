package com.example.logintest.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.logintest.R;
import com.example.logintest.data.model.Message;
import com.example.logintest.databinding.ActivityLoadingBinding;
import com.example.logintest.message.MessageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class LoadingActivity extends AppCompatActivity {
    ActivityLoadingBinding binding;
    MyCountdownTimer countdownTimer;

    Boolean login;

    String username;
    String uid;

    FirebaseStorage storage;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        login = intent.getBooleanExtra("LOGIN", false);
        uid = intent.getStringExtra("uid");
        username = intent.getStringExtra("username");

        Glide.with(this).load(R.drawable.smile).into(binding.stormy);

        if(!login){
            countdownTimer = new MyCountdownTimer(5000, 1000);
            countdownTimer.start();
        } else {

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();

            StorageReference userImageStorage = storageRef.child("usersImages").child(uid);

            while (true){
                userImageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        countdownTimer = new MyCountdownTimer(1000, 1000);
                        countdownTimer.start();
                    }
                });
            }
        }

    }


    public class MyCountdownTimer extends CountDownTimer
    {
        int progress = 0;
        public MyCountdownTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            progress = progress + 20;
            binding.loadingProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish()
        {
            if (!login){
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(LoadingActivity.this, MessageActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
            }

        }
    }
}