/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

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
        //return (-metrics.top) + metrics.bottom;
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
