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
import android.graphics.Paint;

/**
 * Convenience methods that operate on Paint instances.  These methods primarily deal with
 * converting pixel values to/from dp values.
 */
public class PaintUtils {

    /*public static Paint getPaint() {
        Paint p = new Paint();
        return p;
    }*/

    /**
     * Sets a paint instance's line stroke size in dp
     * @param paint
     * @param lineSizeDp
     */
    public static void setLineSizeDp(Paint paint, float lineSizeDp){
        paint.setStrokeWidth(PixelUtils.dpToPix(lineSizeDp));
    }

    /**
     * Sets a paint instance's font size in dp
     * @param paint
     * @param fontSizeDp
     */
    public static void setFontSizeDp(Paint paint, float fontSizeDp){
        paint.setTextSize(PixelUtils.dpToPix(fontSizeDp));
    }

    /**
     * Set a paint instance's shadowing using dp values
     * @param paint
     * @param radiusDp
     * @param dxDp
     * @param dyDp
     * @param color
     */
    public static void setShadowDp(Paint paint, float radiusDp, float dxDp, float dyDp, int color) {
        float radius = PixelUtils.dpToPix(radiusDp);
        float dx = PixelUtils.dpToPix(dxDp);
        float dy = PixelUtils.dpToPix(dyDp);
        paint.setShadowLayer(radius, dx, dy, color);
    }


}
