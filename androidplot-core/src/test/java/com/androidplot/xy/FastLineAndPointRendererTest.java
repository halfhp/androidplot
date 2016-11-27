/*
 * Copyright 2015 AndroidPlot.com
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

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FastLineAndPointRendererTest extends AndroidplotTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnRender() throws Exception {
        RectF gridRect = new RectF(5, 5, 105, 105);
        XYPlot plot = new XYPlot(getContext(), "Test");

        FastLineAndPointRenderer.Formatter formatter =
                new FastLineAndPointRenderer.Formatter(Color.RED, Color.RED, null);


        // create a series composed of 3 "segments"; series portions separated by null values:
        XYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, null, 3, 4, 5, 6, 7, null, null, 8);

        FastLineAndPointRenderer renderer = Mockito.spy(new FastLineAndPointRenderer(plot));
        Canvas canvas = mock(Canvas.class);

        renderer.onRender(canvas, gridRect, series, formatter, null);

        // first segment
        verify(renderer, times(1)).drawSegment(
                eq(canvas),
                any(float[].class),
                eq(0),
                eq(4),
                eq(formatter));

        verify(canvas, times(1)).drawPoints(
                any(float[].class),
                eq(0),
                eq(4),
                eq(formatter.getVertexPaint()));

        // second segment
        verify(renderer, times(1)).drawSegment(
                eq(canvas),
                any(float[].class),
                eq(6),
                eq(10),
                eq(formatter));

        verify(canvas, times(1)).drawPoints(
                any(float[].class),
                eq(6),
                eq(10),
                eq(formatter.getVertexPaint()));

        // third segment
        verify(renderer, times(1)).drawSegment(
                eq(canvas),
                any(float[].class),
                eq(20),
                eq(2),
                eq(formatter));

        verify(canvas, times(1)).drawPoints(
                any(float[].class),
                eq(20),
                eq(2),
                eq(formatter.getVertexPaint()));
    }
}
