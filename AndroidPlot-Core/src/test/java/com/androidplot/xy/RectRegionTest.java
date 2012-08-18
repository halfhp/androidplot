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
