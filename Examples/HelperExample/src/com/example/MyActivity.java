package com.example;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.layout.*;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;

import java.util.Arrays;

public class MyActivity extends Activity implements View.OnTouchListener
{

    private static final int FONT_LABEL_SIZE = 13;
    private XYPlot plot;
    private final Number[] series1Numbers = {1, 4, 9, 9, 5, 2, 12};
    private final Number[] series2Numbers = {5, 2, 3, 2, 17, 9, 1};
    private final Number[] series3Numbers = {8, 9, 2, 1, 2, 1, 2};
    private final Number[] series4Numbers = {2, 1, 1, 11, 6, 5, 7};

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        plot.setTitle("Bezier Test");

        //addBeziers();
        addNormal();

        // use a 2x4 grid:
        plot.getLegendWidget().setTableModel(new DynamicTableModel(2, 4));

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
        plot.getLegendWidget().setSize(new SizeMetrics(60, SizeLayoutType.ABSOLUTE, 85, SizeLayoutType.ABSOLUTE));

        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:
        plot.position(
                plot.getLegendWidget(),
                20,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                35,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.RIGHT_BOTTOM);




        plot.getGraphWidget().setMarginBottom(10);




        //plot.addMarker(new YValueMarker(87.9));
        //plot.addMarker(new XValueMarker(5));

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        //plot.setTicksPerDomainLabel(2);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

        plot.getGraphWidget().setRangeLabelMargin(-1);
        plot.getGraphWidget().setRangeLabelWidth(25);
        plot.getGraphWidget().setDomainLabelWidth(10);
        plot.getGraphWidget().setDomainLabelMargin(-6);
        //plot.getGraphWidget().setRangeLabelTickExtension(-15);
        plot.position(
                plot.getRangeLabelWidget(),
                0,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.RIGHT_MIDDLE);
        plot.position(
                plot.getDomainLabelWidget(),
                0,
                XLayoutStyle.ABSOLUTE_FROM_CENTER,
                0,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.BOTTOM_MIDDLE);
        
        plot.position(
                plot.getGraphWidget(),
                0,
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_MIDDLE
        );


        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        plot.disableAllMarkup();
        plot.setOnTouchListener(this);


        plot.setBackgroundPaint(null);
        plot.getGraphWidget().setBackgroundPaint(null);
        //plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.setBorderPaint(null);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getDomainLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(FONT_LABEL_SIZE);

        plot.getGraphWidget().getRangeLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(FONT_LABEL_SIZE);

        plot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1, 2, 1, 2}, 0));

                plot.getTitleWidget().getLabelPaint().setTextSize(FONT_LABEL_SIZE);
        plot.getTitleWidget().pack();
        
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
       
        if(plot.containsPoint(x, y)) {

            plot.setCursorPosition(x, y);
            plot.redraw();
        }
        return true;
    }

    protected void addBeziers() {
        BezierLineAndPointFormatter lpFormatter1 = new BezierLineAndPointFormatter(
                Color.rgb(100, 25, 20),
                Color.rgb(4, 100, 88),
                Color.rgb(66, 100, 3));
        lpFormatter1.setFillPaint(null);
        lpFormatter1.setVertexPaint(null);
        lpFormatter1.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter1.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "BS1"), lpFormatter1);

        BezierLineAndPointFormatter lpFormatter2 = new BezierLineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(114, 100, 88),
                Color.rgb(66, 100, 200));
        lpFormatter2.setFillPaint(null);
        lpFormatter2.setVertexPaint(null);
        lpFormatter2.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter2.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "BS2"), lpFormatter2);

        BezierLineAndPointFormatter lpFormatter3 = new BezierLineAndPointFormatter(
                Color.rgb(200, 25, 200),
                Color.rgb(200, 100, 88),
                Color.rgb(66, 100, 100));
        lpFormatter3.setFillPaint(null);
        lpFormatter3.setVertexPaint(null);
        lpFormatter3.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter3.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series3Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "BS3"), lpFormatter3);

        BezierLineAndPointFormatter lpFormatter4 = new BezierLineAndPointFormatter(
                Color.rgb(220, 25, 20),
                Color.rgb(4, 220, 88),
                Color.rgb(1, 100, 225));
        lpFormatter4.setFillPaint(null);
        lpFormatter4.setVertexPaint(null);
        lpFormatter4.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter4.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series4Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "BS4"), lpFormatter4);

    }

    protected void addNormal() {
        LineAndPointFormatter lpFormatter1 = new LineAndPointFormatter(
                Color.rgb(100, 25, 20),
                Color.rgb(4, 100, 88),
                Color.rgb(66, 100, 3));
        lpFormatter1.setFillPaint(null);
        lpFormatter1.setVertexPaint(null);
        lpFormatter1.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter1.getVertexPaint().setShadowLayer(0, 0, 0, 0);
        plot.addSeries(new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "S1"), lpFormatter1);

        LineAndPointFormatter lpFormatter2 = new LineAndPointFormatter(
                Color.rgb(100, 25, 200),
                Color.rgb(114, 100, 88),
                Color.rgb(66, 100, 200));
        lpFormatter2.setFillPaint(null);
        lpFormatter2.setVertexPaint(null);
        lpFormatter2.getLinePaint().setShadowLayer(0, 0, 0, 0);
        //lpFormatter2.getVertexPaint().setShadowLayer(0, 0, 0, 0);
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
    }
}
