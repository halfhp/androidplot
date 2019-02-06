/*
 * Copyright 2015 AndroidPlot.com
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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.ui.PositionMetric;
import com.androidplot.ui.TextOrientation;
import com.androidplot.util.FontUtils;

/**
 * Encapsulates a single axis line marker drawn onto an XYPlot at a specified value.
 * @param <PositionMetricType>
 */
public abstract class ValueMarker<PositionMetricType extends PositionMetric> {

    private static final int MARKER_LABEL_SPACING = 2;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private Number value;
    private Paint linePaint;
    private Paint textPaint;
    private TextOrientation textOrientation;
    private int textMargin = 2;
    private PositionMetricType textPosition;
    private String text;

    {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.RED);
    }

    public ValueMarker(Number value, String text, PositionMetricType textPosition) {
        this.value = value;
        this.textPosition = textPosition;
        this.text = text;
    }

    /**
     *
     * @param value
     * @param text
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public ValueMarker(Number value, String text, PositionMetricType textPosition, Paint linePaint, Paint textPaint) {
        this(value, text, textPosition);
        this.linePaint = linePaint;
        this.textPaint = textPaint;
    }

    public ValueMarker(Number value, String text, PositionMetricType textPosition, int linePaint, int textPaint) {
        this(value, text, textPosition);
        this.linePaint.setColor(linePaint);
        this.textPaint.setColor(textPaint);
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

    public PositionMetricType getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(PositionMetricType textPosition) {
        this.textPosition = textPosition;
    }

    /**
     * Renders the text associated with user defined markers
     *
     * @param canvas
     * @param text
     * @param gridRect
     * @param x
     * @param y
     */
    protected void drawMarkerText(Canvas canvas, String text, RectF gridRect,
                                  float x, float y) {
        if (getText() != null) {
            x += MARKER_LABEL_SPACING;
            y -= MARKER_LABEL_SPACING;
            RectF textRect = new RectF(FontUtils.getStringDimensions(text, getTextPaint()
            ));
            textRect.offsetTo(x, y - textRect.height());

            if (textRect.right > gridRect.right) {
                textRect.offset(-(textRect.right - gridRect.right), 0);
            }

            if (textRect.top < gridRect.top) {
                textRect.offset(0, gridRect.top - textRect.top);
            }

            canvas.drawText(text, textRect.left, textRect.bottom, getTextPaint()
            );
        }
    }

    public abstract void draw(Canvas canvas, XYPlot plot, RectF gridRect);
}
