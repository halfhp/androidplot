package com.androidplot.xy;

import android.graphics.Paint;

public class YValueMarker extends ValueMarker<XPositionMetric> {


    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     */
    public YValueMarker(Number value, String text) {
        super(value, text, new XPositionMetric(3, XLayoutStyle.ABSOLUTE_FROM_LEFT));
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public YValueMarker(Number value, String text, XPositionMetric textPosition, Paint linePaint, Paint textPaint) {
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
    public YValueMarker(Number value, String text, XPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }
}
