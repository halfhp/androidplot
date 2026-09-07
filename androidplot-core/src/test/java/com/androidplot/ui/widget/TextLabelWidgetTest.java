// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TextLabelWidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    Size size;

    @Mock
    Canvas canvas;

    @Mock
    RectF rectF;

    private TextLabelWidget textLabelWidget;

    @Before
    public void before() {
        textLabelWidget = spy(new TextLabelWidget(layoutManager, size));
    }

    @Test
    public void onMetricsChanged_invokesPack_ifAutopackEnabled() {
        textLabelWidget.setAutoPackEnabled(true);
        textLabelWidget.onMetricsChanged(size, size);
        verify(textLabelWidget, times(2)).pack();
    }

    @Test
    public void onMetricsChanged_doesNotInvokePack_ifAutopackDisabled() {
        textLabelWidget.setAutoPackEnabled(false);
        textLabelWidget.onMetricsChanged(size, size);
        verify(textLabelWidget, never()).pack();
    }

    @Test
    public void doOnDraw_rotatesThenDraws_ifVerticalAscending() {
        textLabelWidget.setText("this is a test");
        textLabelWidget.setOrientation(TextOrientation.VERTICAL_ASCENDING);
        textLabelWidget.doOnDraw(canvas, rectF);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).rotate(-90);
        inOrder.verify(canvas).drawText(eq(textLabelWidget.getText()), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void doOnDraw_rotatesThenDraws_ifVerticalDescending() {
        textLabelWidget.setText("this is a test");
        textLabelWidget.setOrientation(TextOrientation.VERTICAL_DESCENDING);
        textLabelWidget.doOnDraw(canvas, rectF);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).rotate(90);
        inOrder.verify(canvas).drawText(eq(textLabelWidget.getText()), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void doOnDraw_horizontal_drawsWithoutRotation() {
        textLabelWidget.setText("plain");
        textLabelWidget.setOrientation(TextOrientation.HORIZONTAL);
        textLabelWidget.doOnDraw(canvas, rectF);

        verify(canvas, never()).rotate(anyFloat());
        verify(canvas).drawText(eq("plain"), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void doOnDraw_emptyText_drawsNothing() {
        textLabelWidget.setText("");
        textLabelWidget.doOnDraw(canvas, rectF);
        textLabelWidget.setText(null);
        textLabelWidget.doOnDraw(canvas, rectF);
        verify(canvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void titleConstructor_setsTextAndOrientation() {
        Size realSize = new Size(10, SizeMode.ABSOLUTE, 20, SizeMode.ABSOLUTE);
        TextLabelWidget widget = new TextLabelWidget(layoutManager, "the title", realSize,
                TextOrientation.VERTICAL_DESCENDING);
        assertEquals("the title", widget.getText());
        assertEquals(TextOrientation.VERTICAL_DESCENDING, widget.getOrientation());
        // auto-packing is on by default, so the initial size was replaced by the packed size:
        assertTrue(widget.isAutoPackEnabled());
        assertNotSame(realSize, widget.getSize());
        assertEquals(SizeMode.ABSOLUTE, widget.getSize().getWidth().getLayoutType());
    }

    @Test
    public void setOrientation_roundTrips() {
        textLabelWidget.setOrientation(TextOrientation.VERTICAL_ASCENDING);
        assertEquals(TextOrientation.VERTICAL_ASCENDING, textLabelWidget.getOrientation());
    }

    @Test
    public void setLabelPaint_roundTrips_andPacksWhenAutoPackEnabled() {
        textLabelWidget.setAutoPackEnabled(true);
        Paint paint = new Paint();
        textLabelWidget.setLabelPaint(paint);
        assertSame(paint, textLabelWidget.getLabelPaint());
        // once from setAutoPackEnabled, once from setLabelPaint:
        verify(textLabelWidget, times(2)).pack();
    }

    @Test
    public void setLabelPaint_doesNotPackWhenAutoPackDisabled() {
        textLabelWidget.setAutoPackEnabled(false);
        assertFalse(textLabelWidget.isAutoPackEnabled());
        textLabelWidget.setLabelPaint(new Paint());
        verify(textLabelWidget, never()).pack();
    }

    @Test
    public void pack_sizesWidgetToText() {
        TextLabelWidget widget = new TextLabelWidget(layoutManager,
                new Size(0, SizeMode.ABSOLUTE, 0, SizeMode.ABSOLUTE));
        widget.setText("abc");
        widget.setOrientation(TextOrientation.HORIZONTAL);
        widget.pack();
        Size packed = widget.getSize();
        assertEquals(SizeMode.ABSOLUTE, packed.getWidth().getLayoutType());
        assertEquals(SizeMode.ABSOLUTE, packed.getHeight().getLayoutType());
        // the width carries the +2 padding so it is never zero even for zero-width font metrics:
        assertTrue(packed.getWidth().getValue() >= 2);

        // vertical text swaps the axes: the padded font height becomes the widget width
        widget.setOrientation(TextOrientation.VERTICAL_ASCENDING);
        widget.pack();
        assertTrue(widget.getSize().getWidth().getValue() >= 2);
    }
}
