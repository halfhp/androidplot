// SPDX-License-Identifier: Apache-2.0

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
    private static final int NUM_GRIDLINES = 5;
    private XYPlot plot;
    private PanZoom panZoom;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_zoom_example);
        plot = findViewById(R.id.plot);

        // set a fixed origin and a "by-value" step mode so that grid lines will
        // move dynamically with the data when the users pans or zooms:
        plot.setUserDomainOrigin(0);
        plot.setUserRangeOrigin(0);

        // StepModelFit picks a tick increment from the list that best fits NUM_GRIDLINES
        double[] domainIncrements = new double[]{10,50,100,500};
        double[] rangeIncrements = new double[]{1,5,10,20,50,100};
        plot.setDomainStepModel(new StepModelFit(plot.getBounds().getxRegion(),domainIncrements,NUM_GRIDLINES));
        plot.setRangeStepModel( new StepModelFit(plot.getBounds().getyRegion(),rangeIncrements,NUM_GRIDLINES));

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

        // ZoomEstimator swaps SampledXYSeries levels as the visible range changes
        plot.getRegistry().setEstimator(new ZoomEstimator());

        generateAndAddSeries(625, new LineAndPointFormatter(Color.rgb(50, 0, 0), null,
                Color.rgb(100, 0, 0), null));
        generateAndAddSeries(125, new LineAndPointFormatter(Color.rgb(50, 50, 0), null,
                Color.rgb(100, 100, 0), null));
        generateAndAddSeries(25, new LineAndPointFormatter(Color.rgb(0, 50, 0), null,
                Color.rgb(0, 100, 0), null));
        generateAndAddSeries(5, new LineAndPointFormatter(Color.rgb(0, 0, 0), null,
                Color.rgb(0, 0, 150), null));
        reset();

        findViewById(R.id.resetButton).setOnClickListener(v -> reset());
        initSpinners(findViewById(R.id.pan_spinner), findViewById(R.id.zoom_spinner));
    }

    private void reset() {
        // start fully zoomed out to the outer limits; pinch to zoom in, then drag to pan.
        // (a window wider than the limits, as this example once used, can never be panned.)
        plot.setDomainBoundaries(0, SERIES_SIZE, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 1000, BoundaryMode.FIXED);
        plot.redraw();
    }

    // (optional) save the current pan/zoom state
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("pan-zoom-state", panZoom.getState());
    }

    // (optional) restore the previously saved pan/zoom state
    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        PanZoom.State state = (PanZoom.State) bundle.getSerializable("pan-zoom-state");
        panZoom.setState(state);
        plot.redraw();
    }

    private void generateAndAddSeries(int max, LineAndPointFormatter formatter) {
        FixedSizeEditableXYSeries series = new FixedSizeEditableXYSeries("s" + max, SERIES_SIZE);
        Random r = new Random();
        for(int i = 0; i < SERIES_SIZE; i++) {
            series.setX(i, i);
            series.setY(r.nextInt(max), i);
        }

        // SampledXYSeries builds down-sampled copies (ratio 2) until a level has <= 100 points
        SampledXYSeries sampledSeries =
                new SampledXYSeries(series, OrderedXYSeries.XOrder.ASCENDING, 2,100);
        plot.addSeries(sampledSeries, formatter);
    }

    private void initSpinners(Spinner panSpinner, Spinner zoomSpinner) {
        panSpinner.setAdapter(
                new ArrayAdapter<>(this, R.layout.spinner_item, PanZoom.Pan.values()));
        panSpinner.setSelection(panZoom.getPan().ordinal());
        panSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panZoom.setPan(PanZoom.Pan.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
