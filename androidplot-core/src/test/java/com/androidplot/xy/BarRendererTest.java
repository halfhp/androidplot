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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BarRendererTest extends AndroidplotTest {

    XYPlot xyPlot;

    Canvas canvas;

    RectF plotArea = new RectF(0, 0, 100, 100);

    BarFormatter barFormatter;

    @Mock
    RenderStack renderStack;

    @Captor
    ArgumentCaptor<BarRenderer.Bar> barCaptor;

    @Captor
    ArgumentCaptor<RectF> rectCaptor;

    @Before
    public void setUp() throws Exception {
        canvas = spy(new Canvas());
        xyPlot = spy(new XYPlot(getContext(), "My Plot"));
        barFormatter = spy(new BarFormatter(Color.RED, Color.RED));
    }

    @Test
    public void testOnRender_handlesNullValues() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", null, 5, null);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", null, 5, null);

        BarRenderer renderer = setupRendererForTesting(s1, s2);

        xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(6))
                .drawBar(eq(canvas), barCaptor.capture(), rectCaptor.capture());
    }

    @Test
    public void testOnRender_stacked() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 2, 5, 7);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 8, 5, 3);

        BarRenderer renderer = setupRendererForTesting(s1, s2);

        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // make sure the expected number of bars draws were attempted:
        verify(renderer, times(s1.size() + s2.size())).
                drawBar(eq(canvas), any(BarRenderer.Bar.class), any(RectF.class));

        // s1[0]
        verifyBarHeight(80, 100, barFormatter, 1);

        // s2[0]
        verifyBarHeight(0, 80, barFormatter, 1);

        // s1[1]
        verifyBarHeight(50, 100, barFormatter, 1);

        // s2[1]
        verifyBarHeight(0, 50, barFormatter, 1);

        // s1[2]
        verifyBarHeight(30, 100, barFormatter, 1);

        // s2[2]
        verifyBarHeight(0, 30, barFormatter, 1);
    }

    @Test
    public void testOnRender_sideBySide() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // make sure the expected number of bars draws were attempted:
        verify(renderer, times(s1.size() + s2.size())).
                drawBar(eq(canvas), any(BarRenderer.Bar.class), any(RectF.class));

        // s1[0] has zero height so should not be drawn:
        verifyBarHeight(100, 100, barFormatter, 0);

        // s1[1]:
        verifyBarHeight(50, 100, barFormatter, 1);

        // s1[2] & s2[2]:
        verifyBarHeight(0, 100, barFormatter, 2);

        // s2[0]:
        verifyBarHeight(90, 100, barFormatter, 1);

        // s2[1]:
        verifyBarHeight(25, 100, barFormatter, 1);
    }

    @Test
    public void testOnRender_overlaid() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // make sure the expected number of bars draws were attempted:
        verify(renderer, times(s1.size() + s2.size())).
                drawBar(eq(canvas), any(BarRenderer.Bar.class), any(RectF.class));

        // s1[0] has zero height so should not be drawn:
        verifyBarHeight(100, 100, barFormatter, 0);

        // s1[1]:
        verifyBarHeight(50, 100, barFormatter, 1);

        // s1[2] & s2[2]:
        verifyBarHeight(0, 100, barFormatter, 2);

        // s2[0]:
        verifyBarHeight(90, 100, barFormatter, 1);

        // s2[1]:
        verifyBarHeight(25, 100, barFormatter, 1);
    }

    /**
     * Verify that positive values are drawn in order of highest yVal, while negative values are
     * drawn in order of lowest yVal.
     * @throws Exception
     */
    @Test
    public void testOnRender_overlaid_drawsBarsInOrder() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", -1, -2, 1, 2);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", -2, -1, 2, 1);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);

        xyPlot.setUserRangeOrigin(0);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(8))
                .drawBar(eq(canvas), barCaptor.capture(), rectCaptor.capture());

        // list of all bars drawn, in the exact order they were drawn.
        List<BarRenderer.Bar> bars = barCaptor.getAllValues();

        assertEquals(s2.getY(0), bars.get(0).getY());
        assertEquals(s1.getY(0), bars.get(1).getY());

        assertEquals(s1.getY(1), bars.get(2).getY());
        assertEquals(s2.getY(1), bars.get(3).getY());
    }

    @Test
    public void testFixedBarWidth() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        final float barWidth = 10;
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, barWidth);

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(6))
                .drawBar(eq(canvas), barCaptor.capture(), rectCaptor.capture());

        List<RectF> barRects = rectCaptor.getAllValues();

        for(RectF rect : barRects) {
            assertEquals(barWidth, rect.width());
        }
    }

    @Test
    public void testFixedGapWidth() throws Exception {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 0, 5, 10);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 1, 7.5, 10);

        BarRenderer renderer = setupRendererForTesting(s1, s2);

        final float gap = 5;
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, gap);

        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(6))
                .drawBar(eq(canvas), barCaptor.capture(), rectCaptor.capture());

        List<RectF> barRects = rectCaptor.getAllValues();

        // verify that the spacing between each bar group is exactly what was set:
        assertEquals(gap, barRects.get(2).left - barRects.get(0).right);
        assertEquals(gap, barRects.get(3).left - barRects.get(1).right);

        assertEquals(gap, barRects.get(4).left - barRects.get(2).right);
        assertEquals(gap, barRects.get(5).left - barRects.get(3).right);
    }

    private void verifyBarHeight(float top, float bottom, BarFormatter formatter, int times) {
        final Paint borderPaint = formatter.getBorderPaint();
        verify(canvas, times(times)).drawRect(
                anyFloat(),
                eq(top),
                anyFloat(),
                eq(bottom),
                eq(borderPaint));

        final Paint fillPaint = formatter.getFillPaint();
        verify(canvas, times(times)).drawRect(
                anyFloat(),
                eq(top),
                anyFloat(),
                eq(bottom),
                eq(fillPaint));
    }

    protected BarRenderer setupRendererForTesting(XYSeries... series) {
        BarRenderer renderer = spy((BarRenderer)barFormatter.getRendererInstance(xyPlot));
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);

        for(XYSeries s : series) {
            xyPlot.addSeries(s, barFormatter);
        }

        doReturn(renderer.getClass()).when(barFormatter).getRendererClass();
        return renderer;
    }
}
