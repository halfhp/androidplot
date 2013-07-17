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
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PixelUtils {
    private static DisplayMetrics metrics;
    private static final float FLOAT_INT_AVG_NUDGE = 0.5f;
    //private static float SCALE = 1;   //  pix per dp
    //private static int X_PIX = 1;     // total display horizontal pix
    //private static int Y_PIX = 1;     // total display vertical pix

    /**
     * Recalculates scale value etc.  Should be called when an application starts or
     * whenever the screen is rotated.
     */
    public static void init(Context ctx) {
        //DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        //SCALE = dm.density;
        //X_PIX = dm.widthPixels;
        //Y_PIX = dm.heightPixels;
        metrics = ctx.getResources().getDisplayMetrics();

    }

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
     * @param dp
     * @return Pixel value of dp.
     */
    public static float dpToPix(float dp) {
        //return SCALE * dp + FLOAT_INT_AVG_NUDGE;
        //InternalDimension id = new InternalDimension(dp, TypedValue.COMPLEX_UNIT_DIP);
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);

    }

    /**
     * Converts an sp value to pixels.
     * @param sp
     * @return Pixel value of sp.
     */
    @SuppressWarnings("SameParameterValue")
    public static float spToPix(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }


    /**
     *
     * @param fraction A float value between 0 and 1.
     * @return Number of pixels fraction represents on the current device's display.
     */
    public static float fractionToPixH(float fraction) {
        return metrics.heightPixels * fraction;

    }

    /**
     *
     * @param fraction A float value between 0 and 1.
     * @return Number of pixels fraction represents on the current device's display.
     */
    public static float fractionToPixW(float fraction) {
        return metrics.widthPixels * fraction;
    }


    /**
     *
     * CODE BELOW IS ADAPTED IN PART FROM MINDRIOT'S SAMPLE CODE HERE:
     * http://stackoverflow.com/questions/8343971/how-to-parse-a-dimension-string-and-convert-it-to-a-dimension-value
     */
    // -- Initialize dimension string to constant lookup.
    public static final Map<String, Integer> dimensionConstantLookup = initDimensionConstantLookup();

    private static Map<String, Integer> initDimensionConstantLookup() {
        Map<String, Integer> m = new HashMap<String, Integer>();
        m.put("px", TypedValue.COMPLEX_UNIT_PX);
        m.put("dip", TypedValue.COMPLEX_UNIT_DIP);
        m.put("dp", TypedValue.COMPLEX_UNIT_DIP);
        m.put("sp", TypedValue.COMPLEX_UNIT_SP);
        m.put("pt", TypedValue.COMPLEX_UNIT_PT);
        m.put("in", TypedValue.COMPLEX_UNIT_IN);
        m.put("mm", TypedValue.COMPLEX_UNIT_MM);
        return Collections.unmodifiableMap(m);
    }

    // -- Initialize pattern for dimension string.
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$");

    /*public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        final float value = internalDimension.value;
        final float f = TypedValue.applyDimension(internalDimension.unit, value, metrics);
        final int res = (int) (f + 0.5f);
        if (res != 0) return res;
        if (value == 0) return 0;
        if (value > 0) return 1;
        return -1;
    }*/

    public static float stringToDimension(String dimension) {
        // -- Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
    }

    private static InternalDimension stringToInternalDimension(String dimension) {
        // -- Match target against pattern.
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            // -- Match found.
            // -- Extract value.
            float value = Float.valueOf(matcher.group(1));
            // -- Extract dimension units.
            String unit = matcher.group(3).toLowerCase();
            // -- Get Android dimension constant.
            Integer dimensionUnit = dimensionConstantLookup.get(unit);
            if (dimensionUnit == null) {
                // -- Invalid format.
                throw new NumberFormatException();
            } else {
                // -- Return valid dimension.
                return new InternalDimension(value, dimensionUnit);
            }
        } else {
            // -- Invalid format.
            throw new NumberFormatException();
        }
    }

    private static class InternalDimension {
        float value;
        int unit;

        public InternalDimension(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }
    }


}



