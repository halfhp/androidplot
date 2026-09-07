// SPDX-License-Identifier: Apache-2.0

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
