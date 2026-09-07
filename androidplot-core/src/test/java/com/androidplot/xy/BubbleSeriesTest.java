// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import com.androidplot.Region;
import com.androidplot.util.SeriesUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class BubbleSeriesTest {

    @Test
    public void interleavedConstructor_splitsXYZTriples() {
        final BubbleSeries series = new BubbleSeries(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(3, series.size());
        assertNull(series.getTitle());

        assertEquals(1, series.getX(0));
        assertEquals(2, series.getY(0));
        assertEquals(3, series.getZ(0));
        assertEquals(4, series.getX(1));
        assertEquals(5, series.getY(1));
        assertEquals(6, series.getZ(1));
        assertEquals(7, series.getX(2));
        assertEquals(8, series.getY(2));
        assertEquals(9, series.getZ(2));
        assertEquals(Arrays.<Number>asList(3, 6, 9), series.getZVals());
    }

    @Test
    public void interleavedConstructor_singleTripleAndNullValues() {
        final BubbleSeries series = new BubbleSeries(1.5, null, 2);
        assertEquals(1, series.size());
        assertEquals(1.5, series.getX(0));
        assertNull(series.getY(0));
        assertEquals(2, series.getZ(0));
    }

    @Test
    public void interleavedConstructor_lengthNotMultipleOfThree_throws() {
        for (Number[] values : new Number[][]{{1}, {1, 2}, {1, 2, 3, 4}, {1, 2, 3, 4, 5}}) {
            try {
                new BubbleSeries(values);
                fail("expected RuntimeException for " + values.length + " values");
            } catch (RuntimeException expected) {
                // ok
            }
        }
    }

    @Test
    public void interleavedConstructor_nullArray_throws() {
        try {
            new BubbleSeries((Number[]) null);
            fail("expected RuntimeException");
        } catch (RuntimeException expected) {
            // ok
        }
    }

    @Test
    public void interleavedConstructor_empty_isEmptySeries() {
        final BubbleSeries series = new BubbleSeries();
        assertEquals(0, series.size());
        assertEquals(0, series.getZVals().size());
    }

    @Test
    public void yzConstructor_generatesXFromIndex() {
        final List<Number> yVals = Arrays.<Number>asList(10, 20, 30);
        final List<Number> zVals = Arrays.<Number>asList(1, 2, 3);
        final BubbleSeries series = new BubbleSeries(yVals, zVals, "yz");

        assertEquals("yz", series.getTitle());
        assertEquals(3, series.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i, series.getX(i));
            assertEquals(yVals.get(i), series.getY(i));
            assertEquals(zVals.get(i), series.getZ(i));
        }
        assertSame(zVals, series.getZVals());
    }

    @Test
    public void yzConstructor_mismatchedSizes_sizeFollowsZ() {
        // x is generated from z, so a shorter y list is only detected on access:
        final BubbleSeries series = new BubbleSeries(
                Arrays.<Number>asList(10, 20), Arrays.<Number>asList(1, 2, 3), "yz");
        assertEquals(3, series.size());
        assertEquals(2, series.getX(2));
        assertEquals(3, series.getZ(2));
        assertEquals(20, series.getY(1));
        try {
            series.getY(2);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // ok
        }

        // a longer y list is simply truncated by size():
        final BubbleSeries longerY = new BubbleSeries(
                Arrays.<Number>asList(10, 20, 30, 40), Arrays.<Number>asList(1, 2), "yz");
        assertEquals(2, longerY.size());
        assertEquals(30, longerY.getY(2));
    }

    @Test
    public void xyzConstructor_usesListsAsGiven() {
        final List<Number> xVals = Arrays.<Number>asList(5, 6);
        final List<Number> yVals = Arrays.<Number>asList(7, 8);
        final List<Number> zVals = Arrays.<Number>asList(9, 10);
        final BubbleSeries series = new BubbleSeries(xVals, yVals, zVals, "xyz");

        assertEquals("xyz", series.getTitle());
        assertEquals(2, series.size());
        assertEquals(5, series.getX(0));
        assertEquals(8, series.getY(1));
        assertEquals(10, series.getZ(1));
        assertSame(zVals, series.getZVals());
    }

    @Test
    public void xyzConstructor_mismatchedSizes_sizeFollowsX() {
        final BubbleSeries series = new BubbleSeries(
                Arrays.<Number>asList(1, 2, 3), Arrays.<Number>asList(4, 5), Arrays.<Number>asList(6), "xyz");
        assertEquals(3, series.size());
        assertEquals(5, series.getY(1));
        try {
            series.getZ(1);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // ok
        }
    }

    @Test
    public void minMax_includesZValues() {
        final BubbleSeries series = new BubbleSeries(1, 2, 3, 4, 5, 6, 7, 8, 9);

        // x/y bounds via the XYSeries contract:
        final RectRegion xy = SeriesUtils.minMax(series);
        assertEquals(1.0, xy.getMinX().doubleValue(), 0);
        assertEquals(7.0, xy.getMaxX().doubleValue(), 0);
        assertEquals(2.0, xy.getMinY().doubleValue(), 0);
        assertEquals(8.0, xy.getMaxY().doubleValue(), 0);

        // z bounds the way BubbleRenderer computes them:
        final Region z = SeriesUtils.minMax(new Region(), series.getZVals());
        assertEquals(3.0, z.getMin().doubleValue(), 0);
        assertEquals(9.0, z.getMax().doubleValue(), 0);

        // z values are ignored by the xy min/max:
        final BubbleSeries bigZ = new BubbleSeries(1, 1, 1000);
        final RectRegion bigZBounds = SeriesUtils.minMax(bigZ);
        assertEquals(1.0, bigZBounds.getMaxX().doubleValue(), 0);
        assertEquals(1.0, bigZBounds.getMaxY().doubleValue(), 0);
    }

    @Test
    public void minMax_zValuesAcrossSeries_unionIntoBounds() {
        final Region bounds = new Region(5, 5);
        SeriesUtils.minMax(bounds, new BubbleSeries(0, 0, 2).getZVals());
        SeriesUtils.minMax(bounds, new BubbleSeries(0, 0, 11, 0, 0, 7).getZVals());
        assertEquals(2.0, bounds.getMin().doubleValue(), 0);
        assertEquals(11.0, bounds.getMax().doubleValue(), 0);

        // nulls in z are skipped:
        final List<Number> zWithNull = new ArrayList<>(Arrays.<Number>asList(null, 3));
        final Region withNull = SeriesUtils.minMax(new Region(), zWithNull);
        assertEquals(3.0, withNull.getMin().doubleValue(), 0);
        assertEquals(3.0, withNull.getMax().doubleValue(), 0);
    }
}
