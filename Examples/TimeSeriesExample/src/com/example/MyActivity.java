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
import android.graphics.*;
import android.os.Bundle;
import com.androidplot.Plot;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.*;

import java.text.*;
import java.util.Arrays;
import java.util.Date;

public class MyActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        Number[] numSightings = {5, 8, 9, 2, 5};

        Number[] timestamps = {
                978307200000L,  // 1/1/2001
                1009843200000L, // 1/1/2002
                1041379200000L, // 1/1/2003
                1072915200000L, // 1/1/2004
                1104537600000L  // 1/1/2005
        };

        // create our series from our array of nums:
        XYSeries series = new SimpleXYSeries(
                Arrays.asList(timestamps),
                Arrays.asList(numSightings),
                "Sightings in USA");

        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        plot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        plot.getBorderPaint().setStrokeWidth(1);
        plot.getBorderPaint().setAntiAlias(false);
        plot.getBorderPaint().setColor(Color.WHITE);

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 100, 0),                   // line color
                Color.rgb(0, 100, 0),                   // point color
                Color.rgb(100, 200, 0));                // fill color


        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));

        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        formatter.setFillPaint(lineFill);
        plot.getGraphWidget().setPaddingRight(2);
        plot.addSeries(series, formatter);

        // draw a domain tick for each year:
        plot.setDomainStep(XYStepMode.SUBDIVIDE, timestamps.length);

        // customize our domain/range labels
        plot.setDomainLabel("Year");
        plot.setRangeLabel("# of Sightings");

        // get rid of decimal points in our range labels:
        plot.setRangeValueFormat(new DecimalFormat("0"));

        plot.setDomainValueFormat(new MyDateFormat());


        //plot.addMarker(new XValueMarker(1086048000000L, null));
        plot.getGraphWidget().getDomainLabelPaint().setAlpha(0);
        plot.getGraphWidget().getDomainOriginLabelPaint().setAlpha(0);
        plot.getGraphWidget().getRangeLabelPaint().setAlpha(0);
        plot.getGraphWidget().getRangeOriginLabelPaint().setAlpha(0);





        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        plot.disableAllMarkup();
    }

    private class MyDateFormat extends Format {


            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");



            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long timestamp = ((Number) obj).longValue();
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;

            }

    }
}
