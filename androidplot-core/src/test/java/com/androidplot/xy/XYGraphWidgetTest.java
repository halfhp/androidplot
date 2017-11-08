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

import android.content.res.*;
import android.graphics.*;

import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XYGraphWidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    XYPlot xyPlot;

    @Mock
    TypedArray typedArray;

    @Mock
    XYSeriesRegistry seriesRegistry;

    RectRegion bounds = new RectRegion(0, 100, 0, 100);

    @Mock
    Canvas canvas;

    // this is a spy
    Size size;

    // this is a spy
    XYGraphWidget graphWidget;


    @Before
    public void setUp() throws Exception {
        size = spy(new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE));
        xyPlot = spy(new XYPlot(getContext(), "XYPlot"));
        when(xyPlot.getRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getBounds()).thenReturn(bounds);
        when(xyPlot.getDomainOrigin()).thenReturn(0);
        when(xyPlot.getRangeOrigin()).thenReturn(0);

        xyPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        xyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

        graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));
        graphWidget.setGridRect(new RectF(0, 0, 10, 100));
        graphWidget.setLabelRect(new RectF(0, 0, 100, 100));
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcessAttrs() throws Exception {
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));

        graphWidget.processAttrs(typedArray);
        verify(graphWidget, times(1)).setGridClippingEnabled(false);
    }

    @Test
    public void testDoOnDraw_drawGridOnTopFalse() throws Exception {
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));
        graphWidget.setDrawGridOnTop(false);
        doNothing().when(graphWidget).drawGrid(canvas);
        doNothing().when(graphWidget).drawData(canvas);

        graphWidget.doOnDraw(canvas, new RectF(0, 0, 100, 100));

        InOrder io = inOrder(graphWidget);
        io.verify(graphWidget).drawGrid(canvas);
        io.verify(graphWidget).drawData(canvas);
    }

    @Test
    public void testDoOnDraw_drawGridOnTopTrue() throws Exception {
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));
        graphWidget.setDrawGridOnTop(true);
        doNothing().when(graphWidget).drawGrid(canvas);
        doNothing().when(graphWidget).drawData(canvas);

        graphWidget.doOnDraw(canvas, new RectF(0, 0, 100, 100));

        InOrder io = inOrder(graphWidget);
        io.verify(graphWidget).drawData(canvas);
        io.verify(graphWidget).drawGrid(canvas);
    }

    @Test
    public void testDrawMarkers() throws Exception {
        xyPlot.addMarker(new XValueMarker(1, "x"));
        xyPlot.addMarker(new YValueMarker(-1, "y"));

        graphWidget.drawMarkers(canvas);

        // TODO: verifications
        verify(canvas, times(2))
                .drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }

    protected void runDrawGridTest() {
        doNothing().when(graphWidget).
                drawDomainLine(any(Canvas.class), anyFloat(), any(Number.class), any(Paint.class), anyBoolean());

        doNothing().when(graphWidget).
                drawRangeLine(any(Canvas.class), anyFloat(), any(Number.class), any(Paint.class), anyBoolean());

        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);

        graphWidget.drawGrid(canvas);

        // expecting a 100x100 grid to be drawn:
        verify(graphWidget, times(100))
                .drawDomainLine(eq(canvas), anyFloat(), anyFloat(), any(Paint.class), eq(false));

        verify(graphWidget, times(100))
                .drawRangeLine(eq(canvas), anyFloat(), anyFloat(), any(Paint.class), eq(false));
    }

    @Test
    public void testDrawGrid_nullOrigin() throws Exception {
        when(xyPlot.getDomainOrigin()).thenReturn(null);
        when(xyPlot.getRangeOrigin()).thenReturn(null);
        runDrawGridTest();
    }

    @Test
    public void testDrawGrid_zeroOrigin() throws Exception {
        when(xyPlot.getDomainOrigin()).thenReturn(0);
        when(xyPlot.getRangeOrigin()).thenReturn(0);
        runDrawGridTest();
    }

    @Test
    public void testDrawGrid_centeredOrigin() throws Exception {

        // set origin to midpoint so we exercise
        // code to draw lines on both sides of the origin:
        when(xyPlot.getDomainOrigin()).thenReturn(50);
        when(xyPlot.getRangeOrigin()).thenReturn(50);
        runDrawGridTest();
    }

    @Test
    public void testDrawCursors_ifCursorPaintAndPositionAreSet() throws Exception {
        final Paint domainCursorPaint = new Paint();
        graphWidget.setDomainCursorPaint(domainCursorPaint);

        final Paint rangeCursorPaint = new Paint();
        graphWidget.setRangeCursorPaint(rangeCursorPaint);
        graphWidget.setRangeCursorPosition(0f);
        graphWidget.setDomainCursorPosition(0f);
        graphWidget.drawCursors(canvas);

        // expect one line to be drawn for each cursor:
        verify(canvas, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(domainCursorPaint));
        verify(canvas, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(rangeCursorPaint));
    }

    @Test
    public void testDrawCursors_ifCursorPaintAndPositionAreNotSet() throws Exception {
        graphWidget.setDomainCursorPaint(null);
        graphWidget.setRangeCursorPaint(null);

        graphWidget.drawCursors(canvas);

        // no lines should be drawn onto the canvas:
        verify(canvas, times(0)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void testDrawCursorLabel() throws Exception {
        graphWidget.setDomainCursorPosition(0f);
        graphWidget.setRangeCursorPosition(0f);
        XYGraphWidget.CursorLabelFormatter clf = mock(XYGraphWidget.CursorLabelFormatter.class);
        when(clf.getTextPaint()).thenReturn(new Paint());
        when(clf.getLabelText(any(Number.class), any(Number.class))).thenReturn("bla");
        when(graphWidget.getCursorLabelFormatter()).thenReturn(clf);
        graphWidget.drawCursorLabel(canvas);
        verify(canvas, times(1)).drawText(eq("bla"), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void testSetLineLabelEdges() throws Exception {

        graphWidget.setLineLabelEdges(XYGraphWidget.Edge.LEFT, XYGraphWidget.Edge.BOTTOM);
        assertTrue(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.LEFT));
        assertTrue(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.BOTTOM));

        graphWidget.setLineLabelEdges(XYGraphWidget.Edge.NONE);
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.TOP));
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.BOTTOM));
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.LEFT));
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.RIGHT));
    }

    @Test
    public void testSetLineLabelEdges_bitfield() throws Exception {

        graphWidget.setLineLabelEdges(
                XYGraphWidget.Edge.TOP.getValue() | XYGraphWidget.Edge.RIGHT.getValue());

        assertTrue(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.TOP));
        assertTrue(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.RIGHT));
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.BOTTOM));
        assertFalse(graphWidget.isLineLabelEnabled(XYGraphWidget.Edge.LEFT));
    }

    @Test
    public void testScreenToSeries() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        XYCoords coords = graphWidget.screenToSeries(new PointF(0, 0));
        assertEquals(-100, coords.x.intValue());
        assertEquals(100, coords.y.intValue());

        coords = graphWidget.screenToSeries(new PointF(10, 100));
        assertEquals(100, coords.x.intValue());
        assertEquals(-100, coords.y.intValue());

        coords = graphWidget.screenToSeries(new PointF(5, 50));
        assertEquals(0, coords.x.intValue());
        assertEquals(0, coords.y.intValue());
    }

    @Test
    public void testSeriesToScreen() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        PointF point = graphWidget.seriesToScreen(new XYCoords(-100, 100));
        assertEquals(0f, point.x);
        assertEquals(0f, point.y);

        point = graphWidget.seriesToScreen(new XYCoords(100, -100));
        assertEquals(10f, point.x);
        assertEquals(100f, point.y);

        point = graphWidget.seriesToScreen(new XYCoords(0, 0));
        assertEquals(5f, point.x);
        assertEquals(50f, point.y);
    }

    @Test
    public void testScreenToSeriesX() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        assertEquals(-100, graphWidget.screenToSeriesX(new PointF(0, 0)).intValue());
        assertEquals(100, graphWidget.screenToSeriesX(new PointF(10, 100)).intValue());
        assertEquals(0, graphWidget.screenToSeriesX(new PointF(5, 50)).intValue());
    }

    @Test
    public void testScreenToSeriesY() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        assertEquals(100, graphWidget.screenToSeriesY(new PointF(0, 0)).intValue());
        assertEquals(-100, graphWidget.screenToSeriesY(new PointF(100, 100)).intValue());
        assertEquals(0, graphWidget.screenToSeriesY(new PointF(50, 50)).intValue());
    }

    @Test
    public void testSeriesToScreenX() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        assertEquals(0f, graphWidget.seriesToScreenX(-100));
        assertEquals(10f, graphWidget.seriesToScreenX(100));
        assertEquals(5f, graphWidget.seriesToScreenX(0));
    }

    @Test
    public void testSeriesToScreenY() throws Exception {
        when(xyPlot.getBounds()).thenReturn(new RectRegion(-100, 100, -100, 100));

        assertEquals(100f, graphWidget.seriesToScreenY(100));
        assertEquals(0f, graphWidget.seriesToScreenY(-100));
        assertEquals(50f, graphWidget.seriesToScreenY(0));
    }
}
