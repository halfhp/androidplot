// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.Region;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Accessors and small branches of {@link RectRegion} not covered by {@link RectRegionTest}.
 */
public class RectRegionAccessorsTest {

    @Test
    public void withDefaults_requiresFullyDefinedDefaults() {
        try {
            RectRegion.withDefaults(null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            RectRegion.withDefaults(new RectRegion(0, 1, null, 1));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        RectRegion region = RectRegion.withDefaults(new RectRegion(1, 2, 3, 4));
        assertFalse(region.isFullyDefined());
        assertEquals(1.0, region.getMinX().doubleValue(), 0);
        assertEquals(2.0, region.getMaxX().doubleValue(), 0);
        assertEquals(3.0, region.getMinY().doubleValue(), 0);
        assertEquals(4.0, region.getMaxY().doubleValue(), 0);
    }

    @Test
    public void widthAndHeight_areAbsoluteDistances() {
        RectRegion region = new RectRegion(-2, 8, 100, 50);
        assertEquals(10.0, region.getWidth().doubleValue(), 0);
        assertEquals(50.0, region.getHeight().doubleValue(), 0);
    }

    @Test
    public void setxRegion_setyRegion_replaceTheAxisRegions() {
        RectRegion region = new RectRegion(0, 1, 0, 1);
        Region x = new Region(10, 20);
        Region y = new Region(30, 40);
        region.setxRegion(x);
        region.setyRegion(y);
        assertSame(x, region.getxRegion());
        assertSame(y, region.getyRegion());
        assertEquals(10.0, region.getMinX().doubleValue(), 0);
        assertEquals(40.0, region.getMaxY().doubleValue(), 0);
    }

    @Test
    public void contains_checksBothAxes() {
        RectRegion region = new RectRegion(0, 10, 0, 10);
        assertTrue(region.contains(5, 5));
        assertTrue(region.contains(0, 10));
        assertFalse(region.contains(11, 5));
        assertFalse(region.contains(5, -1));
    }

    @Test
    public void toString_includesBoundsAndLabel() {
        RectRegion region = new RectRegion(0, 1, 2, 3, "lbl");
        String s = region.toString();
        assertTrue(s, s.startsWith("RectRegion{"));
        assertTrue(s, s.contains("xRegion=Region{min=0"));
        assertTrue(s, s.contains("yRegion=Region{min=2"));
        assertTrue(s, s.contains("label='lbl'"));
    }
}
