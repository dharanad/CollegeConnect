/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by dharanaditya on 28/01/18.
 */

public class AppApplication extends Application {

    private RefWatcher mRefWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        AppApplication appApplication = (AppApplication) context.getApplicationContext();
        return appApplication.mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        mRefWatcher = LeakCanary.install(this);
    }

    // TODO : 31/10/18 Retrieve User Data and Store them in shared preferences
    // TODO: 30/01/18 Details Activity
}
