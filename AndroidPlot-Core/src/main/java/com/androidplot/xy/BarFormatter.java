package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;

public class BarFormatter extends XYSeriesFormatter {
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

    {
        fillPaint = new Paint();
        //fillPaint.setColor(Color.RED);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAlpha(100);

        borderPaint = new Paint();
        //borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAlpha(100);
    }

    public BarFormatter(int fillColor, int borderColor) {
        fillPaint.setColor(fillColor);
        borderPaint.setColor(borderColor);
    }
}
