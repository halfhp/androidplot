package com.androidplot.mock;

import android.os.Looper;
import mockit.Mock;
import mockit.MockClass;


/**
 * myLooper and getMainLooper will always be equal.  The implication is that any calling
 * entity will assume that it is running on the main thread.  Simulation of background mode
 * is not supported.
 */
@MockClass(realClass = Looper.class)
public class MockLooper {

    @Mock
    public static Looper myLooper() {
        return null;
    }

    @Mock
    public static Looper getMainLooper() {
        return null;
    }
}
