// SPDX-License-Identifier: Apache-2.0

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XYLegendWidgetTest extends AndroidplotTest {

    @Mock LayoutManager layoutManager;
    @Mock XYPlot xyPlot;
    @Mock Canvas canvas;
    @Mock XYRegionFormatter xyRegionFormatter;
    LineAndPointRenderer lineAndPointRenderer;

    Size widgetSize = new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE);
    Size iconSize = new Size(10, SizeMode.ABSOLUTE, 10, SizeMode.ABSOLUTE);
    XYSeriesRegistry seriesRegistry;

    XYLegendWidget legendWidget;

    @Before
    public void before() {
        seriesRegistry = new XYSeriesRegistry();
        legendWidget = spy(new XYLegendWidget(layoutManager, xyPlot, widgetSize,
                new DynamicTableModel(4, 4), iconSize));

        lineAndPointRenderer = new LineAndPointRenderer(xyPlot);

        when(xyPlot.getRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getRendererList()).thenReturn(Lists.<XYSeriesRenderer>newArrayList(lineAndPointRenderer));
        when(xyPlot.getRenderer(any(Class.class))).thenReturn(lineAndPointRenderer);
    }

    @Test
    public void draw_drawsLegendIcons_forEnabledItemsOnly() throws Exception {
        final XYSeries  s1 = mock(XYSeries.class);
        final XYSeriesFormatter f1 = new LineAndPointFormatter();
        f1.setLegendIconEnabled(true);

        final XYSeries  s2 = mock(XYSeries.class);
        final XYSeriesFormatter f2 = new LineAndPointFormatter();
        f2.setLegendIconEnabled(false);

        final RectRegion r1 = new RectRegion(0, 0, 10, 10, "r1");
        final RectRegion r2 = new RectRegion(0, 0, 20, 20, "r2");
        f1.addRegion(r1, new XYRegionFormatter(0));
        f2.addRegion(r2, new XYRegionFormatter(0));

        seriesRegistry.add(s1, f1);
        seriesRegistry.add(s2, f2);
        legendWidget.draw(canvas);

        verify(legendWidget, times(2))
                .drawRegionLegendIcon(any(Canvas.class), any(RectF.class), any(XYRegionFormatter.class));
        verify(legendWidget, times(3))
                .drawIcon(any(Canvas.class), any(RectF.class), any(XYLegendItem.class));
    }

    @Test
    public void draw_sortsItemsAlphabeticallyByTitle() throws Exception{
        final XYLegendItem i1 = new XYLegendItem(XYLegendItem.Type.SERIES,
                new LineAndPointFormatter(), "zoo");
        final XYLegendItem i2 = new XYLegendItem(XYLegendItem.Type.SERIES,
                new LineAndPointFormatter(), "apple");
        final XYLegendItem i3 = new XYLegendItem(XYLegendItem.Type.SERIES,
                new LineAndPointFormatter(), "boo");

        final List<XYLegendItem> legendItems = Lists.newArrayList(i1, i2, i3);
        doReturn(legendItems).when(legendWidget).getLegendItems();

        legendWidget.draw(canvas);

        InOrder inOrder = Mockito.inOrder(legendWidget);

        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i2));
        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i3));
        inOrder.verify(legendWidget).drawIcon(any(Canvas.class), any(RectF.class), eq(i1));
    }

    @Test
    public void draw_nullSeriesTitles_sortsAndDrawsWithoutThrowing() throws Exception {
        // CandlestickSeries et al. create series with a null title:
        final XYSeries untitled1 = new SimpleXYSeries(null);
        final XYSeries untitled2 = new SimpleXYSeries(null);
        final XYSeries titled = new SimpleXYSeries("titled");
        seriesRegistry.add(untitled1, new LineAndPointFormatter());
        seriesRegistry.add(untitled2, new LineAndPointFormatter());
        seriesRegistry.add(titled, new LineAndPointFormatter());

        legendWidget.draw(canvas);

        verify(legendWidget, times(3))
                .drawIcon(any(Canvas.class), any(RectF.class), any(XYLegendItem.class));
        verify(canvas, times(1)).drawText(eq("titled"), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText((String) isNull(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void draw_nullTitleWithoutComparator_drawsNoTextForThatItem() throws Exception {
        legendWidget.setLegendItemComparator(null);
        final XYLegendItem untitled = new XYLegendItem(XYLegendItem.Type.SERIES,
                new LineAndPointFormatter(), null);
        final XYLegendItem titled = new XYLegendItem(XYLegendItem.Type.SERIES,
                new LineAndPointFormatter(), "titled");
        doReturn(Lists.newArrayList(untitled, titled)).when(legendWidget).getLegendItems();

        legendWidget.draw(canvas);

        verify(legendWidget, times(2))
                .drawIcon(any(Canvas.class), any(RectF.class), any(XYLegendItem.class));
        verify(canvas, times(1)).drawText(eq("titled"), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText((String) isNull(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void draw_regionWithoutLabel_drawsRegionIconWithoutThrowing() throws Exception {
        final XYSeries series = new SimpleXYSeries("series");
        final XYSeriesFormatter formatter = new LineAndPointFormatter();

        // the 4-arg RectRegion constructor leaves the label null:
        formatter.addRegion(new RectRegion(0, 10, 0, 10), new XYRegionFormatter(0));
        formatter.addRegion(new RectRegion(0, 20, 0, 20, "labelled"), new XYRegionFormatter(0));
        seriesRegistry.add(series, formatter);

        legendWidget.draw(canvas);

        verify(legendWidget, times(2))
                .drawRegionLegendIcon(any(Canvas.class), any(RectF.class), any(XYRegionFormatter.class));
        verify(canvas, times(1)).drawText(eq("labelled"), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText((String) isNull(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void draw_moreItemsThanTableCells_drawsWhatFitsWithoutThrowing() throws Exception {
        // a 2x2 table can only hold 4 of the 5 items:
        legendWidget.setTableModel(new DynamicTableModel(2, 2));
        final List<XYLegendItem> legendItems = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            legendItems.add(new XYLegendItem(XYLegendItem.Type.SERIES,
                    new LineAndPointFormatter(), "item " + i));
        }
        doReturn(legendItems).when(legendWidget).getLegendItems();

        legendWidget.draw(canvas);

        verify(legendWidget, times(4))
                .drawIcon(any(Canvas.class), any(RectF.class), any(XYLegendItem.class));
    }
}
