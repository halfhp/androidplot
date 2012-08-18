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

import java.util.Arrays;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.androidplot.ui.layout.AnchorPosition;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.XYPlot;

public class NormalDistribution extends Activity {
    
    private XYPlot plot1;
    private XYPlot plot2;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Paint blueLinePaint = new Paint();
        blueLinePaint.setAntiAlias(true);
        blueLinePaint.setStrokeWidth(.5f);
        blueLinePaint.setColor(Color.BLUE);
        
        Paint cyanFillPaint = new Paint();
        cyanFillPaint.setAntiAlias(true);
        cyanFillPaint.setStrokeWidth(.1f);
        cyanFillPaint.setColor(Color.CYAN);
        //cyanFillPaint.setStyle(Style.STROKE);
        
        Paint vertexPaint = new Paint();
        vertexPaint.setAntiAlias(true);
        vertexPaint.setStrokeWidth(0f);
        vertexPaint = null;

        double[] randomInts = RandomXYSeries.getRandomArray();
        Arrays.sort(randomInts);
        
        
        RandomXYSeries series1 = new RandomXYSeries(randomInts, false);
        RandomXYSeries series2 = new RandomXYSeries(randomInts, true);
        
        /*
        // initialize our XYPlot reference:
        plot1 = (XYPlot) findViewById(R.id.NormalDistribution1);
        plot1.addSeries(series, 
        		LineAndPointRenderer.class, new LineAndPointFormatter(blueLinePaint, vertexPaint, null));
        drawPlot(plot1);
        
        
        // initialize our XYPlot reference:
        plot2 = (XYPlot) findViewById(R.id.NormalDistribution2);
        plot2.addSeries(new RandomXYSeries(), 
        		LineAndPointRenderer.class, new LineAndPointFormatter(blueLinePaint, vertexPaint, cyanFillPaint));
        drawPlot(plot2);
        */
        // initialize our XYPlot reference:
        plot1 = (XYPlot) findViewById(R.id.NormalDistribution1);
        plot1.addSeries(series1, 
        		LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0), null));
        drawPlot(plot1);
        series1.turnOnNormalDist();
        
        // initialize our XYPlot reference:
        plot2 = (XYPlot) findViewById(R.id.NormalDistribution2);
        plot2.addSeries(series2, 
        		LineAndPointRenderer.class, new LineAndPointFormatter(blueLinePaint, null, null));
        drawPlot(plot2);
    }
    
    private void drawPlot(XYPlot plot)
    {
        //TODO:  reduce the number of range labels
        //plot.getGraphWidget().setRangeTicksPerLabel(4);
        plot.setTicksPerRangeLabel(4);
        plot.setTicksPerDomainLabel(2);

        // reposition the domain label to look a little cleaner:
        Widget domainLabelWidget = plot.getDomainLabelWidget();

        plot.position(domainLabelWidget,           // the widget to position
            45,                                    // x position value, in this case 45 pixels
            XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
            0,                                     // y position value
            YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
            AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned


        //plot.setUserRangeOrigin(44);
        //plot.centerOnRangeOrigin(60);
        //plot.centerOnDomainOrigin(5);


        plot.setGridPadding(4, 4, 4, 4);
        //plot.setDomainBoundaries(5,7,BoundaryMode.FIXED);

        // get rid of the visual aids for positioning:
        plot.disableAllMarkup();
    }
    
    
}