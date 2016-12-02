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

package com.androidplot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegionTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() throws Exception {
        Region lr = new Region(0d, 0d);
        assertEquals(0d, lr.getMin().doubleValue(), 0);
        assertEquals(0d, lr.getMax().doubleValue(), 0);

        lr = new Region(1.5d, -2d);
        assertEquals(-2f, lr.getMin().floatValue(), 0);
        assertEquals(1.5f, lr.getMax().floatValue(), 0);

        lr = new Region(10d, 20d);
        assertEquals(10l, lr.getMin().longValue(), 0);
        assertEquals(20l, lr.getMax().longValue(), 0);
    }


    @Test
    public void testContains() throws Exception {

    }

    @Test
    public void testIntersects() throws Exception {
        Region line1 = new Region(1, 10);
        Region line2 = new Region(11, 20);
        assertFalse(line1.intersects(line2));

        line1.setMax(15);
        assertTrue(line1.intersects(line2));

        //l1end = 30;
        line1.setMax(30);
        assertTrue(line1.intersects(line2));

        //l1start = 21;
        line1.setMin(21);
        assertFalse(line1.intersects(line2));
    }

    @Test
    public void testLength() throws Exception {
        Region lr = new Region(0, 10);
        assertEquals(10d, lr.length().doubleValue(), 0);

        lr = new Region(-5, 5);
        assertEquals(10d, lr.length().doubleValue(), 0);
    }

    @Test
    public void testCenter() throws Exception {
        Region r1 = new Region(1, 2);
        assertEquals(1.5, r1.center());

        Region r2 = new Region(-10, 10);
        assertEquals(0.0, r2.center());

        Region r3 = new Region(-2, -1);
        assertEquals(-1.5, r3.center());
    }

    @Test
    public void testTransform() throws Exception {
        Region r1 = new Region(1, 2);
        Region r2 = new Region(0, 10);

        assertEquals(5.0, r1.transform(1.5, r2));
        assertEquals(0.0, r1.transform(1, r2));
        assertEquals(10.0, r1.transform(2, r2));

        Region r3 = new Region(-10, 10);
        assertEquals(-10.0, r1.transform(1, r3));
    }

    @Test
    public void testTransformWithFlip() throws Exception {
        Region r1 = new Region(1, 2);
        Region r2 = new Region(0, 10);

        assertEquals(5.0, r1.transform(1.5, r2, true));
        assertEquals(10.0, r1.transform(1, r2, true));
        assertEquals(0.0, r1.transform(2, r2, true));

        Region r3 = new Region(-10, 10);
        assertEquals(10.0, r1.transform(1, r3, true));
    }

    @Test
    public void testRatio() throws Exception {
        Region r1 = new Region(1, 2);
        Region r2 = new Region(0, 100);
        assertEquals(0.01, r1.ratio(r2));
        assertEquals(100.0, r2.ratio(r1));

        r1 = new Region(0f, 21402646f);
        r2 = new Region(0, 999);

        //assertTrue(r2.ratio(r1).doubleValue() > 0);
    }

    @Test
    public void testRegionRegionUnion() throws Exception {
        Region r1 = new Region(1, 2);
        Region r2 = new Region(0, 100);

        r1.union(r2);
        assertEquals(0, r1.getMin().doubleValue(), 0);
        assertEquals(100, r1.getMax().doubleValue(), 0);
    }

    @Test
    public void testRegionPointUnion() throws Exception {
        Region r1 = new Region(1, 2);

        r1.union(0);
        assertEquals(0, r1.getMin().doubleValue(), 0);
        assertEquals(2, r1.getMax().doubleValue(), 0);

        r1.union(100);
        assertEquals(0, r1.getMin().doubleValue(), 0);
        assertEquals(100, r1.getMax().doubleValue(), 0);
    }

}
