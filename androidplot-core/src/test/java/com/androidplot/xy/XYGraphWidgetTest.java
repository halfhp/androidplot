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

import android.content.res.*;
import android.graphics.*;

import com.androidplot.*;
import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XYGraphWidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    XYPlot xyPlot;

    @Mock
    TypedArray typedArray;

    @Mock
    SeriesRegistry seriesRegistry;

    RectRegion bounds = new RectRegion(0, 100, 0, 100);

    @Mock
    Canvas canvas;


    @Before
    public void setUp() throws Exception {
        xyPlot = spy(new XYPlot(getContext(), "XYPlot"));
        when(xyPlot.getSeriesRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getBounds()).thenReturn(bounds);
        when(xyPlot.getDomainOrigin()).thenReturn(0);
        when(xyPlot.getRangeOrigin()).thenReturn(0);

        xyPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        xyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcessAttrs() throws Exception {
        Size size = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));

        graphWidget.processAttrs(typedArray);
        verify(graphWidget, times(1)).setGridClippingEnabled(false);
    }

    @Test
    public void testDrawMarkers() throws Exception {
        Size size = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));
        graphWidget.setGridRect(new RectF(0, 0, 100, 100));

        xyPlot.addMarker(new XValueMarker(1, "x"));
        xyPlot.addMarker(new YValueMarker(-1, "y"));

        graphWidget.drawMarkers(canvas);

        // TODO: verifications
        verify(canvas, times(2))
                .drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void testDrawGrid() throws Exception {
        Size size = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
        XYGraphWidget graphWidget = spy(new XYGraphWidget(layoutManager, xyPlot, size));
        graphWidget.setLabelRect(new RectF(0, 0, 100, 100));
        doNothing().when(graphWidget).
                drawDomainLine(any(Canvas.class), anyFloat(), any(Number.class), any(Paint.class), anyBoolean());

        doNothing().when(graphWidget).
                drawRangeLine(any(Canvas.class), anyFloat(), any(Number.class), any(Paint.class), anyBoolean());

        graphWidget.setGridRect(new RectF(0, 0, 100, 100));
        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);

        graphWidget.drawGrid(canvas);

        // expecting a 100x100 grid to be drawn:
        verify(graphWidget, times(100))
                .drawDomainLine(eq(canvas), anyFloat(), anyFloat(), any(Paint.class), eq(false));

        verify(graphWidget, times(100))
                .drawRangeLine(eq(canvas), anyFloat(), anyFloat(), any(Paint.class), eq(false));

    }
}
