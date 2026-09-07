// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PixelUtilsTest extends AndroidplotTest {

    @Test
    public void testDimensionPattern() {
        Pattern DIMENSION_PATTERN = Pattern.compile(PixelUtils.DIMENSION_REGEX);
        assertTrue("Dimension failed dimension pattern match", DIMENSION_PATTERN.matcher("20dp").matches());
        assertTrue("Negative dimension failed dimension pattern match", DIMENSION_PATTERN.matcher("-20dp").matches());
        assertTrue(DIMENSION_PATTERN.matcher("1.5sp").matches());
        assertTrue(DIMENSION_PATTERN.matcher(" 20 dp ").matches());
        assertFalse(DIMENSION_PATTERN.matcher("20").matches());
        assertFalse(DIMENSION_PATTERN.matcher("dp").matches());
        assertFalse(DIMENSION_PATTERN.matcher("20dp5").matches());
    }

    @Test
    public void addAndSub() {
        assertEquals(new PointF(4, 6), PixelUtils.add(new PointF(1, 2), new PointF(3, 4)));
        assertEquals(new PointF(-2, -2), PixelUtils.sub(new PointF(1, 2), new PointF(3, 4)));
    }

    @Test
    public void dpAndSpToPix_atDefaultDensity_areIdentity() {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        assertEquals(1f, metrics.density, 0);
        assertEquals(10f, PixelUtils.dpToPix(10), 0);
        assertEquals(12.5f, PixelUtils.spToPix(12.5f), 0);
        assertEquals(0f, PixelUtils.dpToPix(0), 0);
    }

    @Test
    @Config(qualifiers = "xhdpi")
    public void dpAndSpToPix_scaleWithDensity() {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        assertEquals(2f, metrics.density, 0);
        assertEquals(20f, PixelUtils.dpToPix(10), 0);
        assertEquals(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, metrics),
                PixelUtils.spToPix(10), 0);
        assertEquals(-20f, PixelUtils.dpToPix(-10), 0);
    }

    @Test
    public void stringToDimension_convertsEachUnit() {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        assertEquals(20f, PixelUtils.stringToDimension("20px"), 0);
        assertEquals(20f, PixelUtils.stringToDimension("20dp"), 0);
        assertEquals(20f, PixelUtils.stringToDimension("20dip"), 0);
        assertEquals(1.5f, PixelUtils.stringToDimension("1.5DP"), 0);
        assertEquals(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, metrics),
                PixelUtils.stringToDimension("20sp"), 0);
        assertEquals(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 20, metrics),
                PixelUtils.stringToDimension("20pt"), 0);
        assertEquals(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 2, metrics),
                PixelUtils.stringToDimension("2in"), 0);
        assertEquals(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 3, metrics),
                PixelUtils.stringToDimension("3mm"), 0);
        assertEquals(20f, PixelUtils.stringToDimension(" 20 px "), 0);
    }

    @Test
    @Config(qualifiers = "xhdpi")
    public void stringToDimension_scalesWithDensity() {
        assertEquals(40f, PixelUtils.stringToDimension("20dp"), 0);
        assertEquals(20f, PixelUtils.stringToDimension("20px"), 0);
    }

    @Test
    public void stringToDimension_negativeValue_keepsSign() {
        assertEquals(-20f, PixelUtils.stringToDimension("-20dp"), 0);
        assertEquals(-1.5f, PixelUtils.stringToDimension("-1.5px"), 0);
    }

    @Test
    public void stringToDimension_invalidInput_throwsNumberFormatException() {
        for (String invalid : new String[]{"20", "dp", "20zz", "", "abc", "20dp5"}) {
            try {
                PixelUtils.stringToDimension(invalid);
                fail("expected NumberFormatException for '" + invalid + "'");
            } catch (NumberFormatException expected) {
                // ok
            }
        }
    }

    @Test
    public void dimensionConstantLookup_isReadOnly() {
        assertEquals(TypedValue.COMPLEX_UNIT_DIP, PixelUtils.dimensionConstantLookup.get("dp").intValue());
        try {
            PixelUtils.dimensionConstantLookup.put("foo", 1);
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void uninitialized_throwsHelpfulError() throws Exception {
        final Field metrics = PixelUtils.class.getDeclaredField("metrics");
        metrics.setAccessible(true);
        final Object saved = metrics.get(null);
        try {
            metrics.set(null, null);
            try {
                PixelUtils.dpToPix(1);
                fail("expected RuntimeException");
            } catch (RuntimeException expected) {
                assertTrue(expected.getMessage().contains("PixelUtils.init"));
            }
            try {
                PixelUtils.spToPix(1);
                fail("expected RuntimeException");
            } catch (RuntimeException expected) {
                assertTrue(expected.getMessage().contains("PixelUtils.init"));
            }
        } finally {
            metrics.set(null, saved);
        }
        // init restores normal operation:
        PixelUtils.init(getContext());
        assertEquals(1f, PixelUtils.dpToPix(1), 0);
    }
}
