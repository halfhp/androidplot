package com.androidplot.util;

import android.util.Log;
import com.androidplot.Plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for invoking Plot.redraw() on a backgorund thread
 * at a set frequency.
 */
public class Redrawer implements Runnable {

    private static final String TAG = Redrawer.class.getName();

    private List<Plot> plots;
    private long sleepTime;
    private boolean keepRunning;
    private boolean keepAlive;

    /**
     *
     * @param plots List of Plot instances to be redrawn
     * @param maxRefreshRate Desired frequency at which to redraw plots.
     * @param startImmediately If true, invokes run() immediately after construction.
     */
    public Redrawer(List<Plot> plots, float maxRefreshRate, boolean startImmediately) {
        this.plots = plots;
        setMaxRefreshRate(maxRefreshRate);
        new Thread(this).start();
        if(startImmediately) {
            run();
        }
    }

    public Redrawer(Plot plot, float maxRefreshRate, boolean startImmediately) {
        this(Arrays.asList(new Plot[]{plot}), maxRefreshRate, startImmediately);
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
                for(Plot plot : plots) {
                    plot.redraw();
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
        } catch(InterruptedException e) {

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
        sleepTime = (long)(1000 / refreshRate);
        Log.d(TAG, "Set Redrawer refresh rate to " +
                refreshRate + "( " + sleepTime + " ms)");
    }
}
