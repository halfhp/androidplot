// SPDX-License-Identifier: Apache-2.0

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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
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
    public void setUp() {
        canvas = spy(new Canvas());
        xyPlot = spy(new XYPlot(getContext(), "My Plot"));
        barFormatter = spy(new BarFormatter(Color.RED, Color.RED));
    }

    @Test
    public void onRender_handlesNullValues() {
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
    public void onRender_stacked() {
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
    public void onRender_stacked_measuresBarsFromRangeOrigin() {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 3);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 4);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        // range 2..12 over 100px => 10px per unit; origin (y = 0) is at 120px:
        xyPlot.setRangeBoundaries(2, 12, BoundaryMode.FIXED);
        xyPlot.setUserRangeOrigin(0);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(2)).
                drawBar(eq(canvas), any(BarRenderer.Bar.class), any(RectF.class));

        // s1[0]: y = 3 spans the origin (120) to the pixel for y = 3 (90):
        verifyBarHeight(90, 120, barFormatter, 1);

        // s2[0]: y = 4 stacks on top, ending at the pixel for y = 7 (50).
        // Previously heights were measured from the plot bottom, giving a top of 70:
        verifyBarHeight(50, 90, barFormatter, 1);
    }

    @Test
    public void onRender_stacked_negativeValuesStackDownwardFromRangeOrigin() {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", -2);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", -3);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        // range -10..10 over 100px => 5px per unit; origin (y = 0) is at 50px:
        xyPlot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);
        xyPlot.setUserRangeOrigin(0);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        // s1[0]: y = -2 hangs from the origin down to 60:
        verifyBarHeight(50, 60, barFormatter, 1);

        // s2[0]: y = -3 continues down to the pixel for y = -5 (75):
        verifyBarHeight(60, 75, barFormatter, 1);
    }

    @Test
    public void setFillPaint_null_disablesFill() {
        BarFormatter formatter = new BarFormatter(Color.RED, Color.RED);
        formatter.setFillPaint(null);

        // previously BarFormatter shadowed the inherited fillPaint field so this stayed true:
        assertFalse(formatter.hasFillPaint());
        assertNull(formatter.getFillPaint());
        assertTrue(formatter.hasLinePaint());

        formatter.setBorderPaint(null);
        assertFalse(formatter.hasLinePaint());
        assertNull(formatter.getBorderPaint());
    }

    @Test
    public void onRender_withNullFillAndBorderPaint_drawsNothingWithNullPaint() {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 2, 5, 7);
        BarRenderer renderer = setupRendererForTesting(s1);
        barFormatter.setFillPaint(null);
        barFormatter.setBorderPaint(null);

        xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);
        renderer.doDrawLegendIcon(canvas, new RectF(0, 0, 10, 10), barFormatter);

        verify(canvas, never()).drawRect(anyFloat(), anyFloat(), anyFloat(), anyFloat(), isNull());
        verify(canvas, never()).drawRect(any(RectF.class), isNull());
    }

    @Test
    public void onRender_sideBySide() {
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
    public void onRender_inOrder_drawsFirstAddedSeriesFirst() {
        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1", 1, 5, 6);
        XYSeries s2 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s2", 2, 4, 7);

        BarRenderer renderer = setupRendererForTesting(s1, s2);
        renderer.setBarOrientation(BarRenderer.BarOrientation.IN_ORDER);

        xyPlot.setUserRangeOrigin(0);
        xyPlot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, s1, barFormatter, renderStack);

        verify(renderer, times(6))
                .drawBar(eq(canvas), barCaptor.capture(), rectCaptor.capture());

        // list of all bars drawn, in the exact order they were drawn.
        List<BarRenderer.Bar> bars = barCaptor.getAllValues();

        assertEquals(bars.get(0).getY(), s1.getY(0));
        assertEquals(bars.get(1).getY(), s2.getY(0));

        assertEquals(bars.get(2).getY(), s1.getY(1));
        assertEquals(bars.get(3).getY(), s2.getY(1));

        assertEquals(bars.get(4).getY(), s1.getY(2));
        assertEquals(bars.get(5).getY(), s2.getY(2));
    }

    @Test
    public void onRender_overlaid_drawsBarsWithExpectedHeight() {
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
     */
    @Test
    public void onRender_overlaid_drawsSmallestBarsLast() {
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
    public void onRender_fixedBarWidth_rendersAllBarsWithSameWidth() {
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
    public void onRender_fixedGapWidth_rendersFixedGapBetweenAllBars() {
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
