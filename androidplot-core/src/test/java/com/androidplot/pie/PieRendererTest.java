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
