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

package com.androidplot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineRegionTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() throws Exception {
        LineRegion lr = new LineRegion(0d, 0d);
        assertEquals(0d, lr.getMinVal());
        assertEquals(0d, lr.getMaxVal());

        lr = new LineRegion(1.5d, -2d);
        assertEquals(-2d, lr.getMinVal());
        assertEquals(1.5d, lr.getMaxVal());

        lr = new LineRegion(10d, 20d);
        assertEquals(10d, lr.getMinVal());
        assertEquals(20d, lr.getMaxVal());
    }


    @Test
    public void testContains() throws Exception {

    }

    @Test
    public void testIntersects() throws Exception {
        LineRegion line1 = new LineRegion(1, 10);
        LineRegion line2 = new LineRegion(11, 20);
        assertFalse(line1.intersects(line2));

        line1.setMaxVal(15);
        assertTrue(line1.intersects(line2));

        //l1end = 30;
        line1.setMaxVal(30);
        assertTrue(line1.intersects(line2));

        //l1start = 21;
        line1.setMinVal(21);
        assertFalse(line1.intersects(line2));
    }

    @Test
    public void testLength() throws Exception {
        LineRegion lr = new LineRegion(0, 10);
        assertEquals(10d, lr.length().doubleValue(), 0);

        lr = new LineRegion(-5, 5);
        assertEquals(10d, lr.length().doubleValue(), 0);
    }
}
