package com.socialservicesconnect.twatchconnect;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.socialservicesconnect.twatchconnect.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {
    private ProgressDialog progressDialog;
    private TextView tv_rate1, tvRateNormal, tv_update;
    private String str_bpm_valu;
    private ImageView iv_bpm, iv_settings, ivBpmNormal;
    private ActivityMainBinding binding;
    private Sensor bpmSensor;
    private SensorManager sensorManager;
    private Boolean isBpmSensorAvailable;
    private static final int PERMISSION_REQUEST_READ_BODY_SENSORS = 1;
    private final String TAG = MainActivity.class.getSimpleName();
    Timer timer;
    PowerManager.WakeLock wl;
    private Context context;
    TimerTask doAsynchronousTask;
    private String isFirst = "yes";
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private String isOnWrist="no";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wl.acquire();







            ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                WifiManager wman = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                Log.d("result", "wifi not available");
                try {
                    wman.reconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("result", "wifi not available "+e);

                }
                Toast.makeText(context, "wifi error ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "wifi working ", Toast.LENGTH_SHORT).show();
                Log.d("result", "**wifi is available**");

            }

            Log.d("result", "broadcast called in main ");


//            if(isOnWrist.equals("yes")){
//                sensorManager.registerListener(MainActivity.this, bpmSensor, sensorManager.SENSOR_DELAY_FASTEST);
//            }

            sensorManager.registerListener(MainActivity.this, bpmSensor, sensorManager.SENSOR_DELAY_FASTEST);

//            if (isFirst.equals("no")) {
//                Log.d("result", "API called in main is first = " + isFirst);
//                sendSensorData();
//
//            }


//            startService(new Intent(getApplicationContext(),MyService.class));







            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFirst.equals("no")) {
                        Log.d("result", "API called in main is first = " + isFirst);
                        sendSensorData();
                        wl.release();

                    }
                }
            }, 3000);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = MainActivity.this;

        tv_rate1 = binding.tvRate;
        tvRateNormal = binding.tvRateNormal;
        iv_bpm = binding.ivBpm;
        tv_update = binding.tvUpdate;
        iv_settings = binding.ivSettings;
        ivBpmNormal = binding.ivBpmNormal;


        String sub_domain = Utils.getPreferences(Utils.SUB_DOMAIN, context);
        if (TextUtils.isEmpty(sub_domain)) {
            Utils.savePreferences(Utils.SUB_DOMAIN, "test", context);
        }

        String last_updated = Utils.getPreferences(Utils.KEY_LAST_UPDATED, context);
        if (!TextUtils.isEmpty(last_updated)) {
            tv_update.setText(last_updated);
        }
//        String dtime=(String)android.text.format.DateFormat.format("dd-MMM-yyyy HH:mm", new Date());
//
        iv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendSensorData();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });


        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS},
                    PERMISSION_REQUEST_READ_BODY_SENSORS);
        } else {
            Log.d(TAG, "ALREADY GRANTED");
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        boolean hasSensor = sensorManager.getDefaultSensor(34, true /* wakeup */) != null;

        if (hasSensor) {
            Sensor offBodySensor = sensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
            sensorManager.registerListener(this, offBodySensor, sensorManager.SENSOR_DELAY_FASTEST);


        } else {
            Toast.makeText(this, "Off body  Sensor not available!", Toast.LENGTH_SHORT);

        }
        Log.d("res", "res" + hasSensor);


        if (sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null) {


            bpmSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            sensorManager.registerListener(this, bpmSensor, sensorManager.SENSOR_DELAY_NORMAL);
            isBpmSensorAvailable = true;
        } else {
            Toast.makeText(this, "BPM Sensor not available!", Toast.LENGTH_SHORT);
            isBpmSensorAvailable = false;
        }
        String isTimerStarted = Utils.getPreferences(Utils.KEY_IS_TIMER_STARTED, context);

//        if (isTimerStarted.equals("no")) {
//            startTimerTask();
//
//        }


        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
        }

        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        1000*60*30, pendingIntent);
        Log.d("result", "on create called");

    }

    private void startTimerTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("result", "timer is running********** isFirst" + isFirst);


                        if (isFirst.equals("no")) {
                            Log.d("result", "timer is running********** isFirst" + isFirst);
                            sendSensorData();

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);
    }

//    1800000


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("result", "on resume  called");

        context.registerReceiver(mMessageReceiver, new IntentFilter("aws"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("result", "on pause called" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("result", "on onStop called" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("result", "on onDestroy called" );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT) {
            int offbody = (int) event.values[0];

            if (offbody == 0.0) {
                sensorManager.unregisterListener(this, bpmSensor);
                isOnWrist="no";

//                Toast.makeText(context, "OFF body state", Toast.LENGTH_SHORT).show();
                str_bpm_valu = "0";
                ivBpmNormal.setVisibility(View.VISIBLE);
                tvRateNormal.setVisibility(View.VISIBLE);
                iv_bpm.setVisibility(View.INVISIBLE);
                tv_rate1.setVisibility(View.INVISIBLE);
                tvRateNormal.setText(str_bpm_valu);

            } else {
                isOnWrist="yes";
                sensorManager.registerListener(this, bpmSensor, sensorManager.SENSOR_DELAY_FASTEST);
//                Toast.makeText(context, "ON body state", Toast.LENGTH_SHORT).show();
            }


        } else if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            int bpm = (int) event.values[0];
            str_bpm_valu = "" + bpm;
            if (bpm < 101 && bpm > 50) {
                ivBpmNormal.setVisibility(View.VISIBLE);
                tvRateNormal.setVisibility(View.VISIBLE);

                iv_bpm.setVisibility(View.INVISIBLE);
                tv_rate1.setVisibility(View.INVISIBLE);


                tvRateNormal.setText(str_bpm_valu);

            } else {

                ivBpmNormal.setVisibility(View.INVISIBLE);
                tvRateNormal.setVisibility(View.INVISIBLE);
                iv_bpm.setVisibility(View.VISIBLE);
                tv_rate1.setVisibility(View.VISIBLE);
                tv_rate1.setText(str_bpm_valu);

            }


        }
        String isTimerStarted = Utils.getPreferences(Utils.KEY_IS_TIMER_STARTED, context);

        if (!TextUtils.isEmpty(str_bpm_valu)) {
            try {

                int str_bpm_valu_int = Integer.parseInt(str_bpm_valu);
                if (isFirst.equals("yes") && str_bpm_valu_int > 0 && isTimerStarted.equals("no")) {
                    isFirst = "no";
                    Utils.savePreferences(Utils.KEY_IS_TIMER_STARTED, "yes", context);


                    sendSensorData();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


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
        if (TextUtils.isEmpty(str_bpm_valu)) {
            str_bpm_valu = "0";
        }
        postParam.put("sensor_value", str_bpm_valu);


//    postParam.put("entity", "DeviceData");
//    postParam.put("action", "create");
//    postParam.put("date", "1-May-2022");
//    postParam.put("sensor_id", "1");
//    postParam.put("device_code", "dc607dd399068d01");
//    postParam.put("sensor_value", "100");

        JSONObject parameters = new JSONObject(postParam);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Utils.HTTP_URL + Utils.getPreferences(Utils.SUB_DOMAIN, context) + Utils.MAIN_URL + Utils.getPreferences(Utils.KEY, context) + "&json=1&api_key=" + Utils.getPreferences(Utils.KEY_API_KEY, context), parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("result", response.toString());
                try {
                    String is_error=response.getString("is_error");

                    if(is_error.equalsIgnoreCase("0")){
                        String dtime = (String) android.text.format.DateFormat.format("dd-MMM-yyyy HH:mm", new Date());
                        tv_update.setText(dtime);
                        Utils.savePreferences(Utils.KEY_LAST_UPDATED, dtime, context);
                    }else {
                        Toast.makeText(context, "Error: Your device is not registered  ", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                sensorManager.unregisterListener(MainActivity.this, bpmSensor);

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