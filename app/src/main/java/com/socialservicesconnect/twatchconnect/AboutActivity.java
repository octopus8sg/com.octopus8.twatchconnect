package com.socialservicesconnect.twatchconnect;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.socialservicesconnect.twatchconnect.databinding.ActivityAboutBinding;

public class AboutActivity extends Activity {

    private TextView tv_version, tv_uuid;
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tv_version = binding.tvVersion;
        tv_uuid = binding.tvUuid;

        String DeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        tv_uuid.setText(DeviceId);
        Log.d("uuid","*********** "+DeviceId);


        tv_version.setText("1.08");


    }
}