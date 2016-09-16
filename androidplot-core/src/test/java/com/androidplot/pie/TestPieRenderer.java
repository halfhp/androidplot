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

public class TestPieRenderer extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    PieChart pieChart;

    @Mock
    TypedArray typedArray;

    @Mock
    SeriesRegistry seriesRegistry;

    @Before
    public void setUp() throws Exception {
        //when(xyPlot.getSeriesRegistry()).thenReturn(seriesRegistry);
    }

    @After
    public void tearDown() throws Exception {

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
                new RectF(0, 0, 100, 100),
                segment,
                formatterWithoutTextPaint,
                100, 100, 100);

        SegmentFormatter formatterWithTextPaint = new SegmentFormatter(Color.GREEN);

        pieRenderer.drawSegment(
                canvas,
                new RectF(0, 0, 100, 100),
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
}
