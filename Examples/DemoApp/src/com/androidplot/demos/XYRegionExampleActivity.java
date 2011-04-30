package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.Region;
import com.androidplot.ui.layout.*;
import com.androidplot.xy.*;

import java.util.Arrays;


public class XYRegionExampleActivity extends Activity {
    private XYPlot plot;

    private final Number[] series1Numbers = {1, 4, 9, 9, 5, 2, 12};
    private final Number[] series2Numbers = {5, 2, 3, 2, 17, 9, 1};
    private final Number[] series3Numbers = {8, 9, 2, 1, 2, 1, 2};
    private final Number[] series4Numbers = {2, 1, 1, 11, 6, 5, 7};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyregion_example);

         plot = (XYPlot) findViewById(R.id.xyRegionExamplePlot);

        addNormal();

        // use a 2x2 grid:
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
        plot.disableAllMarkup();
    }

    protected void addNormal() {


        LineAndPointFormatter lpFormatter1 = new LineAndPointFormatter(
                Color.rgb(100, 25, 20),
                Color.rgb(4, 100, 88),
                Color.rgb(66, 100, 3));
        lpFormatter1.setFillPaint(null);
        lpFormatter1.setVertexPaint(null);
        lpFormatter1.getLinePaint().setShadowLayer(0, 0, 0, 0);

        XYRegionFormatter regionFormatter = new XYRegionFormatter(Color.RED);
        lpFormatter1.addRegion(new XYRegion(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 5, "R1"), regionFormatter);

        XYRegionFormatter regionFormatter2 = new XYRegionFormatter(Color.BLUE);

        lpFormatter1.addRegion(new XYRegion(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 5, Double.POSITIVE_INFINITY, "R2"), regionFormatter2);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S1"), lpFormatter1);

        LineAndPointFormatter lpFormatter2 = new LineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(114, 100, 88),
                Color.rgb(66, 100, 200));
        XYRegionFormatter regionFormatter3 = new XYRegionFormatter(Color.GREEN);
        XYRegionFormatter regionFormatter4 = new XYRegionFormatter(Color.YELLOW);
        XYRegionFormatter regionFormatter5 = new XYRegionFormatter(Color.MAGENTA);
        lpFormatter2.addRegion(new XYRegion(0, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R3"), regionFormatter3);
        lpFormatter2.addRegion(new XYRegion(2, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R4"), regionFormatter4);
        lpFormatter2.addRegion(new XYRegion(4, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "R5"), regionFormatter5);

        lpFormatter2.setFillPaint(null);
        lpFormatter2.setVertexPaint(null);
        lpFormatter2.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter2.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter2.addRegion(new XYRegion(0, 5, 0, 5), regionFormatter);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S2"), lpFormatter2);

        LineAndPointFormatter lpFormatter3 = new LineAndPointFormatter(
                Color.rgb(200, 25, 200),
                Color.rgb(200, 100, 88),
                Color.rgb(66, 100, 100));
        lpFormatter3.setFillPaint(null);
        lpFormatter3.setVertexPaint(null);
        lpFormatter3.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter3.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series3Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S3"), lpFormatter3);

        LineAndPointFormatter lpFormatter4 = new LineAndPointFormatter(
                Color.rgb(220, 25, 20),
                Color.rgb(4, 220, 88),
                Color.rgb(1, 100, 225));
        lpFormatter4.setFillPaint(null);
        lpFormatter4.setVertexPaint(null);
        lpFormatter4.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter4.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series4Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S4"), lpFormatter4);
        plot.setTicksPerRangeLabel(3);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

        plot.getGraphWidget().addRangeLabelRegion(new Region(null, 5), new XYAxisRegionFormatter(Color.RED));
    }
}