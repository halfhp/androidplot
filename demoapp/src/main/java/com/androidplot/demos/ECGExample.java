// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.*;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * A real-time plot of an asynchronously updated ECG model.  Three things to pay attention to:
 * 1 - The model is updated by its own background thread, as is typical of signal inputs.
 * 2 - The render loop is driven by a {@link Redrawer} thread rather than by calling
 * {@link Plot#redraw()} from whatever updates the model, which would severely degrade performance.
 * 3 - The plot renders on a background thread via a config attr in R.layout.ecg_example.xml, so the
 * rest of the app stays responsive during rendering.
 */
public class ECGExample extends Activity {

    private ECGModel ecgSeries;
    private Redrawer redrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecg_example);
        XYPlot plot = findViewById(R.id.plot);

        ecgSeries = new ECGModel(2000, 200);

        FadeFormatter formatter = new FadeFormatter(2000);
        formatter.setLegendIconEnabled(false);
        plot.addSeries(ecgSeries, formatter);
        plot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, 2000, BoundaryMode.FIXED);

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);

        // start generating ecg data in the background:
        ecgSeries.start(new WeakReference<>(plot.getRenderer(AdvancedLineAndPointRenderer.class)));

        // redraw at up to 30hz; started in onResume
        redrawer = new Redrawer(plot, 30, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        redrawer.start();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        ecgSeries.stop();
        super.onDestroy();
    }

    // AdvancedLineAndPointRenderer.Formatter lets us vary the paint per point; here alpha fades
    // with distance from the newest sample
    private static class FadeFormatter extends AdvancedLineAndPointRenderer.Formatter {

        private final int trailSize;

        FadeFormatter(int trailSize) {
            this.trailSize = trailSize;
        }

        @Override
        public Paint getLinePaint(int thisIndex, int latestIndex, int seriesSize) {
            // offset from the latest index:
            int offset;
            if(thisIndex > latestIndex) {
                offset = latestIndex + (seriesSize - thisIndex);
            } else {
                offset =  latestIndex - thisIndex;
            }

            float scale = 255f / trailSize;
            int alpha = (int) (255 - (offset * scale));
            getLinePaint().setAlpha(alpha > 0 ? alpha : 0);
            return getLinePaint();
        }
    }

    /**
     * Primitive simulation of some kind of signal.  For this example,
     * we'll pretend its an ecg.  This class represents the data as a circular buffer;
     * data is added sequentially from left to right.  When the end of the buffer is reached,
     * i is reset back to 0 and simulated sampling continues.
     */
    private static class ECGModel implements XYSeries {

        private final Number[] data;
        private final long delayMs;
        private final int blipInterval;
        private final Thread thread;
        private volatile boolean keepRunning;
        private int latestIndex;

        private WeakReference<AdvancedLineAndPointRenderer> rendererRef;

        /**
         *
         * @param size Sample size contained within this model
         * @param updateFreqHz Frequency at which new samples are added to the model
         */
        ECGModel(int size, int updateFreqHz) {
            data = new Number[size];
            Arrays.fill(data, 0);

            // translate hz into delay (ms):
            delayMs = 1000 / updateFreqHz;

            // add 7 "blips" into the signal:
            blipInterval = size / 7;

            thread = new Thread(this::generateSamples);
        }

        void start(WeakReference<AdvancedLineAndPointRenderer> rendererRef) {
            this.rendererRef = rendererRef;
            keepRunning = true;
            thread.start();
        }

        void stop() {
            keepRunning = false;
        }

        private void generateSamples() {
            try {
                while (keepRunning) {
                    if (latestIndex >= data.length) {
                        latestIndex = 0;
                    }

                    // generate some random data:
                    if (latestIndex % blipInterval == 0) {
                        // insert a "blip" to simulate a heartbeat:
                        data[latestIndex] = (Math.random() * 10) + 3;
                    } else {
                        // insert a random sample:
                        data[latestIndex] = Math.random() * 2;
                    }

                    if(latestIndex < data.length - 1) {
                        // null out the point immediately following i, to disable
                        // connecting i and i+1 with a line:
                        data[latestIndex +1] = null;
                    }

                    if(rendererRef.get() != null) {
                        rendererRef.get().setLatestIndex(latestIndex);
                        Thread.sleep(delayMs);
                    } else {
                        keepRunning = false;
                    }
                    latestIndex++;
                }
            } catch (InterruptedException e) {
                keepRunning = false;
            }
        }

        @Override
        public int size() {
            return data.length;
        }

        @Override
        public Number getX(int index) {
            return index;
        }

        @Override
        public Number getY(int index) {
            return data[index];
        }

        @Override
        public String getTitle() {
            return "Signal";
        }
    }
}
