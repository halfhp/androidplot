package com.androidplot.xy;

import android.graphics.Paint;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.XPositionMetric;

public class YValueMarker extends ValueMarker<XPositionMetric> {


    public YValueMarker(Number value) {
        super(value, new XPositionMetric(3, XLayoutStyle.ABSOLUTE_FROM_LEFT));
    }

    public YValueMarker(Number value, XPositionMetric textPosition, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        super(value, textPosition, linePaint, textPaint, backgroundPaint);
    }
}
