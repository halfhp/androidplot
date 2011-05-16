package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.ui.*;
import com.androidplot.xy.*;

import java.util.Arrays;


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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyregion_example);
        plot = (XYPlot) findViewById(R.id.xyRegionExamplePlot);
        seriesSetup();
        markerSetup();
        axisLabelSetup();
        regionSetup();
        makePlotPretty();
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
        plot.getLegendWidget().setSize(new SizeMetrics(70, SizeLayoutType.ABSOLUTE, 80, SizeLayoutType.ABSOLUTE));

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
        plot.getGraphWidget().setRangeLabelWidth(25);
        plot.getGraphWidget().setDomainLabelWidth(10);
        plot.getGraphWidget().setDomainLabelVerticalOffset(-6);
        plot.setBackgroundPaint(null);
        plot.getGraphWidget().setBackgroundPaint(null);
        plot.setBorderPaint(null);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getDomainLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getRangeLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1, 2, 1, 2}, 0));
        plot.getTitleWidget().getLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getTitleWidget().pack();
        plot.disableAllMarkup();
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


        plot.addSeries(new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S1"), lpFormatter1);

        // SERIES #2:
        lpFormatter2 = new LineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(114, 100, 88),
                Color.rgb(66, 100, 200));

        lpFormatter2.setFillPaint(null);
        lpFormatter2.setVertexPaint(null);
        lpFormatter2.getLinePaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S2"), lpFormatter2);

        // SERIES #3:
        lpFormatter3 = new LineAndPointFormatter(
                Color.rgb(200, 25, 200),
                Color.rgb(200, 100, 88),
                Color.rgb(66, 100, 100));
        lpFormatter3.setFillPaint(null);
        lpFormatter3.setVertexPaint(null);
        lpFormatter3.getLinePaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series3Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S3"), lpFormatter3);

        // SERIES #4:
        lpFormatter4 = new LineAndPointFormatter(
                Color.rgb(220, 25, 20),
                Color.rgb(4, 220, 88),
                Color.rgb(1, 100, 225));
        lpFormatter4.setFillPaint(null);
        lpFormatter4.setVertexPaint(null);
        lpFormatter4.getLinePaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series4Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S4"), lpFormatter4);
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

        // the below three regions are horizontal regions so we set minY/maxY to
        // negative and positive infinity respectively.
        XYRegionFormatter regionFormatter3 = new XYRegionFormatter(Color.GREEN);
        XYRegionFormatter regionFormatter4 = new XYRegionFormatter(Color.YELLOW);
        XYRegionFormatter regionFormatter5 = new XYRegionFormatter(Color.MAGENTA);
        lpFormatter2.addRegion(new RectRegion(0, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R3"), regionFormatter3);
        lpFormatter2.addRegion(new RectRegion(2, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R4"), regionFormatter4);
        lpFormatter2.addRegion(new RectRegion(4, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R5"), regionFormatter5);


    }
}