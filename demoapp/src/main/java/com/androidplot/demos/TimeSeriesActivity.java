// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_series_example);
        XYPlot plot = findViewById(R.id.plot1);

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

        Number[] yVals = {5, 8, 6, 9, 3, 8, 5, 4, 7, 4};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(yVals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Sightings in USA");

        LineAndPointFormatter formatter =
                new LineAndPointFormatter(Color.BLACK, Color.RED, Color.RED, null);
        formatter.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(10));
        formatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(5));

        // semi-transparent black fill under the line
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        formatter.setFillPaint(lineFill);

        plot.addSeries(series, formatter);

        plot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);

        plot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraph().getDomainGridLinePaint().setColor(Color.BLACK);
        plot.getGraph().getDomainGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot.getGraph().getRangeGridLinePaint().setColor(Color.BLACK);
        plot.getGraph().getRangeGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot.getGraph().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraph().getRangeOriginLinePaint().setColor(Color.BLACK);

        plot.getGraph().setPaddingRight(2);

        // customize our domain/range labels
        plot.setDomainLabel("Year");
        plot.setRangeLabel("# of Sightings");

        // one decimal place on range labels
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("0.0"));

        // draw a domain tick for each year:
        plot.setDomainStep(StepMode.SUBDIVIDE, years.length);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new Format() {

                    // formats the timestamp at this index as "MMM yyyy"
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
}
