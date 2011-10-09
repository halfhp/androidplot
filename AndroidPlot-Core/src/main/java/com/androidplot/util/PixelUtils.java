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
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class PixelUtils {
    private static final float FLOAT_INT_AVG_NUDGE = 0.5f;

    public static PointF add(PointF lhs, PointF rhs) {
        return new PointF(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    public static PointF sub(PointF lhs, PointF rhs) {
        return new PointF(lhs.x - rhs.x, lhs.y - rhs.y);
    }

    /**
     * Converts a sub-pixel accurate RectF to a Rect
     * using the closest matching full pixel vals.  This is
     * useful for clipping operations etc.
     * @param rectIn The rect to be converted
     * @return
     */
    /*public static Rect toRect(RectF rectIn) {
        return new Rect(
                (int) (rectIn.left + FLOAT_INT_AVG_NUDGE),
                (int) (rectIn.top + FLOAT_INT_AVG_NUDGE),
                (int) (rectIn.right + FLOAT_INT_AVG_NUDGE),
                (int) (rectIn.bottom + FLOAT_INT_AVG_NUDGE));
    }*/

    /**
     * Converts a sub-pixel accurate RectF to
     * a single pixel accurate rect.  This is helpful
     * for clipping operations which dont do a good job with
     * subpixel vals.
     * @param in
     * @return
     */
    public static RectF sink(RectF in) {
        return nearestPixRect(in.left, in.top, in.right, in.bottom);
    }

    public static RectF nearestPixRect(float left, float top, float right, float bottom) {
        return new RectF(
                (int) (left + FLOAT_INT_AVG_NUDGE),
                (int) (top + FLOAT_INT_AVG_NUDGE),
                (int) (right + FLOAT_INT_AVG_NUDGE),
                (int) (bottom + FLOAT_INT_AVG_NUDGE));
    }

    /**
     * Converts a dp value to pixels.
     * @param context
     * @param dp
     * @return
     */
    public static float dpToPix(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return scale * dp + 0.5f;
    }

    /**
     *
     * @param context
     * @param fraction A float value between 0 and 1.
     * @return Number of pixels fraction represents on the current device's display.
     */
    public static float fractionToPixH(Context context, float fraction) {
        return context.getResources().getDisplayMetrics().heightPixels * fraction;

    }

    /**
     *
     * @param context
     * @param fraction A float value between 0 and 1.
     * @return Number of pixels fraction represents on the current device's display.
     */
    public static float fractionToPixW(Context context, float fraction) {
        return context.getResources().getDisplayMetrics().widthPixels * fraction;
    }



}



