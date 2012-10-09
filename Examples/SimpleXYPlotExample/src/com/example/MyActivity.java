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

package com.example;

import android.app.Activity;
import android.os.Bundle;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.*;

import java.util.Arrays;


public class MyActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        
        // add a new series
        XYSeries mySeries = new SimpleXYSeries(
                Arrays.asList(new Integer[]{0, 25, 55, 2, 80, 30, 99, 0, 44, 6}),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "series1");
        plot.addSeries(mySeries, new LineAndPointFormatter(
                getApplicationContext(), R.xml.f1));

        // reposition the domain label to look a little cleaner:
        plot.position(plot.getDomainLabelWidget(), // the widget to position
                45,                                // x position value, in this case 45 pixels
                XLayoutStyle.ABSOLUTE_FROM_LEFT,   // how the x position value is applied, in this case from the left
                0,                                 // y position value
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM, // how the y position is applied, in this case from the bottom
                AnchorPosition.LEFT_BOTTOM);       // point to use as the origin of the widget being positioned

        plot.centerOnRangeOrigin(60);
        plot.centerOnDomainOrigin(5);
    }
}
