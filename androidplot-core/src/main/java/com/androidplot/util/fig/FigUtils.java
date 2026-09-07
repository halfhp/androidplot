// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util.fig;

import android.content.*;
import android.util.*;

import java.util.*;
import java.util.regex.*;

/**
 * Utilities used by {@link Fig}.
 */
abstract class FigUtils {

    protected static final String DIMENSION_REGEX = "^\\-?\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$";
    protected static final Pattern DIMENSION_PATTERN = Pattern.compile(DIMENSION_REGEX);

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

    private static class InternalDimension {
        float value;
        int unit;

        public InternalDimension(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    public static float stringToDimension(Context context, String dimension) {
        // Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        return TypedValue.applyDimension(internalDimension.unit,
                internalDimension.value, context.getResources().getDisplayMetrics());
    }

    private static InternalDimension stringToInternalDimension(String dimension) {
        // Match target against pattern.
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            // Match found; extract value.
            float value = Float.valueOf(matcher.group(1));
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
}
