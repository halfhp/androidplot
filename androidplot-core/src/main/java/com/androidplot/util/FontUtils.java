// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.graphics.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FontUtils {

    private static final int ZERO = 0;

    /**
     * Determines the height of the tallest character that can be drawn by paint.
     * @param paint
     * @return
     */
    public static float getFontHeight(@NonNull Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (-metrics.ascent) + metrics.descent;
        //return (-metrics.top) + metrics.bottom;
    }

    /**
     * Get the smallest rect that ecompasses the text to be drawn using paint.
     * @param text
     * @param paint
     * @return
     */
    @NonNull
    public static Rect getPackedStringDimensions(@NonNull String text, @NonNull Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, ZERO, text.length(), size);
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
    @Nullable
    public static Rect getStringDimensions(@Nullable String text, @NonNull Paint paint) {
        Rect size = new Rect();
        if(text == null || text.length() == ZERO) {
            return null;
        }
        paint.getTextBounds(text, ZERO, text.length(), size);
        size.bottom = size.top + (int) getFontHeight(paint);
        return size;
    }

    /**
     * Draws text vertically centered on the specified coordinates
     * @param canvas
     * @param paint
     * @param text
     * @param cx
     * @param cy
     */
    public static void drawTextVerticallyCentered(@NonNull Canvas canvas, @NonNull String text, float cx, float cy, @NonNull Paint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint);
    }

}
