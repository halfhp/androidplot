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

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesBundle;

import java.util.List;

/**
 * Renders a group of {@link com.androidplot.xy.XYSeries} as a candlestick chart
 * into an {@link com.androidplot.xy.XYPlot}.
 *
 * Constraints:
 * - Exactly four series must be added using the same {@link CandlestickFormatter}.
 * - Each of the four series has the same x(i) value.
 * - Expects that series are added in the order of:
 * high, low, open, close
 *
 * {@link CandlestickSeries} and {@link CandlestickMaker} provide simplified classes and methods
 * for setting up a candlestick chart.
 * @since 0.9.7
 */
public class CandlestickRenderer<FormatterType extends CandlestickFormatter> extends GroupRenderer<FormatterType> {

    protected static final int HIGH_INDEX = 0;
    protected static final int LOW_INDEX = 1;
    protected static final int OPEN_INDEX = 2;
    protected static final int CLOSE_INDEX = 3;

    public CandlestickRenderer(XYPlot plot) {
        super(plot);
    }


    @Override
    public void onRender(Canvas canvas, RectF plotArea, List<SeriesBundle<XYSeries,
                ? extends FormatterType>> sfList, int seriesSize,  RenderStack stack) {

        final FormatterType formatter = sfList.get(0).getFormatter();
        for(int i = 0; i < seriesSize; i++) {

            final XYSeries highSeries = sfList.get(HIGH_INDEX).getSeries();
            final XYSeries lowSeries = sfList.get(LOW_INDEX).getSeries();
            final XYSeries openSeries = sfList.get(OPEN_INDEX).getSeries();
            final XYSeries closeSeries = sfList.get(CLOSE_INDEX).getSeries();

            // x-val for all series should be identical so just grab x from the first series:
            Number x = highSeries.getX(i);

            Number high = highSeries.getY(i);
            Number low = lowSeries.getY(i);
            Number open = openSeries.getY(i);
            Number close = closeSeries.getY(i);

            // draw the candlestick:
            final PointF highPix = getPlot().getBounds().transformScreen(x, high, plotArea);
            final PointF lowPix = getPlot().getBounds().transformScreen(x, low, plotArea);
            final PointF openPix = getPlot().getBounds().transformScreen(x, open, plotArea);
            final PointF closePix = getPlot().getBounds().transformScreen(x, close, plotArea);

            drawWick(canvas, highPix, lowPix, formatter);
            drawBody(canvas, openPix, closePix, formatter);
            drawUpperCap(canvas, highPix, formatter);
            drawLowerCap(canvas, lowPix, formatter);

            // draw labels, if any:
            final PointLabelFormatter plf = formatter.hasPointLabelFormatter()
                                            ? formatter.getPointLabelFormatter() : null;
            final PointLabeler pointLabeler = formatter.getPointLabeler();
            if(plf != null && pointLabeler != null) {
                drawTextLabel(canvas, highPix, pointLabeler.getLabel(highSeries, i), plf);
                drawTextLabel(canvas, lowPix, pointLabeler.getLabel(lowSeries, i), plf);
                drawTextLabel(canvas, openPix, pointLabeler.getLabel(openSeries, i), plf);
                drawTextLabel(canvas, closePix, pointLabeler.getLabel(closeSeries, i), plf);
            }
        }
    }

    protected void drawTextLabel(Canvas canvas, PointF coords, String text, PointLabelFormatter plf) {
        if(text != null) {
            canvas.drawText(text, coords.x + plf.hOffset, coords.y + plf.vOffset, plf.getTextPaint());
        }
    }

    protected void drawWick(Canvas canvas, PointF min, PointF max, FormatterType formatter) {
        canvas.drawLine(min.x, min.y, max.x, max.y, formatter.getWickPaint());
    }

    protected void drawBody(Canvas canvas, PointF open, PointF close, FormatterType formatter) {
        final float halfWidth = formatter.getBodyWidth() / 2;
        final RectF rect = new RectF(open.x - halfWidth, open.y, close.x + halfWidth, close.y);

        Paint bodyFillPaint = open.y >= close.y ?
                formatter.getRisingBodyFillPaint() : formatter.getFallingBodyFillPaint();

        Paint bodyStrokePaint = open.y >= close.y ?
                formatter.getRisingBodyStrokePaint() : formatter.getFallingBodyStrokePaint();

        switch(formatter.getBodyStyle()) {
            case SQUARE:
                canvas.drawRect(rect, bodyFillPaint);
                canvas.drawRect(rect, bodyStrokePaint);
                break;
            case TRIANGULAR:
                drawTriangle(canvas, rect, bodyFillPaint, bodyStrokePaint);
        }
    }

    protected void drawUpperCap(Canvas canvas, PointF val, FormatterType formatter) {
        final float halfWidth = formatter.getUpperCapWidth();
        canvas.drawLine(val.x - halfWidth, val.y, val.x + halfWidth, val.y, formatter.getUpperCapPaint());
    }

    protected void drawLowerCap(Canvas canvas, PointF val, FormatterType formatter) {
        final float halfWidth = formatter.getLowerCapWidth();
        canvas.drawLine(val.x - halfWidth, val.y, val.x + halfWidth, val.y, formatter.getLowerCapPaint());
    }

    @Override
    protected void doDrawLegendIcon(Canvas canvas, RectF rect, FormatterType formatter) {
        // TODO
    }

    protected void drawTriangle(Canvas canvas, RectF rect,
                                Paint fillPaint, Paint strokePaint) {
        Path path = new Path();
        path.moveTo(rect.centerX(), rect.bottom);
        path.lineTo(rect.left,rect.top);
        path.lineTo(rect.right, rect.top);
        path.close();
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);
    }
}
