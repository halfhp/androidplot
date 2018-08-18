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

package com.androidplot.demos;

import java.text.DecimalFormat;
import java.util.Random;

import android.app.*;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.androidplot.Plot;
import com.androidplot.xy.*;

public class TouchZoomExampleActivity extends Activity {
    private static final int SERIES_SIZE = 3000;
    private static final int SERIES_ALPHA = 255;
    private static final int NUM_GRIDLINES = 5;
    private XYPlot plot;
    private PanZoom panZoom;
    private Button resetButton;
    private Spinner panSpinner;
    private Spinner zoomSpinner;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_zoom_example);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        plot = findViewById(R.id.plot);

        // set a fixed origin and a "by-value" step mode so that grid lines will
        // move dynamically with the data when the users pans or zooms:
        plot.setUserDomainOrigin(0);
        plot.setUserRangeOrigin(0);

        // predefine the stepping of both axis
        // increment will be chosen from list to best fit NUM_GRIDLINES grid lines
        double[] inc_domain = new double[]{10,50,100,500};
        double[] inc_range = new double[]{1,5,10,20,50,100};
        plot.setDomainStepModel(new StepModelFit(plot.getBounds().getxRegion(),inc_domain,NUM_GRIDLINES));
        plot.setRangeStepModel( new StepModelFit(plot.getBounds().getyRegion(),inc_range,NUM_GRIDLINES));


        panSpinner = findViewById(R.id.pan_spinner);
        zoomSpinner = findViewById(R.id.zoom_spinner);
        plot.getGraph().setLinesPerRangeLabel(2);
        plot.getGraph().setLinesPerDomainLabel(2);
        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#####"));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#####.#"));

        plot.setRangeLabel("");
        plot.setDomainLabel("");

        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

        panZoom = PanZoom.attach(plot, PanZoom.Pan.BOTH, PanZoom.Zoom.STRETCH_BOTH, PanZoom.ZoomLimit.MIN_TICKS);
        plot.getOuterLimits().set(0, 3000, 0, 1000);
        initSpinners();

        // enable autoselect of sampling level based on visible boundaries:
        plot.getRegistry().setEstimator(new ZoomEstimator());

        generateSeriesData();
        reset();
    }

    private void reset() {
        plot.setDomainBoundaries(0, 10000, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 1000, BoundaryMode.FIXED);
        plot.redraw();
    }

    private ProgressDialog progress;

    private void generateSeriesData() {
        progress = ProgressDialog.show(this, "Loading", "Please wait...", true);
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                generateAndAddSeries(625, new LineAndPointFormatter(Color.rgb(50, 0, 0), null,
                        Color.argb(SERIES_ALPHA, 100, 0, 0), null));
                generateAndAddSeries(125, new LineAndPointFormatter(Color.rgb(50, 50, 0), null,
                        Color.argb(SERIES_ALPHA, 100, 100, 0), null));
                generateAndAddSeries(25, new LineAndPointFormatter(Color.rgb(0, 50, 0), null,
                        Color.argb(SERIES_ALPHA, 0, 100, 0), null));
                generateAndAddSeries(5, new LineAndPointFormatter(Color.rgb(0, 0, 0), null,
                        Color.argb(SERIES_ALPHA, 0, 0, 150), null));
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                progress.dismiss();
                plot.redraw();
            }
        }.execute();
    }

    private void generateAndAddSeries(int max, LineAndPointFormatter formatter) {
        final FixedSizeEditableXYSeries series = new FixedSizeEditableXYSeries("s" + max, SERIES_SIZE);
        Random r = new Random();
        for(int i = 0; i < SERIES_SIZE; i++) {
            series.setX(i, i);
            series.setY(r.nextInt(max), i);
        }

        // wrap our series in a SampledXYSeries with a threshold of 1000.
        final SampledXYSeries sampledSeries =
                new SampledXYSeries(series, OrderedXYSeries.XOrder.ASCENDING, 2,100);
        plot.addSeries(sampledSeries, formatter);
    }

    private void initSpinners() {
        panSpinner.setAdapter(
                new ArrayAdapter<>(this, R.layout.spinner_item, PanZoom.Pan.values()));
        panSpinner.setSelection(panZoom.getPan().ordinal());
        panSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panZoom.setPan(PanZoom.Pan.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to do
            }
        });


        zoomSpinner.setAdapter(
                new ArrayAdapter<>(this, R.layout.spinner_item, PanZoom.Zoom.values()));
        zoomSpinner.setSelection(panZoom.getZoom().ordinal());
        zoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panZoom.setZoom(PanZoom.Zoom.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to do
            }
        });
    }

    // (optional) save the current pan/zoom state
    @Override
    public void onSaveInstanceState(Bundle bundle) {
         bundle.putSerializable("pan-zoom-state", panZoom.getState());
    }

    // (optional) restore the previously saved pan/zoom state
    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        PanZoom.State state = (PanZoom.State) bundle.getSerializable("pan-zoom-state");
        panZoom.setState(state);
        plot.redraw();
    }
}

