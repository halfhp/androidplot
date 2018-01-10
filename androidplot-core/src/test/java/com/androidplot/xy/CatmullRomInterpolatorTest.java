package com.androidplot.xy;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CatmullRomInterpolatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void interpolate_invalidPointsPerSegment_throwsIllegalArgumentException() {
        final XYSeries series = mock(XYSeries.class);
        final CatmullRomInterpolator.Params params =
                new CatmullRomInterpolator.Params(1, CatmullRomInterpolator.Type.Centripetal);

        new CatmullRomInterpolator().interpolate(series, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void interpolate_twoElementSeries_throwsIllegalArgumentException() {
        final XYSeries series = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "test", 1, 2);
        final CatmullRomInterpolator.Params params =
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal);

        new CatmullRomInterpolator().interpolate(series, params);
    }

    @Test
    public void interpolate_threeElementSeriesAndThreePointsPerSegment_producesFivePoints() {
        final XYSeries series = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "test", 1, 2, 3);
        final CatmullRomInterpolator.Params params =
                new CatmullRomInterpolator.Params(3, CatmullRomInterpolator.Type.Centripetal);
        final List<XYCoords> interpolated = new CatmullRomInterpolator().interpolate(series, params);

        assertEquals(5, interpolated.size());

        // control points should exactly match input:
        assertEquals(1, interpolated.get(0).y);
        assertEquals(2, interpolated.get(2).y);
        assertEquals(3, interpolated.get(4).y);
    }

    @Test
    public void interpolate_threeElementSeriesAndFourPointsPerSegment_producesSavenPoints() {
        final XYSeries series = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "test", 1, 2, 3);
        final CatmullRomInterpolator.Params params =
                new CatmullRomInterpolator.Params(4, CatmullRomInterpolator.Type.Centripetal);
        final List<XYCoords> interpolated = new CatmullRomInterpolator().interpolate(series, params);

        assertEquals(7, interpolated.size());

        // control points should exactly match input:
        assertEquals(1, interpolated.get(0).y);
        assertEquals(2, interpolated.get(3).y);
        assertEquals(3, interpolated.get(6).y);
    }
}
