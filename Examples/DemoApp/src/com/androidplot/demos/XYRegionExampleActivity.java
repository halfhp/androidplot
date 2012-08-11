/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.demos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.*;
import com.androidplot.util.PaintUtils;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * Demonstrates the usage of RectRegion.
 */
public class XYRegionExampleActivity extends Activity {
    private static final int FONT_LABEL_SIZE = 13;
    private XYPlot plot;
    private final Number[] series1Numbers = {1, 4, 9, 9, 5, 2, 12};
    private final Number[] series2Numbers = {5, 2, 3, 2, 17, 9, 1};
    private final Number[] series3Numbers = {8, 9, 2, 1, 2, 1, 2};
    private final Number[] series4Numbers = {2, 1, 1, 11, 6, 5, 7};
    private LineAndPointFormatter lpFormatter1;
    private LineAndPointFormatter lpFormatter2;
    private LineAndPointFormatter lpFormatter3;
    private LineAndPointFormatter lpFormatter4;

    private XYSeries s1;
    private XYSeries s2;
    private XYSeries s3;
    private XYSeries s4;

    private CheckBox s1CheckBox;
    private CheckBox s2CheckBox;
    private CheckBox s3CheckBox;
    private CheckBox s4CheckBox;

    private CheckBox r1CheckBox;
    private CheckBox r2CheckBox;
    private CheckBox r3CheckBox;
    private CheckBox r4CheckBox;
    private CheckBox r5CheckBox;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyregion_example);
        plot = (XYPlot) findViewById(R.id.xyRegionExamplePlot);
        s1CheckBox = (CheckBox) findViewById(R.id.s1CheckBox);
        s1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked();
            }
        });

        s2CheckBox = (CheckBox) findViewById(R.id.s2CheckBox);
        s2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS2CheckBoxClicked();
            }
        });

        s3CheckBox = (CheckBox) findViewById(R.id.s3CheckBox);
        s3CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS3CheckBoxClicked();
            }
        });

        s4CheckBox = (CheckBox) findViewById(R.id.s4CheckBox);
        s4CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS4CheckBoxClicked();
            }
        });

        r1CheckBox = (CheckBox) findViewById(R.id.r1CheckBox);
        r2CheckBox = (CheckBox) findViewById(R.id.r2CheckBox);
        r3CheckBox = (CheckBox) findViewById(R.id.r3CheckBox);
        r4CheckBox = (CheckBox) findViewById(R.id.r4CheckBox);
        r5CheckBox = (CheckBox) findViewById(R.id.r5CheckBox);

        seriesSetup();
        markerSetup();
        axisLabelSetup();
        regionSetup();
        makePlotPretty();
        plot.disableAllMarkup();
    }

    private void onS1CheckBoxClicked() {
        if(s1CheckBox.isChecked()) {
            showS1();
            r1CheckBox.setEnabled(true);
            r2CheckBox.setEnabled(true);
        } else {
            plot.removeSeries(s1);
            r1CheckBox.setEnabled(false);
            r2CheckBox.setEnabled(false);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked() {
        if(s2CheckBox.isChecked()) {
            showS2();
            r3CheckBox.setEnabled(true);
            r4CheckBox.setEnabled(true);
            r5CheckBox.setEnabled(true);
        } else {
            plot.removeSeries(s2);
            r3CheckBox.setEnabled(false);
            r4CheckBox.setEnabled(false);
            r5CheckBox.setEnabled(false);
        }
        plot.redraw();
    }

    private void onS3CheckBoxClicked() {
        if(s3CheckBox.isChecked()) {
            showS3();
        } else {
            plot.removeSeries(s3);
        }
        plot.redraw();
    }

    private void onS4CheckBoxClicked() {
        if(s4CheckBox.isChecked()) {
            showS4();
        } else {
            plot.removeSeries(s4);
        }
        plot.redraw();
    }

    /**
     * Cleans up the plot's general layout and color scheme
     */
    private void makePlotPretty() {
        // use a 2x5 grid with room for 10 items:
        plot.getLegendWidget().setTableModel(new DynamicTableModel(2, 5));

        // add a semi-transparent black background to the legend
        // so it's easier to see overlaid on top of our plot:
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(140);

        plot.getLegendWidget().setBackgroundPaint(bgPaint);

        // adjust the padding of the legend widget to look a little nicer:
        plot.getLegendWidget().setPadding(10, 1, 1, 1);

        // adjust the legend size so there is enough room
        // to draw the new legend grid:
        plot.getLegendWidget().setSize(
            new SizeMetrics(70, SizeLayoutType.ABSOLUTE, 80, SizeLayoutType.ABSOLUTE));

        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:
        plot.position(
                plot.getLegendWidget(),
                20,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                35,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.RIGHT_BOTTOM);

        // make our domain and range labels invisible:
        plot.getDomainLabelWidget().setVisible(false);
        plot.getRangeLabelWidget().setVisible(false);

        plot.getGraphWidget().setRangeLabelHorizontalOffset(-1);

        // add enough space to ensure range value labels arent cut off on the left/right:
        plot.getGraphWidget().setRangeLabelWidth(25);

        // add enough space to make sure domain value labels arent cut off on the bottom:
        plot.getGraphWidget().setDomainLabelWidth(15);

        plot.getGraphWidget().setDomainLabelVerticalOffset(-6);
        plot.setBackgroundPaint(null);
        plot.getGraphWidget().setBackgroundPaint(null);
        plot.setBorderPaint(null);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        Context ctx = getApplicationContext();
        PaintUtils.setFontSizeDp(ctx,
            plot.getDomainLabelWidget().getLabelPaint(), GlobalDefs.PLOT_DOMAIN_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getRangeLabelWidget().getLabelPaint(), GlobalDefs.PLOT_RANGE_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getGraphWidget().getRangeOriginLabelPaint(), GlobalDefs.PLOT_TICK_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getGraphWidget().getRangeLabelPaint(), GlobalDefs.PLOT_TICK_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getGraphWidget().getDomainOriginLabelPaint(), GlobalDefs.PLOT_TICK_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getGraphWidget().getDomainLabelPaint(), GlobalDefs.PLOT_TICK_LABEL_FONT_SIZE_DP);
        PaintUtils.setFontSizeDp(ctx,
            plot.getTitleWidget().getLabelPaint(), GlobalDefs.PLOT_TITLE_FONT_SIZE_DP);
        plot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1, 2, 1, 2}, 0));
        plot.getTitleWidget().pack();
    }

    private void showS1() {
        plot.addSeries(s1, lpFormatter1);
    }

    private void showS2() {
        plot.addSeries(s2, lpFormatter2);
    }

    private void showS3() {
        plot.addSeries(s3, lpFormatter3);
    }

    private void showS4() {
        plot.addSeries(s4, lpFormatter4);
    }

    /**
     * Create 4 XYSeries from the values defined above add add them to the plot.
     * Also add some arbitrary regions.
     */
    private void seriesSetup() {


        // SERIES #1:
        lpFormatter1 = new LineAndPointFormatter(
                Color.rgb(100, 25, 20),
                Color.rgb(4, 100, 88),
                Color.rgb(66, 100, 3));
        lpFormatter1.setFillPaint(null);
        lpFormatter1.setVertexPaint(null);
        lpFormatter1.getLinePaint().setShadowLayer(0, 0, 0, 0);

        s1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "S1");

        showS1();

        // SERIES #2:
        lpFormatter2 = new LineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(114, 100, 88),
                Color.rgb(66, 100, 200));

        lpFormatter2.setFillPaint(null);
        lpFormatter2.setVertexPaint(null);
        lpFormatter2.getLinePaint().setShadowLayer(0, 0, 0, 0);

        s2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "S2");

        showS2();

        // SERIES #3:
        lpFormatter3 = new LineAndPointFormatter(
                Color.rgb(200, 25, 200),
                Color.rgb(200, 100, 88),
                Color.rgb(66, 100, 100));
        lpFormatter3.setFillPaint(null);
        lpFormatter3.setVertexPaint(null);
        lpFormatter3.getLinePaint().setShadowLayer(0, 0, 0, 0);

        s3 = new SimpleXYSeries(Arrays.asList(series3Numbers),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "S3");

        showS3();

        // SERIES #4:
        lpFormatter4 = new LineAndPointFormatter(
                Color.rgb(220, 25, 20),
                Color.rgb(4, 220, 88),
                Color.rgb(1, 100, 225));
        lpFormatter4.setFillPaint(null);
        lpFormatter4.setVertexPaint(null);
        lpFormatter4.getLinePaint().setShadowLayer(0, 0, 0, 0);
        s4 = new SimpleXYSeries(Arrays.asList(series4Numbers),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"S4");
        showS4();

        plot.setTicksPerRangeLabel(3);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
    }

    /**
     * Add some color coded regions to our axis labels.
     */
    private void axisLabelSetup() {
        plot.getGraphWidget().addRangeAxisValueLabelRegion(Double.NEGATIVE_INFINITY, 3, new AxisValueLabelFormatter(Color.RED));
        plot.getGraphWidget().addRangeAxisValueLabelRegion(3, 7, new AxisValueLabelFormatter(Color.BLUE));
    }

    /**
     * Add some markers to our plot.
     */
    private void markerSetup() {

        // the easy way to add a marker (uses default font color and positioning):
        plot.addMarker(new YValueMarker(3, "Marker #1"));

        // the comprehensive way:
        plot.addMarker(new YValueMarker(
                7,                                          // y-val to mark
                "Marker #2",                                // marker label
                new XPositionMetric(                        // object instance to set text positioning on the marker
                        3,                                  // 3 pixel positioning offset
                        XLayoutStyle.ABSOLUTE_FROM_LEFT    // how/where the positioning offset is applied
                ),
                Color.BLUE,                                 // line paint color
                Color.BLUE                                  // text paint color
        ));
    }

    /**
     * Add some fill regions to our series data
     */
    private void regionSetup() {

        // create a new region:
        XYRegionFormatter regionFormatter = new XYRegionFormatter(Color.RED);

        // add the new region to the formatter for this series:
        // we want to create a vertical region so we set the minX/maxX values to
        // negative and positive infinity repectively:
        lpFormatter1.addRegion(new RectRegion(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 3, "R1"), regionFormatter);

        // and another region:
        XYRegionFormatter regionFormatter2 = new XYRegionFormatter(Color.BLUE);
        lpFormatter1.addRegion(new RectRegion(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3, 7, "R2"), regionFormatter2);
        lpFormatter1.setFillDirection(FillDirection.RANGE_ORIGIN);
        plot.setUserRangeOrigin(3);

        // the below three regions are horizontal regions so we set minY/maxY to
        // negative and positive infinity respectively.
        XYRegionFormatter regionFormatter3 = new XYRegionFormatter(Color.GREEN);
        XYRegionFormatter regionFormatter4 = new XYRegionFormatter(Color.YELLOW);
        XYRegionFormatter regionFormatter5 = new XYRegionFormatter(Color.MAGENTA);
        lpFormatter2.addRegion(new RectRegion(0, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R3"), regionFormatter3);
        lpFormatter2.addRegion(new RectRegion(2, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R4"), regionFormatter4);
        lpFormatter2.addRegion(new RectRegion(4, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R5"), regionFormatter5);

        lpFormatter2.setFillDirection(FillDirection.RANGE_ORIGIN);
        plot.setUserRangeOrigin(3);


    }
}