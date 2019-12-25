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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeSeriesActivity extends Activity {

    private static final String SERIES_TITLE = "Signthings in USA";

    private XYPlot plot1;
    private SimpleXYSeries series;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_series_example);

        plot1 = (XYPlot) findViewById(R.id.plot1);

        // these will be our domain index labels:
        final Date[] years = {
                new GregorianCalendar(2001, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2001, Calendar.JULY, 1).getTime(),
                new GregorianCalendar(2002, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2002, Calendar.JULY, 1).getTime(),
                new GregorianCalendar(2003, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2003, Calendar.JULY, 1).getTime(),
                new GregorianCalendar(2004, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2004, Calendar.JULY, 1).getTime(),
                new GregorianCalendar(2005, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2005, Calendar.JULY, 1).getTime()
        };

        addSeries(savedInstanceState);

        plot1.setRangeBoundaries(0, 10, BoundaryMode.FIXED);

        plot1.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        plot1.getGraph().getDomainGridLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getDomainGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraph().getRangeGridLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getRangeGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraph().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot1.getGraph().getRangeOriginLinePaint().setColor(Color.BLACK);

        plot1.getGraph().setPaddingRight(2);

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
                    @SuppressLint("SimpleDateFormat")
                    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");

                    @Override
                    public StringBuffer format(Object obj,
                                               @NonNull StringBuffer toAppendTo,
                                               @NonNull FieldPosition pos) {

                        // this rounding is necessary to avoid precision loss when converting from
                        // double back to int:
                        int yearIndex = (int) Math.round(((Number) obj).doubleValue());
                        return dateFormat.format(years[yearIndex], toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, @NonNull ParsePosition pos) {
                        return null;

                    }
                });
    }

    /**
     * Instantiates our XYSeries, checking the current savedInstanceState for existing series data
     * to avoid having to regenerate on each resume.  If your series data is small and easy to
     * regenerate (as it is here) then you can skip saving/restoring your series data to
     * savedInstanceState.
     * @param savedInstanceState Current saved instance state, if any; may be null.
     */
    private void addSeries(Bundle savedInstanceState) {
        Number[] yVals;

        if(savedInstanceState != null) {
            yVals = (Number[]) savedInstanceState.getSerializable(SERIES_TITLE);
        } else {
            yVals = new Number[]{5, 8, 6, 9, 3, 8, 5, 4, 7, 4};
        }

        // create our series from our array of nums:
        series = new SimpleXYSeries(Arrays.asList(yVals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, SERIES_TITLE);

        LineAndPointFormatter formatter =
                new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.RED, Color.RED, null);
        formatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(10));
        formatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(5));

        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);

        formatter.setFillPaint(lineFill);

        plot1.addSeries(series, formatter);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        // persist our series data so we don't have to regenerate each time:
        bundle.putSerializable(SERIES_TITLE, series.getyVals().toArray(new Number[]{}));
    }
}