package com.androidplot.xy;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests {@link ScalingXYSeries}.
 */
public class ScalingXYSeriesTest extends AndroidplotTest {

    @Test
    public void testScale_yOnly() {
        SimpleXYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        ScalingXYSeries scaled1 = new ScalingXYSeries(s1, 0.5, ScalingXYSeries.Mode.Y_ONLY);

        assertEquals(0d, scaled1.getX(0).doubleValue());
        assertEquals(0d, scaled1.getY(0).doubleValue());

        assertEquals(1d, scaled1.getX(1).doubleValue());
        assertEquals(2.5d, scaled1.getY(1).doubleValue());

        assertEquals(2d, scaled1.getX(2).doubleValue());
        assertEquals(5d, scaled1.getY(2).doubleValue());
    }

    @Test
    public void testScale_xOnly() {
        SimpleXYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        ScalingXYSeries scaled1 = new ScalingXYSeries(s1, 0.5, ScalingXYSeries.Mode.X_ONLY);

        assertEquals(0d, scaled1.getX(0).doubleValue());
        assertEquals(0d, scaled1.getY(0).doubleValue());

        assertEquals(0.5d, scaled1.getX(1).doubleValue());
        assertEquals(5d, scaled1.getY(1).doubleValue());

        assertEquals(1d, scaled1.getX(2).doubleValue());
        assertEquals(10d, scaled1.getY(2).doubleValue());
    }

    @Test
    public void testScale_xAndY() {
        SimpleXYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        ScalingXYSeries scaled1 = new ScalingXYSeries(s1, 0.5, ScalingXYSeries.Mode.X_AND_Y);

        assertEquals(0d, scaled1.getX(0).doubleValue());
        assertEquals(0d, scaled1.getY(0).doubleValue());

        assertEquals(0.5d, scaled1.getX(1).doubleValue());
        assertEquals(2.5d, scaled1.getY(1).doubleValue());

        assertEquals(1d, scaled1.getX(2).doubleValue());
        assertEquals(5d, scaled1.getY(2).doubleValue());
    }
}
