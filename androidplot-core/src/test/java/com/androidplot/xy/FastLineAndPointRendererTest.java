// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
