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

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYRegionFormatter;
import com.androidplot.xy.XYSeriesFormatter;

/**
 * Format for drawing a value in a {@link CandlestickSeries}.
 */
public class CandlestickFormatter extends XYSeriesFormatter<XYRegionFormatter> {

    private Paint wickPaint = getDefaultStrokePaint(1.5f, Color.GREEN);
    private Paint bodyFillPaint = getDefaultFillPaint(Color.YELLOW);
    private Paint bodyStrokePaint = getDefaultStrokePaint(1.5f, Color.GREEN);
    private Paint upperCapPaint = getDefaultStrokePaint(1.5f, Color.GREEN);
    private Paint lowerCapPaint = getDefaultStrokePaint(1.5f, Color.GREEN);

    private float bodyWidth = PixelUtils.dpToPix(10f);
    private float upperCapWidth = PixelUtils.dpToPix(10f);
    private float lowerCapWidth = PixelUtils.dpToPix(10f);

    protected static Paint getDefaultFillPaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        return p;
    }

    protected static Paint getDefaultStrokePaint(float strokeDp, int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(PixelUtils.dpToPix(strokeDp));
        p.setColor(color);
        return p;
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return CandlestickRenderer.class;
    }

    @Override
    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new CandlestickRenderer(plot);
    }

    public Paint getWickPaint() {
        return wickPaint;
    }

    public void setWickPaint(Paint wickPaint) {
        this.wickPaint = wickPaint;
    }

    public Paint getBodyFillPaint() {
        return bodyFillPaint;
    }

    public void setBodyFillPaint(Paint bodyFillPaint) {
        this.bodyFillPaint = bodyFillPaint;
    }

    public Paint getBodyStrokePaint() {
        return bodyStrokePaint;
    }

    public void setBodyStrokePaint(Paint bodyStrokePaint) {
        this.bodyStrokePaint = bodyStrokePaint;
    }

    public Paint getUpperCapPaint() {
        return upperCapPaint;
    }

    public void setUpperCapPaint(Paint upperCapPaint) {
        this.upperCapPaint = upperCapPaint;
    }

    public Paint getLowerCapPaint() {
        return lowerCapPaint;
    }

    public void setLowerCapPaint(Paint lowerCapPaint) {
        this.lowerCapPaint = lowerCapPaint;
    }

    public float getBodyWidth() {
        return bodyWidth;
    }

    public void setBodyWidth(float bodyWidth) {
        this.bodyWidth = bodyWidth;
    }

    public float getLowerCapWidth() {
        return lowerCapWidth;
    }

    public void setLowerCapWidth(float lowerCapWidth) {
        this.lowerCapWidth = lowerCapWidth;
    }

    public float getUpperCapWidth() {
        return upperCapWidth;
    }

    public void setUpperCapWidth(float upperCapWidth) {
        this.upperCapWidth = upperCapWidth;
    }
}
