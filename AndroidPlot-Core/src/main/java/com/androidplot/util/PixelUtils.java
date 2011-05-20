package com.androidplot.util;

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


}



