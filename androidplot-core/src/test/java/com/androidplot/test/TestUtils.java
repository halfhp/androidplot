package com.androidplot.test;

import android.annotation.*;
import android.view.*;

import com.androidplot.xy.*;

import org.robolectric.shadows.*;

import static org.robolectric.Shadows.shadowOf;

/**
 * Utilities to help with unit testing
 */
public abstract class TestUtils {

    public static XYSeries generateXYSeries(String title, int size) {
        return generateXYSeries(title, size, 0, 1);
    }

    /**
     * Generate a series of random numbers within a min/max range
     * @param title
     * @param size
     * @param min
     * @param max
     * @return
     */
    public static XYSeries generateXYSeries(String title, int size, double min, double max) {
        SimpleXYSeries series = new SimpleXYSeries(title);
        for(int i = 0; i < size; i++) {
            series.addLast(i, Math.random() * max - min);
        }
        return series;
    }

    public static XYSeries generateXYSeriesWithNulls(String title, int size) {
        SimpleXYSeries series = new SimpleXYSeries(title);
        for(int i = 0; i < size; i++) {
            if(Math.random() > 0.5) {
                series.addLast(i, null);
            } else {
                series.addLast(i, Math.random());
            }
        }
        return series;
    }

    /**
     * Generate a two finger {@link MotionEvent}.
     * @param finger1x
     * @param finger1y
     * @param finger2x
     * @param finger2y
     * @return
     */
    @SuppressLint("NewApi")
    public static MotionEvent newPointerDownEvent(int finger1x, int finger1y, int finger2x, int finger2y) {
        MotionEvent me = MotionEvent.obtain(0, 0, 0, 0, 0, 0);
        ShadowMotionEvent sme = shadowOf(me);
        sme.setAction(MotionEvent.ACTION_POINTER_DOWN);
        sme.setPointerIds(0, 1);
        sme.setLocation(finger1x, finger1y);
        sme.setPointer2(finger2x, finger2y);
        return me;
    }
}
