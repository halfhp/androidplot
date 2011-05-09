package com.androidplot.xy;

import android.graphics.Paint;

public class XValueMarker extends ValueMarker<YPositionMetric> {

    public XValueMarker(Number value, String text) {
        super(value, text, new YPositionMetric(3, YLayoutStyle.ABSOLUTE_FROM_TOP));
    }

    public XValueMarker(Number value, String text, YPositionMetric textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    public XValueMarker(Number value, String text, YPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }
}
