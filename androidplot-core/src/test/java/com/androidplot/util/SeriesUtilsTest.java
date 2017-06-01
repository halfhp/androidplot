/*
 * Copyright 2016 AndroidPlot.com
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

package com.androidplot.util;

import com.androidplot.*;
import com.androidplot.xy.*;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SeriesUtilsTest {

    // 8 element lists:
    final List<Number> LINEAR = Arrays.asList(new Number[]{ 1, 2, 3, 4 ,5 , 6, 7, 8});
    final List<Number> LINEAR_INVERSE = Arrays.asList(new Number[]{ 8, 7, 6, 5, 4, 3, 2, 1});
    final List<Number> ZIG_ZAG = Arrays.asList(new Number[]{1, 10, 1, 10, 1, 10, 1, 10});
    final List<Number> NULLS = Arrays.asList(new Number[]{null, 2, null, 4, null, 0, -1, null});

    // single element lists:
    final List<Number> SINGLE_VALUE = Arrays.asList(new Number[]{3});
    final List<Number> SINGLE_VALUE_NULL = Arrays.asList(new Number[]{null});

    // empty list:
    final List<Number> EMPTY = new ArrayList<>();

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void minMax_onSimpleXYSeries_calculatesExpectedRegion() {
        SimpleXYSeries series = new SimpleXYSeries(LINEAR, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        RectRegion minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX().doubleValue(), 0);
        assertEquals(7, minMax.getMaxX().doubleValue(), 0);
        assertEquals(1, minMax.getMinY().doubleValue(), 0);
        assertEquals(8, minMax.getMaxY().doubleValue(), 0);

        series = new SimpleXYSeries(LINEAR_INVERSE, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX().doubleValue(), 0);
        assertEquals(7, minMax.getMaxX().doubleValue(), 0);
        assertEquals(1, minMax.getMinY().doubleValue(), 0);
        assertEquals(8, minMax.getMaxY().doubleValue(), 0);

        series = new SimpleXYSeries(ZIG_ZAG, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX().doubleValue(), 0);
        assertEquals(7, minMax.getMaxX().doubleValue(), 0);
        assertEquals(1, minMax.getMinY().doubleValue(), 0);
        assertEquals(10, minMax.getMaxY().doubleValue(), 0);

        series = new SimpleXYSeries(NULLS, NULLS, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(-1, minMax.getMinX().doubleValue(), 0);
        assertEquals(4, minMax.getMaxX().doubleValue(), 0);
        assertEquals(-1, minMax.getMinY().doubleValue(), 0);
        assertEquals(4, minMax.getMaxY().doubleValue(), 0);

        series = new SimpleXYSeries(SINGLE_VALUE, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX().doubleValue(), 0);
        assertEquals(0, minMax.getMaxX().doubleValue(), 0);
        assertEquals(3, minMax.getMinY().doubleValue(), 0);
        assertEquals(3, minMax.getMaxY().doubleValue(), 0);

        series = new SimpleXYSeries(SINGLE_VALUE_NULL, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX().doubleValue(), 0);
        assertEquals(0, minMax.getMaxX().doubleValue(), 0);
        assertEquals(null, minMax.getMinY());
        assertEquals(null, minMax.getMaxY());

        series = new SimpleXYSeries(EMPTY, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(null, minMax.getMinX());
        assertEquals(null, minMax.getMaxX());
        assertEquals(null, minMax.getMinY());
        assertEquals(null, minMax.getMaxY());
    }

    @Test
    public void minMax_onFastXYSeries_usesSeriesMinMax() {
        FastXYSeries series = mock(FastXYSeries.class);
        SeriesUtils.minMax(series);
        verify(series).minMax();
    }

    @Test
    public void minMax_calculatesExpectedRegion_onFastXYSeriesWithLayoutConstraints() {
        FastXYSeries series = new FastXYSeries() {

            // create a couple arrays of y-values to plot:
            final ArrayList<Integer> times = new ArrayList<>();
            final ArrayList<Integer> values = new ArrayList<>();

            {
                for (int i = 0; i < 10; i++) {
                    times.add(i);
                    values.add(i);
                }
            }

            @Override
            public int size() {
                return times.size();
            }

            @Override
            public Number getX(int index) {
                return times.get(index);
            }

            @Override
            public Number getY(int index) {
                return values.get(index);
            }

            @Override
            public String getTitle() {
                return "Isaac's crazy thing";
            }

            @Override
            public RectRegion minMax() {
                return new RectRegion(0, 10, 0, 10);
            }
        };

        final XYConstraints constraints = new XYConstraints();
        constraints.setDomainLowerBoundaryMode(BoundaryMode.FIXED);
        constraints.setDomainUpperBoundaryMode(BoundaryMode.FIXED);
        constraints.setMinX(5);
        constraints.setMaxX(9);

        final RectRegion result = SeriesUtils.minMax(constraints, series);
        assertEquals(5d, result.getMinX().doubleValue());
        assertEquals(9d, result.getMaxX().doubleValue());
        assertEquals(5d, result.getMinY().doubleValue());
        assertEquals(9d, result.getMaxY().doubleValue());
    }

    @Test
    public void minMax_onSeriesList_producesAggregateResult() {
        Region minMax = SeriesUtils.minMax(LINEAR);
        assertEquals(1, minMax.getMin().doubleValue(), 0);
        assertEquals(8, minMax.getMax().doubleValue(), 0);

        minMax = SeriesUtils.minMax(LINEAR_INVERSE);
        assertEquals(1, minMax.getMin().doubleValue(), 0);
        assertEquals(8, minMax.getMax().doubleValue(), 0);

        minMax = SeriesUtils.minMax(ZIG_ZAG);
        assertEquals(1, minMax.getMin().doubleValue(), 0);
        assertEquals(10, minMax.getMax().doubleValue(), 0);

        minMax = SeriesUtils.minMax(NULLS);
        assertEquals(-1, minMax.getMin().doubleValue(), 0);
        assertEquals(4, minMax.getMax().doubleValue(), 0);

        minMax = SeriesUtils.minMax(SINGLE_VALUE);
        assertEquals(3, minMax.getMin().doubleValue(), 0);
        assertEquals(3, minMax.getMax().doubleValue(), 0);

        minMax = SeriesUtils.minMax(SINGLE_VALUE_NULL);
        assertEquals(null, minMax.getMin());
        assertEquals(null, minMax.getMax());

        minMax = SeriesUtils.minMax(EMPTY);
        assertEquals(null, minMax.getMin());
        assertEquals(null, minMax.getMax());
    }

    @Test
    public void getNullRegion_producesExpectedResult() {
        XYSeries s1 = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "s1",
                0, 0, // 0
                1, 1,         // 1
                2, 2,         // 2
                null, null,   // 3
                null, null,   // 4
                5, 5,         // 5
                6, 6);        // 6

        try {
            Region r1 = SeriesUtils.getNullRegion(s1, 0);
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException e) {
            // expected
        }

        try {
            Region r2 = SeriesUtils.getNullRegion(s1, s1.size() - 1);
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException e) {
            // expected
        }

        Region r3 = SeriesUtils.getNullRegion(s1, 3);
        assertEquals(2, r3.getMin().intValue());
        assertEquals(5, r3.getMax().intValue());

        Region r4 = SeriesUtils.getNullRegion(s1, 4);
        assertEquals(2, r4.getMin().intValue());
        assertEquals(5, r4.getMax().intValue());
    }

    @Test
    public void iBoundsMin_findsMin() {
        XYSeries s1 = new SimpleXYSeries(
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                "s1");

        assertEquals(0, SeriesUtils.iBoundsMin(s1, 0, 1));
        assertEquals(6, SeriesUtils.iBoundsMin(s1, 6, 1));
        assertEquals(12, SeriesUtils.iBoundsMin(s1, 12, 1));

        // now test with null vals:
        XYSeries s2 = new SimpleXYSeries(
                Arrays.asList(null, 1, 2, null, null, 5, 6, 7, 8, null, 10, 11, null),
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                "s2");

        assertEquals(0, SeriesUtils.iBoundsMin(s2, 0, 1));
        assertEquals(2, SeriesUtils.iBoundsMin(s2, 3, 1));
        assertEquals(11, SeriesUtils.iBoundsMin(s2, 12, 1));

        // test with a higher step value:
        assertEquals(0, SeriesUtils.iBoundsMin(s2, 0, 5));
        assertEquals(2, SeriesUtils.iBoundsMin(s2, 3, 5));
        assertEquals(11, SeriesUtils.iBoundsMin(s2, 12, 5));
    }

    @Test
    public void iBoundsMax_findsMax() {
        XYSeries s1 = new SimpleXYSeries(
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                "s1");

        assertEquals(12, SeriesUtils.iBoundsMax(s1, 12, 1));
        assertEquals(6, SeriesUtils.iBoundsMax(s1, 6, 1));
        assertEquals(12, SeriesUtils.iBoundsMax(s1, 12, 1));

        // now test with null vals:
        XYSeries s2 = new SimpleXYSeries(
                Arrays.asList(null, 1, 2, null, null, 5, 6, 7, 8, null, 10, 11, null),
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                "s2");

        assertEquals(1, SeriesUtils.iBoundsMax(s2, 0, 1));
        assertEquals(5, SeriesUtils.iBoundsMax(s2, 3, 1));
        assertEquals(12, SeriesUtils.iBoundsMax(s2, 12, 1));

        // test with a higher step value:
        assertEquals(1, SeriesUtils.iBoundsMax(s2, 0, 5));
        assertEquals(5, SeriesUtils.iBoundsMax(s2, 3, 5));
        assertEquals(12, SeriesUtils.iBoundsMax(s2, 12, 5));
    }

    @Test
    public void iBounds_findsMinMax() {
        FastXYSeries series = mock(FastXYSeries.class);
        when(series.size()).thenReturn(3);
        when(series.getX(0)).thenReturn(0);
        when(series.getX(1)).thenReturn(1);
        when(series.getX(2)).thenReturn(2);

        Region result = SeriesUtils.iBounds(series, new RectRegion(0, 1, 0, 1));
        assertEquals(0, result.getMin().intValue());
        assertEquals(1, result.getMax().intValue());

        // test with nulls:
        when(series.size()).thenReturn(6);
        when(series.getX(0)).thenReturn(0);
        when(series.getX(1)).thenReturn(0.5);
        when(series.getX(2)).thenReturn(null);
        when(series.getX(3)).thenReturn(null);
        when(series.getX(4)).thenReturn(1);
        when(series.getX(5)).thenReturn(3);

        result = SeriesUtils.iBounds(series, new RectRegion(0, 1, 0, 1));
        assertEquals(0, result.getMin().intValue());
        assertEquals(4, result.getMax().intValue());
    }
}
