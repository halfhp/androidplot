// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.androidplot.Plot;
import com.androidplot.PlotListener;
import androidx.annotation.NonNull;

/**
 * !!! THIS CLASS IS STILL UNDER DEVELOPMENT AND MAY CONTAIN BUGS !!!
 * Gathers performance statistics from a Plot.  Instances of PlotStatistics
 * should never be added to more than one Plot, otherwise the statiscs will
 * be invalid.
 */
public class PlotStatistics implements PlotListener {
    long updateDelayMs;
    long longestRenderMs = 0;
    long shortestRenderMs = 0;
    long lastStart = 0;
    long lastLatency = 0;
    long lastAnnotation;
    long latencySamples = 0;
    long latencySum = 0;
    String annotationString = "";
    private boolean annotatePlotEnabled;

    private Paint paint;
    {
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        resetCounters();
    }

    public PlotStatistics(long updateDelayMs, boolean annotatePlotEnabled) {
        this.updateDelayMs = updateDelayMs;
        this.annotatePlotEnabled = annotatePlotEnabled;
    }

    public void setAnnotatePlotEnabled(boolean enabled) {
        this.annotatePlotEnabled = enabled;
    }

    private void resetCounters() {
        longestRenderMs = 0;
        shortestRenderMs = 999999999;
        latencySamples = 0;
        latencySum = 0;
    }

    private void annotatePlot(Plot source, Canvas canvas) {
        long nowMs = System.currentTimeMillis();
        // throttle the update frequency:
        long msSinceUpdate = (nowMs - lastAnnotation);
        if(msSinceUpdate >= updateDelayMs) {

            float avgLatency = latencySamples > 0 ?  latencySum/latencySamples : 0;
            String overallFPS = String.format("%.2f", latencySamples > 0 ?  (1000f/msSinceUpdate) * latencySamples : 0);
            String potentialFPS = String.format("%.2f", latencySamples > 0 ? 1000f/avgLatency : 0);
            annotationString = "FPS (potential): " + potentialFPS + " FPS (actual): " + overallFPS + " Latency (ms) Avg: " + lastLatency + " \nMin: " + shortestRenderMs +
                    " Max: " + longestRenderMs;
            lastAnnotation = nowMs;
            resetCounters();
        }
        RectF r = source.getDisplayDimensions().canvasRect;
        if(annotatePlotEnabled) {
            canvas.drawText(annotationString, r.centerX(),  r.centerY(), paint);
        }
    }

    @Override
    public void onBeforeDraw(@NonNull Plot source, @NonNull Canvas canvas) {
        lastStart = System.currentTimeMillis();
    }

    @Override
    public void onAfterDraw(@NonNull Plot source, @NonNull Canvas canvas) {
        lastLatency = System.currentTimeMillis() - lastStart;
        if(lastLatency < shortestRenderMs) {
            shortestRenderMs = lastLatency;
        }

        if(lastLatency > longestRenderMs) {
            longestRenderMs = lastLatency;
        }
        latencySum += lastLatency;
        latencySamples++;
        annotatePlot(source, canvas);
    }

    public void setEnabled(boolean isEnabled) {
        this.annotatePlotEnabled = isEnabled;
    }
}
