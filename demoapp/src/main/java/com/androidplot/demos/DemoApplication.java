package com.androidplot.demos;

import android.app.*;

import com.squareup.leakcanary.*;
import com.squareup.leakcanary.BuildConfig;

/**
 * Created by halfhp on 10/1/16.
 */
public class DemoApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        if(com.androidplot.BuildConfig.DEBUG) {
            initLeakCanary();
        }
    }

    protected void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}

