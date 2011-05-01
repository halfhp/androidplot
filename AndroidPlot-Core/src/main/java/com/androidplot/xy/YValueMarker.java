package com.androidplot.xy;

import android.graphics.Paint;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.XPositionMetric;

public class YValueMarker extends ValueMarker<XPositionMetric> {


    public YValueMarker(Number value, String text) {
        super(value, text, new XPositionMetric(3, XLayoutStyle.ABSOLUTE_FROM_LEFT));
    }

    public YValueMarker(Number value, String text, XPositionMetric textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    public YValueMarker(Number value, String text, XPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }
}
