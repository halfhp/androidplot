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
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XYLegendWidgetTest extends AndroidplotTest {

    @Mock LayoutManager layoutManager;
    @Mock XYPlot xyPlot;
    @Mock Canvas canvas;
    @Mock XYRegionFormatter xyRegionFormatter;
    @Mock XYSeriesFormatter xySeriesFormatter;
    @Mock XYSeriesRenderer xySeriesRenderer;

    Size widgetSize = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
    Size iconSize = new Size(10, SizeMode.ABSOLUTE, 10, SizeMode.ABSOLUTE);
    XYSeriesRegistry seriesRegistry;

    XYLegendWidget legendWidget;

    @Before
    public void before() {
        seriesRegistry = new XYSeriesRegistry();
        legendWidget = spy(new XYLegendWidget(layoutManager, xyPlot, widgetSize,
                new DynamicTableModel(4, 4), iconSize));

        when(xyPlot.getRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getRenderer(any(Class.class))).thenReturn(xySeriesRenderer);
    }

    @Test
    public void draw_drawsLegendIcons_forEnabledItemsOnly() throws Exception {
        final XYSeries  s1 = mock(XYSeries.class);
        final XYSeriesFormatter f1 = mock(XYSeriesFormatter.class);

        final XYSeries  s2 = mock(XYSeries.class);
        final XYSeriesFormatter f2 = mock(XYSeriesFormatter.class);

        when(f1.isLegendIconEnabled()).thenReturn(true);
        when(f2.isLegendIconEnabled()).thenReturn(false);

        seriesRegistry.add(s1, f1);
        seriesRegistry.add(s2, f2);
        legendWidget.draw(canvas);

        verify(legendWidget, never()).drawRegionLegendIcon(any(Canvas.class), any(RectF.class), any(XYRegionFormatter.class));
        verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), any(XYLegendItem.class));
    }

    @Test
    public void draw_sortsItemsAlphabeticallyByTitle() throws Exception{
        final XYLegendItem i1 = new XYLegendItem(XYLegendItem.Type.SERIES, xySeriesFormatter, "zoo");
        final XYLegendItem i2 = new XYLegendItem(XYLegendItem.Type.SERIES, xySeriesFormatter, "apple");
        final XYLegendItem i3 = new XYLegendItem(XYLegendItem.Type.SERIES, xySeriesFormatter, "boo");

        final List<XYLegendItem> legendItems = Lists.newArrayList(i1, i2, i3);
        doReturn(legendItems).when(legendWidget).getLegendItems();

        legendWidget.draw(canvas);

        InOrder inOrder = Mockito.inOrder(legendWidget);

        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i2));
        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i3));
        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i1));
    }
}
