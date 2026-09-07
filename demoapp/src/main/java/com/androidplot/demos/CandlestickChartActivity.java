// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;

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
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A simple example of a candlestick chart rendered on an {@link XYPlot}.
 */
public class CandlestickChartActivity extends Activity {

    private static final String[] DAYS = {"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candlestick_example);
        XYPlot plot = findViewById(R.id.plot);

        final DecimalFormat currencyFormat = new DecimalFormat("$0.00");

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

        // bodies default to SQUARE; try BodyStyle.TRIANGULAR (points up when close > open)
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.SQUARE);

        formatter.setPointLabelFormatter(
                new PointLabelFormatter(Color.BLACK, PixelUtils.dpToPix(8), 0));
        formatter.getPointLabelFormatter().getTextPaint().setFakeBoldText(true);
        formatter.getPointLabelFormatter().getTextPaint().setTextAlign(Paint.Align.LEFT);

        // add labels for close vals:
        formatter.setPointLabeler((series, index) -> {
            if(series == candlestickSeries.getCloseSeries()) {
                return currencyFormat.format(series.getY(index).doubleValue());
            }
            return null;
        });

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
                int day = ((Number) object).intValue();
                if (day >= 0) {
                    buffer.append(DAYS[day % 7]);
                }
                return buffer;
            }

            @Override
            public Object parseObject(String string, @NonNull ParsePosition position) {
                return null;
            }
        });
    }
}
