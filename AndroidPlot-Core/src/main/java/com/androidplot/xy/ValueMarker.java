/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
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
