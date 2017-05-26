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
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.NormedXYSeries;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * Demonstrates normalizing series data using {@link com.androidplot.xy.NormedXYSeries}
 * and displaying a dual scale on an XYPlot.
 *
 * This particular example displays the yearly cost of raising of a child along the federal
 * minimum wage from 2012 to 2016.
 *
 */
public class DualScaleActivity extends Activity {

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dual_scale_example);
        plot = (XYPlot) findViewById(R.id.plot);

        Number[] childCosts = {5500, 5550,5496, 5800, 5815};
        Number[] minWages = {9, 9, 9, 9, 10};

        // create and normalize series data:
        final NormedXYSeries costsSeries = new NormedXYSeries(new SimpleXYSeries(
                Arrays.asList(childCosts), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Yearly Cost"));
        final NormedXYSeries minWageSeries = new NormedXYSeries(new SimpleXYSeries(
                Arrays.asList(minWages), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Min Wage"));

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter childCostsFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
        childCostsFormat.setVertexPaint(null);
        childCostsFormat.setPointLabelFormatter(null);

        LineAndPointFormatter minWageFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_2);
        minWageFormat.setVertexPaint(null);
        minWageFormat.setPointLabelFormatter(null);

        // add an "dash" effect to the series2 line:
        minWageFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));

        // add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        childCostsFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        minWageFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        plot.addSeries(costsSeries, childCostsFormat);
        plot.addSeries(minWageSeries, minWageFormat);

        plot.setRangeBoundaries(-1, 2, BoundaryMode.FIXED);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.GREEN);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {

            final DecimalFormat df = new DecimalFormat("$#,###");

            @Override
            public StringBuffer format(Object o, @NonNull StringBuffer stringBuffer,
                                       @NonNull FieldPosition fieldPosition) {
                final Number cost = costsSeries.denormalizeYVal((Number) o);
                stringBuffer.append(df.format(cost.doubleValue()));
                return stringBuffer;
            }

            @Override
            public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
                return null;
            }
        });

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.RIGHT).getPaint().setColor(Color.BLUE);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.RIGHT).setFormat(new Format() {

            final DecimalFormat df = new DecimalFormat("$#.##");

            @Override
            public StringBuffer format(Object o, @NonNull StringBuffer stringBuffer,
                                       @NonNull FieldPosition fieldPosition) {
                Number minWage = minWageSeries.denormalizeYVal((Number) o);
                stringBuffer.append(df.format(minWage.doubleValue()));
                return stringBuffer;
            }

            @Override
            public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
                return null;
            }
        });

        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {

            @Override
            public StringBuffer format(Object o, @NonNull StringBuffer stringBuffer,
                                       @NonNull FieldPosition fieldPosition) {

                Number year = ((Number) o).intValue() + 2012;
                stringBuffer.append(year);
                return stringBuffer;
            }

            @Override
            public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
                return null;
            }
        });
    }
}
