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

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XYSeries;
import com.androidplot.ui.*;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * Demonstration of the usage of Marker and RectRegion.
 */
public class XYRegionExampleActivity extends Activity {

    private static final float HOME_RUN_DIST = 325;
    private static final int LINE_THICKNESS_DP = 2;
    private static final int POINT_SIZE_DP = 6;
    private XYPlot plot;
    private final Number[] timHits = {105, 252, 220, 350, 12, 250, 353};
    private final Number[] nickHits = {110, 191, 61, 371, 289, 101, 10};
    private final Number[] joeHits = {25, 375, 364, 128, 178, 289, 346};
    private final Number[] jamesHits = {250, 285, 295, 211, 311, 365, 241};
    private LineAndPointFormatter timFormatter;
    private LineAndPointFormatter nickFormatter;
    private LineAndPointFormatter joeFormatter;
    private LineAndPointFormatter jamesFormatter;

    private XYSeries timSeries;
    private XYSeries nickSeries;
    private XYSeries joeSeries;
    private XYSeries jamesSeries;

    private RectRegion shortRegion;
    private RectRegion warmupRegion;
    private RectRegion homeRunRegion;

    //private XYRegionFormatter rf1;
    private XYRegionFormatter shortRegionFormatter;
    private XYRegionFormatter warmupRegionFormatter;
    private XYRegionFormatter homeRunRegionFormatter;
    //private XYRegionFormatter rf5;

    private CheckBox timCB;
    private CheckBox nickCB;
    private CheckBox joeCB;
    private CheckBox jamesCB;

    private CheckBox r2CheckBox;
    private CheckBox r3CheckBox;
    private CheckBox r4CheckBox;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyregion_example);
        plot = (XYPlot) findViewById(R.id.xyRegionExamplePlot);
        timCB = (CheckBox) findViewById(R.id.s1CheckBox);
        timCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked();
            }
        });

        nickCB = (CheckBox) findViewById(R.id.s2CheckBox);
        nickCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS2CheckBoxClicked();
            }
        });

        joeCB = (CheckBox) findViewById(R.id.s3CheckBox);
        joeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS3CheckBoxClicked();
            }
        });

        jamesCB = (CheckBox) findViewById(R.id.s4CheckBox);
        jamesCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS4CheckBoxClicked();
            }
        });



        r2CheckBox = (CheckBox) findViewById(R.id.r2CheckBox);
        r2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckBoxClicked(r2CheckBox, timFormatter, shortRegionFormatter, shortRegion);
            }
        });

        r3CheckBox = (CheckBox) findViewById(R.id.r3CheckBox);
        r3CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckBoxClicked(r3CheckBox, nickFormatter, warmupRegionFormatter, warmupRegion);
            }
        });

        r4CheckBox = (CheckBox) findViewById(R.id.r4CheckBox);
        r4CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckBoxClicked(r4CheckBox, nickFormatter, homeRunRegionFormatter, homeRunRegion);
            }
        });

        seriesSetup();
        markerSetup();
        axisLabelSetup();
        regionSetup();
        makePlotPretty();
    }

    private void onS1CheckBoxClicked() {
        if(timCB.isChecked()) {
            plot.addSeries(timSeries, timFormatter);
            r2CheckBox.setEnabled(true);
        } else {
            plot.removeSeries(timSeries);
            r2CheckBox.setEnabled(false);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked() {
        if(nickCB.isChecked()) {
            plot.addSeries(nickSeries, nickFormatter);
            r3CheckBox.setEnabled(true);
            r4CheckBox.setEnabled(true);
        } else {
            plot.removeSeries(nickSeries);
            r3CheckBox.setEnabled(false);
            r4CheckBox.setEnabled(false);
        }
        plot.redraw();
    }

    private void onS3CheckBoxClicked() {
        if(joeCB.isChecked()) {
            plot.addSeries(joeSeries, joeFormatter);
        } else {
            plot.removeSeries(joeSeries);
        }
        plot.redraw();
    }

    private void onS4CheckBoxClicked() {
        if(jamesCB.isChecked()) {
            plot.addSeries(jamesSeries, jamesFormatter);
        } else {
            plot.removeSeries(jamesSeries);
        }
        plot.redraw();
    }

    /**
     * Processes a check box event
     * @param cb The checkbox event origin
     * @param lpf LineAndPointFormatter with which rr and rf are to be added/removed
     * @param rf The XYRegionFormatter with which rr should be rendered
     * @param rr The RectRegion to add/remove
     */
    private void onCheckBoxClicked(CheckBox cb, LineAndPointFormatter lpf,
                                   XYRegionFormatter rf, RectRegion rr) {
        if(cb.isChecked()) {
            lpf.removeRegion(rr);
        } else {
            lpf.addRegion(rr, rf);
        }
    }

    /**
     * Cleans up the plot's general layout and color scheme
     */
    private void makePlotPretty() {
        // use a 2x5 grid with room for 10 items:
        plot.getLegendWidget().setTableModel(new DynamicTableModel(4, 2));

        // add a semi-transparent black background to the legend
        // so it's easier to see overlaid on top of our plot:
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(40);

        plot.getLegendWidget().setBackgroundPaint(bgPaint);

        // adjust the padding of the legend widget to look a little nicer:
        plot.getLegendWidget().setPadding(5, 5, 5, 5);

        plot.setRangeValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                return new StringBuffer(value + "'");
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });

        plot.setDomainValueFormat(new DecimalFormat("#"));

        plot.getLegendWidget().setWidth(PixelUtils.dpToPix(100), SizeLayoutType.FILL);


        // adjust the legend size so there is enough room
        // to draw the new legend grid:
        //plot.getLegendWidget().getHeightMetric().setLayoutType(SizeLayoutType.ABSOLUTE);
        //plot.getLegendWidget().getWidthMetric().setLayoutType(SizeLayoutType.ABSOLUTE);
        //plot.getLegendWidget().setSize(
        //    new SizeMetrics(70, SizeLayoutType.ABSOLUTE, 80, SizeLayoutType.ABSOLUTE));

        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:

        plot.getLegendWidget().position(
                125,
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                65,
                YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.LEFT_TOP);

        plot.getGraphWidget().setRangeLabelHorizontalOffset(-1);

        // add enough space to ensure range value labels arent cut off on the left/right:
        plot.getGraphWidget().setRangeLabelWidth(25);

        // add enough space to make sure domain value labels arent cut off on the bottom:
        plot.getGraphWidget().setDomainLabelWidth(15);

        plot.getGraphWidget().setDomainLabelVerticalOffset(-6);

        plot.setRangeBoundaries(0, BoundaryMode.FIXED, 500, BoundaryMode.FIXED);
    }

    /**
     * Create 4 XYSeries from the values defined above add add them to the plot.
     * Also add some arbitrary regions.
     */
    private void seriesSetup() {


        // TIM
        timFormatter = new LineAndPointFormatter(
                Color.rgb(100, 25, 20),
                Color.rgb(100, 25, 20),
                null, null);
        timFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        timFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));

        timSeries = new SimpleXYSeries(Arrays.asList(timHits),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Tim");

        plot.addSeries(timSeries, timFormatter);

        // SERIES #2:
        nickFormatter = new LineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(100, 25, 200),
                null, null);
        nickFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        nickFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));



        nickSeries = new SimpleXYSeries(Arrays.asList(nickHits),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Nick");

        plot.addSeries(nickSeries, nickFormatter);

        // SERIES #3:
        joeFormatter = new LineAndPointFormatter(
                Color.rgb(200, 25, 200),
                Color.rgb(200, 25, 200),
                null, null);
        joeFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        joeFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));

        joeSeries = new SimpleXYSeries(Arrays.asList(joeHits),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Joe");

        plot.addSeries(joeSeries, joeFormatter);

        // SERIES #4:
        jamesFormatter = new LineAndPointFormatter(
                Color.rgb(220, 25, 20),
                Color.rgb(220, 25, 20),
                null, null);

        jamesFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        jamesFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));

        jamesSeries = new SimpleXYSeries(Arrays.asList(jamesHits),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"James");
        plot.addSeries(jamesSeries, jamesFormatter);

        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 100);
        //plot.setTicksPerRangeLabel(1);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
    }

    /**
     * Add some color coded regions to our axis labels.
     */
    private void axisLabelSetup() {
        // DOMAIN
        plot.getGraphWidget().addDomainAxisValueLabelRegion(
                Double.NEGATIVE_INFINITY, 2, new AxisValueLabelFormatter(Color.GRAY));
        plot.getGraphWidget().addDomainAxisValueLabelRegion(
                2, Double.POSITIVE_INFINITY, new AxisValueLabelFormatter(Color.WHITE));
        // RANGE
        plot.getGraphWidget().addRangeAxisValueLabelRegion(
                Double.NEGATIVE_INFINITY, HOME_RUN_DIST, new AxisValueLabelFormatter(Color.RED));
        plot.getGraphWidget().addRangeAxisValueLabelRegion(
                HOME_RUN_DIST, Double.POSITIVE_INFINITY, new AxisValueLabelFormatter(Color.GREEN));
    }

    /**
     * Add some markers to our plot.
     */
    private void markerSetup() {

        YValueMarker fenwayLfMarker = new YValueMarker(
                380,                                        // y-val to mark
                "Fenway Park LF Wall",                      // marker label
                new XPositionMetric(                        // object instance to set text positioning on the marker
                        PixelUtils.dpToPix(5),              // 5dp offset
                        XLayoutStyle.ABSOLUTE_FROM_RIGHT),  // offset origin
                Color.BLUE,                                 // line paint color
                Color.BLUE);                                // text paint color

        YValueMarker attRfMarker = new YValueMarker(
                        309,                                        // y-val to mark
                        "ATT Park RF Wall",                         // marker label
                        new XPositionMetric(                        // object instance to set text positioning on the marker
                                PixelUtils.dpToPix(5),              // 5dp offset
                                XLayoutStyle.ABSOLUTE_FROM_RIGHT),  // offset origin
                        Color.CYAN,                                 // line paint color
                        Color.CYAN);                                // text paint color


        fenwayLfMarker.getTextPaint().setTextSize(PixelUtils.dpToPix(14));
        attRfMarker.getTextPaint().setTextSize(PixelUtils.dpToPix(14));

        DashPathEffect dpe = new DashPathEffect(
                        new float[]{PixelUtils.dpToPix(2), PixelUtils.dpToPix(2)}, 0);

        fenwayLfMarker.getLinePaint().setPathEffect(dpe);
        attRfMarker.getLinePaint().setPathEffect(dpe);

        plot.addMarker(fenwayLfMarker);
        plot.addMarker(attRfMarker);
    }

    /**
     * Add some fill regions to our series data
     */
    private void regionSetup() {


        // and another region:
        shortRegionFormatter = new XYRegionFormatter(Color.RED);
        shortRegionFormatter.getPaint().setAlpha(75);
        shortRegion = new RectRegion(2, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, HOME_RUN_DIST, "Short");
        timFormatter.addRegion(shortRegion, shortRegionFormatter);
        nickFormatter.addRegion(shortRegion, shortRegionFormatter);
        joeFormatter.addRegion(shortRegion, shortRegionFormatter);
        jamesFormatter.addRegion(shortRegion, shortRegionFormatter);

        // the next three regions are horizontal regions with minY/maxY
        // set to negative and positive infinity respectively.
        warmupRegionFormatter = new XYRegionFormatter(Color.WHITE);
        warmupRegionFormatter.getPaint().setAlpha(75);

        warmupRegion = new RectRegion(0, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "Warmup");
        timFormatter.addRegion(warmupRegion, warmupRegionFormatter);
        nickFormatter.addRegion(warmupRegion, warmupRegionFormatter);
        joeFormatter.addRegion(warmupRegion, warmupRegionFormatter);
        jamesFormatter.addRegion(warmupRegion, warmupRegionFormatter);

        homeRunRegionFormatter = new XYRegionFormatter(Color.GREEN);
        homeRunRegionFormatter.getPaint().setAlpha(75);

        homeRunRegion = new RectRegion(2, Double.POSITIVE_INFINITY, HOME_RUN_DIST, Double.POSITIVE_INFINITY, "H. Run");
        timFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);
        nickFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);
        joeFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);
        jamesFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);

        nickFormatter.setFillDirection(FillDirection.RANGE_ORIGIN);
    }
}