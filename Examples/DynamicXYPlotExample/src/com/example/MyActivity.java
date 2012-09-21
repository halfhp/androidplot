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
import android.graphics.Color;
import android.os.Bundle;
import com.androidplot.Plot;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class MyActivity extends Activity {

    // redraws a plot whenever an update is received:
    private class MyPlotUpdater implements Observer {
        Plot plot;
        public MyPlotUpdater(Plot plot) {
            this.plot = plot;
        }
        @Override
        public void update(Observable o, Object arg) {
            //try {
                //plot.postRedraw();
                plot.redraw();
            /*} catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }*/
        }
    }

    private XYPlot dynamicPlot;
    private XYPlot staticPlot;
    private MyPlotUpdater plotUpdater;
    SampleDynamicXYDatasource data;
    private Thread myThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // android boilerplate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);

        plotUpdater = new MyPlotUpdater(dynamicPlot);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));

        // getInstance and position datasets:
        data = new SampleDynamicXYDatasource();
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Sine 1");
        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Sine 2");

        dynamicPlot.addSeries(sine1Series, new LineAndPointFormatter(Color.rgb(0, 0, 0), null, Color.rgb(0, 80, 0)));

        // create a series using a formatter with some transparency applied:
        LineAndPointFormatter f1 = new LineAndPointFormatter(Color.rgb(0, 0, 200), null, Color.rgb(0, 0, 80));
        f1.getFillPaint().setAlpha(220);
        dynamicPlot.addSeries(sine2Series, f1);
        //dynamicPlot.setGridPadding(5, 0, 5, 0);

        //dynamicPlot.addSeries(sine1Series, new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
        //dynamicPlot.addSeries(sine2Series, new BarFormatter(Color.argb(100, 0, 0, 200), Color.rgb(0, 0, 80)));

        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);

        dynamicPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
        dynamicPlot.setDomainStepValue(sine1Series.size());
        // thin out domain/range tick labels so they dont overlap each other:
        dynamicPlot.setTicksPerDomainLabel(5);
        dynamicPlot.setTicksPerRangeLabel(3);
        //dynamicPlot.getGraphWidget().setTicksPerRangeLabel(3);
        //dynamicPlot.disableAllMarkup();

        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);

        // comment this line to get rid of "panning" or modify
        // the x/y values to move the view left or right.
        //dynamicPlot.setDomainBoundaries(5,10, BoundaryMode.FIXED);

    }

    @Override
    public void onResume() {
        // kick off the data generating thread:
        myThread = new Thread(data);
        myThread.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        data.stopThread();
        super.onPause();
    }

}
