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
 * Tests {@link AdvancedLineAndPointRenderer} and some of
 * {@link com.androidplot.xy.AdvancedLineAndPointRenderer.Formatter}.
 */
public class AdvanceLineAndPointRendererTest extends AndroidplotTest {

    XYPlot xyPlot;

    Canvas canvas;

    RectF plotArea = new RectF(0, 0, 100, 100);

    @Mock
    RenderStack renderStack;

    @Before
    public void setUp() throws Exception {
        canvas = new Canvas();
        xyPlot = spy(new XYPlot(getContext(), "My Plot"));
    }

    @Test
    public void testOnRender() throws Exception {
        AdvancedLineAndPointRenderer.Formatter formatter = spy(new AdvancedLineAndPointRenderer.Formatter());
        AdvancedLineAndPointRenderer renderer = formatter.getRendererInstance(xyPlot);

        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).getRendererInstance(any(XYPlot.class));

        XYSeries s = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1", 1, 2, 3, 4);

        xyPlot.addSeries(s, formatter);

        renderer.onRender(canvas, plotArea, s, formatter, renderStack);
    }
}
