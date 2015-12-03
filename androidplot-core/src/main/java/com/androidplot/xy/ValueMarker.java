/*
 * Copyright 2012 AndroidPlot.com
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

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.PositionMetric;

/**
 * Encapsulates a single axis line marker drawn onto an XYPlot at a specified value.
 * @param <PositionMetricType>
 */
public abstract class ValueMarker<PositionMetricType extends PositionMetric> {

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum TextOrientation {
        HORIZONTAL,
        VERTICAL
    }
    private Number value;
    private Paint linePaint;
    private Paint textPaint;
    //private Paint backgroundPaint;
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
        //backgroundPaint = new Paint();
        //backgroundPaint.setColor(Color.argb(100, 100, 100, 100));
        //backgroundPaint.setColor(Color.DKGRAY);

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
        //this.backgroundPaint = backgroundPaint;
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

    /*public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }*/

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
}
