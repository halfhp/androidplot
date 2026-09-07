// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;

import org.junit.*;
import org.mockito.*;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LineAndPointRendererTest extends AndroidplotTest {

    private XYPlot xyPlot;

    @Mock
    Canvas canvas;

    RectF plotArea = new RectF(0, 0, 100, 100);

    @Before
    public void setUp() throws Exception {
        xyPlot = spy(new XYPlot(getContext(), "XYPlot"));
    }

    @Test
    public void testDrawSeries_withInterpolation() throws Exception {
        LineAndPointRenderer renderer = spy(new LineAndPointRenderer(xyPlot));
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        XYSeries series = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1", 1, 2, 3);
        renderer.drawSeries(canvas, plotArea, series, formatter);
    }

    /**
     * Sanity run to make sure that at the end of the day, points are being drawn at the expected
     * screen-coords.
     * @throws Exception
     */
    @Test
    public void testDrawSeries() throws Exception {

        // 100x100 plot space:
        plotArea = new RectF(0, 0, 99, 99);
        FastLineAndPointRenderer.Formatter formatter =
                new FastLineAndPointRenderer.Formatter(Color.RED, Color.RED, null);

        // create a series composed of 3 "segments"; series portions separated by null values:
        XYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        LineAndPointRenderer renderer = Mockito.spy(new LineAndPointRenderer(xyPlot));

        xyPlot.addSeries(series, formatter);

        xyPlot.calculateMinMaxVals();
        renderer.drawSeries(canvas, plotArea, series, formatter);

        PointF[] expectedPoints = new PointF[] {
                new PointF(0, 99),
                new PointF(11, 88),
                new PointF(22, 77),
                new PointF(33, 66),
                new PointF(44, 55),
                new PointF(55, 44),
                new PointF(66, 33),
                new PointF(77, 22),
                new PointF(88, 11),
                new PointF(99, 0)
        };
        ArgumentCaptor<List> capturedPoints= ArgumentCaptor.forClass(List.class);

        verify(renderer, times(1)).renderPoints(
                eq(canvas),
                eq(plotArea),
                eq(series),
                eq(0),
                eq(expectedPoints.length),
                capturedPoints.capture(),
                eq(formatter));

        List<PointF> pList = capturedPoints.getValue();

        // {0, 1}
        assertEquals(expectedPoints[0].x, pList.get(0).x);
        assertEquals(expectedPoints[0].y, pList.get(0).y);

        // {1, 2}
        assertEquals(expectedPoints[1].x, pList.get(1).x);
        assertEquals(expectedPoints[1].y, pList.get(1).y);

        // {2, 3}
        assertEquals(expectedPoints[2].x, pList.get(2).x);
        assertEquals(expectedPoints[2].y, pList.get(2).y);

        // {3, 4}
        assertEquals(expectedPoints[3].x, pList.get(3).x);
        assertEquals(expectedPoints[3].y, pList.get(3).y);

        // {4, 5}
        assertEquals(expectedPoints[4].x, pList.get(4).x);
        assertEquals(expectedPoints[4].y, pList.get(4).y);

        // {5, 6}
        assertEquals(expectedPoints[5].x, pList.get(5).x);
        assertEquals(expectedPoints[5].y, pList.get(5).y);

        // {6, 7}
        assertEquals(expectedPoints[6].x, pList.get(6).x);
        assertEquals(expectedPoints[6].y, pList.get(6).y);

        // {7, 8}
        assertEquals(expectedPoints[7].x, pList.get(7).x);
        assertEquals(expectedPoints[7].y, pList.get(7).y);

        // {8, 9}
        assertEquals(expectedPoints[8].x, pList.get(8).x);
        assertEquals(expectedPoints[8].y, pList.get(8).y);

        // {9, 10}
        assertEquals(expectedPoints[9].x, pList.get(9).x);
        assertEquals(expectedPoints[9].y, pList.get(9).y);
    }

    @Test
    public void testDrawSeries_supportsOrderedXYSeries() throws Exception {
        // 100x100 plot space:
        //RectF plotArea = new RectF(0, 0, 99, 99);
        FastLineAndPointRenderer.Formatter formatter =
                new FastLineAndPointRenderer.Formatter(Color.RED, Color.RED, null);

        // create a series composed of 3 "segments"; series portions separated by null values:
        SimpleXYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        LineAndPointRenderer renderer = Mockito.spy(new LineAndPointRenderer(xyPlot));

        xyPlot.addSeries(series, formatter);

        xyPlot.calculateMinMaxVals();
        renderer.drawSeries(canvas, plotArea, series, formatter);

        verify(renderer, times(1)).renderPoints(
                eq(canvas),
                eq(plotArea),
                eq(series),
                eq(0),
                eq(series.size()),
                any(List.class),
                eq(formatter));

        xyPlot.setDomainBoundaries(5, 6, BoundaryMode.FIXED);
        series.setXOrder(OrderedXYSeries.XOrder.ASCENDING);
        xyPlot.calculateMinMaxVals();
        renderer.drawSeries(canvas, plotArea, series, formatter);

        verify(renderer, times(1)).renderPoints(
                eq(canvas),
                eq(plotArea),
                eq(series),
                eq(4),
                eq(8),
                any(List.class),
                eq(formatter));

    }

    @Test
    public void testCullPointsCache() throws Exception {
        LineAndPointFormatter formatter =
                new LineAndPointFormatter(0, 0, 0, null);

        // create a series composed of 3 "segments"; series portions separated by null values:
        SimpleXYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        xyPlot.addSeries(series, formatter);
        LineAndPointRenderer renderer = xyPlot.getRenderer(LineAndPointRenderer.class);

        assertEquals(0, renderer.pointsCaches.size());

        // should generate a new pointCache:
        renderer.getPointsCache(series);
        assertEquals(1, renderer.pointsCaches.size());

        // culling should not delete it since it is
        // registered in the series registry:
        renderer.getPointsCache(series);
        assertEquals(1, renderer.pointsCaches.size());
        renderer.cullPointsCache();
        assertEquals(1, renderer.pointsCaches.size());

        // unregister the series.  this time, culling should remove the series
        // from the points cache:
        xyPlot.removeSeries(series);
        renderer.cullPointsCache();
        assertEquals(0, renderer.pointsCaches.size());
    }

    @Test
    public void renderPath_rendersRegions() {
        LineAndPointFormatter formatter =
                new LineAndPointFormatter(0, 0, 0, null);

        XYRegionFormatter r1 = new XYRegionFormatter(Color.RED);
        formatter.addRegion(new RectRegion(0, 2, 0, 2, "region1"), r1);

        XYRegionFormatter r2 = new XYRegionFormatter(Color.GREEN);
        formatter.addRegion(new RectRegion(0, 2, 0, 2, "region2"), r2);

        SimpleXYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        xyPlot.addSeries(series, formatter);
        LineAndPointRenderer renderer = xyPlot.getRenderer(LineAndPointRenderer.class);

        //xyPlot.draw(canvas);
        renderer.renderPath(canvas, plotArea, new Path(), mock(PointF.class), mock(PointF.class), formatter);
        verify(canvas).drawRect(any(RectF.class), eq(r1.getPaint()));
        verify(canvas).drawRect(any(RectF.class), eq(r2.getPaint()));
    }

    @Test
    public void renderPath_rendersRegionsWithUnboundedEdges() {
        LineAndPointFormatter formatter =
                new LineAndPointFormatter(0, 0, 0, null);

        // null edges represent infinity; this region covers everything below / left of 0.5:
        XYRegionFormatter open = new XYRegionFormatter(Color.RED);
        formatter.addRegion(new RectRegion(null, 0.5, null, 0.5, "open"), open);

        // everything above / right of 5, which is outside the plot's visible bounds of -1..1:
        XYRegionFormatter far = new XYRegionFormatter(Color.GREEN);
        formatter.addRegion(new RectRegion(5, null, 5, null, "far"), far);

        SimpleXYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        xyPlot.addSeries(series, formatter);
        LineAndPointRenderer renderer = xyPlot.getRenderer(LineAndPointRenderer.class);

        renderer.renderPath(canvas, plotArea, new Path(), mock(PointF.class), mock(PointF.class), formatter);

        ArgumentCaptor<RectF> rect = ArgumentCaptor.forClass(RectF.class);
        verify(canvas).drawRect(rect.capture(), eq(open.getPaint()));
        verify(canvas, never()).drawRect(any(RectF.class), eq(far.getPaint()));

        // the unbounded edges are clipped to the plot area:
        assertEquals(plotArea.left, rect.getValue().left);
        assertEquals(plotArea.bottom, rect.getValue().bottom);
    }

    @Test
    public void drawSeries_withPointLabelFormatter_drawsPointLabels() {
        LineAndPointFormatter formatter =
                new LineAndPointFormatter(0, 0, 0, null);
        formatter.setPointLabelFormatter(new PointLabelFormatter(Color.RED));
        SimpleXYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        xyPlot.addSeries(series, formatter);
        LineAndPointRenderer renderer = xyPlot.getRenderer(LineAndPointRenderer.class);
        renderer.drawSeries(canvas, plotArea, series, formatter);

        verify(canvas, times(series.size())).drawText(
                anyString(),
                anyFloat(),
                anyFloat(),
                eq(formatter.getPointLabelFormatter().getTextPaint()));

    }
}
