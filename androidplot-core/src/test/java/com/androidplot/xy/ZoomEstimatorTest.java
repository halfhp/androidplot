package com.androidplot.xy;

import com.androidplot.test.*;

import org.junit.*;
import org.mockito.*;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by halfhp on 10/8/16.
 */
public class ZoomEstimatorTest extends AndroidplotTest {

    @Mock
    XYPlot xyPlot;


    @Test
    public void testCheck() throws Exception {
        ZoomEstimator estimator = spy(new ZoomEstimator());

        SampledXYSeries series =
                spy(new SampledXYSeries(TestUtils
                        .generateXYSeries("test series", 1000), 2, 100));
        series.resample();
        assertEquals(8d, series.getMaxZoomFactor());

        XYSeriesBundle bundle = new XYSeriesBundle(series, null);

        when(xyPlot.getBounds())
                .thenReturn(new RectRegion(0, 1000, 0, 1000))
                .thenReturn(new RectRegion(0, 500, 0, 500))
                .thenReturn(new RectRegion(0, 1, 0, 1));

        estimator.run(xyPlot, bundle);
        estimator.run(xyPlot, bundle);
        estimator.run(xyPlot, bundle);

        verify(series).setZoomFactor(8);
        verify(series).setZoomFactor(4);
        verify(series).setZoomFactor(1);
    }

    @Test
    public void testCalculateZoom() {
        ZoomEstimator estimator = spy(new ZoomEstimator());

        SampledXYSeries series =
                spy(new SampledXYSeries(TestUtils
                        .generateXYSeries("test series", 1000), 2, 100));
        series.resample();

        when(xyPlot.getBounds())
                .thenReturn(new RectRegion(0, 1000, 0, 1000));

        // fully zoomed out so max zoom factor should be applied:
        assertEquals(series.getMaxZoomFactor(), estimator.calculateZoom(series, new RectRegion(0, 1000, 0, 1000)));

        // fully zoomed in so min zoom factor should be applied:
        assertEquals(1d, estimator.calculateZoom(series, new RectRegion(0, 1, 0, 1)));

    }
}
