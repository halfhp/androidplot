package com.androidplot.demos;

import android.app.*;

import com.squareup.leakcanary.*;

public class DemoApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    protected void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}

