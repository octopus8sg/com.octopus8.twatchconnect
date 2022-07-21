package com.socialservicesconnect.twatchconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.socialservicesconnect.twatchconnect.databinding.ActivitySiteUrlBinding;

public class SiteUrlActivity extends Activity {

    private EditText etUrl;
    private ImageView ivSend;
    private ActivitySiteUrlBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteUrlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=SiteUrlActivity.this;
        etUrl = binding.etUrl;
        ivSend = binding.ivSend;

        String sub_domain=Utils.getPreferences(Utils.SUB_DOMAIN,context);
        if(!TextUtils.isEmpty(sub_domain)){
            etUrl.setText(sub_domain);
        }

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=etUrl.getText().toString();
                if(TextUtils.isEmpty(url)){
                    etUrl.setError("Enter url");
                    Toast.makeText(SiteUrlActivity.this, "Enter url", Toast.LENGTH_SHORT).show();

                }else {
                    Utils.savePreferences(Utils.SUB_DOMAIN,url,context);
                    Toast.makeText(SiteUrlActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SiteUrlActivity.this, SplashActivity.class));
                    finish();

                }

            }
        });


    }
}