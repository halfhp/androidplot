package com.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.androidplot.ui.layout.SizeLayoutType;
import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.ui.layout.TableModel;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.*;

import java.util.Arrays;
import java.util.Vector;

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

        // Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 1, 9, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

        //Double seed = Math.random() * 100;

        for (int i = 0; i < 4; i++) {
            Number[] seriesNumbers = {Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100, Math.random() * 100};
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
                            Arrays.asList(seriesNumbers),          // SimpleXYSeries takes a List so turn our array into a List
                            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                            "S" + i),
                    lpFormatter);                             // Set the display title of the series);
        }

        mySimpleXYPlot.getLegendWidget().setSize(new SizeMetrics(11, SizeLayoutType.ABSOLUTE, 170, SizeLayoutType.ABSOLUTE));
        //mySimpleXYPlot.getLegendWidget().setTableModel(new TableModel(0, 1));

        // Turn the above arrays into XYSeries':
        /*XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series
        
        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Series2");*/

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
       /* LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 100, 0),                   // line color
                Color.rgb(0, 100, 0),                   // point color
                Color.rgb(100, 200, 0));                // fill color
*/
        // add a new series' to the xyplot:
        //mySimpleXYPlot.addSeries(series1, series1Format);

        // same as above:
        //mySimpleXYPlot.addSeries(series2, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100)));
       // mySimpleXYPlot.addSeries(series2, new StepFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100)));


        // reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);

        //mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, 5);

        // draw a domain line for every element plotted on the domain:
        //mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

        // get rid of the decimal place on the display:
        //mySimpleXYPlot.setDomainValueFormat(new DecimalFormat("#"));

        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();

    }
}
