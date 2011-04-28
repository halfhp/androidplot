package com.androidplot;

import com.androidplot.xy.XYRegion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineTest {
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
        Line line1 = new Line(1, 10);
        Line line2 = new Line(11, 20);
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
