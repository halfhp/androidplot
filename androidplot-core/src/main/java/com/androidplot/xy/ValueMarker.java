// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.ui.PositionMetric;
import com.androidplot.ui.TextOrientation;
import com.androidplot.util.FontUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Encapsulates a single axis line marker drawn onto an XYPlot at a specified value.
 * @param <PositionMetricType>
 */
public abstract class ValueMarker<PositionMetricType extends PositionMetric> {

    private static final int MARKER_LABEL_SPACING = 2;

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
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

    public ValueMarker(@Nullable Number value, @Nullable String text, @NonNull PositionMetricType textPosition) {
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
    public ValueMarker(@Nullable Number value, @Nullable String text, @NonNull PositionMetricType textPosition, @NonNull Paint linePaint, @NonNull Paint textPaint) {
        this(value, text, textPosition);
        this.linePaint = linePaint;
        this.textPaint = textPaint;
    }

    public ValueMarker(@Nullable Number value, @Nullable String text, @NonNull PositionMetricType textPosition, int linePaint, int textPaint) {
        this(value, text, textPosition);
        this.linePaint.setColor(linePaint);
        this.textPaint.setColor(textPaint);
    }

    @Nullable
    public Number getValue() {
        return value;
    }

    public void setValue(@Nullable Number value) {
        this.value = value;
    }

    @NonNull
    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(@NonNull Paint linePaint) {
        this.linePaint = linePaint;
    }

    @NonNull
    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(@NonNull Paint textPaint) {
        this.textPaint = textPaint;
    }

    @Nullable
    public TextOrientation getTextOrientation() {
        return textOrientation;
    }

    /**
     * Currently not implemented.  Sets the orientation of the text portion of this
     * ValueMarker.
     * @param textOrientation
     */
    public void setTextOrientation(@Nullable TextOrientation textOrientation) {
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

    @NonNull
    public PositionMetricType getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(@NonNull PositionMetricType textPosition) {
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
    protected void drawMarkerText(@NonNull Canvas canvas, @Nullable String text, @NonNull RectF gridRect,
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

    public abstract void draw(@NonNull Canvas canvas, @NonNull XYPlot plot, @NonNull RectF gridRect);
}
