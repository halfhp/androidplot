package com.androidplot.test;

import android.content.Context;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Base class for all Androidplot tests that make use of Android platform classes.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AndroidplotTest {

    /**
     * Convience method - to access the application context.
     * @return
     */
    protected Context getContext() {
        return RuntimeEnvironment.application;
    }
}
