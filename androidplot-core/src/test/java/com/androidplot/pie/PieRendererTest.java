// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import android.content.res.*;
import android.graphics.*;
import android.view.*;

import com.androidplot.*;
import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

public class PieRendererTest extends AndroidplotTest {

    RectF plotArea = new RectF(0, 0, 100, 100);

    @Mock
    LayoutManager layoutManager;

    PieChart pieChart;

    @Mock
    TypedArray typedArray;

    @Mock
    SeriesRegistry seriesRegistry;

    @Mock
    RenderStack renderStack;

    Canvas canvas;

    @Before
    public void setUp() throws Exception {
        pieChart = spy(new PieChart(getContext(), "My Pie"));
        canvas = spy(new Canvas());
    }

    @Test
    public void testDrawSegment_withoutTextPaintDoesntDrawLabel() throws Exception {
        PieRenderer pieRenderer = spy(new PieRenderer(pieChart));
        Segment segment = spy(new Segment("My Segment", 100));

        SegmentFormatter formatterWithoutTextPaint = new SegmentFormatter(Color.GREEN);
        formatterWithoutTextPaint.setLabelPaint(null);

        pieRenderer.drawSegment(
                new Canvas(),
                plotArea,
                segment,
                formatterWithoutTextPaint,
                100, 100, 100);

        SegmentFormatter formatterWithTextPaint = new SegmentFormatter(Color.GREEN);

        pieRenderer.drawSegment(
                canvas,
                plotArea,
                segment,
                formatterWithTextPaint,
                100, 100, 100);

        verify(pieRenderer, times(0))
                .drawSegmentLabel(
                        any(Canvas.class),
                        any(PointF.class),
                        any(Segment.class),
                        eq(formatterWithoutTextPaint));

        verify(pieRenderer, times(1))
                .drawSegmentLabel(
                        eq(canvas),
                        any(PointF.class),
                        eq(segment),
                        eq(formatterWithTextPaint));
    }

    @Test
    public void testOnRender() throws Exception {
        Segment segment = spy(new Segment("My Segment", 100));
        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer pr = formatter.getRendererInstance(pieChart);
        PieRenderer renderer = spy(pr);
        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(PieChart.class));
        pieChart.addSegment(segment, formatter);
        renderer.onRender(canvas, plotArea, segment, formatter, renderStack);
    }

    @Test
    public void getContainingSegment_returnsCorrectSegment() throws Exception {
        Segment segment1 = spy(new Segment("s1", 25));
        Segment segment2 = spy(new Segment("s2", 25));
        Segment segment3 = spy(new Segment("s3", 25));
        Segment segment4 = spy(new Segment("s4", 25));
        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer renderer = formatter.getRendererInstance(pieChart);

        pieChart.addSegment(segment1, formatter);
        pieChart.addSegment(segment2, formatter);
        pieChart.addSegment(segment3, formatter);
        pieChart.addSegment(segment4, formatter);

        // southeast
        assertEquals(segment1, renderer.getContainingSegment(new PointF(100, 100)));

        // southwest
        assertEquals(segment2, renderer.getContainingSegment(new PointF(0, 100)));

        // northwest
        assertEquals(segment3, renderer.getContainingSegment(new PointF(0, 0)));

        // northeast
        assertEquals(segment4, renderer.getContainingSegment(new PointF(100, 0)));

        renderer.setStartDegs(90);
        // southeast
        assertEquals(segment2, renderer.getContainingSegment(new PointF(100, 100)));

        // southwest
        assertEquals(segment3, renderer.getContainingSegment(new PointF(0, 100)));

        // northwest
        assertEquals(segment4, renderer.getContainingSegment(new PointF(0, 0)));

        // northeast
        assertEquals(segment1, renderer.getContainingSegment(new PointF(100, 0)));
    }

    @Test
    public void getContainingSegment_handlesSegmentsLargerThanHalfPie() throws Exception {
        Segment segment1 = spy(new Segment("s1", 25));
        Segment segment2 = spy(new Segment("s2", 24));
        Segment segment3 = spy(new Segment("s3", 51));
        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer renderer = formatter.getRendererInstance(pieChart);

        pieChart.addSegment(segment1, formatter);
        pieChart.addSegment(segment2, formatter);
        pieChart.addSegment(segment3, formatter);

        // southeast
        assertEquals(segment1, renderer.getContainingSegment(new PointF(100, 100)));

        // southwest
        assertEquals(segment2, renderer.getContainingSegment(new PointF(0, 100)));

        // northwest
        assertEquals(segment3, renderer.getContainingSegment(new PointF(0, 0)));

        // northeast
        assertEquals(segment3, renderer.getContainingSegment(new PointF(100, 0)));

        renderer.setStartDegs(90);
        // southeast
        assertEquals(segment2, renderer.getContainingSegment(new PointF(100, 100)));

        // southwest
        assertEquals(segment3, renderer.getContainingSegment(new PointF(0, 100)));

        // northwest
        assertEquals(segment3, renderer.getContainingSegment(new PointF(0, 0)));

        // northeast
        assertEquals(segment1, renderer.getContainingSegment(new PointF(100, 0)));
    }

    /**
     * Regression test for https://github.com/halfhp/androidplot/issues/118: with values 8, 1, 1
     * the first segment sweeps 288 degrees clockwise from east; the region just past its start
     * (first quadrant) and the region just before its end must both hit it.
     */
    @Test
    public void getContainingSegment_hitsWholeOfSegmentLargerThanHalfPie() throws Exception {
        Segment big = spy(new Segment("big", 8));
        Segment small1 = spy(new Segment("small1", 1));
        Segment small2 = spy(new Segment("small2", 1));
        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer renderer = formatter.getRendererInstance(pieChart);

        pieChart.addSegment(big, formatter);
        pieChart.addSegment(small1, formatter);
        pieChart.addSegment(small2, formatter);

        // screen angle 0 is east, increasing clockwise (south is 90).
        // big spans 0..288, small1 288..324, small2 324..360.
        RectF area = pieChart.getPie().getWidgetDimensions().marginatedRect;
        PointF origin = new PointF(area.centerX(), area.centerY());
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 1)));
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 45)));
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 90)));
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 180)));
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 270)));
        assertEquals(big, renderer.getContainingSegment(atScreenDegs(origin, 287)));
        assertEquals(small1, renderer.getContainingSegment(atScreenDegs(origin, 289)));
        assertEquals(small1, renderer.getContainingSegment(atScreenDegs(origin, 323)));
        assertEquals(small2, renderer.getContainingSegment(atScreenDegs(origin, 325)));
        assertEquals(small2, renderer.getContainingSegment(atScreenDegs(origin, 359)));
    }

    /** A point 40px from origin at the given screen angle (clockwise from east). */
    private static PointF atScreenDegs(PointF origin, double degs) {
        double rad = Math.toRadians(degs);
        return new PointF((float) (origin.x + 40 * Math.cos(rad)),
                (float) (origin.y + 40 * Math.sin(rad)));
    }

    @Test
    public void testDegsToScreenDegs() throws Exception {
        assertEquals(0f, PieRenderer.degsToScreenDegs(0));
        assertEquals(359f, PieRenderer.degsToScreenDegs(1));
        assertEquals(271f, PieRenderer.degsToScreenDegs(89));
        assertEquals(270f, PieRenderer.degsToScreenDegs(90));
        assertEquals(269f, PieRenderer.degsToScreenDegs(91));
        assertEquals(181f, PieRenderer.degsToScreenDegs(179));
        assertEquals(180f, PieRenderer.degsToScreenDegs(180));
        assertEquals(179f, PieRenderer.degsToScreenDegs(181));
        assertEquals(91f, PieRenderer.degsToScreenDegs(269));
        assertEquals(90f, PieRenderer.degsToScreenDegs(270));
        assertEquals(89f, PieRenderer.degsToScreenDegs(271));
        assertEquals(1f, PieRenderer.degsToScreenDegs(359));
        assertEquals(0f, PieRenderer.degsToScreenDegs(360));

    }

    @Test
    public void getDonutSizePx_pixelsMode_zeroMeansNoHole() throws Exception {
        PieRenderer renderer = new PieRenderer(pieChart);

        renderer.setDonutSize(0, PieRenderer.DonutMode.PIXELS);
        assertEquals(0f, renderer.getDonutSizePx(100));

        renderer.setDonutSize(10, PieRenderer.DonutMode.PIXELS);
        assertEquals(10f, renderer.getDonutSizePx(100));

        // negative values are an inset from the outer radius:
        renderer.setDonutSize(-10, PieRenderer.DonutMode.PIXELS);
        assertEquals(90f, renderer.getDonutSizePx(100));

        renderer.setDonutSize(0.25f, PieRenderer.DonutMode.PERCENT);
        assertEquals(25f, renderer.getDonutSizePx(100));
    }

    @Test
    public void drawSegment_zeroPixelDonut_drawsFullWedge() throws Exception {
        PieRenderer renderer = new PieRenderer(pieChart);
        renderer.setDonutSize(0, PieRenderer.DonutMode.PIXELS);
        SegmentFormatter formatter = new SegmentFormatter(Color.GREEN);

        renderer.drawSegment(canvas, plotArea, new Segment("s1", 10), formatter, 50, 0, 90);

        // the inner edge of the wedge should be at the center of the pie, not at its outer radius:
        verify(canvas).drawCircle(anyFloat(), anyFloat(), eq(0f), eq(formatter.getInnerEdgePaint()));
        verify(canvas).drawCircle(anyFloat(), anyFloat(), eq(50f), eq(formatter.getOuterEdgePaint()));
    }

    @Test
    public void onRender_doesNotLabelZeroValueSegments() throws Exception {
        Segment s1 = new Segment("s1", 8);
        Segment s2 = new Segment("s2", 0);
        Segment s3 = new Segment("s3", 2);
        SegmentFormatter formatter = new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN);
        PieRenderer renderer = spy(formatter.getRendererInstance(pieChart));

        pieChart.addSegment(s1, formatter);
        pieChart.addSegment(s2, formatter);
        pieChart.addSegment(s3, formatter);

        renderer.onRender(canvas, plotArea, s1, formatter, renderStack);

        verify(renderer).drawSegmentLabel(eq(canvas), any(PointF.class), eq(s1), eq(formatter));
        verify(renderer, never()).drawSegmentLabel(eq(canvas), any(PointF.class), eq(s2), eq(formatter));
        verify(renderer).drawSegmentLabel(eq(canvas), any(PointF.class), eq(s3), eq(formatter));
        verify(canvas, times(2)).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void testSetDonutSize() throws Exception {

        Segment segment1 = spy(new Segment("s1", 25));
        Segment segment2 = spy(new Segment("s2", 25));
        Segment segment3 = spy(new Segment("s3", 25));
        Segment segment4 = spy(new Segment("s4", 25));

        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer renderer = formatter.getRendererInstance(pieChart);

        pieChart.addSegment(segment1, formatter);
        pieChart.addSegment(segment2, formatter);
        pieChart.addSegment(segment3, formatter);
        pieChart.addSegment(segment4, formatter);

        renderer.setDonutSize(0.25f, PieRenderer.DonutMode.PERCENT);

        renderer.onRender(canvas, plotArea, segment1, formatter, renderStack);

        // TODO: verify radials are drown at the correct offsets from center
        //verify(canvas).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }
}
