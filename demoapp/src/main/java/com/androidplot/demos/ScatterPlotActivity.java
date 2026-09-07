// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.os.Bundle;
import com.androidplot.xy.*;

/**
 * A scatter plot
 */
public class ScatterPlotActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scatter_plot_example);
        XYPlot plot = findViewById(R.id.plot);

        XYSeries series1 = generateScatter("series1", 80, new RectRegion(10, 50, 10, 50));
        XYSeries series2 = generateScatter("series2", 80, new RectRegion(30, 70, 30, 70));

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.point_formatter);

        LineAndPointFormatter series2Format =
                new LineAndPointFormatter(this, R.xml.point_formatter_2);

        // add each series to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        plot.setDomainBoundaries(0, 80, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 80, BoundaryMode.FIXED);

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);
    }

    // random points inside region
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
