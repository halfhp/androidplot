// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.widget.CheckBox;

import com.androidplot.util.PixelUtils;
import com.androidplot.ui.*;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Demonstration of the usage of Marker and RectRegion.
 */
public class XYRegionExampleActivity extends Activity {

    private static final float HOME_RUN_DIST = 325;
    private static final int LINE_THICKNESS_DP = 4;
    private static final int POINT_SIZE_DP = 7;
    private XYPlot plot;
    private final Number[] timHits = {105, 252, 216, 10, 301, 320, 220, 350, 12, 250, 353};
    private final Number[] nickHits = {110, 191, 61, 300, 205, 40, 224, 371, 289, 101, 10};
    private LineAndPointFormatter timFormatter;
    private LineAndPointFormatter nickFormatter;

    private XYSeries timSeries;
    private XYSeries nickSeries;

    private RectRegion shortRegion;
    private RectRegion warmupRegion;
    private RectRegion homeRunRegion;

    private XYRegionFormatter shortRegionFormatter;
    private XYRegionFormatter warmupRegionFormatter;
    private XYRegionFormatter homeRunRegionFormatter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyregion_example);
        plot = findViewById(R.id.xyRegionExamplePlot);

        seriesSetup();
        markerSetup();
        regionSetup();
        makePlotPretty();
        bindCheckBoxes();
    }

    /**
     * Two SimpleXYSeries with interpolated LineAndPointFormatters.
     */
    private void seriesSetup() {

        // TIM
        timFormatter = new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);
        timFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        timFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));
        timFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(8,
                CatmullRomInterpolator.Type.Centripetal));

        timSeries = new SimpleXYSeries(Arrays.asList(timHits),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Tim");

        plot.addSeries(timSeries, timFormatter);

        // NICK
        nickFormatter = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);
        nickFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(LINE_THICKNESS_DP));
        nickFormatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(POINT_SIZE_DP));
        nickFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(8,
                CatmullRomInterpolator.Type.Centripetal));

        nickSeries = new SimpleXYSeries(Arrays.asList(nickHits),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Nick");

        plot.addSeries(nickSeries, nickFormatter);

        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 100);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
    }

    /**
     * YValueMarker draws a labeled horizontal line at a range value.
     */
    private void markerSetup() {
        plot.addMarker(wallMarker(380, "Fenway Park LF Wall", Color.BLUE));
        plot.addMarker(wallMarker(309, "ATT Park RF Wall", Color.CYAN));
    }

    private YValueMarker wallMarker(float y, String label, int color) {
        YValueMarker marker = new YValueMarker(y, label,
                new HorizontalPosition(PixelUtils.dpToPix(5), HorizontalPositioning.ABSOLUTE_FROM_RIGHT),
                color, color);
        marker.getTextPaint().setTextSize(PixelUtils.dpToPix(14));
        marker.getLinePaint().setPathEffect(new DashPathEffect(
                new float[] {PixelUtils.dpToPix(2), PixelUtils.dpToPix(2)}, 0));
        return marker;
    }

    /**
     * RectRegion + XYRegionFormatter: a series is filled with the region's color
     * wherever it passes through the region.
     */
    private void regionSetup() {
        shortRegionFormatter = new XYRegionFormatter(Color.RED);
        shortRegionFormatter.getPaint().setAlpha(60);
        shortRegion = new RectRegion(
                2, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, HOME_RUN_DIST, "Short");

        timFormatter.addRegion(shortRegion, shortRegionFormatter);
        nickFormatter.addRegion(shortRegion, shortRegionFormatter);

        warmupRegionFormatter = new XYRegionFormatter(Color.LTGRAY);
        warmupRegionFormatter.getPaint().setAlpha(60);

        warmupRegion = new RectRegion(
                0, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "Warmup");

        timFormatter.addRegion(warmupRegion, warmupRegionFormatter);
        nickFormatter.addRegion(warmupRegion, warmupRegionFormatter);

        homeRunRegionFormatter = new XYRegionFormatter(Color.GREEN);
        homeRunRegionFormatter.getPaint().setAlpha(60);

        homeRunRegion = new RectRegion(
                2, Double.POSITIVE_INFINITY, HOME_RUN_DIST, Double.POSITIVE_INFINITY, "H. Run");
        timFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);
        nickFormatter.addRegion(homeRunRegion, homeRunRegionFormatter);
        nickFormatter.setFillDirection(FillDirection.RANGE_ORIGIN);
    }

    /**
     * Cleans up the plot's general layout and color scheme
     */
    private void makePlotPretty() {
        // use a 4x2 grid with room for 8 items:
        plot.getLegend().setTableModel(new DynamicTableModel(4, 2));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT)
                .setFormat(new DecimalFormat("0.0''"));

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                .setFormat(new DecimalFormat("#"));

        plot.getGraph().setDomainGridLinePaint(null);

        plot.getLegend().setWidth(PixelUtils.dpToPix(100), SizeMode.FILL);

        // reposition the legend so that it rests above the bottom-left
        // edge of the graph widget:
        plot.getLegend().position(
                50,
                HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                200,
                VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.TOP_MIDDLE);

        plot.setRangeBoundaries(0, BoundaryMode.FIXED, 500, BoundaryMode.FIXED);
    }

    private void bindCheckBoxes() {
        CheckBox r2CheckBox = findViewById(R.id.r2CheckBox);
        CheckBox r3CheckBox = findViewById(R.id.r3CheckBox);
        CheckBox r4CheckBox = findViewById(R.id.r4CheckBox);
        r2CheckBox.setOnCheckedChangeListener((cb, checked) ->
                toggleRegion(checked, timFormatter, shortRegion, shortRegionFormatter));
        r3CheckBox.setOnCheckedChangeListener((cb, checked) ->
                toggleRegion(checked, nickFormatter, warmupRegion, warmupRegionFormatter));
        r4CheckBox.setOnCheckedChangeListener((cb, checked) ->
                toggleRegion(checked, nickFormatter, homeRunRegion, homeRunRegionFormatter));

        CheckBox timCB = findViewById(R.id.s1CheckBox);
        timCB.setOnCheckedChangeListener((cb, checked) -> {
            toggleSeries(checked, timSeries, timFormatter);
            r2CheckBox.setEnabled(checked);
        });

        CheckBox nickCB = findViewById(R.id.s2CheckBox);
        nickCB.setOnCheckedChangeListener((cb, checked) -> {
            toggleSeries(checked, nickSeries, nickFormatter);
            r3CheckBox.setEnabled(checked);
            r4CheckBox.setEnabled(checked);
        });
    }

    private void toggleSeries(boolean checked, XYSeries series, LineAndPointFormatter formatter) {
        if (checked) {
            plot.addSeries(series, formatter);
        } else {
            plot.removeSeries(series);
        }
        plot.redraw();
    }

    private void toggleRegion(boolean checked, LineAndPointFormatter lpf,
            RectRegion rr, XYRegionFormatter rf) {
        if (checked) {
            lpf.addRegion(rr, rf);
        } else {
            lpf.removeRegion(rr);
        }
        plot.redraw();
    }
}
