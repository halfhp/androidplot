package com.androidplot;

import android.os.Handler;
import android.os.Message;

/**
 * Passes display refresh requests to the thread handling them.  Using a Handler to coordinate these kinds
 * of updates is compulsory on the Android platform.
 */
class PlotUpdateHandler extends Handler {

    Plot plot;
    public PlotUpdateHandler(Plot plot) {
        this.plot = plot;
    }

    /**
     * Invalidates the View so that it will be redrawn.
     * @param msg
     */
    public void handleMessage(Message msg) {
        plot.invalidate();
    }
}
