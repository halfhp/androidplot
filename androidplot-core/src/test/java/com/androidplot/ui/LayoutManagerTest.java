// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.DisplayDimensions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LayoutManagerTest extends AndroidplotTest {

    private static final RectF CANVAS_RECT = new RectF(0, 0, 300, 200);
    private static final RectF MARGINATED_RECT = new RectF(5, 5, 295, 195);
    private static final RectF PADDED_RECT = new RectF(10, 20, 210, 120);

    @Mock
    Canvas canvas;

    LayoutManager layoutManager;
    DisplayDimensions dims;

    @Before
    public void setUp() {
        layoutManager = new LayoutManager();
        dims = new DisplayDimensions(CANVAS_RECT, MARGINATED_RECT, PADDED_RECT);
    }

    @Test
    public void addToTopAndBottom_orderElements() {
        final Widget a = newWidget();
        final Widget b = newWidget();
        final Widget c = newWidget();
        layoutManager.addToTop(a);
        layoutManager.addToTop(b);
        layoutManager.addToBottom(c);
        assertEquals(Arrays.asList(c, a, b), layoutManager.elements());
        assertEquals(3, layoutManager.size());
    }

    @Test
    public void position_addsWidgetToTopExactlyOnce() {
        final Widget a = newWidget();
        final Widget b = newWidget();
        assertFalse(layoutManager.contains(a));

        a.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        b.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        a.position(5, HorizontalPositioning.ABSOLUTE_FROM_RIGHT, 5, VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.RIGHT_BOTTOM);
        a.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);

        assertEquals(Arrays.asList(a, b), layoutManager.elements());
        assertEquals(1, layoutManager.stream().filter(w -> w == a).count());
    }

    @Test
    public void zOrderOperations_reorderElements() {
        final Widget a = newWidget();
        final Widget b = newWidget();
        final Widget c = newWidget();
        layoutManager.addToTop(a);
        layoutManager.addToTop(b);
        layoutManager.addToTop(c);
        assertEquals(Arrays.asList(a, b, c), layoutManager.elements());

        assertTrue(layoutManager.moveToTop(a));
        assertEquals(Arrays.asList(b, c, a), layoutManager.elements());

        assertTrue(layoutManager.moveToBottom(c));
        assertEquals(Arrays.asList(c, b, a), layoutManager.elements());

        assertTrue(layoutManager.moveAbove(c, b));
        assertEquals(Arrays.asList(b, c, a), layoutManager.elements());

        assertTrue(layoutManager.moveBeneath(a, b));
        assertEquals(Arrays.asList(a, b, c), layoutManager.elements());

        assertTrue(layoutManager.moveUp(a));
        assertEquals(Arrays.asList(b, a, c), layoutManager.elements());

        assertTrue(layoutManager.moveDown(c));
        assertEquals(Arrays.asList(b, c, a), layoutManager.elements());

        // already at the extremes is a no-op success:
        assertTrue(layoutManager.moveUp(a));
        assertTrue(layoutManager.moveDown(b));
        assertEquals(Arrays.asList(b, c, a), layoutManager.elements());
    }

    @Test
    public void zOrderOperations_unknownWidget() {
        final Widget a = newWidget();
        final Widget stranger = newWidget();
        layoutManager.addToTop(a);

        assertFalse(layoutManager.moveToTop(stranger));
        assertFalse(layoutManager.moveUp(stranger));
        assertFalse(layoutManager.moveDown(stranger));
        assertEquals(Arrays.asList(a), layoutManager.elements());

        try {
            layoutManager.moveAbove(a, a);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            layoutManager.moveBeneath(a, newWidget());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void remove_dropsWidgetAndPreservesOrderOfOthers() {
        final Widget a = newWidget();
        final Widget b = newWidget();
        final Widget c = newWidget();
        layoutManager.addToTop(a);
        layoutManager.addToTop(b);
        layoutManager.addToTop(c);

        assertTrue(layoutManager.remove(b));
        assertEquals(Arrays.asList(a, c), layoutManager.elements());
        assertFalse(layoutManager.remove(b));

        // a removed widget is re-added by its next position() call:
        b.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        assertEquals(Arrays.asList(a, c, b), layoutManager.elements());
    }

    @Test
    public void layout_propagatesDimensionsToEveryWidget() {
        final Widget a = newWidget();
        final Widget b = newWidget();
        a.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        b.position(0, HorizontalPositioning.ABSOLUTE_FROM_RIGHT, 0, VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.RIGHT_BOTTOM);

        layoutManager.layout(dims);

        assertEquals(new RectF(10, 20, 50, 50), a.getWidgetDimensions().canvasRect);
        assertEquals(new RectF(170, 90, 210, 120), b.getWidgetDimensions().canvasRect);

        // a widget added later is laid out on refresh against the last dims:
        final Widget c = newWidget();
        c.position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER, 0, VerticalPositioning.ABSOLUTE_FROM_CENTER,
                Anchor.CENTER);
        assertEquals(new RectF(1, 1, 1, 1), c.getWidgetDimensions().canvasRect);
        layoutManager.refreshLayout();
        assertEquals(new RectF(90, 55, 130, 85), c.getWidgetDimensions().canvasRect);
    }

    @Test
    public void onPostInit_propagatesToEveryWidget() {
        final Widget a = spy(newWidget());
        final Widget b = spy(newWidget());
        layoutManager.addToTop(a);
        layoutManager.addToTop(b);
        layoutManager.onPostInit();
        verify(a).onPostInit();
        verify(b).onPostInit();
    }

    @Test
    public void draw_drawsWidgetsBottomToTopWithSaveRestore() {
        final Widget bottom = spy(newWidget());
        final Widget top = spy(newWidget());
        bottom.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        top.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        layoutManager.layout(dims);

        layoutManager.draw(canvas);

        final InOrder inOrder = Mockito.inOrder(bottom, top);
        inOrder.verify(bottom).draw(canvas);
        inOrder.verify(top).draw(canvas);

        // one save/restore pair per widget from the layout manager plus one from Widget.draw:
        verify(canvas, times(4)).save();
        verify(canvas, times(4)).restore();

        // no markup by default:
        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
        verify(canvas, never()).clipRect(any(RectF.class), any(Region.Op.class));
    }

    @Test
    public void draw_clippingEnabledWidget_clipsToWidgetRect() {
        final Widget widget = newWidget();
        widget.setClippingEnabled(true);
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        layoutManager.layout(dims);

        layoutManager.draw(canvas);

        verify(canvas).clipRect(new RectF(10, 20, 50, 50), Region.Op.INTERSECT);
    }

    @Test
    public void draw_restoresCanvasWhenWidgetDrawThrows() {
        final Widget widget = spy(newWidget());
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        layoutManager.layout(dims);
        Mockito.doThrow(new IllegalStateException("boom")).when(widget).draw(canvas);

        try {
            layoutManager.draw(canvas);
            fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // ok
        }
        verify(canvas).save();
        verify(canvas).restore();
    }

    @Test
    public void setMarkupEnabled_togglesAllMarkupFlags() {
        assertFalse(layoutManager.isDrawOutlinesEnabled());
        assertFalse(layoutManager.isDrawAnchorsEnabled());
        assertFalse(layoutManager.isDrawMarginsEnabled());
        assertFalse(layoutManager.isDrawPaddingEnabled());
        assertFalse(layoutManager.isDrawOutlineShadowsEnabled());
        assertNull(layoutManager.getOutlineShadowPaint());

        layoutManager.setMarkupEnabled(true);
        assertTrue(layoutManager.isDrawOutlinesEnabled());
        assertTrue(layoutManager.isDrawAnchorsEnabled());
        assertTrue(layoutManager.isDrawMarginsEnabled());
        assertTrue(layoutManager.isDrawPaddingEnabled());
        assertTrue(layoutManager.isDrawOutlineShadowsEnabled());
        // a default shadow paint is created lazily:
        assertNotNull(layoutManager.getOutlineShadowPaint());

        layoutManager.setMarkupEnabled(false);
        assertFalse(layoutManager.isDrawOutlinesEnabled());
        assertFalse(layoutManager.isDrawAnchorsEnabled());
        assertFalse(layoutManager.isDrawMarginsEnabled());
        assertFalse(layoutManager.isDrawPaddingEnabled());
        assertFalse(layoutManager.isDrawOutlineShadowsEnabled());
    }

    @Test
    public void paintSetters_replacePaints() {
        final Paint outline = new Paint();
        final Paint shadow = new Paint();
        final Paint margin = new Paint();
        final Paint padding = new Paint();
        layoutManager.setOutlinePaint(outline);
        layoutManager.setOutlineShadowPaint(shadow);
        layoutManager.setMarginPaint(margin);
        layoutManager.setPaddingPaint(padding);
        assertSame(outline, layoutManager.getOutlinePaint());
        assertSame(shadow, layoutManager.getOutlineShadowPaint());
        assertSame(margin, layoutManager.getMarginPaint());
        assertSame(padding, layoutManager.getPaddingPaint());

        // an explicitly set shadow paint is not replaced by the lazy default:
        layoutManager.setDrawOutlineShadowsEnabled(true);
        assertSame(shadow, layoutManager.getOutlineShadowPaint());
    }

    @Test
    public void draw_markupEnabled_drawsPlotSpacingAndWidgetMarkup() {
        final Widget widget = newWidget();
        widget.setMargins(1, 1, 1, 1);
        widget.setPadding(2, 2, 2, 2);
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.LEFT_TOP);
        layoutManager.layout(dims);
        layoutManager.setMarkupEnabled(true);

        layoutManager.draw(canvas);

        final RectF widgetRect = new RectF(10, 20, 50, 50);
        // plot margins & padding: clip out the inner rect, fill the outer one
        verify(canvas).clipRect(MARGINATED_RECT, Region.Op.DIFFERENCE);
        verify(canvas).drawRect(CANVAS_RECT, layoutManager.getMarginPaint());
        verify(canvas).clipRect(PADDED_RECT, Region.Op.DIFFERENCE);
        verify(canvas).drawRect(MARGINATED_RECT, layoutManager.getPaddingPaint());

        // widget shadow & outline
        verify(canvas).drawRect(widgetRect, layoutManager.getOutlineShadowPaint());
        verify(canvas).drawRect(widgetRect, layoutManager.getOutlinePaint());

        // widget margins & padding
        verify(canvas).clipRect(new RectF(11, 21, 49, 49), Region.Op.DIFFERENCE);
        verify(canvas).drawRect(widgetRect, layoutManager.getMarginPaint());
        verify(canvas).clipRect(new RectF(13, 23, 47, 47), Region.Op.DIFFERENCE);
        verify(canvas).drawRect(new RectF(11, 21, 49, 49), layoutManager.getPaddingPaint());

        // anchor marker: 8x8 square centered on the LEFT_TOP anchor at (10, 20)
        verify(canvas).drawRect(eq(6f), eq(16f), eq(14f), eq(24f), any(Paint.class));

        // every save (plot margin, plot padding, widget, widget.draw, widget margin, widget padding)
        // is paired with a restore:
        verify(canvas, times(6)).save();
        verify(canvas, times(6)).restore();
    }

    @Test
    public void draw_anchorsEnabled_marksAnchorOfEachWidget() {
        final Widget widget = newWidget();
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_RIGHT, 0, VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.RIGHT_BOTTOM);
        layoutManager.layout(dims);
        layoutManager.setDrawAnchorsEnabled(true);

        layoutManager.draw(canvas);

        // anchor is at the padded rect's bottom right corner (210, 120):
        verify(canvas).drawRect(eq(206f), eq(116f), eq(214f), eq(124f), any(Paint.class));
        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
        verify(canvas, never()).clipRect(any(RectF.class), any(Region.Op.class));
    }

    @Test
    public void onTouch_isNotConsumed() {
        assertFalse(layoutManager.onTouch(Mockito.mock(View.class), Mockito.mock(MotionEvent.class)));
    }

    private Widget newWidget() {
        return new TestWidget(layoutManager, new Size(30, SizeMode.ABSOLUTE, 40, SizeMode.ABSOLUTE));
    }

    static class TestWidget extends Widget {
        TestWidget(LayoutManager layoutManager, Size size) {
            super(layoutManager, size);
        }

        @Override
        protected void doOnDraw(Canvas canvas, RectF widgetRect) {
            // nothing to do
        }
    }
}
