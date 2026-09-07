// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;
import android.content.*;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;

public class BarFormatter extends LineAndPointFormatter {

    /**
     * @return The fill paint, or null if none has been set.  Unlike
     * {@link LineAndPointFormatter#getFillPaint()} no default is instantiated.
     */
    @Override
    public Paint getFillPaint() {
        return fillPaint;
    }

    /**
     * @return The border paint, or null if none has been set.  Bar borders are stored as the
     * inherited line paint, so {@link #hasLinePaint()} reports whether a border will be drawn.
     */
    public Paint getBorderPaint() {
        return linePaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.linePaint = borderPaint;
    }

    private float marginTop;
    private float marginBottom;
    private float marginLeft;
    private float marginRight;

    /**
     * Should only be used in conjunction with calls to configure()...
     */
    public BarFormatter() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAlpha(100);
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAlpha(100);
    }

    public BarFormatter(int fillColor, int borderColor) {
        this();
        fillPaint.setColor(fillColor);
        linePaint.setColor(borderColor);
    }

    public BarFormatter(Context context, int xmlCfgId) {
        this();
        configure(context, xmlCfgId);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return BarRenderer.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(XYPlot plot) {
        return new BarRenderer(plot);
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }
}
