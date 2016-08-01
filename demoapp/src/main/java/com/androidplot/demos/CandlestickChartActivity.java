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
import android.graphics.Paint;
import android.os.Bundle;

import com.androidplot.xy.CandlestickFormatter;
import com.androidplot.xy.CandlestickMaker;
import com.androidplot.xy.CandlestickSeries;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A simple example of a candlestick chart rendered on an {@link XYPlot}.
 */
public class CandlestickChartActivity extends Activity {

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candlestick_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        CandlestickSeries candlestickSeries = new CandlestickSeries(
                new CandlestickSeries.Item(1, 10, 2, 9),
                new CandlestickSeries.Item(4, 18, 6, 5),
                new CandlestickSeries.Item(3, 11, 5, 10),
                new CandlestickSeries.Item(2, 17, 2, 15),
                new CandlestickSeries.Item(6, 11, 11, 7),
                new CandlestickSeries.Item(8, 16, 10, 15));

        // draw a simple line plot of the close vals:
        LineAndPointFormatter lpf = new LineAndPointFormatter(Color.BLACK, Color.BLACK, null, null);
        lpf.getLinePaint().setPathEffect(
                new DashPathEffect(
                        new float[] {PixelUtils.dpToPix(5), PixelUtils.dpToPix(5)}, 0));
        lpf.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(candlestickSeries.getCloseSeries(), lpf);

        CandlestickFormatter formatter = new CandlestickFormatter();
        Paint p = formatter.getWickPaint();
        p.setColor(Color.BLACK);
        formatter.setCapAndWickPaint(p);
        formatter.setRisingBodyStrokePaint(p);
        formatter.setFallingBodyStrokePaint(p);

        // draw candlestick bodies as triangles instead of squares:
        // triangles will point up for items that closed higher than they opened
        // and down for those that closed lower:
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.Square);

        // add the candlestick series data to the plot:
        CandlestickMaker.make(plot, formatter, candlestickSeries);

        // setup the range tick label formatting, etc:
        plot.setLinesPerRangeLabel(3);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("$0.00"));

        // setup the domain tick label formatting, etc:
        plot.setDomainBoundaries(-1, 6, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer buffer,
                    FieldPosition field) {
                int day = ((Number) object).intValue() % 7;
                switch (day) {
                    case 0:
                        buffer.append("Sun");
                        break;
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
                    case 6:
                        buffer.append("Sat");
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
    }
}
