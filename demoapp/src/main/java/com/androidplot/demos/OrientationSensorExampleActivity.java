// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;
import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.PlotStatistics;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 * Monitors the phone's orientation sensor and plots the resulting azimuth, pitch and roll values.
 * See: http://developer.android.com/reference/android/hardware/SensorEvent.html
 */
public class OrientationSensorExampleActivity extends Activity implements SensorEventListener {

    private static final String TAG = OrientationSensorExampleActivity.class.getSimpleName();
    private static final int HISTORY_SIZE = 1000;

    private XYPlot aprLevelsPlot;
    private XYPlot aprHistoryPlot;

    private SimpleXYSeries aLvlSeries;
    private SimpleXYSeries pLvlSeries;
    private SimpleXYSeries rLvlSeries;
    private SimpleXYSeries azimuthHistorySeries;
    private SimpleXYSeries pitchHistorySeries;
    private SimpleXYSeries rollHistorySeries;

    private Redrawer redrawer;

    private SensorManager sensorMgr;
    private Sensor orSensor;
    // scratch space for orientationDegrees(), reused so nothing is allocated per sensor event
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orientation_sensor_example);
        aprLevelsPlot = findViewById(R.id.aprLevelsPlot);
        aprHistoryPlot = findViewById(R.id.aprHistoryPlot);

        configureLevelsPlot();
        configureHistoryPlot();

        // PlotStatistics is an Androidplot utility that can annotate a plot with its frame rate:
        PlotStatistics levelStats = new PlotStatistics(1000, false);
        PlotStatistics histStats = new PlotStatistics(1000, false);
        aprLevelsPlot.addListener(levelStats);
        aprHistoryPlot.addListener(histStats);
        CheckBox showFpsCb = findViewById(R.id.showFpsCb);
        showFpsCb.setOnCheckedChangeListener((cb, checked) -> {
            levelStats.setAnnotatePlotEnabled(checked);
            histStats.setAnnotatePlotEnabled(checked);
        });

        // redraw both plots from a background thread at up to 100hz:
        redrawer = new Redrawer(Arrays.<Plot>asList(aprHistoryPlot, aprLevelsPlot), 100, false);

        // prefer the fused rotation vector; fall back to the legacy orientation sensor.
        // the listener is registered in onResume and unregistered in onPause.
        sensorMgr = getSystemService(SensorManager.class);
        orSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (orSensor == null) {
            orSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if (orSensor == null) {
            Log.w(TAG, "No orientation sensor available.");
            Toast.makeText(this, "This device has no orientation sensor", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);
        redrawer.start();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        sensorMgr.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }

    // one bar per reading, so the sensor's latest values can be compared at a glance
    private void configureLevelsPlot() {
        aLvlSeries = new SimpleXYSeries("A");
        pLvlSeries = new SimpleXYSeries("P");
        rLvlSeries = new SimpleXYSeries("R");

        aprLevelsPlot.addSeries(aLvlSeries,
                new BarFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 80, 0)));
        aprLevelsPlot.addSeries(pLvlSeries,
                new BarFormatter(Color.rgb(200, 0, 0), Color.rgb(0, 80, 0)));
        aprLevelsPlot.addSeries(rLvlSeries,
                new BarFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 80, 0)));

        aprLevelsPlot.setDomainBoundaries(-1, 1, BoundaryMode.FIXED);
        aprLevelsPlot.setDomainStepValue(3);
        aprLevelsPlot.setLinesPerRangeLabel(3);

        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        aprLevelsPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);

        aprLevelsPlot.setDomainLabel("");
        aprLevelsPlot.getDomainTitle().pack();
        aprLevelsPlot.setRangeLabel("Angle (Degs)");
        aprLevelsPlot.getRangeTitle().pack();
        aprLevelsPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));

        // make our bars a little thicker than the default so they can be seen better:
        aprLevelsPlot.getRenderer(BarRenderer.class).setBarGroupWidth(
                BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(18));
    }

    // a scrolling window of the last HISTORY_SIZE readings
    private void configureHistoryPlot() {
        azimuthHistorySeries = new SimpleXYSeries("Az.");
        azimuthHistorySeries.useImplicitXVals();
        pitchHistorySeries = new SimpleXYSeries("Pitch");
        pitchHistorySeries.useImplicitXVals();
        rollHistorySeries = new SimpleXYSeries("Roll");
        rollHistorySeries.useImplicitXVals();

        aprHistoryPlot.addSeries(azimuthHistorySeries,
                new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        aprHistoryPlot.addSeries(pitchHistorySeries,
                new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
        aprHistoryPlot.addSeries(rollHistorySeries,
                new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));

        aprHistoryPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, HISTORY_SIZE / 10);
        aprHistoryPlot.setLinesPerRangeLabel(3);

        aprHistoryPlot.setDomainLabel("Sample Index");
        aprHistoryPlot.getDomainTitle().pack();
        aprHistoryPlot.setRangeLabel("Angle (Degs)");
        aprHistoryPlot.getRangeTitle().pack();
        aprHistoryPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));
        aprHistoryPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#"));
    }

    // sensor plumbing

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        float[] apr = sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
                ? orientationDegrees(sensorEvent) : sensorEvent.values;

        // update level data:
        aLvlSeries.setModel(Collections.singletonList((Number) apr[0]),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        pLvlSeries.setModel(Collections.singletonList((Number) apr[1]),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        rLvlSeries.setModel(Collections.singletonList((Number) apr[2]),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        // get rid the oldest sample in history:
        if (rollHistorySeries.size() > HISTORY_SIZE) {
            rollHistorySeries.removeFirst();
            pitchHistorySeries.removeFirst();
            azimuthHistorySeries.removeFirst();
        }

        // add the latest history sample:
        azimuthHistorySeries.addLast(null, apr[0]);
        pitchHistorySeries.addLast(null, apr[1]);
        rollHistorySeries.addLast(null, apr[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not interested in this event
    }

    // The rotation vector is the modern replacement for the legacy orientation sensor; this turns
    // it into the same azimuth/pitch/roll degrees. It is the only non-Androidplot logic in this file.
    private float[] orientationDegrees(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);
        for (int i = 0; i < orientation.length; i++) {
            orientation[i] = (float) Math.toDegrees(orientation[i]);
        }
        if (orientation[0] < 0) {
            orientation[0] += 360; // azimuth: -180..180 -> 0..360, as TYPE_ORIENTATION reports it
        }
        return orientation;
    }
}
