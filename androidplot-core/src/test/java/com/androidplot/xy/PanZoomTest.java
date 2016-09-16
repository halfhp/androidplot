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

import com.androidplot.*;
import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static org.mockito.Mockito.*;

public class PanZoomTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    XYPlot xyPlot;

    @Mock
    TypedArray typedArray;

    @Mock
    SeriesRegistry seriesRegistry;

    @Before
    public void setUp() throws Exception {
        when(xyPlot.getSeriesRegistry()).thenReturn(seriesRegistry);
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
                any(PointF.class), any(PointF.class), anyBoolean());

        when(moveEvent.getAction())
                .thenReturn(MotionEvent.ACTION_DOWN)
                .thenReturn(MotionEvent.ACTION_MOVE);

        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_MOVE

        verify(panZoom, times(1)).pan(moveEvent);
        verify(panZoom, times(0)).zoom(moveEvent);
    }

}
