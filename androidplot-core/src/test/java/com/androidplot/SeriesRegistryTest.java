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

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Formatter;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeriesRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SeriesRegistryTest extends AndroidplotTest {

    SeriesRegistry seriesRegistry;

    @Before
    public void setUp() throws Exception {
        seriesRegistry = new XYSeriesRegistry();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAdd() {
        assertEquals(0, seriesRegistry.size());
        seriesRegistry.add(new SimpleXYSeries("s1"), new LineAndPointFormatter());
        assertEquals(1, seriesRegistry.size());
    }

    @Test
    public void testAdd_failsOnNullArgument() throws Exception {
        try {
            seriesRegistry.add(null, null);
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException e) {
            // expected
        }

        try {
            seriesRegistry.add(new SimpleXYSeries("s1"), null);
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException e) {
            // expected
        }

        try {
            seriesRegistry.add(null, new LineAndPointFormatter());
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGet() {
        Series s1 = new SimpleXYSeries("s1");
        Formatter f1 = new LineAndPointFormatter();
        Series s2 = new SimpleXYSeries("s2");
        Formatter f2 = new LineAndPointFormatter();
        Formatter f3 = new LineAndPointFormatter();
        seriesRegistry.add(s1, f1);
        seriesRegistry.add(s1, f3);
        seriesRegistry.add(s2, f2);


        assertEquals(2, seriesRegistry.get(s1).size());
        assertEquals(1, seriesRegistry.get(s2).size());
    }

    @Test
    public void testRemove() {
        Series series = new SimpleXYSeries("s1");
        seriesRegistry.add(series, new LineAndPointFormatter());
        assertEquals(1, seriesRegistry.size());

        seriesRegistry.remove(new SimpleXYSeries("s2"));
        assertEquals(1, seriesRegistry.size());

        seriesRegistry.remove(series);
        assertEquals(0, seriesRegistry.size());
    }

    @Test
    public void testClear() {
        seriesRegistry.add(new SimpleXYSeries("s1"), new LineAndPointFormatter());
        seriesRegistry.add(new SimpleXYSeries("s2"), new LineAndPointFormatter());
        assertEquals(2, seriesRegistry.size());

        seriesRegistry.clear();
        assertEquals(0, seriesRegistry.size());

    }

    @Test
    public void testContains() {
        Series s1 = new SimpleXYSeries("s1");
        Series s2 = new SimpleXYSeries("s1");
        Series s3 = new SimpleXYSeries("s1");

        seriesRegistry.add(s1, new LineAndPointFormatter());
        seriesRegistry.add(s2, new LineAndPointFormatter());

        assertTrue(seriesRegistry.contains(s1, LineAndPointFormatter.class));
        assertFalse(seriesRegistry.contains(s1, BarFormatter.class));
        assertTrue(seriesRegistry.contains(s2, LineAndPointFormatter.class));
        assertFalse(seriesRegistry.contains(s3, LineAndPointFormatter.class));

    }

}
