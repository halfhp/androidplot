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
import android.os.Bundle;
import com.androidplot.candlestick.CandlestickFormatter;
import com.androidplot.candlestick.CandlestickMaker;
import com.androidplot.xy.*;

/**
 * A simple example of a candlestick chart rendered on an {@link XYPlot}.
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

        XYSeries highVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "high", 12, 10, 15, 8, 7);
        XYSeries lowVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "low", 3, 1, 5, 0, 2);
        XYSeries openVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,  "open", 5, 2, 7, 5, 3);
        XYSeries closeVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,  "close", 7, 9, 6, 0, 4);

        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.Triangle);
        CandlestickMaker.make(plot, formatter,
                openVals, closeVals, highVals, lowVals);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
}
