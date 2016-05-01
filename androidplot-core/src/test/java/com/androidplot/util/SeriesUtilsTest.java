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

import com.androidplot.Bounds;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYBounds;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;

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
    public void testSeriesMinMax() {
        SimpleXYSeries series = new SimpleXYSeries(LINEAR, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        XYBounds minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX());
        assertEquals(7, minMax.getMaxX());
        assertEquals(1, minMax.getMinY());
        assertEquals(8, minMax.getMaxY());

        series = new SimpleXYSeries(LINEAR_INVERSE, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX());
        assertEquals(7, minMax.getMaxX());
        assertEquals(1, minMax.getMinY());
        assertEquals(8, minMax.getMaxY());

        series = new SimpleXYSeries(ZIG_ZAG, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX());
        assertEquals(7, minMax.getMaxX());
        assertEquals(1, minMax.getMinY());
        assertEquals(10, minMax.getMaxY());

        series = new SimpleXYSeries(NULLS, NULLS, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(-1, minMax.getMinX());
        assertEquals(4, minMax.getMaxX());
        assertEquals(-1, minMax.getMinY());
        assertEquals(4, minMax.getMaxY());

        series = new SimpleXYSeries(SINGLE_VALUE, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX());
        assertEquals(0, minMax.getMaxX());
        assertEquals(3, minMax.getMinY());
        assertEquals(3, minMax.getMaxY());

        series = new SimpleXYSeries(SINGLE_VALUE_NULL, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        minMax = SeriesUtils.minMax(series);
        assertEquals(0, minMax.getMinX());
        assertEquals(0, minMax.getMaxX());
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
    public void testListMinMax() {
        Bounds minMax = SeriesUtils.minMax(LINEAR);
        assertEquals(1, minMax.getMin());
        assertEquals(8, minMax.getMax());

        minMax = SeriesUtils.minMax(LINEAR_INVERSE);
        assertEquals(1, minMax.getMin());
        assertEquals(8, minMax.getMax());

        minMax = SeriesUtils.minMax(ZIG_ZAG);
        assertEquals(1, minMax.getMin());
        assertEquals(10, minMax.getMax());

        minMax = SeriesUtils.minMax(NULLS);
        assertEquals(-1, minMax.getMin());
        assertEquals(4, minMax.getMax());

        minMax = SeriesUtils.minMax(SINGLE_VALUE);
        assertEquals(3, minMax.getMin());
        assertEquals(3, minMax.getMax());

        minMax = SeriesUtils.minMax(SINGLE_VALUE_NULL);
        assertEquals(null, minMax.getMin());
        assertEquals(null, minMax.getMax());

        minMax = SeriesUtils.minMax(EMPTY);
        assertEquals(null, minMax.getMin());
        assertEquals(null, minMax.getMax());
    }
}
