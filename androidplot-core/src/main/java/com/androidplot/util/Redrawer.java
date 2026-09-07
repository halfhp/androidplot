// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.util.Log;

import com.androidplot.Plot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    private volatile boolean keepRunning;

    // when set to false, run thread will be allowed to exit the main run loop
    private volatile boolean keepAlive;

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
                if (!redrawAll(plots)) {
                    // every plot has been garbage collected; there is nothing left to redraw
                    // so let the thread exit rather than spin until finish() is called.
                    keepAlive = false;
                    break;
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
    /**
     * Redraws every plot that is still reachable, dropping references to plots that have been
     * garbage collected.
     *
     * @return false if no plots remain.
     */
    static boolean redrawAll(List<WeakReference<Plot>> plots) {
        Iterator<WeakReference<Plot>> it = plots.iterator();
        while (it.hasNext()) {
            Plot plot = it.next().get();
            if (plot == null) {
                it.remove();
            } else {
                plot.redraw();
            }
        }
        return !plots.isEmpty();
    }

    public void setMaxRefreshRate(float refreshRate) {
        sleepTime = (long)(ONE_SECOND_MS / refreshRate);
        Log.d(TAG, "Set Redrawer refresh rate to " +
                refreshRate + "( " + sleepTime + " ms)");
    }
}
