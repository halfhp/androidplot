// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.annotation.NonNull;

public class PixelUtils {
    private static DisplayMetrics metrics;

    /**
     * Recalculates scale value etc.  Should be called when an application starts or
     * whenever the screen is rotated.
     */
    public static void init(@NonNull Context ctx) {
        metrics = ctx.getResources().getDisplayMetrics();
    }

    @NonNull
    public static PointF add(@NonNull PointF lhs, @NonNull PointF rhs) {
        return new PointF(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    @NonNull
    public static PointF sub(@NonNull PointF lhs, @NonNull PointF rhs) {
        return new PointF(lhs.x - rhs.x, lhs.y - rhs.y);
    }

    /**
     * Converts a dp value to pixels.
     * @param dp
     * @return Pixel value of dp.
     */
    public static float dpToPix(float dp) {
        checkMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);

    }

    /**
     * Converts an sp value to pixels.
     * @param sp
     * @return Pixel value of sp.
     */
    @SuppressWarnings("SameParameterValue")
    public static float spToPix(float sp) {
        checkMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    /**
     *
     * CODE BELOW IS ADAPTED IN PART FROM MINDRIOT'S SAMPLE CODE HERE:
     * http://stackoverflow.com/questions/8343971/how-to-parse-a-dimension-string-and-convert-it-to-a-dimension-value
     */
    // Initialize dimension string to constant lookup.
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

    // Initialize pattern for dimension string.
    protected static final String DIMENSION_REGEX = "^\\-?\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$";
    protected static final Pattern DIMENSION_PATTERN = Pattern.compile(DIMENSION_REGEX);

    public static float stringToDimension(@NonNull String dimension) {
        // Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
    }

    private static InternalDimension stringToInternalDimension(String dimension) {
        // Match target against pattern.
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            // Match found.
            // Extract value.
            float value = Float.valueOf(matcher.group(1));
            // the pattern accepts a leading '-' outside the value group; apply it here:
            if (dimension.trim().startsWith("-")) {
                value = -value;
            }
            // Extract dimension units.
            String unit = matcher.group(3).toLowerCase();
            // Get Android dimension constant.
            Integer dimensionUnit = dimensionConstantLookup.get(unit);
            if (dimensionUnit == null) {
                // Invalid format.
                throw new NumberFormatException();
            } else {
                // Return valid dimension.
                return new InternalDimension(value, dimensionUnit);
            }
        } else {
            // Invalid format.
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

    /**
     * Safety run to hopefully help clarify what could otherwise be a confusing NPE.
     */
    private static void checkMetrics() {
        if(metrics == null) {
            throw new RuntimeException("PixelUtils not initialized; call PixelUtils.init(Context) before using.");
        }
    }
}



