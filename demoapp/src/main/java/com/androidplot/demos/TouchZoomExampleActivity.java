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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.androidplot.Plot;
import com.androidplot.xy.*;

/***********************************
 * @author David Buezas (david.buezas at gmail.com)
 * Feel free to copy, modify and use the source as it fits you.
 * 09/27/2012 nfellows - updated for 0.5.1 and made a few simplifications
 */
public class TouchZoomExampleActivity extends Activity {
    private static final int SERIES_SIZE = 200;
    private static final int SERIES_ALPHA = 255;
    private XYPlot plot;
    private PanZoom panZoom;
    private Button resetButton;
    private Spinner panSpinner;
    private Spinner zoomSpinner;
    private SimpleXYSeries[] series = null;
    private PointF minXY;
    private PointF maxXY;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_zoom_example);
        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minXY.x = series[0].getX(0).floatValue();
                maxXY.x = series[3].getX(series[3].size() - 1).floatValue();
                plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
                plot.redraw();
            }
        });
        plot = (XYPlot) findViewById(R.id.plot);

        // set a fixed origin and a "by-value" step mode so that grid lines will
        // move dynamically with the data when the users pans or zooms:
        plot.setUserDomainOrigin(0);
        plot.setUserRangeOrigin(0);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 20);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 10);

        panSpinner = (Spinner) findViewById(R.id.pan_spinner);
        zoomSpinner = (Spinner) findViewById(R.id.zoom_spinner);
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
        series = new SimpleXYSeries[4];
        int scale = 1;
        for (int i = 0; i < 4; i++, scale *= 5) {
            series[i] = new SimpleXYSeries("S" + i);
            populateSeries(series[i], scale);
        }
        plot.addSeries(series[3],
                new LineAndPointFormatter(Color.rgb(50, 0, 0), null,
                        Color.argb(SERIES_ALPHA, 100, 0, 0), null));
        plot.addSeries(series[2],
                new LineAndPointFormatter(Color.rgb(50, 50, 0), null,
                        Color.argb(SERIES_ALPHA, 100, 100, 0), null));
        plot.addSeries(series[1],
                new LineAndPointFormatter(Color.rgb(0, 50, 0), null,
                        Color.argb(SERIES_ALPHA, 0, 100, 0), null));
        plot.addSeries(series[0],
                new LineAndPointFormatter(Color.rgb(0, 0, 0), null,
                        Color.argb(SERIES_ALPHA, 0, 0, 150), null));
        plot.redraw();

        // record min/max for the reset button:
        plot.calculateMinMaxVals();
        minXY = new PointF(plot.getCalculatedMinX().floatValue(),
                plot.getCalculatedMinY().floatValue());
        maxXY = new PointF(plot.getCalculatedMaxX().floatValue(),
                plot.getCalculatedMaxY().floatValue());

        // enable pan/zoom behavior:
        panZoom = PanZoom.attach(plot);
        initSpinners();
    }

    private void populateSeries(SimpleXYSeries series, int max) {
        Random r = new Random();
        for(int i = 0; i < SERIES_SIZE; i++) {
            series.addLast(i, r.nextInt(max));
        }
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
}

