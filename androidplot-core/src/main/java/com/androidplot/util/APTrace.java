package com.androidplot.util;

import android.os.*;

/**
 * Wraps {@link Trace} to provide API-safe methods as well as an easy target for runtime removal
 * via obfuscation.
 */
public abstract class APTrace {

    public static void begin(final String sectionName) {
        if(Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(sectionName);
        } else {
            // TODO: alternate impl?
        }
    }

    public static void end() {
        if(Build.VERSION.SDK_INT >= 18) {
            Trace.endSection();
        } else {
            // TODO: alternate impl?
        }
    }
}
