package com.example.logintest.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.logintest.R;
import com.example.logintest.databinding.ActivityLoadingBinding;


public class LoadingActivity extends AppCompatActivity {
    ActivityLoadingBinding binding;
    MyCountdownTimer countdownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Glide.with(this).load(R.drawable.smile).into(binding.stormy);

        countdownTimer = new MyCountdownTimer(5000, 1000);
        countdownTimer.start();

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
            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}