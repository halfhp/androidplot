/*
 * Copyright 2015 AndroidPlot.com
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

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.AndroidplotTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RectRegionTest extends AndroidplotTest{

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

    @Test
    public void testTransform() throws Exception {
        RectRegion r1 = new RectRegion(1, 2, 0, 10);
        RectRegion r2 = new RectRegion(2, 4, 10, 20);

        final XYCoords trans1 = r1.transform(new XYCoords(1.5, 5), r2);
        assertEquals(3.0, trans1.x);
        assertEquals(15.0, trans1.y);

        // try a screen transform:
        RectF screen = new RectF(0, 0, 100, 100);
        PointF result = r1.transform(2, 10, screen, false, true);
        assertEquals(100f, result.x);
        assertEquals(0f, result.y);

        // now transform back to real:
        RectRegion screenRegion = new RectRegion(screen);
        XYCoords result2 = screenRegion.transform(result.x, result.y, r1, false, true);
        assertEquals(2d, result2.x);
        assertEquals(10d, result2.y);
    }

    @Test
    public void testIntersects() throws Exception {
        RectRegion r1 = new RectRegion(0, 10, 0, 10);
        RectRegion r2 = new RectRegion(5, 15, 5, 15);
        RectRegion r3 = new RectRegion(11, 21, 11, 21);

        assertTrue(r1.intersects(r2));
        assertTrue(r2.intersects(r1));
        assertFalse(r1.intersects(r3));
        assertFalse(r3.intersects(r1));
        assertTrue(r2.intersects(r3));
        assertTrue(r3.intersects(r2));
    }

    @Test
    public void testClip_sameDimensions() throws Exception {
        RectRegion r1 = new RectRegion(0, 10, 0, 10);
        RectRegion r2 = new RectRegion(0, 10, 0, 10);

        r1.intersect(r2);
        assertEquals(0, r1.getMinX().doubleValue(), 0);
        assertEquals(10, r1.getMaxX().doubleValue(), 0);
        assertEquals(0, r1.getMinY().doubleValue(), 0);
        assertEquals(10, r1.getMaxY().doubleValue(), 0);
    }

    @Test
    public void testClip_intersectingDimensions() throws Exception {
        RectRegion r1 = new RectRegion(0, 10, 0, 10);
        RectRegion r2 = new RectRegion(5, 15, 5, 15);

        r1.intersect(r2);
        assertEquals(5, r1.getMinX().doubleValue(), 0);
        assertEquals(10, r1.getMaxX().doubleValue(), 0);
        assertEquals(5, r1.getMinY().doubleValue(), 0);
        assertEquals(10, r1.getMaxY().doubleValue(), 0);

        r1 = new RectRegion(0, 10, 0, 10);
        r2 = new RectRegion(-5, 5, -5, 5);
        r1.intersect(r2);

        assertEquals(0, r1.getMinX().doubleValue(), 0);
        assertEquals(5, r1.getMaxX().doubleValue(), 0);
        assertEquals(0, r1.getMinY().doubleValue(), 0);
        assertEquals(5, r1.getMaxY().doubleValue(), 0);
    }

    @Test
    public void testClip_nonIntersectingDimensions() throws Exception {
        RectRegion r1 = new RectRegion(0, 10, 0, 10);
        RectRegion r2 = new RectRegion(100, 200, 100, 200);
        r1.intersect(r2);

        assertEquals(null, r1.getMinX());
        assertEquals(null, r1.getMaxX());
        assertEquals(null, r1.getMinY());
        assertEquals(null, r1.getMaxY());

        r1 = new RectRegion(0, 10, 0, 10);
        r2 = new RectRegion(-200, -100, -200, -100);
        r1.intersect(r2);

        assertEquals(null, r1.getMinX());
        assertEquals(null, r1.getMaxX());
        assertEquals(null, r1.getMinY());
        assertEquals(null, r1.getMaxY());
    }

    @Test
    public void testUnion() throws Exception {
        RectRegion r1 = new RectRegion(0, 10, 0, 10);
        RectRegion r2 = new RectRegion(100, 200, 100, 200);

        r1.union(r2);

        assertEquals(0, r1.getMinX().doubleValue(), 0);
        assertEquals(200, r1.getMaxX().doubleValue(), 0);
        assertEquals(0, r1.getMinY().doubleValue(), 0);
        assertEquals(200, r1.getMaxY().doubleValue(), 0);

        r1 = new RectRegion(0, 10, 0, 10);
        r2.union(r1);

        assertEquals(0, r2.getMinX().doubleValue(), 0);
        assertEquals(200, r2.getMaxX().doubleValue(), 0);
        assertEquals(0, r2.getMinY().doubleValue(), 0);
        assertEquals(200, r2.getMaxY().doubleValue(), 0);
    }
}
