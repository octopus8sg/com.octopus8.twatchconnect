package com.socialservicesconnect.twatchconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.socialservicesconnect.twatchconnect.databinding.ActivitySiettingsBinding;

public class SettingsActivity extends Activity {

    private TextView tv_site, tvAbout;
    private ActivitySiettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tv_site = binding.tvSite;
        tvAbout = binding.tvAbout;


        tv_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, SiteUrlActivity.class));
            }
        });

        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });


    }
}