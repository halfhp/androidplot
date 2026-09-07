// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.util.DisplayDimensions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PieLegendWidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    Canvas canvas;

    PieChart pieChart;
    PieLegendWidget legend;
    Segment s1;
    Segment s2;
    SegmentFormatter f1;
    SegmentFormatter f2;

    @Before
    public void setUp() {
        pieChart = new PieChart(getContext(), "pie");
        legend = new PieLegendWidget(layoutManager, pieChart,
                new Size(100, SizeMode.ABSOLUTE, 100, SizeMode.ABSOLUTE),
                new DynamicTableModel(1, 4),
                new Size(10, SizeMode.ABSOLUTE, 10, SizeMode.ABSOLUTE));
        s1 = new Segment("first", 1);
        s2 = new Segment("second", 2);
        f1 = new SegmentFormatter(Color.RED);
        f2 = new SegmentFormatter(Color.BLUE);
        pieChart.addSegment(s1, f1);
        pieChart.addSegment(s2, f2);

        // lay the legend out over a 100x100 area at the origin:
        legend.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        RectF rect = new RectF(0, 0, 100, 100);
        legend.layout(new DisplayDimensions(rect, rect, rect));
    }

    private void draw() {
        legend.draw(canvas);
    }

    @Test
    public void legendItem_exposesSegmentTitleAndFormatter() {
        PieLegendItem item = new PieLegendItem(s1, f1);
        assertSame(s1, item.segment);
        assertSame(f1, item.formatter);
        assertEquals("first", item.getTitle());
    }

    @Test
    public void getLegendItems_returnsOneItemPerLegendEnabledSegment() {
        List<PieLegendItem> items = legend.getLegendItems();
        assertEquals(2, items.size());
        assertSame(s1, items.get(0).segment);
        assertSame(f1, items.get(0).formatter);
        assertSame(s2, items.get(1).segment);
        assertSame(f2, items.get(1).formatter);

        f2.setLegendIconEnabled(false);
        items = legend.getLegendItems();
        assertEquals(1, items.size());
        assertSame(s1, items.get(0).segment);
    }

    @Test
    public void drawIcon_fillsIconRectWithSegmentFillPaint() {
        RectF iconRect = new RectF(0, 0, 10, 10);
        legend.drawIcon(canvas, iconRect, new PieLegendItem(s1, f1));
        verify(canvas).drawRect(iconRect, f1.getFillPaint());
    }

    @Test
    public void doOnDraw_drawsIconAndTitleForEachSegment() {
        draw();

        verify(canvas).drawRect(any(RectF.class), eq(f1.getFillPaint()));
        verify(canvas).drawRect(any(RectF.class), eq(f2.getFillPaint()));
        verify(canvas).drawText(eq("first"), anyFloat(), anyFloat(), eq(legend.getTextPaint()));
        verify(canvas).drawText(eq("second"), anyFloat(), anyFloat(), eq(legend.getTextPaint()));
    }

    @Test
    public void doOnDraw_rightAlignedText_drawsTitleLeftOfIcon() {
        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.RIGHT);
        legend.setTextPaint(textPaint);
        assertSame(textPaint, legend.getTextPaint());
        pieChart.removeSegment(s2);

        draw();

        // left-of-icon: the icon starts at cell.left + 1 == 1, so the text x is 1 - 2
        verify(canvas).drawText(eq("first"), eq(-1f), anyFloat(), eq(textPaint));
    }

    @Test
    public void doOnDraw_withComparator_sortsItems() {
        legend.setLegendItemComparator(new Comparator<PieLegendItem>() {
            @Override
            public int compare(PieLegendItem a, PieLegendItem b) {
                return b.getTitle().compareTo(a.getTitle());
            }
        });
        assertTrue(legend.getLegendItemComparator() != null);

        draw();

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).drawText(eq("second"), anyFloat(), anyFloat(), any(Paint.class));
        inOrder.verify(canvas).drawText(eq("first"), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void iconBackgroundAndBorder_areDrawnOnlyWhenEnabled() {
        legend.setDrawIconBackgroundEnabled(false);
        legend.setDrawIconBorderEnabled(false);
        assertFalse(legend.isDrawIconBackgroundEnabled());
        assertFalse(legend.isDrawIconBorderEnabled());

        draw();
        // only the two segment fills:
        verify(canvas, times(2)).drawRect(any(RectF.class), any(Paint.class));

        legend.setDrawIconBackgroundEnabled(true);
        legend.setDrawIconBorderEnabled(true);
        assertTrue(legend.isDrawIconBackgroundEnabled());
        assertTrue(legend.isDrawIconBorderEnabled());

        draw();
        // plus a background and a border rect per item:
        verify(canvas, times(2 + 6)).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void setIconSize_roundTrips() {
        Size iconSize = new Size(3, SizeMode.ABSOLUTE, 4, SizeMode.ABSOLUTE);
        legend.setIconSize(iconSize);
        assertSame(iconSize, legend.getIconSize());
    }

    @Test
    public void legendItemComparator_defaultsToNull() {
        assertNull(legend.getLegendItemComparator());
    }
}
