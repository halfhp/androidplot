// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Insets;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.xy.XYGraphWidget.Edge;
import com.androidplot.xy.XYGraphWidget.LineLabelRenderer;
import com.androidplot.xy.XYGraphWidget.LineLabelStyle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Accessors and the label / cursor / sub-grid drawing branches of {@link XYGraphWidget}.
 */
public class XYGraphWidgetAccessorsTest extends AndroidplotTest {

    private static final float DELTA = 0.0001f;

    @Mock
    LayoutManager layoutManager;

    @Mock
    Canvas canvas;

    XYPlot xyPlot;
    XYGraphWidget widget;

    @Before
    public void setUp() {
        xyPlot = new XYPlot(getContext(), "XYPlot");
        xyPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        xyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
        xyPlot.addSeries(new SimpleXYSeries(Arrays.asList(0, 5, 10),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s"), new LineAndPointFormatter());
        xyPlot.calculateMinMaxVals(); // bounds: x 0..2, y 0..10
        xyPlot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        xyPlot.calculateMinMaxVals(); // bounds: x 0..10, y 0..10

        widget = new XYGraphWidget(layoutManager, xyPlot,
                new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE));
        widget.setGridRect(new RectF(0, 0, 100, 100));
        widget.setLabelRect(new RectF(0, 0, 100, 100));
    }

    // ---- paints ----

    @Test
    public void paintSetters_roundTrip() {
        Paint p1 = new Paint();
        widget.setGridBackgroundPaint(p1);
        assertSame(p1, widget.getGridBackgroundPaint());

        Paint p2 = new Paint();
        widget.setDomainGridLinePaint(p2);
        assertSame(p2, widget.getDomainGridLinePaint());

        Paint p3 = new Paint();
        widget.setRangeGridLinePaint(p3);
        assertSame(p3, widget.getRangeGridLinePaint());

        Paint p4 = new Paint();
        widget.setDomainSubGridLinePaint(p4);
        assertSame(p4, widget.getDomainSubGridLinePaint());

        Paint p5 = new Paint();
        widget.setRangeSubGridLinePaint(p5);
        assertSame(p5, widget.getRangeSubGridLinePaint());

        Paint p6 = new Paint();
        widget.setDomainOriginLinePaint(p6);
        assertSame(p6, widget.getDomainOriginLinePaint());

        Paint p7 = new Paint();
        widget.setRangeOriginLinePaint(p7);
        assertSame(p7, widget.getRangeOriginLinePaint());

        Paint p8 = new Paint();
        widget.setDomainCursorPaint(p8);
        assertSame(p8, widget.getDomainCursorPaint());

        Paint p9 = new Paint();
        widget.setRangeCursorPaint(p9);
        assertSame(p9, widget.getRangeCursorPaint());
    }

    // ---- simple properties ----

    @Test
    public void linesPerLabel_roundTrip() {
        widget.setLinesPerRangeLabel(3);
        assertEquals(3, widget.getLinesPerRangeLabel());
        widget.setLinesPerDomainLabel(4);
        assertEquals(4, widget.getLinesPerDomainLabel());
    }

    @Test
    public void drawMarkersEnabled_roundTrip() {
        widget.setDrawMarkersEnabled(false);
        assertFalse(widget.isDrawMarkersEnabled());
        widget.setDrawMarkersEnabled(true);
        assertTrue(widget.isDrawMarkersEnabled());
    }

    @Test
    public void lineExtensions_roundTrip() {
        widget.setLineExtensionTop(1.5f);
        widget.setLineExtensionBottom(2.5f);
        widget.setLineExtensionLeft(3.5f);
        widget.setLineExtensionRight(4.5f);
        assertEquals(1.5f, widget.getLineExtensionTop(), DELTA);
        assertEquals(2.5f, widget.getLineExtensionBottom(), DELTA);
        assertEquals(3.5f, widget.getLineExtensionLeft(), DELTA);
        assertEquals(4.5f, widget.getLineExtensionRight(), DELTA);
    }

    @Test
    public void labelRect_roundTrip() {
        RectF rect = new RectF(1, 2, 3, 4);
        widget.setLabelRect(rect);
        assertSame(rect, widget.getLabelRect());
    }

    @Test
    public void setLineLabelInsets_storesInsetsAndRecalculates() {
        Insets insets = new Insets(1, 2, 3, 4);
        widget.setLineLabelInsets(insets);
        assertSame(insets, widget.getLineLabelInsets());
    }

    @Test
    public void setLineLabelEdges_collection_replacesEdges() {
        widget.setLineLabelEdges(Edge.TOP);
        widget.setLineLabelEdges(EnumSet.of(Edge.LEFT, Edge.RIGHT));
        assertTrue(widget.isLineLabelEnabled(Edge.LEFT));
        assertTrue(widget.isLineLabelEnabled(Edge.RIGHT));
        assertFalse(widget.isLineLabelEnabled(Edge.TOP));
        assertFalse(widget.isLineLabelEnabled(Edge.BOTTOM));
    }

    @Test
    public void containsPoint_usesGridRect() {
        assertTrue(widget.containsPoint(50, 50));
        assertFalse(widget.containsPoint(101, 50));
        widget.setGridRect(null);
        assertFalse(widget.containsPoint(50, 50));
    }

    // ---- cursor ----

    @Test
    public void setCursorPosition_floats_setsBothCursors() {
        widget.setCursorPosition(10f, 20f);
        assertEquals(10f, widget.getDomainCursorPosition(), DELTA);
        assertEquals(20f, widget.getRangeCursorPosition(), DELTA);
    }

    @Test
    public void setCursorPosition_point_setsBothCursors() {
        widget.setCursorPosition(new PointF(30f, 40f));
        assertEquals(30f, widget.getDomainCursorPosition(), DELTA);
        assertEquals(40f, widget.getRangeCursorPosition(), DELTA);
    }

    @Test
    public void cursorVals_reflectCursorPosition() {
        widget.setCursorPosition(50f, 0f);
        assertEquals(5.0, widget.getDomainCursorVal().doubleValue(), DELTA);
        assertEquals(10.0, widget.getRangeCursorVal().doubleValue(), DELTA);
    }

    @Test
    public void setCursorLabelFormatter_roundTrip() {
        XYGraphWidget.CursorLabelFormatter clf = mock(XYGraphWidget.CursorLabelFormatter.class);
        assertNull(widget.getCursorLabelFormatter());
        widget.setCursorLabelFormatter(clf);
        assertSame(clf, widget.getCursorLabelFormatter());
    }

    private XYGraphWidget.CursorLabelFormatter cursorLabelFormatter(Paint backgroundPaint) {
        XYGraphWidget.CursorLabelFormatter clf = mock(XYGraphWidget.CursorLabelFormatter.class);
        when(clf.getTextPaint()).thenReturn(new Paint());
        when(clf.getBackgroundPaint()).thenReturn(backgroundPaint);
        when(clf.getLabelText(any(Number.class), any(Number.class))).thenReturn("label");
        return clf;
    }

    @Test
    public void drawCursors_withFormatterAndBothCursors_drawsLabel() {
        widget.setCursorLabelFormatter(cursorLabelFormatter(null));
        widget.setCursorPosition(50f, 50f);

        widget.drawCursors(canvas);

        verify(canvas, times(2)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas).drawText(eq("label"), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void drawCursors_withoutFormatter_drawsNoLabel() {
        widget.setCursorPosition(50f, 50f);
        widget.drawCursors(canvas);
        verify(canvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void drawCursorLabel_withBackgroundPaint_drawsBackground() {
        Paint background = new Paint();
        widget.setCursorLabelFormatter(cursorLabelFormatter(background));
        widget.setCursorPosition(50f, 50f);

        widget.drawCursorLabel(canvas);

        verify(canvas).drawRect(any(RectF.class), eq(background));
        verify(canvas).drawText(eq("label"), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void drawCursorLabel_atRightEdge_drawsLabelLeftOfCursor() {
        widget.setCursorLabelFormatter(cursorLabelFormatter(null));
        // domain cursor on the right edge of the grid so the label must flip to the left:
        widget.setCursorPosition(widget.getGridRect().right, 50f);

        widget.drawCursorLabel(canvas);

        verify(canvas).drawText(eq("label"), anyFloat(), anyFloat(), any(Paint.class));
    }

    // ---- line labels ----

    @Test
    public void lineLabelStyle_accessorsRoundTrip() {
        LineLabelStyle style = new LineLabelStyle();
        Format format = new DecimalFormat("0.00");
        style.setFormat(format);
        assertSame(format, style.getFormat());

        Paint paint = new Paint();
        style.setPaint(paint);
        assertSame(paint, style.getPaint());

        style.setRotation(45f);
        assertEquals(45f, style.getRotation(), DELTA);
    }

    @Test
    public void setLineLabelStyle_roundTrip() {
        LineLabelStyle style = new LineLabelStyle();
        widget.setLineLabelStyle(Edge.RIGHT, style);
        assertSame(style, widget.getLineLabelStyle(Edge.RIGHT));
    }

    @Test
    public void setLineLabelRenderer_roundTrip() {
        LineLabelRenderer renderer = new LineLabelRenderer();
        widget.setLineLabelRenderer(Edge.TOP, renderer);
        assertSame(renderer, widget.getLineLabelRenderer(Edge.TOP));
    }

    @Test
    public void lineLabelRenderer_drawLabel_formatsRotatesAndDrawsText() {
        LineLabelStyle style = new LineLabelStyle();
        style.setRotation(90f);
        style.setFormat(new DecimalFormat("0.00"));
        when(canvas.save()).thenReturn(7);

        new LineLabelRenderer().drawLabel(canvas, style, 2.5, 10f, 20f, false);

        verify(canvas).save();
        verify(canvas).rotate(90f, 10f, 20f);
        verify(canvas).drawText("2.50", 10f, 20f, style.getPaint());
        verify(canvas).restoreToCount(7);
    }

    @Test
    public void drawDomainLine_withTopAndBottomLabels_drawsBothLabels() {
        widget.setLineLabelEdges(Edge.TOP, Edge.BOTTOM);
        Paint linePaint = new Paint();

        widget.drawDomainLine(canvas, 10f, 5, linePaint, false, true);

        verify(canvas).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(linePaint));
        // label at the top edge of the label rect and at the bottom edge:
        verify(canvas).drawText(eq("5.0"), eq(10f), eq(0f), any(Paint.class));
        verify(canvas).drawText(eq("5.0"), eq(10f), eq(100f), any(Paint.class));
    }

    @Test
    public void drawDomainLine_labelsSuppressed_drawsNoLabels() {
        widget.setLineLabelEdges(Edge.TOP, Edge.BOTTOM);
        widget.drawDomainLine(canvas, 10f, 5, new Paint(), false, false);
        verify(canvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void drawRangeLine_withLeftAndRightLabels_drawsBothLabels() {
        widget.setLineLabelEdges(Edge.LEFT, Edge.RIGHT);
        Paint linePaint = new Paint();

        widget.drawRangeLine(canvas, 20f, 7, linePaint, false, true);

        verify(canvas).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(linePaint));
        verify(canvas).drawText(eq("7.0"), eq(0f), eq(20f), any(Paint.class));
        verify(canvas).drawText(eq("7.0"), eq(100f), eq(20f), any(Paint.class));
    }

    @Test
    public void drawRangeLine_nullPaint_drawsNoLine() {
        widget.setLineLabelEdges(Edge.NONE);
        widget.drawRangeLine(canvas, 20f, 7, null, false, true);
        verify(canvas, never()).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void drawLineLabel_usesRendererAndStyleForEdge() {
        LineLabelRenderer renderer = mock(LineLabelRenderer.class);
        LineLabelStyle style = new LineLabelStyle();
        widget.setLineLabelRenderer(Edge.LEFT, renderer);
        widget.setLineLabelStyle(Edge.LEFT, style);

        widget.drawLineLabel(canvas, Edge.LEFT, 3, 4f, 5f, true);

        verify(renderer).drawLabel(canvas, style, 3, 4f, 5f, true);
    }

    // ---- grid ----

    @Test
    public void drawGrid_withMultipleLinesPerLabel_usesSubGridPaints() {
        Paint domainSub = new Paint();
        Paint rangeSub = new Paint();
        widget.setDomainSubGridLinePaint(domainSub);
        widget.setRangeSubGridLinePaint(rangeSub);
        widget.setLinesPerDomainLabel(2);
        widget.setLinesPerRangeLabel(2);

        widget.drawGrid(canvas);

        // on each axis the origin (0) gets the origin paint, ticks 2, 4, 6, 8, 10 the grid paint
        // and ticks 1, 3, 5, 7, 9 the sub-grid paint:
        verify(canvas, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(),
                eq(widget.getDomainOriginLinePaint()));
        verify(canvas, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(),
                eq(widget.getRangeOriginLinePaint()));
        verify(canvas, times(5)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(),
                eq(widget.getDomainGridLinePaint()));
        verify(canvas, times(5)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(),
                eq(widget.getRangeGridLinePaint()));
        verify(canvas, times(5)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(domainSub));
        verify(canvas, times(5)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(rangeSub));
    }

    @Test
    public void drawData_gridOnTop_drawsGridBackgroundFirst() {
        Paint background = new Paint();
        widget.setGridBackgroundPaint(background);
        widget.setDrawGridOnTop(true);

        widget.drawData(canvas);

        verify(canvas).drawRect(widget.getGridRect(), background);
    }

    @Test
    public void drawData_gridNotOnTop_drawsNoGridBackground() {
        widget.setGridBackgroundPaint(new Paint());
        widget.setDrawGridOnTop(false);

        widget.drawData(canvas);

        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void drawPoint_drawsPoint() {
        Paint paint = new Paint();
        widget.drawPoint(canvas, new PointF(3f, 4f), paint);
        verify(canvas).drawPoint(3f, 4f, paint);
    }
}
