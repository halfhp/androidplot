package com.androidplot.xy;

import android.graphics.Paint;

public class BezierLineAndPointFormatter extends LineAndPointFormatter {
    /*public BezierLineAndPointFormatter(Paint linePaint, Paint vertexPaint, Paint fillPaint) {
        super(linePaint, vertexPaint, fillPaint);
    }*/

    public BezierLineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor) {
        super(lineColor, vertexColor, fillColor);
    }
}
