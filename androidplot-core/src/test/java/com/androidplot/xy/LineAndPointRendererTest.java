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

import android.graphics.*;

import com.androidplot.test.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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
     * Sanity check to make sure that at the end of the day, points are being drawn at the expected
     * screen-coords.
     * @throws Exception
     */
    @Test
    public void testRenderPoints() throws Exception {

        // 100x100 plot space:
        RectF plotArea = new RectF(0, 0, 99, 99);
        XYPlot plot = new XYPlot(getContext(), "Test");

        FastLineAndPointRenderer.Formatter formatter =
                new FastLineAndPointRenderer.Formatter(Color.RED, Color.RED, null, null);

        // create a series composed of 3 "segments"; series portions separated by null values:
        XYSeries series = new SimpleXYSeries(
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "some data", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        LineAndPointRenderer renderer = Mockito.spy(new LineAndPointRenderer(plot));

        plot.addSeries(series, formatter);

        plot.calculateMinMaxVals();
        renderer.onRender(canvas, plotArea, series, formatter, null);

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
}
