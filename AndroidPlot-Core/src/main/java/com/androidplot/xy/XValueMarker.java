package com.androidplot.xy;

import android.graphics.Paint;

public class XValueMarker extends ValueMarker<YPositionMetric> {

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     */
    public XValueMarker(Number value, String text) {
        super(value, text, new YPositionMetric(3, YLayoutStyle.ABSOLUTE_FROM_TOP));
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public XValueMarker(Number value, String text, YPositionMetric textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }


    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public XValueMarker(Number value, String text, YPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }
}
