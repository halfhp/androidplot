package com.androidplot.util;

import android.graphics.Paint;
import android.graphics.Rect;

public class FontUtils {

    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (-metrics.ascent) + metrics.descent;
    }

    @Deprecated
    public static float getStringHeight(String text, Paint paint) {
        Rect size = new Rect();
        //String label = getText();
        paint.getTextBounds(text, 0, text.length(), size);
        return size.height();
    }

    @Deprecated
    public static float getHalfStringHeight(String text, Paint paint) {
        return getStringHeight(text, paint)/2;
    }

    public static Rect getStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, 0, text.length(), size);
        size.bottom = size.top + (int) getFontHeight(paint);
        return size;
    }

}
