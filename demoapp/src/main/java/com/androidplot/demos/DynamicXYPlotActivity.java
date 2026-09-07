// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.*;

import java.text.DecimalFormat;

public class DynamicXYPlotActivity extends Activity {

    private XYPlot plot;
    private Redrawer redrawer;
    private SampleDynamicXYDatasource data;
    private Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_xyplot_example);
        plot = findViewById(R.id.dynamicXYPlot);

        data = new SampleDynamicXYDatasource();
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Sine 1");
        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Sine 2");

        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                Color.rgb(0, 200, 0), null, null, null);
        formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        formatter1.getLinePaint().setStrokeWidth(10);
        plot.addSeries(sine1Series, formatter1);

        LineAndPointFormatter formatter2 = new LineAndPointFormatter(
                Color.rgb(0, 0, 200), null, null, null);
        formatter2.getLinePaint().setStrokeWidth(10);
        formatter2.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        plot.addSeries(sine2Series, formatter2);

        // thin out domain tick labels so they dont overlap each other:
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 5);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 10);

        // only display whole numbers in domain labels
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("0"));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("###.#"));

        // freeze the range so the plot doesn't auto-scale as the amplitude changes
        plot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraph().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraph().getRangeGridLinePaint().setPathEffect(dashFx);

        // redraw the plot from a background thread at up to 100hz:
        redrawer = new Redrawer(plot, 100, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // kick off the data generating thread:
        thread = new Thread(data);
        thread.start();
        redrawer.start();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        data.stopThread();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }

    // custom XYSeries backed by a live model
    private static class SampleDynamicSeries implements XYSeries {
        private final SampleDynamicXYDatasource datasource;
        private final int seriesIndex;
        private final String title;

        SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.size();
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }
    }

    private static class SampleDynamicXYDatasource implements Runnable {

        private static final double FREQUENCY = 5; // larger is lower frequency
        private static final int MAX_AMP_SEED = 100;
        private static final int MIN_AMP_SEED = 10;
        private static final int AMP_STEP = 1;
        static final int SINE1 = 0;
        static final int SINE2 = 1;
        private static final int SAMPLE_SIZE = 31;
        private int phase = 0;
        private int sinAmp = 1;
        private volatile boolean keepRunning = false;

        void stopThread() {
            keepRunning = false;
        }

        @Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {
                    Thread.sleep(10); // decrease or remove to speed up the refresh rate.
                    phase++;
                    if (sinAmp >= MAX_AMP_SEED) {
                        isRising = false;
                    } else if (sinAmp <= MIN_AMP_SEED) {
                        isRising = true;
                    }

                    if (isRising) {
                        sinAmp += AMP_STEP;
                    } else {
                        sinAmp -= AMP_STEP;
                    }
                }
            } catch (InterruptedException e) {
                // interrupted: let the loop end
            }
        }

        int size() {
            return SAMPLE_SIZE;
        }

        Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            double angle = (index + (phase))/FREQUENCY;
            double amp = sinAmp * Math.sin(angle);
            switch (series) {
                case SINE1:
                    return amp;
                case SINE2:
                    return -amp;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
