// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

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
