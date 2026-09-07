// SPDX-License-Identifier: Apache-2.0

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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bubble_chart_example);
        XYPlot plot = findViewById(R.id.plot);

        // BubbleSeries(yVals, zVals, title): x is the element index, z is bubble size
        BubbleSeries series1 = new BubbleSeries(
                Arrays.<Number>asList(3, 5, 2, 3, 6),
                Arrays.<Number>asList(1, 5, 2, 2, 3), "s1");

        BubbleSeries series2 = new BubbleSeries(
                Arrays.<Number>asList(2, 7, 3, 1, 3),
                Arrays.<Number>asList(2, 1, 2, 6, 7), "s2");

        BubbleSeries series3 = new BubbleSeries(
                Arrays.<Number>asList(7, 2, 5, 6, 5),
                Arrays.<Number>asList(3, 2, 4, 6, 7), "s3");

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

        // pinch/drag to pan and zoom:
        PanZoom.attach(plot);
    }
}
