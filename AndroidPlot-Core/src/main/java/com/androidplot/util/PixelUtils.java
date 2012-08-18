/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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



