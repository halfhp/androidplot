// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.test.TestUtils;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertTrue;
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

    @Test
    public void getY_singlePoint_returnsFiniteValue() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 7);
        NormedXYSeries normedData = new NormedXYSeries(rawData);

        // min == max; previously the transform produced NaN.  A flat series maps
        // onto the center of the normalized range:
        final double y = normedData.getY(0).doubleValue();
        assertTrue("expected a finite value but got " + y, !Double.isNaN(y) && !Double.isInfinite(y));
        assertEquals(0.5d, y, DELTA);
    }

    @Test
    public void onBeforeDraw_renormalizesAgainstUpdatedRawData() {
        SimpleXYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4);
        NormedXYSeries normedData = new NormedXYSeries(rawData);
        assertEquals(1.0d, normedData.getY(2).doubleValue(), DELTA);

        // the wrapped series grows beyond the bounds captured at construction time:
        rawData.addLast(null, 8);
        assertEquals(2.0d, normedData.getY(3).doubleValue(), DELTA);

        // the plot notifies the series before drawing, which refreshes the normalization:
        normedData.onBeforeDraw(null, null);
        assertEquals(0.5d, normedData.getY(2).doubleValue(), DELTA);
        assertEquals(1.0d, normedData.getY(3).doubleValue(), DELTA);
        normedData.onAfterDraw(null, null);
    }

    @Test
    public void onBeforeDraw_doesNotAlterUserSpecifiedBounds() {
        SimpleXYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 2, 4);
        NormedXYSeries normedData = new NormedXYSeries(rawData, null,
                new NormedXYSeries.Norm(new com.androidplot.Region(0, 8)));
        assertEquals(0.5d, normedData.getY(2).doubleValue(), DELTA);

        rawData.addLast(null, 16);
        normedData.onBeforeDraw(null, null);
        assertEquals(0.5d, normedData.getY(2).doubleValue(), DELTA);
        assertEquals(2.0d, normedData.getY(3).doubleValue(), DELTA);
        normedData.onAfterDraw(null, null);
    }

    @Test
    public void norm_offsetCompressionWithOffsetOutOfRange_throws() {
        for (double offset : new double[] {-1, 1, -1.5, 2}) {
            try {
                new NormedXYSeries.Norm(null, offset, true);
                fail("expected IllegalArgumentException for offset " + offset);
            } catch (IllegalArgumentException e) {
                // expected
            }
        }
        // without compression any offset is accepted:
        new NormedXYSeries.Norm(null, 2, false);
    }

    @Test
    public void singleArgConstructor_normalizesYOnly() {
        XYSeries rawData = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "raw", 10, 20, 30);
        NormedXYSeries normed = new NormedXYSeries(rawData);

        assertEquals("raw", normed.getTitle());
        assertEquals(3, normed.size());
        // x is passed through untouched:
        assertEquals(0, normed.getX(0).intValue());
        assertEquals(2, normed.getX(2).intValue());
        // y is normalized into 0..1:
        assertEquals(0d, normed.getY(0).doubleValue(), DELTA);
        assertEquals(0.5d, normed.getY(1).doubleValue(), DELTA);
        assertEquals(1d, normed.getY(2).doubleValue(), DELTA);
    }

    @Test
    public void getX_withXNorm_normalizesXVals() {
        XYSeries rawData = new SimpleXYSeries(
                java.util.Arrays.asList(100, 200, 300), java.util.Arrays.asList(1, 2, 3), "raw");
        NormedXYSeries normed = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null), null);

        assertEquals(0d, normed.getX(0).doubleValue(), DELTA);
        assertEquals(0.5d, normed.getX(1).doubleValue(), DELTA);
        assertEquals(1d, normed.getX(2).doubleValue(), DELTA);
        // y is not normalized:
        assertEquals(2, normed.getY(1).intValue());
    }

    @Test
    public void getX_withNullXVal_returnsNull() {
        SimpleXYSeries rawData = new SimpleXYSeries("raw");
        rawData.addLast(null, 5);
        rawData.addLast(10, 6);
        NormedXYSeries normed = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(new com.androidplot.Region(0, 10)), null);

        assertNull(normed.getX(0));
        assertEquals(1d, normed.getX(1).doubleValue(), DELTA);
    }

    @Test
    public void denormalize_invertsNormalization() {
        XYSeries rawData = new SimpleXYSeries(
                java.util.Arrays.asList(100, 200, 300), java.util.Arrays.asList(10, 20, 30), "raw");
        NormedXYSeries normed = new NormedXYSeries(rawData,
                new NormedXYSeries.Norm(null), new NormedXYSeries.Norm(null));

        for (int i = 0; i < rawData.size(); i++) {
            assertEquals(rawData.getX(i).doubleValue(),
                    normed.denormalizeXVal(normed.getX(i)).doubleValue(), DELTA);
            assertEquals(rawData.getY(i).doubleValue(),
                    normed.denormalizeYVal(normed.getY(i)).doubleValue(), DELTA);
        }
        assertNull(normed.denormalizeXVal(null));
        assertNull(normed.denormalizeYVal(null));
    }

    @Test
    public void plotListenerCallbacks_areForwardedToRawData() {
        ListeningSeries rawData = new ListeningSeries();
        NormedXYSeries normed = new NormedXYSeries(rawData);
        com.androidplot.Plot plot = new XYPlot(getContext(), "plot");

        normed.onBeforeDraw(plot, null);
        assertEquals(1, rawData.beforeDraws);
        assertEquals(0, rawData.afterDraws);

        normed.onAfterDraw(plot, null);
        assertEquals(1, rawData.beforeDraws);
        assertEquals(1, rawData.afterDraws);
    }

    @Test
    public void plotListenerCallbacks_nonListeningRawData_areIgnored() {
        // a bare XYSeries that is not a PlotListener:
        XYSeries rawData = new XYSeries() {
            @Override public String getTitle() { return "bare"; }
            @Override public int size() { return 2; }
            @Override public Number getX(int index) { return index; }
            @Override public Number getY(int index) { return index * 10; }
        };
        NormedXYSeries normed = new NormedXYSeries(rawData);
        com.androidplot.Plot plot = new XYPlot(getContext(), "plot");
        normed.onBeforeDraw(plot, null);
        normed.onAfterDraw(plot, null);
        assertEquals(2, normed.size());
        assertEquals(1d, normed.getY(1).doubleValue(), DELTA);
    }

    static class ListeningSeries extends SimpleXYSeries implements com.androidplot.PlotListener {
        int beforeDraws;
        int afterDraws;

        ListeningSeries() {
            super(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "listening", 1, 2, 3);
        }

        @Override
        public void onBeforeDraw(com.androidplot.Plot source, android.graphics.Canvas canvas) {
            beforeDraws++;
        }

        @Override
        public void onAfterDraw(com.androidplot.Plot source, android.graphics.Canvas canvas) {
            afterDraws++;
        }
    }
}
