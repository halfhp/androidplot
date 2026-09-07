// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;
import android.content.*;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;

public class BarFormatter extends LineAndPointFormatter {

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    private Paint fillPaint;
    private Paint borderPaint;

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
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAlpha(100);
    }

    public BarFormatter(int fillColor, int borderColor) {
        this();
        fillPaint.setColor(fillColor);
        borderPaint.setColor(borderColor);
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
