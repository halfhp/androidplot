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

    @Before
    public void setUp() throws Exception {
        pieChart = spy(new PieChart(getContext(), "My Pie"));
    }

    @Test
    public void testDrawSegment_withoutTextPaintDoesntDrawLabel() throws Exception {
        PieRenderer pieRenderer = spy(new PieRenderer(pieChart));
        Segment segment = spy(new Segment("My Segment", 100));
        Canvas canvas = new Canvas();


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
        Canvas canvas = new Canvas();
        SegmentFormatter formatter = spy(
                new SegmentFormatter(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
        PieRenderer pr = formatter.getRendererInstance(pieChart);
        PieRenderer renderer = spy(pr);
        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(PieChart.class));
        pieChart.addSegment(segment, formatter);
        renderer.onRender(canvas, plotArea, segment, formatter, renderStack);
    }
}
