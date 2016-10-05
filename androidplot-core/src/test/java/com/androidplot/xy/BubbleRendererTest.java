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
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static org.mockito.Matchers.any;
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
}
