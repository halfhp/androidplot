/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.*;


public class MyActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        //XYPlot xyPlot = (XYPlot) findViewById(R.id.chart);
        Number[] series1Numbers = {0, 40, 5, 40, 10, 40, 15, 40, 20, 30, 25, 30, 30, 30};
// Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                com.androidplot.xy.SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
                "Damage");

// Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                Color.rgb(0, 100, 0),
                Color.TRANSPARENT);                  // point color

        plot.addSeries(series1, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0), Color.RED));
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 5);

// reduce the number of range labels
        plot.setTitle("");
        plot.disableAllMarkup();*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        
        // add a new series
        XYSeries mySeries = new MyXYSeries();
        plot.addSeries(mySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0), null));
        plot.removeSeries(mySeries);
        plot.addSeries(mySeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0), null));

        //plot.addSeries(new MyXYSeries(), BarRenderer.class, new BarFormatter(Color.RED, Color.WHITE));

        // reduce the number of range labels
        //plot.getGraphWidget().setRangeTicksPerLabel(4);
        plot.setTicksPerRangeLabel(4);
        plot.setTicksPerDomainLabel(2);

        // reposition the domain label to look a little cleaner:
        Widget domainLabelWidget = plot.getDomainLabelWidget();

        plot.position(domainLabelWidget,                     // the widget to position
                                45,                                    // x position value, in this case 45 pixels
                                XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                                0,                                     // y position value
                                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                                AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned


        //plot.setUserRangeOrigin(44);
        plot.centerOnRangeOrigin(60);
        plot.centerOnDomainOrigin(5);


        plot.setGridPadding(4, 4, 4, 4);
        //plot.setDomainBoundaries(5,7,BoundaryMode.FIXED);

        // get rid of the visual aids for positioning:
        plot.disableAllMarkup();

    }
}
