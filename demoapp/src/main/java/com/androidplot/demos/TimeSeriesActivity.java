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
import android.graphics.*;
import android.os.Bundle;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.*;
import java.util.Arrays;
import java.util.Date;

public class TimeSeriesActivity extends Activity {

    private XYPlot plot1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_series_example);

        plot1 = (XYPlot) findViewById(R.id.plot1);
        Number[] numSightings = {5, 8, 6, 9, 3, 8, 5};

        // an array of years in milliseconds:
        Number[] years = {
                978307200,  // 2001
                998309300,
                1009843200, // 2002
                1041379200, // 2003
                1052012100,
                1072915200, // 2004
                1104537600  // 2005
        };
        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(years),
                Arrays.asList(numSightings),
                "Sightings in USA");

        plot1.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        plot1.getGraph().getDomainGridLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getDomainGridLinePaint().
                setPathEffect(new DashPathEffect(new float[] {1, 1}, 1));
        plot1.getGraph().getRangeGridLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getRangeGridLinePaint().
                setPathEffect(new DashPathEffect(new float[] {1, 1}, 1));
        plot1.getGraph().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getRangeOriginLinePaint().setColor(Color.BLACK);

        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);

        LineAndPointFormatter formatter =
                new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.BLUE, Color.RED, null);
        formatter.setFillPaint(lineFill);
        plot1.getGraph().setPaddingRight(2);
        plot1.addSeries(series2, formatter);

        // draw a domain tick for each year:
        plot1.setDomainStep(StepMode.SUBDIVIDE, years.length);

        // customize our domain/range labels
        plot1.setDomainLabel("Year");
        plot1.setRangeLabel("# of Sightings");

        // get rid of decimal points in our range labels:
        plot1.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("0.0"));

        plot1.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new Format() {

                    // create a simple date format that draws on the year portion of our timestamp.
                    // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
                    // for a full description of SimpleDateFormat.
                    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");

                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo,
                            FieldPosition pos) {

                        // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                        // we multiply our timestamp by 1000:
                        long timestamp = ((Number) obj).longValue() * 1000;
                        Date date = new Date(timestamp);
                        return dateFormat.format(date, toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;

                    }
                });
    }
}