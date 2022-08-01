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

import static org.mockito.Mockito.*;

public class CandlestickRendererTest extends AndroidplotTest {

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
        CandlestickFormatter formatter = spy(new CandlestickFormatter());
        CandlestickRenderer renderer = spy((CandlestickRenderer) formatter.doGetRendererInstance(xyPlot));
        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).doGetRendererInstance(any(XYPlot.class));

        XYSeries openVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries closeVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries highVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries lowVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        CandlestickMaker.make(xyPlot, formatter, openVals, closeVals, highVals, lowVals);

        renderer.onRender(canvas, plotArea, openVals, formatter, renderStack);
    }
}
