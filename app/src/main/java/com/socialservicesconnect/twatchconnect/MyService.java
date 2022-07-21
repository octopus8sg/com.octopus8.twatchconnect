package com.socialservicesconnect.twatchconnect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        Toast.makeText(getApplicationContext(), "This is a Service running in Background",
                Toast.LENGTH_SHORT).show();
        Log.d("result", "service is running");
        sendSensorData();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    public void sendSensorData() {
        String UUID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String dtime = (String) android.text.format.DateFormat.format("dd-MMM-yyyy HH:mm", new Date());


        Map<String, String> postParam = new HashMap();
        postParam.put("entity", "DeviceData");
        postParam.put("action", "create");
        postParam.put("date", dtime);
        postParam.put("sensor_id", "1");
        postParam.put("device_code", UUID);

        Log.d("uuid", "uuid is " + UUID);
//        if (TextUtils.isEmpty(str_bpm_valu)) {
//            str_bpm_valu = "0";
//        }
        postParam.put("sensor_value", "85");


//    postParam.put("entity", "DeviceData");
//    postParam.put("action", "create");
//    postParam.put("date", "1-May-2022");
//    postParam.put("sensor_id", "1");
//    postParam.put("device_code", "dc607dd399068d01");
//    postParam.put("sensor_value", "100");

        JSONObject parameters = new JSONObject(postParam);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Utils.HTTP_URL + Utils.getPreferences(Utils.SUB_DOMAIN, getApplicationContext()) + Utils.MAIN_URL + Utils.getPreferences(Utils.KEY, getApplicationContext()) + "&json=1&api_key=" + Utils.getPreferences(Utils.KEY_API_KEY, getApplicationContext()), parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("result", response.toString());
                String dtime = (String) android.text.format.DateFormat.format("dd-MMM-yyyy HH:mm", new Date());

                Utils.savePreferences(Utils.KEY_LAST_UPDATED, dtime, getApplicationContext());


//                sensorManager.unregisterListener(MainActivity.this, bpmSensor);

//            Toast.makeText(context, "Error "+response.toString(), Toast.LENGTH_SHORT).show();
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("result", error.toString());
                Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                //TODO: handle failure
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
//            headers.put("key", "Value");
                return headers;
            }
        };

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
}
