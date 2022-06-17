package com.socialservicesconnect.twatchconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.socialservicesconnect.twatchconnect.databinding.ActivitySplashBinding;

public class SplashActivity extends Activity {

    private ActivitySplashBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                Utils.savePreferences(Utils.KEY_IS_TIMER_STARTED, "no", context);
            }
        }, 2000);


    }
}