package com.socialservicesconnect.twatchconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.socialservicesconnect.twatchconnect.databinding.ActivitySplashBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends Activity {
    private PeriodicWorkRequest mPeriodicWorkRequest;

    private ActivitySplashBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;

        String sub_domain = Utils.getPreferences(Utils.SUB_DOMAIN, context);
        if (TextUtils.isEmpty(sub_domain)) {
            Utils.savePreferences(Utils.SUB_DOMAIN, "hindol", context);
        }




//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                Utils.savePreferences(Utils.KEY_IS_TIMER_STARTED, "no", context);
//            }
//        }, 2000);


        loadingData();


//        mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class,
//                15, TimeUnit.MINUTES)
//                .addTag("periodicWorkRequest")
//                .build();
//
//        WorkManager.getInstance().enqueue(mPeriodicWorkRequest);

    }

    public void checkAuth() {



        Map<String, String> postParam = new HashMap();
        postParam.put("email", "twatchconnect@octopus8.com");
        postParam.put("password", "Ps825K692eJ");



        JSONObject parameters = new JSONObject(postParam);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://hindol.socialservicesconnect.com/civicrm/devices/auth", parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("result", response.toString());



//            Toast.makeText(context, "Error "+response.toString(), Toast.LENGTH_SHORT).show();
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("result", error.toString());
                Toast.makeText(context, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                //TODO: handle failure
            }
        }) ;

//        {
//
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
////            headers.put("key", "Value");
//                return headers;
//            }
//        };

        jsonRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });


        Volley.newRequestQueue(this).add(jsonRequest);

    }


    private void loadingData() {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.HTTP_URL+Utils.getPreferences(Utils.SUB_DOMAIN,context)+Utils.AUTH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response.toString();
                        Log.d("result", result);
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            JSONObject inner_object=jsonObject.getJSONObject("values");
                            String api_key=inner_object.getString("api_key");
                            String key=inner_object.getString("key");
                            String site_name=inner_object.getString("site_name");


                            Utils.savePreferences(Utils.KEY_API_KEY, api_key, context);
                            Utils.savePreferences(Utils.KEY, key, context);
                            Utils.savePreferences(Utils.KEY_SITE_NAME, site_name, context);







                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        Utils.savePreferences(Utils.KEY_IS_TIMER_STARTED, "no", context);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("result", "error " + error);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        Utils.savePreferences(Utils.KEY_IS_TIMER_STARTED, "no", context);

                        Toast.makeText(context, "Error: "+error, Toast.LENGTH_SHORT).show();

//                        progressDialog.dismiss();
//                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postParam = new HashMap<String, String>();
                postParam.put("email", "twatchconnect@octopus8.com");
                postParam.put("password", "Ps825K692eJ");


                return postParam;
            }

        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}