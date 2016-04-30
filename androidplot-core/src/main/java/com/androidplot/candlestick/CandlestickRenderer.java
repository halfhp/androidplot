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

package com.androidplot.candlestick;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.RenderStack;
import com.androidplot.util.ValPixConverter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeriesRenderer;

/**
 * Renders {@link CandlestickSeries} data into an {@link com.androidplot.xy.XYPlot}.
 */
public class CandlestickRenderer<FormatterType extends CandlestickFormatter> extends XYSeriesRenderer<CandlestickSeries, FormatterType> {

    public CandlestickRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea, CandlestickSeries series, FormatterType formatter, RenderStack stack)
            throws PlotRenderException {
        for(int i = 0; i < series.size(); i++) {
            Number y = series.getY(i);
            Number x = series.getX(i);
            Number z = series.getZ(i);
            Number a = series.getA(i);
            Number b = series.getB(i);
            drawValue(canvas, plotArea, formatter, x, y, z, a, b);
        }
    }

    protected void drawValue(Canvas canvas, RectF plotArea, FormatterType formatter,
                             Number x, Number y, Number z, Number a, Number b) {
        final PointF yPix = ValPixConverter.valToPix(
                x, y,
                plotArea,
                getPlot().getCalculatedMinX(),
                getPlot().getCalculatedMaxX(),
                getPlot().getCalculatedMinY(),
                getPlot().getCalculatedMaxY());

        final PointF zPix = ValPixConverter.valToPix(
                x, z,
                plotArea,
                getPlot().getCalculatedMinX(),
                getPlot().getCalculatedMaxX(),
                getPlot().getCalculatedMinY(),
                getPlot().getCalculatedMaxY());

        final PointF aPix = ValPixConverter.valToPix(
                x, a,
                plotArea,
                getPlot().getCalculatedMinX(),
                getPlot().getCalculatedMaxX(),
                getPlot().getCalculatedMinY(),
                getPlot().getCalculatedMaxY());

        final PointF bPix = ValPixConverter.valToPix(
                x, b,
                plotArea,
                getPlot().getCalculatedMinX(),
                getPlot().getCalculatedMaxX(),
                getPlot().getCalculatedMinY(),
                getPlot().getCalculatedMaxY());

        drawWick(canvas, zPix, yPix, formatter);
        drawBody(canvas, bPix, aPix, formatter);
        drawUpperCap(canvas, yPix, formatter);
        drawLowerCap(canvas, zPix, formatter);
    }

    protected void drawWick(Canvas canvas, PointF min, PointF max, FormatterType formatter) {
        canvas.drawLine(min.x, min.y, max.x, max.y, formatter.getWickPaint());
    }

    protected void drawBody(Canvas canvas, PointF min, PointF max, FormatterType formatter) {
        final float halfWidth = formatter.getBodyWidth() / 2;
        final RectF rect = new RectF(min.x - halfWidth, min.y, max.x + halfWidth, max.y);
        canvas.drawRect(rect, formatter.getBodyFillPaint());
        canvas.drawRect(rect, formatter.getBodyStrokePaint());
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
}
