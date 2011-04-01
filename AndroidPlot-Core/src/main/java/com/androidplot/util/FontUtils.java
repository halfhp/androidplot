package com.androidplot.util;

import android.graphics.Paint;
import android.graphics.Rect;

public class FontUtils {

    /**
     * Determines the height of the tallest character that can be drawn by paint.
     * @param paint
     * @return
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (-metrics.ascent) + metrics.descent;
    }

    /**
     *
     * @param text
     * @param paint
     * @return
     */
    @Deprecated
    public static float getStringHeight(String text, Paint paint) {
        Rect size = new Rect();
        //String label = getText();
        paint.getTextBounds(text, 0, text.length(), size);
        return size.height();
    }

    /**
     * 
     * @param text
     * @param paint
     * @return
     */
    @Deprecated
    public static float getHalfStringHeight(String text, Paint paint) {
        return getStringHeight(text, paint)/2;
    }

    /**
     * Get the smallest rect that ecompasses the text to be drawn using paint.
     * @param text
     * @param paint
     * @return
     */
    public static Rect getPackedStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, 0, text.length(), size);
        return size;
    }

    /**
     * Like getPackedStringDimensions except adds extra space to accommodate all
     * characters that can be drawn regardless of whether or not they exist in text.
     * This ensures a more uniform appearance for things that have dynamic text.
     * @param text
     * @param paint
     * @return
     */
    public static Rect getStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, 0, text.length(), size);
        size.bottom = size.top + (int) getFontHeight(paint);
        return size;
    }

}
