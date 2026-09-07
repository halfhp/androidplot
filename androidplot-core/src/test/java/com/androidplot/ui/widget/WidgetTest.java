// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.RectF;
import androidx.annotation.NonNull;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.PositionMetrics;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WidgetTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    Size size;

    @Mock
    Canvas canvas;

    Widget widget;

    PositionMetrics positionMetrics;

    @Before
    public void before() {

        positionMetrics = new PositionMetrics(
                0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        when(size.getHeight()).thenReturn(new SizeMetric(100, SizeMode.ABSOLUTE));
        when(size.getWidth()).thenReturn(new SizeMetric(100, SizeMode.ABSOLUTE));
        widget = spy(new TestWidget(layoutManager, size));
        widget.setPositionMetrics(positionMetrics);
        widget.refreshLayout();
    }

    @Test
    public void setGetAnchor_setsAndGetsAnchor() {
        widget.setAnchor(Anchor.LEFT_TOP);
        assertEquals(Anchor.LEFT_TOP, widget.getAnchor());

        widget.setAnchor(Anchor.RIGHT_BOTTOM);
        assertEquals(Anchor.RIGHT_BOTTOM, widget.getAnchor());
    }

    @Test
    public void position_withoutAnchor_updatesPositionWithExistingAnchor() {
        final Anchor existingAnchor = widget.getAnchor();
        widget.position(1, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                1, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);

        assertEquals(existingAnchor, widget.getAnchor());
        assertEquals(1F, widget.getPositionMetrics().getYPositionMetric().getValue());
        assertEquals(1F, widget.getPositionMetrics().getXPositionMetric().getValue());
    }

    @Test
    public void position_withAnchor_updatesPositionWithNewAnchor() {
        final Anchor newAnchor = Anchor.CENTER;
        widget.position(1, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                1, VerticalPositioning.ABSOLUTE_FROM_BOTTOM, newAnchor);

        assertEquals(newAnchor, widget.getAnchor());
        assertEquals(1F, widget.getPositionMetrics().getYPositionMetric().getValue());
        assertEquals(1F, widget.getPositionMetrics().getXPositionMetric().getValue());
    }

    @Test
    public void position_calledRepeatedly_addsWidgetToLayoutManagerOnceAndKeepsZOrder() {
        final LayoutManager realLayoutManager = new LayoutManager();
        final Widget bottom = new TestWidget(realLayoutManager, size);
        final Widget top = new TestWidget(realLayoutManager, size);

        bottom.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        top.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        assertEquals(2, realLayoutManager.size());
        assertEquals(0, realLayoutManager.indexOf(bottom));
        assertEquals(1, realLayoutManager.indexOf(top));

        // re-positioning an already added widget must neither add it again nor change its z-order:
        bottom.position(10, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                10, VerticalPositioning.ABSOLUTE_FROM_BOTTOM, Anchor.RIGHT_BOTTOM);
        assertEquals(2, realLayoutManager.size());
        assertEquals(0, realLayoutManager.indexOf(bottom));
        assertEquals(0, realLayoutManager.lastIndexOf(bottom));
        assertEquals(1, realLayoutManager.indexOf(top));
        assertEquals(10F, bottom.getPositionMetrics().getXPositionMetric().getValue());
        assertEquals(Anchor.RIGHT_BOTTOM, bottom.getAnchor());
    }

    @Test
    public void draw_sizeChanged_invokesOnResizeBeforeDoOnDraw() {
        InOrder inOrder = Mockito.inOrder(widget);

        widget.draw(canvas);

        inOrder.verify(widget).onResize(isNull(), any(RectF.class));
        inOrder.verify(widget).doOnDraw(eq(canvas), any(RectF.class));
        verify(widget).onResize(isNull(), any(RectF.class));
    }

    static class TestWidget extends Widget {

        public TestWidget(@NonNull LayoutManager layoutManager, @NonNull Size size) {
            super(layoutManager, size);
        }

        @Override
        protected void doOnDraw(Canvas canvas, RectF widgetRect) {
            // nothing to do
        }
    }
}
