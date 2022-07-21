package com.socialservicesconnect.twatchconnect;

import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyPeriodicWork extends Worker {

    private static final String TAB = MyPeriodicWork.class.getSimpleName();

    public MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        Log.d("work","PeriodicWork in BackGround after each one minut");

        return Result.success();
    }
}
