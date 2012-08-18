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
}
