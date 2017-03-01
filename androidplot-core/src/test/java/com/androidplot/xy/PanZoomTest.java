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
import android.view.*;

import com.androidplot.Region;
import com.androidplot.test.*;
import com.androidplot.ui.*;
import com.androidplot.util.*;

import org.junit.*;
import org.mockito.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PanZoomTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    XYPlot xyPlot;

    @Mock
    TypedArray typedArray;

    @Mock
    XYSeriesRegistry seriesRegistry;

    RectRegion bounds = new RectRegion(0, 100, 0, 100);

    @Before
    public void setUp() throws Exception {
        when(xyPlot.getRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getBounds()).thenReturn(bounds);
        when(xyPlot.getInnerLimits()).thenReturn(new RectRegion());
        when(xyPlot.getOuterLimits()).thenReturn(new RectRegion());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnTouch_notifiesOnTouchListener() throws Exception {
        PanZoom panZoom = new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);

        View.OnTouchListener listener = mock(View.OnTouchListener.class);
        panZoom.setDelegate(listener);

        MotionEvent motionEvent = mock(MotionEvent.class);
        panZoom.onTouch(xyPlot, motionEvent);

        verify(listener, times(1)).onTouch(xyPlot, motionEvent);
    }

    @Test
    public void testOnTouch_oneFingerMovePansButDoesNotZoom() throws Exception {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));

        View.OnTouchListener listener = mock(View.OnTouchListener.class);
        panZoom.setDelegate(listener);

        MotionEvent moveEvent = mock(MotionEvent.class);

        doNothing().when(panZoom).calculatePan(
                any(PointF.class), any(Region.class), anyBoolean());

        when(moveEvent.getAction())
                .thenReturn(MotionEvent.ACTION_DOWN)
                .thenReturn(MotionEvent.ACTION_MOVE)
                .thenReturn(MotionEvent.ACTION_UP);

        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_MOVE
        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_UP

        verify(panZoom).pan(moveEvent);
        verify(panZoom, never()).zoom(moveEvent);
        verify(panZoom).reset();
    }

    @Test
    public void testOnTouch_twoFingersZoom() throws Exception {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));
        MotionEvent moveEvent = mock(MotionEvent.class);

        // simulate a zoom gesture sequence:
        when(moveEvent.getAction())
                .thenReturn(MotionEvent.ACTION_DOWN)
                .thenReturn(MotionEvent.ACTION_POINTER_DOWN)
                .thenReturn(MotionEvent.ACTION_MOVE)
                .thenReturn(MotionEvent.ACTION_UP);

        when(panZoom.fingerDistance(moveEvent))
                .thenReturn(new RectF(0, 0, 10, 10))
                .thenReturn(new RectF(0, 0, 11, 11))
                .thenReturn(new RectF(0, 0, 12, 12))
                .thenReturn(new RectF(0, 0, 13, 13));

        panZoom.onTouch(xyPlot, moveEvent); // ACTION_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_POINTER_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_MOVE
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_UP

        verify(xyPlot).redraw();
        verify(panZoom, never()).pan(any(MotionEvent.class));
        verify(panZoom).zoom(moveEvent);
        verify(panZoom).reset();
    }

    @Test
    public void testZoom() {
        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        xyPlot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.redraw();

        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));

        // cap our pan/zoom boundaries:
        xyPlot.getOuterLimits().set(0, 100, 0, 100);

        panZoom.setFingersRect(new RectF(0, 0, 20, 20));

        InOrder inOrder = inOrder(xyPlot);
        inOrder.verify(xyPlot).setDomainBoundaries(0, 100, BoundaryMode.FIXED);

        // should result in a 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 40, 40));
        inOrder.verify(xyPlot).setDomainBoundaries(25f, 75f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(25f, 75f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // should result in another 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 80, 80));
        inOrder.verify(xyPlot).setDomainBoundaries(37.5f, 62.5f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(37.5f, 62.5f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // should zoom out and take us back to the original bounds:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 20, 20));
        inOrder.verify(xyPlot).setDomainBoundaries(0f, 100f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(0f, 100f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // zooming out past capped bounds should not result in any change:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 1, 1));
        inOrder.verify(xyPlot).setDomainBoundaries(0f, 100f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(0f, 100f, BoundaryMode.FIXED);
        // TODO: if nothing changed, then why bother redrawing??
        inOrder.verify(xyPlot).redraw();

        // redraw should not be called again
        inOrder.verify(xyPlot, never()).redraw();

        // make sure no panning took place during these zoom ops:
        verify(panZoom, never()).pan(any(MotionEvent.class));

    }

    @Test
    public void testLimitZoom() {
        double[] inc_domain = new double[]{10,50,100};
        double[] inc_range = new double[]{20,50};

        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        xyPlot.setDomainBoundaries(0, 20, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 30, BoundaryMode.FIXED);
        xyPlot.setDomainStepModel(new StepModelFit(xyPlot.getBounds().getxRegion(), inc_domain, 5));
        xyPlot.setRangeStepModel(new StepModelFit(xyPlot.getBounds().getyRegion(), inc_range, 5));
        xyPlot.redraw();

        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE, PanZoom.ZoomLimit.MIN_TICKS));

        // cap our pan/zoom boundaries:
        xyPlot.getOuterLimits().set(0, 20, 0, 30);

        panZoom.setFingersRect(new RectF(0, 0, 20, 20));

        InOrder inOrder = inOrder(xyPlot);
        inOrder.verify(xyPlot).setDomainBoundaries(0, 20, BoundaryMode.FIXED);

        // should NOT result in a 2x zoom on domain centerpoint, but in a zoom to
        // the minimum spacing 10 and 20 respectively
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 40, 40));
        inOrder.verify(xyPlot).setDomainBoundaries(5f, 15f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(5f, 25f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // to zoom in beyond min limits
        panZoom.setZoomLimit(PanZoom.ZoomLimit.OUTER);

        // should result in another 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 80, 80));
        inOrder.verify(xyPlot).setDomainBoundaries(7.5f, 12.5f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(10f, 20f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // back to limited zoom
        panZoom.setZoomLimit(PanZoom.ZoomLimit.MIN_TICKS);

        // try to zoom in further, should snap back to min limit:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 90, 90));
        inOrder.verify(xyPlot).setDomainBoundaries(5f, 15f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(5f, 25f, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // redraw should not be called again
        inOrder.verify(xyPlot, never()).redraw();

        // make sure no panning took place during these zoom ops:
        verify(panZoom, never()).pan(any(MotionEvent.class));

    }

    @Test
    public void testFingerDistance() {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));
        RectF distance = panZoom.fingerDistance(TestUtils.newPointerDownEvent(0, 0, 10, 10));
        assertEquals(0f, distance.left);
        assertEquals(0f, distance.top);
        assertEquals(10f, distance.right);
        assertEquals(10f, distance.bottom);

        // no matter what order the coords are supplied, make sure the same rect is calculated:
        distance = panZoom.fingerDistance(10, 10, 0, 0);
        assertEquals(0f, distance.left);
        assertEquals(0f, distance.top);
        assertEquals(10f, distance.right);
        assertEquals(10f, distance.bottom);
    }
}
