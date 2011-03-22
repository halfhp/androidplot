package com.example;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import com.androidplot.ui.layout.*;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.*;

import java.util.Arrays;

public class MyActivity extends Activity
{

    private XYPlot mySimpleXYPlot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // randomly generate 4 series of data and add them to the plot.
        // values are all within the range of 0 - 100
        for (int i = 0; i < 4; i++) {
            Number[] seriesNumbers = {
                    Math.random() * 100,
                    Math.random() * 100,
                    Math.random() * 100,
                    Math.random() * 100,
                    Math.random() * 100,
                    Math.random() * 100};
            LineAndPointFormatter lpFormatter = new LineAndPointFormatter(
                            Color.rgb(
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue()),
                            Color.rgb(
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue()),
                            Color.rgb(
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue(),
                                    new Double(Math.random()*255).intValue()));
            lpFormatter.setFillPaint(null);
            mySimpleXYPlot.addSeries(
                    new SimpleXYSeries(
                            Arrays.asList(seriesNumbers),           // SimpleXYSeries takes a List so turn our array into a List
                            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                            "S" + i),
                    lpFormatter);                                   // Set the display title of the series);
        }

        // use a 2x2 grid:
        mySimpleXYPlot.getLegendWidget().setTableModel(new DynamicTableModel(2, 2));

        // adjust the legend size so there is enough room
        // to draw the new legend grid:
        mySimpleXYPlot.getLegendWidget().setSize(new SizeMetrics(40, SizeLayoutType.ABSOLUTE, 75, SizeLayoutType.ABSOLUTE));

        // add a semi-transparent black background to the legend
        // so it's easier to see overlaid on top of our plot:
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(140);
        mySimpleXYPlot.getLegendWidget().setBackgroundPaint(bgPaint);

        // adjust the padding of the legend widget to look a little nicer:
        mySimpleXYPlot.getLegendWidget().setPadding(10, 1, 1, 1);

        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:
        mySimpleXYPlot.position(
                mySimpleXYPlot.getLegendWidget(),
                20,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                35,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.RIGHT_BOTTOM);

        // reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);

        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();

    }
}
