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
