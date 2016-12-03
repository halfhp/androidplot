package com.androidplot.xy;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.test.TestUtils;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.spy;

/**
 * Tests {@link NormedXYSeries}.
 */
public class NormedXYSeriesTest extends AndroidplotTest {

    // account for precision issues inherent in floating point math:
    private static final double DELTA = 0.0000001;

    @Test
    public void testConstructor_withNoOffset() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4, 6, 8, 10);
        NormedXYSeries normedData = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null),
                new NormedXYSeries.Norm(null));

        assertEquals(0d, normedData.getY(0).doubleValue(), DELTA);
        assertEquals(0.2d, normedData.getY(1).doubleValue(), DELTA);
        assertEquals(0.4d, normedData.getY(2).doubleValue(), DELTA);
        assertEquals(0.6d, normedData.getY(3).doubleValue(), DELTA);
        assertEquals(0.8d, normedData.getY(4).doubleValue(), DELTA);
        assertEquals(1.0d, normedData.getY(5).doubleValue(), DELTA);
    }

    @Test
    public void testConstructor_withNullYVals() {
        XYSeries rawData = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "s1",
                0, null, 4, null, 8, 10);
        NormedXYSeries normedData = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null),
                new NormedXYSeries.Norm(null));

        assertEquals(0d, normedData.getY(0).doubleValue(), DELTA);
        assertEquals(0.4d, normedData.getY(2).doubleValue(), DELTA);
        assertEquals(0.8d, normedData.getY(4).doubleValue(), DELTA);
        assertEquals(1.0d, normedData.getY(5).doubleValue(), DELTA);
    }

    @Test
    public void testConstructor_withPositiveOffsetAndOffsetCompression() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4, 6, 8, 10);
        NormedXYSeries normedData = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null, 0.5, true),
                new NormedXYSeries.Norm(null, 0.5, true));

        assertEquals(0.5d, normedData.getY(0).doubleValue(), DELTA);
        assertEquals(0.6d, normedData.getY(1).doubleValue(), DELTA);
        assertEquals(1.0d, normedData.getY(5).doubleValue(), DELTA);
    }

    @Test
    public void testConstructor_withNegativeOffsetAndOffsetCompression() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4, 6, 8, 10);
        NormedXYSeries normedData = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null, -0.5, true),
                new NormedXYSeries.Norm(null, -0.5, true));

        assertEquals(0d, normedData.getY(0).doubleValue(), DELTA);
        assertEquals(0.1d, normedData.getY(1).doubleValue(), DELTA);
        assertEquals(0.2d, normedData.getY(2).doubleValue(), DELTA);
        assertEquals(0.5d, normedData.getY(5).doubleValue(), DELTA);
    }

    @Test
    public void testConstructor_withOffsetAndNoOffsetCompression() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4, 6, 8, 10);
        NormedXYSeries normedData = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null, 0.5, false),
                new NormedXYSeries.Norm(null, 0.5, false));

        assertEquals(0.5d, normedData.getY(0).doubleValue(), DELTA);
        assertEquals(0.7d, normedData.getY(1).doubleValue(), DELTA);
        assertEquals(1.5d, normedData.getY(5).doubleValue(), DELTA);
    }
}
