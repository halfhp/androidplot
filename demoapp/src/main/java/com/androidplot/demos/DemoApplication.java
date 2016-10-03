package com.androidplot.demos;

import android.app.*;

import com.squareup.leakcanary.*;

/**
 * Created by halfhp on 10/1/16.
 */
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

