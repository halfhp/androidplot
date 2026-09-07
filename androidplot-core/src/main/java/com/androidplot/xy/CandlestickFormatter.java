// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.*;
import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;
import androidx.annotation.NonNull;

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

    @NonNull
    protected static Paint getDefaultFillPaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        return p;
    }

    @NonNull
    protected static Paint getDefaultStrokePaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(DEFAULT_STROKE_PIX);
        p.setColor(color);
        p.setAntiAlias(true);
        return p;
    }

    public CandlestickFormatter(@NonNull Context context, int xmlCfgId) {
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

    public CandlestickFormatter(@NonNull Paint wickPaint, @NonNull Paint risingBodyFillPaint, @NonNull Paint fallingBodyFillPaint,
                                @NonNull Paint risingBodyStrokePaint, @NonNull Paint fallingBodyStrokePaint,
                                @NonNull Paint upperCapPaint, @NonNull Paint lowerCapPaint, @NonNull BodyStyle bodyStyle) {
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
    @NonNull
    public Class<? extends SeriesRenderer> getRendererClass() {
        return CandlestickRenderer.class;
    }

    @Override
    @NonNull
    public SeriesRenderer doGetRendererInstance(@NonNull XYPlot plot) {
        return new CandlestickRenderer(plot);
    }

    @NonNull
    public Paint getWickPaint() {
        return wickPaint;
    }

    public void setWickPaint(@NonNull Paint wickPaint) {
        this.wickPaint = wickPaint;
    }

    @NonNull
    public Paint getRisingBodyFillPaint() {
        return risingBodyFillPaint;
    }

    public void setRisingBodyFillPaint(@NonNull Paint risingBodyFillPaint) {
        this.risingBodyFillPaint = risingBodyFillPaint;
    }

    @NonNull
    public Paint getRisingBodyStrokePaint() {
        return risingBodyStrokePaint;
    }

    public void setRisingBodyStrokePaint(@NonNull Paint risingBodyStrokePaint) {
        this.risingBodyStrokePaint = risingBodyStrokePaint;
    }

    @NonNull
    public Paint getUpperCapPaint() {
        return upperCapPaint;
    }

    public void setUpperCapPaint(@NonNull Paint upperCapPaint) {
        this.upperCapPaint = upperCapPaint;
    }

    @NonNull
    public Paint getLowerCapPaint() {
        return lowerCapPaint;
    }

    public void setLowerCapPaint(@NonNull Paint lowerCapPaint) {
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

    @NonNull
    public Paint getFallingBodyFillPaint() {
        return fallingBodyFillPaint;
    }

    public void setFallingBodyFillPaint(@NonNull Paint fallingBodyFillPaint) {
        this.fallingBodyFillPaint = fallingBodyFillPaint;
    }

    @NonNull
    public Paint getFallingBodyStrokePaint() {
        return fallingBodyStrokePaint;
    }

    public void setFallingBodyStrokePaint(@NonNull Paint fallingBodyStrokePaint) {
        this.fallingBodyStrokePaint = fallingBodyStrokePaint;
    }

    @NonNull
    public BodyStyle getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(@NonNull BodyStyle bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    /**
     * Convenience method to set caps and wick to a single color in one call.
     * @param paint
     */
    public void setCapAndWickPaint(@NonNull Paint paint) {
        setUpperCapPaint(paint);
        setLowerCapPaint(paint);
        setWickPaint(paint);
    }
}
