package com.androidplot.xy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RectRegionTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testContainsPoint() throws Exception {

    }

    @Test
    public void testContainsValue() throws Exception {

    }

    @Test
    public void testContainsDomainValue() throws Exception {

    }

    @Test
    public void testContainsRangeValue() throws Exception {

    }



    @Test
    public void testIsWithin() throws Exception {

        RectRegion region1 = new RectRegion(0, 100, 0, 100, "");

        RectRegion region2 = new RectRegion(5, 10, 5, 10, "");

        assertTrue(region1.intersects(region2));
        assertTrue(region2.intersects(region1));

        RectRegion region3 = new RectRegion(101, 200, 101, 200, "");
        assertFalse(region1.intersects(region3));
        assertFalse(region3.intersects(region1));

        RectRegion region4 = new RectRegion(99, 109, 99, 109, "");
        assertTrue(region1.intersects(region4));
        assertTrue(region4.intersects(region1));

        // negative numbers:
        RectRegion region5 = new RectRegion(-100, 1, -100, 1, "");
        assertTrue(region1.intersects(region5));
        assertTrue(region5.intersects(region1));
    }
}
