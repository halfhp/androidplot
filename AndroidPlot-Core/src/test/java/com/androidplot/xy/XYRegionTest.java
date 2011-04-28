package com.androidplot.xy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XYRegionTest {
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

        XYRegion region1 = new XYRegion(0, 100, 0, 100, "");

        XYRegion region2 = new XYRegion(5, 10, 5, 10, "");

        assertTrue(region1.intersects(region2));
        assertTrue(region2.intersects(region1));

        XYRegion region3 = new XYRegion(101, 200, 101, 200, "");
        assertFalse(region1.intersects(region3));
        assertFalse(region3.intersects(region1));

        XYRegion region4 = new XYRegion(99, 109, 99, 109, "");
        assertTrue(region1.intersects(region4));
        assertTrue(region4.intersects(region1));

        // negative numbers:
        XYRegion region5 = new XYRegion(-100, 1, -100, 1, "");
        assertTrue(region1.intersects(region5));
        assertTrue(region5.intersects(region1));
    }
}
