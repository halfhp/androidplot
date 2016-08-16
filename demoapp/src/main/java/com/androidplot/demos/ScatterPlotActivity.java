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

import android.app.Activity;
import android.os.Bundle;
import com.androidplot.xy.*;

/**
 * A scatter plot
 */
public class ScatterPlotActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scatter_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        XYSeries series1 = generateScatter("series1", 80, new RectRegion(10, 50, 10, 50));
        XYSeries series2 = generateScatter("series2", 80, new RectRegion(30, 70, 30, 70));

        plot.setDomainBoundaries(0, 80, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 80, BoundaryMode.FIXED);

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.configure(getApplicationContext(),
                R.xml.point_formatter);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.configure(getApplicationContext(),
                R.xml.point_formatter_2);

        // add each series to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        //plot.getGraphWidget().setLineLabelRotation(XYGraphWidget.Edge.BOTTOM, -45f);
    }

    /**
     * Generate a XYSeries of random points within a specified region
     * @param title
     * @param numPoints
     * @param region
     * @return
     */
    private XYSeries generateScatter(String title, int numPoints, RectRegion region) {
        SimpleXYSeries series = new SimpleXYSeries(title);
        for(int i = 0; i < numPoints; i++) {
            series.addLast(
                    region.getMinX().doubleValue() + (Math.random() * region.getWidth().doubleValue()),
                    region.getMinY().doubleValue() + (Math.random() * region.getHeight().doubleValue())
            );
        }
        return series;
    }
}
