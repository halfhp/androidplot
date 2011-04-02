package com.androidplot.xy;

import android.graphics.Paint;

public class XValueMarker extends ValueMarker {
    public XValueMarker(Number value) {
        super(value);
    }

    public XValueMarker(Number value, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        super(value, linePaint, textPaint, backgroundPaint);
    }
}
