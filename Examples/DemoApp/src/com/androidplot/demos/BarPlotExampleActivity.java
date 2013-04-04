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
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class BarPlotExampleActivity extends Activity
{

    private XYPlot plot;

    private CheckBox series1CheckBox;
    private CheckBox series2CheckBox;

    private XYSeries series1;
    private XYSeries series2;

    private BarFormatter formatter1;
    private BarFormatter formatter2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

        // Turn the above arrays into XYSeries':
        series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series

        // same as above
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // add a new series' to the xyplot:
        formatter1 = new BarFormatter(Color.GREEN, Color.WHITE);
        plot.addSeries(series1, formatter1);

        // same as above:
        formatter2 = new BarFormatter(Color.RED, Color.WHITE);
        plot.addSeries(series2, formatter2);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // setup checkbox listers:
        series1CheckBox = (CheckBox) findViewById(R.id.s1CheckBox);
        series1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked(b);
            }
        });

        series2CheckBox = (CheckBox) findViewById(R.id.s2CheckBox);
        series2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {onS2CheckBoxClicked(b);
            }
        });
    }

    private void onS2CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series1, formatter1);
        } else {
            plot.removeSeries(series1);
        }
        plot.redraw();
    }

    private void onS1CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series2, formatter2);
        } else {
            plot.removeSeries(series2);
        }
        plot.redraw();
    }
}
