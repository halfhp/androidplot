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
import android.support.annotation.NonNull;

import com.androidplot.Region;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.SeriesUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CandlestickFormatter;
import com.androidplot.xy.CandlestickMaker;
import com.androidplot.xy.CandlestickSeries;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.PointLabeler;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A simple example of a candlestick chart rendered on an {@link XYPlot}.
 */
public class CandlestickChartActivity extends Activity {

    private XYPlot plot;

    private DecimalFormat currencyFormat = new DecimalFormat("$0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candlestick_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        final CandlestickSeries candlestickSeries = new CandlestickSeries(
                new CandlestickSeries.Item(1, 10, 2, 9.04),
                new CandlestickSeries.Item(4, 18, 6, 5.50),
                new CandlestickSeries.Item(3, 11, 5, 9.21),
                new CandlestickSeries.Item(2, 17, 2, 15.25),
                new CandlestickSeries.Item(6, 11, 11, 7.12),
                new CandlestickSeries.Item(8, 16, 10, 15.02));

        // draw a simple line plot of the close vals:
        LineAndPointFormatter lpf = new LineAndPointFormatter(Color.BLACK, Color.BLACK, null, null);
        lpf.getLinePaint().setPathEffect(
                new DashPathEffect(
                        new float[] {PixelUtils.dpToPix(5), PixelUtils.dpToPix(5)}, 0));
        lpf.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        plot.addSeries(candlestickSeries.getCloseSeries(), lpf);

        CandlestickFormatter formatter = new CandlestickFormatter(this, R.xml.candlestick_formatter);

        // draw candlestick bodies as triangles instead of squares:
        // triangles will point up for items that closed higher than they opened
        // and down for those that closed lower:
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.SQUARE);

        // add the candlestick series data to the plot:
        CandlestickMaker.make(plot, formatter, candlestickSeries);

        // setup the range tick label formatting, etc:
        plot.setLinesPerRangeLabel(3);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(currencyFormat);

        // add some padding to range boundaries:
        final Region minMax = SeriesUtils.minMax(
                candlestickSeries.getHighSeries().getyVals(),
                candlestickSeries.getLowSeries().getyVals());

        plot.setRangeBoundaries(
                minMax.getMin().doubleValue() - 1,
                minMax.getMax().doubleValue() + 1,
                BoundaryMode.FIXED);


        // setup the domain tick label formatting, etc:
        plot.setDomainBoundaries(-1, 6, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, @NonNull StringBuffer buffer,
                                       @NonNull FieldPosition field) {
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
            public Object parseObject(String string, @NonNull ParsePosition position) {
                return null;
            }
        });

        formatter.setPointLabelFormatter(
                new PointLabelFormatter(Color.BLACK, PixelUtils.dpToPix(8), 0));
        formatter.getPointLabelFormatter().getTextPaint().setFakeBoldText(true);
        formatter.getPointLabelFormatter().getTextPaint().setTextAlign(Paint.Align.LEFT);

        // add labels for close vals:
        formatter.setPointLabeler(new PointLabeler() {
            @Override
            public String getLabel(XYSeries series, int index) {
                if(series == candlestickSeries.getCloseSeries()) {
                    return currencyFormat.format(series.getY(index).doubleValue());
                }
                return null;
            }
        });
    }
}
