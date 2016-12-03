package com.androidplot.xy;

import com.androidplot.test.*;

import org.junit.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link SampledXYSeries}
 */
public class SampledXYSeriesTest extends AndroidplotTest {

    @Test
    public void testInit() throws Exception {

        XYSeries rawData = TestUtils.generateXYSeries("my series", 2000);
        SampledXYSeries s1 = spy(new SampledXYSeries(rawData, 2, 100));

        // expect 5 setZoomFactor levels (besides 1x): 2x, 4x, 8x, 16x, 32x
        assertEquals(4, s1.getZoomLevels().size());
        assertEquals(1000, s1.getZoomLevels().get(0).size()); // 2x
        assertEquals(500, s1.getZoomLevels().get(1).size());  // 4x
        assertEquals(250, s1.getZoomLevels().get(2).size());  // 8x
        assertEquals(125, s1.getZoomLevels().get(3).size());  // 16x

        SampledXYSeries s2 = spy(new SampledXYSeries(rawData, 4, 100));

        // expect 2 setZoomFactor levels (besides 1x): 4x & 16x:
        assertEquals(2, s2.getZoomLevels().size());
        assertEquals(500, s2.getZoomLevels().get(0).size());  // 4x
        assertEquals(125, s2.getZoomLevels().get(1).size());  // 16x
    }

    @Test
    public void testGetZoomIndex() throws Exception {
        assertEquals(0, SampledXYSeries.getZoomIndex(2, 2));
        assertEquals(1, SampledXYSeries.getZoomIndex(3, 2));
        assertEquals(1, SampledXYSeries.getZoomIndex(4, 2));
        assertEquals(2, SampledXYSeries.getZoomIndex(8, 2));
        assertEquals(2, SampledXYSeries.getZoomIndex(9, 2));
        assertEquals(2, SampledXYSeries.getZoomIndex(10, 2));
        assertEquals(3, SampledXYSeries.getZoomIndex(15, 2));
        assertEquals(3, SampledXYSeries.getZoomIndex(16, 2));
        assertEquals(3, SampledXYSeries.getZoomIndex(17, 2));
        assertEquals(4, SampledXYSeries.getZoomIndex(31, 2));
        assertEquals(4, SampledXYSeries.getZoomIndex(32, 2));

        assertEquals(0, SampledXYSeries.getZoomIndex(1, 4));
        assertEquals(0, SampledXYSeries.getZoomIndex(4, 4));
        assertEquals(1, SampledXYSeries.getZoomIndex(15, 4));
        assertEquals(1, SampledXYSeries.getZoomIndex(16, 4));
        assertEquals(1, SampledXYSeries.getZoomIndex(17, 4));
        assertEquals(2, SampledXYSeries.getZoomIndex(63, 4));
        assertEquals(2, SampledXYSeries.getZoomIndex(64, 4));
        assertEquals(2, SampledXYSeries.getZoomIndex(65, 4));
    }

    @Test
    public void testSetZoomFactor() throws Exception {
        XYSeries rawData = TestUtils.generateXYSeries("my series", 2000);
        SampledXYSeries sampledXYSeries = spy(new SampledXYSeries(rawData, 2, 100));
        sampledXYSeries.setZoomFactor(2);
        assertEquals(1000, sampledXYSeries.size());
        sampledXYSeries.setZoomFactor(4);
        assertEquals(500, sampledXYSeries.size());
        sampledXYSeries.setZoomFactor(8);
        assertEquals(250, sampledXYSeries.size());
        sampledXYSeries.setZoomFactor(16);
        assertEquals(125, sampledXYSeries.size());
    }

    @Test
    public void testResample() {
        XYSeries rawData = TestUtils.generateXYSeries("my series", 10000);
        SampledXYSeries sampledXYSeries = new SampledXYSeries(rawData, 2, 200);
        assertEquals(5, sampledXYSeries.getZoomLevels().size());
    }

    /**
     * Ignored until null support is added to {@link LTTBSampler}.
     */
    @Ignore
    @Test
    public void testResample_supportsNullVals() {
        XYSeries rawData = TestUtils.generateXYSeriesWithNulls("my series", 10000);
        SampledXYSeries sampledXYSeries = new SampledXYSeries(rawData, 2, 200);
        assertEquals(5, sampledXYSeries.getZoomLevels().size());
    }
}
