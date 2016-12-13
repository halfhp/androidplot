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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.RenderStack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BarRendererTest extends AndroidplotTest {

    XYPlot xyPlot;

    Canvas canvas;

    RectF plotArea = new RectF(0, 0, 100, 100);

    @Mock
    RenderStack renderStack;

    @Before
    public void setUp() throws Exception {
        canvas = spy(new Canvas());
        xyPlot = spy(new XYPlot(getContext(), "My Plot"));
    }

    @Test
    public void testOnRender_stacked() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 2, 5, 7);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 8, 5, 3);

        BarFormatter barFormatter = spy(new BarFormatter(Color.RED, Color.RED));
        BarRenderer renderer = spy((BarRenderer)barFormatter.getRendererInstance(xyPlot));
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        xyPlot.addSeries(s1, barFormatter);
        xyPlot.addSeries(s2, barFormatter);

        doReturn(renderer.getClass()).when(barFormatter).getRendererClass();

        xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // s1[0]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(80f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[0]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(0f),
                anyFloat(),
                eq(80f),
                any(Paint.class));

        // s1[1]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(50f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[1]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(0f),
                anyFloat(),
                eq(50f),
                any(Paint.class));

        // s1[2]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(30f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[2]
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(0f),
                anyFloat(),
                eq(30f),
                any(Paint.class));
    }

    @Test
    public void testOnRender_sideBySide() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarFormatter barFormatter = spy(new BarFormatter(Color.RED, Color.RED));
        BarRenderer renderer = spy((BarRenderer)barFormatter.getRendererInstance(xyPlot));
        renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);

        xyPlot.addSeries(s1, barFormatter);
        xyPlot.addSeries(s2, barFormatter);

        doReturn(renderer.getClass()).when(barFormatter).getRendererClass();

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // s1[0] has zero height so should not be drawn:
        verify(canvas, never()).drawRect(
                anyFloat(),
                eq(100f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s1[1]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(50f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s1[2] & s2[2]:
        verify(canvas, times(4)).drawRect(
                anyFloat(),
                eq(0f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[0]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(90f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[1]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(25f),
                anyFloat(),
                eq(100f),
                any(Paint.class));
    }

    @Test
    public void testOnRender_overlaid() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarFormatter barFormatter = spy(new BarFormatter(Color.RED, Color.RED));
        BarRenderer renderer = spy((BarRenderer)barFormatter.getRendererInstance(xyPlot));
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);

        xyPlot.addSeries(s1, barFormatter);
        xyPlot.addSeries(s2, barFormatter);

        doReturn(renderer.getClass()).when(barFormatter).getRendererClass();

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // s1[0] has zero height so should not be drawn:
        verify(canvas, never()).drawRect(
                anyFloat(),
                eq(100f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s1[1]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(50f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s1[2] & s2[2]:
        verify(canvas, times(4)).drawRect(
                anyFloat(),
                eq(0f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[0]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(90f),
                anyFloat(),
                eq(100f),
                any(Paint.class));

        // s2[1]:
        verify(canvas, times(2)).drawRect(
                anyFloat(),
                eq(25f),
                anyFloat(),
                eq(100f),
                any(Paint.class));
    }
}
