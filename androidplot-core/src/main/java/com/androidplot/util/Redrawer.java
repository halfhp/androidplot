/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.util;

import android.util.Log;

import com.androidplot.Plot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for invoking Plot.redraw() on a background thread
 * at a set frequency.
 */
public class Redrawer implements Runnable {

    private static final int ONE_SECOND_MS = 1000;

    private static final String TAG = Redrawer.class.getName();

    private List<WeakReference<Plot>> plots;
    private long sleepTime;

    // used to temporarily pause rendering without disposing of the run thread
    private boolean keepRunning;

    // when set to false, run thread will be allowed to exit the main run loop
    private boolean keepAlive;

    private Thread thread;

    /**
     *
     * @param plots List of Plot instances to be redrawn
     * @param maxRefreshRate Desired frequency at which to redraw plots.
     * @param startImmediately If true, invokes run() immediately after construction.
     */
    public Redrawer(List<Plot> plots, float maxRefreshRate, boolean startImmediately) {
        this.plots = new ArrayList<>(plots.size());
        for(Plot plot : plots) {
            this.plots.add(new WeakReference<>(plot));
        }
        setMaxRefreshRate(maxRefreshRate);
        thread = new Thread(this, "Androidplot Redrawer");
        thread.start();
        if(startImmediately) {
            start();
        }
    }

    public Redrawer(Plot plot, float maxRefreshRate, boolean startImmediately) {
        this(Collections.singletonList(plot), maxRefreshRate, startImmediately);
    }

    /**
     * Temporarily stop redrawing the plot.
     */
    public synchronized void pause() {
        keepRunning = false;
        notify();
        Log.d(TAG, "Redrawer paused.");
    }

    /**
     * Start/resume redrawing the plot.
     */
    public synchronized void start() {
        keepRunning = true;
        notify();
        Log.d(TAG, "Redrawer started.");
    }

    /**
     * Internally, this causes
     * the refresh thread to exit.  Should always be called
     * before exiting the application.
     */
    public synchronized void finish() {
        keepRunning = false;
        keepAlive = false;
        notify();
    }

    @Override
    public void run() {
        keepAlive = true;
        try {
        while(keepAlive) {
            if(keepRunning) {
                // redraw plot(s) and sleep in an interruptible state for a
                // max of sleepTime ms.
                // TODO: record start and end timestamps and
                // TODO: calculate sleepTime from that, in order to more accurately
                // TODO: meet desired refresh rate.
                for(WeakReference<Plot> plotRef : plots) {
                    plotRef.get().redraw();
                }
                synchronized (this) {
                    wait(sleepTime);
                }
            } else {
                // sleep until notified
                synchronized (this) {
                    wait();
                }
            }
        }
        } catch (InterruptedException ignored) {

        } finally {
            Log.d(TAG, "Redrawer thread exited.");
        }
    }

    /**
     * Set the maximum refresh rate that Redrawer should use.  Actual
     * refresh rate could be slower.
     * @param refreshRate Refresh rate in Hz.
     */
    public void setMaxRefreshRate(float refreshRate) {
        sleepTime = (long)(ONE_SECOND_MS / refreshRate);
        Log.d(TAG, "Set Redrawer refresh rate to " +
                refreshRate + "( " + sleepTime + " ms)");
    }
}
