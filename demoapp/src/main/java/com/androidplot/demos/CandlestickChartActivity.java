/*
 * Copyright 2016 AndroidPlot.com
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
import android.graphics.DashPathEffect;
import android.os.Bundle;
import com.androidplot.candlestick.CandlestickFormatter;
import com.androidplot.candlestick.CandlestickSeries;
import com.androidplot.candlestick.SimpleCandlestickSeries;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import java.util.Arrays;

/**
 * A simple XYPlot
 */
public class CandlestickChartActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_xy_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        plot.getLayoutManager().moveToBottom(plot.getTitleWidget());

        // max
        Number[] yVals = {10, 15, 8};

        // min
        Number[] zVals = {1, 5, 0};

        // open
        Number[] aVals = {2, 7, 5};

        // close
        Number[] bVals = {5, 6, 7};

        CandlestickSeries series1 = new SimpleCandlestickSeries(null,
                Arrays.asList(yVals), Arrays.asList(zVals), Arrays.asList(aVals), Arrays.asList(bVals), "bla");

        CandlestickFormatter cf1 = new CandlestickFormatter();

        plot.addSeries(series1, cf1);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
}
