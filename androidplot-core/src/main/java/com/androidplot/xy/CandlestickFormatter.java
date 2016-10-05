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

import android.content.*;
import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;

/**
 * Format for drawing a value using {@link CandlestickRenderer}.
 * @since 0.9.7
 */
public class CandlestickFormatter extends XYSeriesFormatter<XYRegionFormatter> {

    private static final float DEFAULT_WIDTH_PIX = PixelUtils.dpToPix(10);
    private static final float DEFAULT_STROKE_PIX = PixelUtils.dpToPix(4);

    private Paint wickPaint;
    private Paint risingBodyFillPaint;
    private Paint fallingBodyFillPaint;
    private Paint risingBodyStrokePaint;
    private Paint fallingBodyStrokePaint;
    private Paint upperCapPaint;
    private Paint lowerCapPaint;

    private float bodyWidth = DEFAULT_WIDTH_PIX;
    private float upperCapWidth = DEFAULT_WIDTH_PIX;
    private float lowerCapWidth = DEFAULT_WIDTH_PIX;

    private BodyStyle bodyStyle;

    public enum BodyStyle {
        SQUARE,
        TRIANGULAR
    }

    protected static Paint getDefaultFillPaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        return p;
    }

    protected static Paint getDefaultStrokePaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(DEFAULT_STROKE_PIX);
        p.setColor(color);
        p.setAntiAlias(true);
        return p;
    }

    public CandlestickFormatter(Context context, int xmlCfgId) {
        this();
        configure(context, xmlCfgId);
    }

    public CandlestickFormatter() {
        this(getDefaultStrokePaint(Color.YELLOW),
                getDefaultFillPaint(Color.GREEN),
                getDefaultFillPaint(Color.RED),
                getDefaultStrokePaint(Color.GREEN),
                getDefaultStrokePaint(Color.RED),
                getDefaultStrokePaint(Color.YELLOW),
                getDefaultStrokePaint(Color.YELLOW),
                BodyStyle.SQUARE);
    }

    public CandlestickFormatter(Paint wickPaint, Paint risingBodyFillPaint, Paint fallingBodyFillPaint,
                                Paint risingBodyStrokePaint, Paint fallingBodyStrokePaint,
                                Paint upperCapPaint, Paint lowerCapPaint, BodyStyle bodyStyle) {
        setWickPaint(wickPaint);
        setRisingBodyFillPaint(risingBodyFillPaint);
        setFallingBodyFillPaint(fallingBodyFillPaint);
        setRisingBodyStrokePaint(risingBodyStrokePaint);
        setFallingBodyStrokePaint(fallingBodyStrokePaint);
        setUpperCapPaint(upperCapPaint);
        setLowerCapPaint(lowerCapPaint);
        setBodyStyle(bodyStyle);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return CandlestickRenderer.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(XYPlot plot) {
        return new CandlestickRenderer(plot);
    }

    public Paint getWickPaint() {
        return wickPaint;
    }

    public void setWickPaint(Paint wickPaint) {
        this.wickPaint = wickPaint;
    }

    public Paint getRisingBodyFillPaint() {
        return risingBodyFillPaint;
    }

    public void setRisingBodyFillPaint(Paint risingBodyFillPaint) {
        this.risingBodyFillPaint = risingBodyFillPaint;
    }

    public Paint getRisingBodyStrokePaint() {
        return risingBodyStrokePaint;
    }

    public void setRisingBodyStrokePaint(Paint risingBodyStrokePaint) {
        this.risingBodyStrokePaint = risingBodyStrokePaint;
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

    public Paint getFallingBodyFillPaint() {
        return fallingBodyFillPaint;
    }

    public void setFallingBodyFillPaint(Paint fallingBodyFillPaint) {
        this.fallingBodyFillPaint = fallingBodyFillPaint;
    }

    public Paint getFallingBodyStrokePaint() {
        return fallingBodyStrokePaint;
    }

    public void setFallingBodyStrokePaint(Paint fallingBodyStrokePaint) {
        this.fallingBodyStrokePaint = fallingBodyStrokePaint;
    }

    public BodyStyle getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(BodyStyle bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    /**
     * Convenience method to set caps and wick to a single color in one call.
     * @param paint
     */
    public void setCapAndWickPaint(Paint paint) {
        setUpperCapPaint(paint);
        setLowerCapPaint(paint);
        setWickPaint(paint);
    }
}
