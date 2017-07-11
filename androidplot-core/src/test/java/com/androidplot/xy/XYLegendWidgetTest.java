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
import com.androidplot.Plot;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class XYLegendWidgetTest extends AndroidplotTest {

    @Mock LayoutManager layoutManager;
    @Mock XYPlot xyPlot;
    @Mock Canvas canvas;
    @Mock XYRegionFormatter xyRegionFormatter;

    Size widgetSize = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
    Size iconSize = new Size(10, SizeMode.ABSOLUTE, 10, SizeMode.ABSOLUTE);

    XYLegendWidget legendWidget;

    @Before
    public void before() {
        legendWidget = spy(new XYLegendWidget(layoutManager, xyPlot, widgetSize,
                new DynamicTableModel(4, 4), iconSize));
    }

    @Test
    public void doOnDraw_drawsAllItems() throws Exception {
        final List<XYLegendItem> legendItems = new ArrayList<>();
        legendItems.add(new XYLegendItem(XYLegendItem.Type.REGION, xyRegionFormatter, "foo"));
        legendItems.add(new XYLegendItem(XYLegendItem.Type.REGION, xyRegionFormatter, "bar"));
        doReturn(legendItems).when(legendWidget).getLegendItems();
        legendWidget.draw(canvas);

        verify(legendWidget, times(2))
                .drawRegionLegendIcon(eq(canvas), any(RectF.class), any(XYRegionFormatter.class));
    }
}
