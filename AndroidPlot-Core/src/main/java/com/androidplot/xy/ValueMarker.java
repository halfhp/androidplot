package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.method.NumberKeyListener;

public class ValueMarker {
    private Number value;
    private Paint linePaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    {
        linePaint = new Paint();
        linePaint.setColor(Color.MAGENTA);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.MAGENTA);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(100, 100, 100, 100));
        //backgroundPaint.setColor(Color.DKGRAY);

    }

    public ValueMarker(Number value) {
        this.value = value;
    }

    public ValueMarker(Number value, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        this(value);
        this.linePaint = linePaint;
        this.textPaint = textPaint;
        this.backgroundPaint = backgroundPaint;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }
}
