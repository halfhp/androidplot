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

import android.content.Context;
import android.graphics.Paint;

/**
 * Convenience methods that operate on Paint instances.  These methods primarily deal with
 * converting pixel values to/from dp values.
 */
public class PaintUtils {

    public static Paint getPaint() {
        Paint p = new Paint();
        return p;
    }

    /**
     * Sets a paint instance's line stroke size in dp
     * @param context
     * @param paint
     * @param lineSizeDp
     */
    public static void setLineSizeDp(Context context, Paint paint, float lineSizeDp){
        paint.setStrokeWidth(PixelUtils.dpToPix(context, lineSizeDp));
    }

    /**
     * Sets a paint instance's font size in dp
     * @param context
     * @param paint
     * @param fontSizeDp
     */
    public static void setFontSizeDp(Context context, Paint paint, float fontSizeDp){
        paint.setTextSize(PixelUtils.dpToPix(context, fontSizeDp));
    }

    /**
     * Set a paint instance's shadowing using dp values
     * @param context
     * @param paint
     * @param radiusDp
     * @param dxDp
     * @param dyDp
     * @param color
     */
    public static void setShadowDp(Context context, Paint paint, float radiusDp, float dxDp, float dyDp, int color) {
        float radius = PixelUtils.dpToPix(context, radiusDp);
        float dx = PixelUtils.dpToPix(context, dxDp);
        float dy = PixelUtils.dpToPix(context, dyDp);
        paint.setShadowLayer(radius, dx, dy, color);
    }


}
