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

package com.androidplot.util;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.androidplot.Plot;
import com.androidplot.test.AndroidplotTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlotStatisticsTest extends AndroidplotTest {

    @Mock
    Canvas canvas;

    @Mock
    Paint paint;

    @Mock
    Plot plot;

    @InjectMocks
    PlotStatistics ps = new PlotStatistics(1, true);

    @Before
    public void before() {
        when(plot.getDisplayDimensions()).thenReturn(new DisplayDimensions());
    }

    @Test
    public void annotatePlot_annotates_ifEnabled() {
        ps.onAfterDraw(plot, canvas);
        verify(canvas).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void annotatePlot_doesNotAnnotate_ifDisabled() {
        ps.setEnabled(false);
        ps.onAfterDraw(plot, canvas);
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }
}
