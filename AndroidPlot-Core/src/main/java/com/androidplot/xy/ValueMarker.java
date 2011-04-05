package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.layout.PositionMetric;

/**
 * Encapsulates a single axis line marker drawn onto an XYPlot at a specified value.
 * @param <PositionMetricType>
 */
public abstract class ValueMarker<PositionMetricType extends PositionMetric> {

    public PositionMetricType getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(PositionMetricType textPosition) {
        this.textPosition = textPosition;
    }

    public enum TextOrientation {
        HORIZONTAL,
        VERTICAL
    }
    private Number value;
    private Paint linePaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    private TextOrientation textOrientation;
    private int textMargin = 2;
    private PositionMetricType textPosition;

    {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.RED);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(100, 100, 100, 100));
        //backgroundPaint.setColor(Color.DKGRAY);

    }

    public ValueMarker(Number value, PositionMetricType textPosition) {
        this.value = value;
        this.textPosition = textPosition;
    }

    public ValueMarker(Number value, PositionMetricType textPosition, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        this(value, textPosition);
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

    public TextOrientation getTextOrientation() {
        return textOrientation;
    }

    /**
     * Currently not implemented.  Sets the orientation of the text portion of this
     * ValueMarker.
     * @param textOrientation
     */
    public void setTextOrientation(TextOrientation textOrientation) {
        this.textOrientation = textOrientation;
    }

    /**
     * Currently not implemented.
     * @return
     */
    public int getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(int textMargin) {
        this.textMargin = textMargin;
    }
}
