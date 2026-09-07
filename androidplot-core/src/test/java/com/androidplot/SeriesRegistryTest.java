// SPDX-License-Identifier: Apache-2.0

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

import java.lang.reflect.Modifier;

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
    public void add_and_clear_areSynchronizedLikeRemove() throws Exception {
        // the render thread iterates the registry, so every mutator must hold the same monitor:
        assertTrue(Modifier.isSynchronized(
                SeriesRegistry.class.getMethod("add", Series.class, Formatter.class).getModifiers()));
        assertTrue(Modifier.isSynchronized(SeriesRegistry.class.getMethod("clear").getModifiers()));
        assertTrue(Modifier.isSynchronized(
                SeriesRegistry.class.getMethod("remove", Series.class).getModifiers()));
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
