package com.example;
 
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
 
public class MyActivity extends Activity
{
 
    private XYPlot mySimpleXYPlot;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        // Initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        // Create two arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
 
        // Turn the above arrays into XYSeries:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series
 
        // Same as above, for series2
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, 
                "Series2");
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                null,                   // point color
                Color.rgb(150, 190, 150),
                FillDirection.RANGE_ORIGIN);              // fill color (optional)
 
        // Add series1 to the xyplot:
        mySimpleXYPlot.addSeries(series1, series1Format);
 
        // Same as above, with series2:
        mySimpleXYPlot.addSeries(series2, new LineAndPointFormatter(Color.rgb(0, 0, 200), null,
                Color.rgb(150, 150, 190), FillDirection.RANGE_ORIGIN));
 
 
        // Reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);

        mySimpleXYPlot.setUserRangeOrigin(4);
        mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("0"));
        mySimpleXYPlot.setDomainValueFormat(new Format() {

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int x = ((Number) obj).intValue() + 1;
                return toAppendTo.append(x);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });


        //mySimpleXYPlot.getGraphWidget().setDomainLabelWidth(30);
 
        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();


    }

}