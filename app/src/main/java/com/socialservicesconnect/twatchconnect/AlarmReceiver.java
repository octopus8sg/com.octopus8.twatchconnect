package com.socialservicesconnect.twatchconnect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intentm) {
        Log.d("result","received broadcast xxxxxxx");
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Set the alarm here.
//            Log.d("result","received broadcast **********");
//        }


        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        pendingIntent = PendingIntent.getBroadcast(
               context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(pendingIntent);
        }



        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        1000*60*30, pendingIntent);

        Intent intent1=new Intent("aws");
        context.sendBroadcast(intent1);
    }
}
