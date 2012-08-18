/*
 * Copyright 2012 AndroidPlot.com
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

package com.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.LinkedList;

// Monitor the phone's orientation sensor and plot the resulting azimuth pitch and roll values.
// See: http://developer.android.com/reference/android/hardware/SensorEvent.html
public class MyActivity extends Activity implements SensorEventListener
{

    /**
     * A simple formatter to convert bar indexes into sensor names.
     */
    private class APRIndexFormat extends Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Number num = (Number) obj;

            // using num.intValue() will floor the value, so we add 0.5 to round instead:
            int roundNum = (int) (num.floatValue() + 0.5f);
            switch(roundNum) {
                case 0:
                    toAppendTo.append("Azimuth");
                    break;
                case 1:
                    toAppendTo.append("Pitch");
                    break;
                case 2:
                    toAppendTo.append("Roll");
                    break;
                default:
                    toAppendTo.append("Unknown");
            }
            return toAppendTo;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;  // We don't use this so just return null for now.
        }
    }

    private static final int HISTORY_SIZE = 30;            // number of points to plot in history
    private SensorManager sensorMgr = null;
    private Sensor orSensor = null;

    private XYPlot aprLevelsPlot = null;
    private XYPlot aprHistoryPlot = null;
    private SimpleXYSeries aprLevelsSeries = null;
    private SimpleXYSeries azimuthHistorySeries = null;
    private SimpleXYSeries pitchHistorySeries = null;
    private SimpleXYSeries rollHistorySeries = null;
    private LinkedList<Number> azimuthHistory;
    private LinkedList<Number> pitchHistory;
    private LinkedList<Number> rollHistory;

    {
        azimuthHistory = new LinkedList<Number>();
        pitchHistory = new LinkedList<Number>();
        rollHistory = new LinkedList<Number>();

        aprLevelsSeries = new SimpleXYSeries("APR Levels");
        azimuthHistorySeries = new SimpleXYSeries("Azimuth");
        pitchHistorySeries = new SimpleXYSeries("Pitch");
        rollHistorySeries = new SimpleXYSeries("Roll");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // setup the APR Levels plot:
        aprLevelsPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);
        aprLevelsPlot.addSeries(aprLevelsSeries, BarRenderer.class, new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
        aprLevelsPlot.setDomainStepValue(3);
        aprLevelsPlot.setTicksPerRangeLabel(3);

        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        aprLevelsPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);

        // use our custom domain value formatter:
        aprLevelsPlot.setDomainValueFormat(new APRIndexFormat());

        // update our domain and range axis labels:
        aprLevelsPlot.setDomainLabel("Axis");
        aprLevelsPlot.getDomainLabelWidget().pack();
        aprLevelsPlot.setRangeLabel("Angle (Degs)");
        aprLevelsPlot.getRangeLabelWidget().pack();

        aprLevelsPlot.setGridPadding(15, 0, 15, 0);
        aprLevelsPlot.disableAllMarkup();


        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
        aprHistoryPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        aprHistoryPlot.addSeries(azimuthHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null));
        aprHistoryPlot.addSeries(pitchHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.BLACK, null));
        aprHistoryPlot.addSeries(rollHistorySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null));
        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Sample Index");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Angle (Degs)");
        aprHistoryPlot.getRangeLabelWidget().pack();
        aprHistoryPlot.disableAllMarkup();

        // get a ref to the BarRenderer so we can make some changes to it:
        BarRenderer barRenderer = (BarRenderer) aprLevelsPlot.getRenderer(BarRenderer.class);
        if(barRenderer != null) {
            // make our bars a little thicker than the default so they can be seen better:
            barRenderer.setBarWidth(25);
        }

        // register for orientation sensor events:
        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }
        }

        // if we can't access the orientation sensor then exit:
        if (orSensor == null) {
            System.out.println("Failed to attach to orSensor.");
            cleanup();
        }

        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);

    }

    private void cleanup() {
        // aunregister with the orientation sensor before exiting:
        sensorMgr.unregisterListener(this);
        finish();
    }


    // Called whenever a new orSensor reading is taken.
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {

        // update instantaneous data:
        Number[] series1Numbers = {sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]};
        aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        // get rid the oldest sample in history:
        if (rollHistory.size() > HISTORY_SIZE) {
            rollHistory.removeFirst();
            pitchHistory.removeFirst();
            azimuthHistory.removeFirst();
        }

        // add the latest history sample:
        azimuthHistory.addLast(sensorEvent.values[0]);
        pitchHistory.addLast(sensorEvent.values[1]);
        rollHistory.addLast(sensorEvent.values[2]);

        // update the plot with the updated history Lists:
        azimuthHistorySeries.setModel(azimuthHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        pitchHistorySeries.setModel(pitchHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        rollHistorySeries.setModel(rollHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        // redraw the Plots:
        aprLevelsPlot.redraw();
        aprHistoryPlot.redraw();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not interested in this event
    }
}
