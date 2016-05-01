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
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import com.androidplot.candlestick.CandlestickFormatter;
import com.androidplot.candlestick.CandlestickMaker;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.List;

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
        setContentView(R.layout.candlestick_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);


        // create our min, max, high and low values:
        List<Number> xVals = Arrays.asList(new Number[]{1, 2, 3, 4, 5});
        XYSeries highVals = new SimpleXYSeries(xVals,
                Arrays.asList(12, 10, 15, 8, 7), "high");
        XYSeries lowVals = new SimpleXYSeries(xVals,
                Arrays.asList(3, 1, 5, 0, 2), "low");
        XYSeries openVals = new SimpleXYSeries(xVals,
                Arrays.asList(5, 2, 7, 5, 3), "open");
        XYSeries closeVals = new SimpleXYSeries(xVals,
                Arrays.asList(7, 9, 6, 0, 4), "close");

        // draw a simple line plot of the close vals:
        LineAndPointFormatter lpf = new LineAndPointFormatter(Color.WHITE, Color.WHITE, null, null);
        lpf.getLinePaint().setPathEffect(
                new DashPathEffect(
                        new float[]{PixelUtils.dpToPix(5), PixelUtils.dpToPix(5)}, 0));
        lpf.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(closeVals, lpf);

        CandlestickFormatter formatter = new CandlestickFormatter();

        // draw candlestick bodies as triangles instead of squares:
        // triangles will point up for items that closed higher than they opened
        // and down for those that closed lower:
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.Triangle);

        // add the candlestick series data to the plot:
        CandlestickMaker.make(plot, formatter,
                openVals, closeVals, highVals, lowVals);

        // setup the range label formatting, etc:
        plot.setRangeLabel("Amount");
        plot.setTicksPerRangeLabel(3);

        plot.setRangeValueFormat(new DecimalFormat("$0.00"));

        // setup the domain label formatting, etc:
        plot.setDomainBoundaries(0, 6, BoundaryMode.FIXED);
        plot.setDomainLabel("Day");
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
                switch(((Number) object).intValue()) {
                    case 1:
                        buffer.append("Mon");
                        break;
                    case 2:
                        buffer.append("Tues");
                        break;
                    case 3:
                        buffer.append("Wed");
                        break;
                    case 4:
                        buffer.append("Thurs");
                        break;
                    case 5:
                        buffer.append("Fri");
                        break;
                    default:
                        // show nothing

                }
                return buffer;
            }

            @Override
            public Object parseObject(String string, ParsePosition position) {
                return null;
            }
        });

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
}
