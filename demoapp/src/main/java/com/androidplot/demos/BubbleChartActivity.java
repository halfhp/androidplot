/*
 * Copyright 2015 AndroidPlot.com
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

import android.app.*;
import android.graphics.*;
import android.os.*;

import com.androidplot.xy.*;

import java.util.*;

/**
 * An example of a bubble chart.
 */
public class BubbleChartActivity extends Activity {

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bubble_chart_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        BubbleSeries series1 = new BubbleSeries(
                Arrays.asList(new Number[]{3, 5, 2, 3, 6}),
                Arrays.asList(new Number[]{1, 5, 2, 2, 3}), "s1");

        BubbleSeries series2 = new BubbleSeries(
                Arrays.asList(new Number[]{2, 7, 3, 1, 3}),
                Arrays.asList(new Number[]{2, 1, 2, 6, 7}), "s2");

        BubbleSeries series3 = new BubbleSeries(
                Arrays.asList(new Number[]{7, 2, 5, 6, 5}),
                Arrays.asList(new Number[]{3, 2, 4, 6, 7}), "s3");

        plot.setDomainBoundaries(-1, 5, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 8, BoundaryMode.FIXED);

        BubbleFormatter bf1 = new BubbleFormatter(this, R.xml.bubble_formatter1);
        bf1.setPointLabelFormatter(new PointLabelFormatter(Color.BLACK));
        bf1.getPointLabelFormatter().getTextPaint().setTextAlign(Paint.Align.CENTER);
        bf1.getPointLabelFormatter().getTextPaint().setFakeBoldText(true);

        // add series to the xyplot:
        plot.addSeries(series1, bf1);
        plot.addSeries(series2, new BubbleFormatter(this, R.xml.bubble_formatter2));
        plot.addSeries(series3, new BubbleFormatter(this, R.xml.bubble_formatter3));
        PanZoom.attach(plot);
    }
}
