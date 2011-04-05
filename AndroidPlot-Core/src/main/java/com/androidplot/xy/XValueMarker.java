package com.androidplot.xy;

import android.graphics.Paint;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.XPositionMetric;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.layout.YPositionMetric;

public class XValueMarker extends ValueMarker<YPositionMetric> {

    public XValueMarker(Number value) {
        super(value, new YPositionMetric(3, YLayoutStyle.ABSOLUTE_FROM_TOP));
    }

    public XValueMarker(Number value, YPositionMetric textPosition, Paint linePaint, Paint textPaint, Paint backgroundPaint) {
        super(value, textPosition, linePaint, textPaint, backgroundPaint);
    }
}
