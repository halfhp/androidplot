package com.androidplot.xy;

import android.graphics.Paint;

public class YValueMarker extends ValueMarker {
    public YValueMarker(Number value) {
        super(value);
    }

    public YValueMarker(Number value, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        super(value, linePaint, textPaint, backgroundPaint);
    }
}
