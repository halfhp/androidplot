// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests {@link BubbleRenderer} and some of {@link BubbleFormatter}.
 */
public class BubbleRendererTest extends AndroidplotTest {

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
    public void testOnRender() throws Exception {
        BubbleFormatter formatter = spy(new BubbleFormatter());
        BubbleRenderer br = formatter.getRendererInstance(xyPlot);
        BubbleRenderer renderer = spy(br);

        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(XYPlot.class));

        BubbleSeries bs = new BubbleSeries(1, 2, 3, 4, 5, 6, 7, 8, 9);

        xyPlot.addSeries(bs, formatter);

        renderer.onRender(canvas, plotArea, bs, formatter, renderStack);
    }

    @Test
    public void testOnRender_withPointLabeler() throws Exception {
        BubbleFormatter formatter = spy(new BubbleFormatter());
        PointLabelFormatter plf = new PointLabelFormatter(Color.MAGENTA);
        formatter.setPointLabelFormatter(plf);
        BubbleRenderer br = formatter.getRendererInstance(xyPlot);
        BubbleRenderer renderer = spy(br);

        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(XYPlot.class));

        BubbleSeries bs = new BubbleSeries(2, 2, 22);

        xyPlot.addSeries(bs, formatter);

        renderer.onRender(canvas, plotArea, bs, formatter, renderStack);

        // verify the z-val is the one labeled:
        verify(canvas).drawText(eq("22"), anyFloat(), anyFloat(), eq(plf.getTextPaint()));
    }

    @Test
    public void onRender_withZeroZVal_drawsFiniteRadiiWithinConfiguredBounds() {
        BubbleFormatter formatter = spy(new BubbleFormatter());
        BubbleRenderer renderer = setupRenderer(formatter);

        // z = 0, 4, 9; only the two bubbles with z > 0 are drawn:
        BubbleSeries bs = new BubbleSeries(0, 0, 0, 1, 1, 4, 2, 2, 9);
        xyPlot.addSeries(bs, formatter);
        xyPlot.calculateMinMaxVals();

        renderer.onRender(canvas, plotArea, bs, formatter, renderStack);

        // each bubble is drawn twice (fill + stroke).  Previously the magnitude region
        // collapsed to [0, 0] producing infinite radii:
        ArgumentCaptor<Float> radius = ArgumentCaptor.forClass(Float.class);
        verify(canvas, times(4)).drawCircle(anyFloat(), anyFloat(), radius.capture(), any(Paint.class));
        for (float r : radius.getAllValues()) {
            assertTrue("radius should be finite: " + r, Float.isFinite(r));
            assertTrue(r >= renderer.getMinBubbleRadius());
            assertTrue(r <= renderer.getMaxBubbleRadius());
        }

        // z = 9 is the largest value so it gets the largest radius:
        assertEquals(renderer.getMaxBubbleRadius(), radius.getAllValues().get(2), 0.0001f);
        assertTrue(radius.getAllValues().get(0) < radius.getAllValues().get(2));
    }

    @Test
    public void onRender_singleBubble_drawsFiniteRadius() {
        BubbleFormatter formatter = spy(new BubbleFormatter());
        BubbleRenderer renderer = setupRenderer(formatter);

        BubbleSeries bs = new BubbleSeries(1, 1, 9);
        xyPlot.addSeries(bs, formatter);
        xyPlot.calculateMinMaxVals();

        renderer.onRender(canvas, plotArea, bs, formatter, renderStack);

        // previously the zero-length magnitude region produced a NaN radius:
        ArgumentCaptor<Float> radius = ArgumentCaptor.forClass(Float.class);
        verify(canvas, times(2)).drawCircle(anyFloat(), anyFloat(), radius.capture(), any(Paint.class));
        for (float r : radius.getAllValues()) {
            assertTrue("radius should be finite: " + r, Float.isFinite(r));
            assertEquals(renderer.getMaxBubbleRadius(), r, 0.0001f);
        }
    }

    private BubbleRenderer setupRenderer(BubbleFormatter formatter) {
        BubbleRenderer renderer = spy(formatter.getRendererInstance(xyPlot));
        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(XYPlot.class));
        return renderer;
    }
}
