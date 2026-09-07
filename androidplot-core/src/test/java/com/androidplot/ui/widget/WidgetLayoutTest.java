// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
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
import com.androidplot.util.DisplayDimensions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Layout math of {@link Widget}: how size mode, positioning mode, anchor, box model and
 * rotation combine into the widget's on-screen rect.
 */
public class WidgetLayoutTest extends AndroidplotTest {

    // the plot's padded rect is what widgets are laid out against; canvas and marginated rects
    // are deliberately different so a widget accidentally using them is caught.
    private static final RectF CANVAS_RECT = new RectF(0, 0, 300, 200);
    private static final RectF MARGINATED_RECT = new RectF(5, 5, 295, 195);
    private static final RectF PADDED_RECT = new RectF(10, 20, 210, 120); // 200 x 100

    private static final float WIDGET_W = 40;
    private static final float WIDGET_H = 30;

    private static final float ABS_X = 30;
    private static final float ABS_Y = 10;
    private static final float REL = 0.25f;

    @Mock
    Canvas canvas;

    LayoutManager layoutManager;
    DisplayDimensions plotDims;

    @Before
    public void setUp() {
        layoutManager = new LayoutManager();
        plotDims = new DisplayDimensions(CANVAS_RECT, MARGINATED_RECT, PADDED_RECT);
    }

    /**
     * Expected x (relative to the padded rect's left edge) of the anchor point for each
     * horizontal positioning, computed independently of {@link com.androidplot.ui.PositionMetric}.
     */
    private static float expectedAnchorX(HorizontalPositioning positioning) {
        final float w = PADDED_RECT.width(); // 200
        switch (positioning) {
            case ABSOLUTE_FROM_LEFT:
                return ABS_X;                       // 30
            case ABSOLUTE_FROM_RIGHT:
                return w - ABS_X;                   // 170
            case ABSOLUTE_FROM_CENTER:
                return w / 2 + ABS_X;               // 130
            case RELATIVE_TO_LEFT:
                return w * REL;                     // 50
            case RELATIVE_TO_RIGHT:
                return w + w * -REL;                // 150 (negative value moves inward)
            case RELATIVE_TO_CENTER:
                return w / 2 + (w / 2) * REL;       // 125
            default:
                throw new IllegalArgumentException();
        }
    }

    private static float valueFor(HorizontalPositioning positioning) {
        switch (positioning) {
            case ABSOLUTE_FROM_LEFT:
            case ABSOLUTE_FROM_RIGHT:
            case ABSOLUTE_FROM_CENTER:
                return ABS_X;
            case RELATIVE_TO_RIGHT:
                return -REL;
            default:
                return REL;
        }
    }

    private static float expectedAnchorY(VerticalPositioning positioning) {
        final float h = PADDED_RECT.height(); // 100
        switch (positioning) {
            case ABSOLUTE_FROM_TOP:
                return ABS_Y;                       // 10
            case ABSOLUTE_FROM_BOTTOM:
                return h - ABS_Y;                   // 90
            case ABSOLUTE_FROM_CENTER:
                return h / 2 + ABS_Y;               // 60
            case RELATIVE_TO_TOP:
                return h * REL;                     // 25
            case RELATIVE_TO_BOTTOM:
                return h + h * -REL;                // 75
            case RELATIVE_TO_CENTER:
                return h / 2 + (h / 2) * REL;       // 62.5
            default:
                throw new IllegalArgumentException();
        }
    }

    private static float valueFor(VerticalPositioning positioning) {
        switch (positioning) {
            case ABSOLUTE_FROM_TOP:
            case ABSOLUTE_FROM_BOTTOM:
            case ABSOLUTE_FROM_CENTER:
                return ABS_Y;
            case RELATIVE_TO_BOTTOM:
                return -REL;
            default:
                return REL;
        }
    }

    /**
     * Offset of the anchor point from the widget's top-left corner, for a 40 x 30 widget.
     */
    private static PointF anchorOffset(Anchor anchor) {
        switch (anchor) {
            case LEFT_TOP:
                return new PointF(0, 0);
            case LEFT_MIDDLE:
                return new PointF(0, 15);
            case LEFT_BOTTOM:
                return new PointF(0, 30);
            case RIGHT_TOP:
                return new PointF(40, 0);
            case RIGHT_MIDDLE:
                return new PointF(40, 15);
            case RIGHT_BOTTOM:
                return new PointF(40, 30);
            case TOP_MIDDLE:
                return new PointF(20, 0);
            case BOTTOM_MIDDLE:
                return new PointF(20, 30);
            case CENTER:
                return new PointF(20, 15);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    public void layout_everyPositioningAndAnchorCombination_producesExpectedRect() {
        for (HorizontalPositioning hp : HorizontalPositioning.values()) {
            for (VerticalPositioning vp : VerticalPositioning.values()) {
                for (Anchor anchor : Anchor.values()) {
                    final String combo = hp + " / " + vp + " / " + anchor;
                    final Widget widget = newAbsoluteWidget();
                    widget.position(valueFor(hp), hp, valueFor(vp), vp, anchor);
                    widget.layout(plotDims);

                    final PointF offset = anchorOffset(anchor);
                    final float expectedLeft = PADDED_RECT.left + expectedAnchorX(hp) - offset.x;
                    final float expectedTop = PADDED_RECT.top + expectedAnchorY(vp) - offset.y;

                    final RectF actual = widget.getWidgetDimensions().canvasRect;
                    assertEquals(combo + " left", expectedLeft, actual.left, 0);
                    assertEquals(combo + " top", expectedTop, actual.top, 0);
                    assertEquals(combo + " right", expectedLeft + WIDGET_W, actual.right, 0);
                    assertEquals(combo + " bottom", expectedTop + WIDGET_H, actual.bottom, 0);

                    // the anchor point of the resulting rect must be back where we asked for it:
                    final PointF anchorCoords = Widget.getAnchorCoordinates(actual, anchor);
                    assertEquals(combo + " anchor x",
                            PADDED_RECT.left + expectedAnchorX(hp), anchorCoords.x, 0);
                    assertEquals(combo + " anchor y",
                            PADDED_RECT.top + expectedAnchorY(vp), anchorCoords.y, 0);
                }
            }
        }
    }

    @Test
    public void layout_positionWithoutAnchor_defaultsToLeftTop() {
        final Widget widget = newAbsoluteWidget();
        widget.position(ABS_X, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                ABS_Y, VerticalPositioning.ABSOLUTE_FROM_TOP);
        widget.layout(plotDims);
        assertEquals(Anchor.LEFT_TOP, widget.getAnchor());
        assertEquals(new RectF(40, 30, 80, 60), widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void layout_fillSize_fillsPaddedRect() {
        final Widget widget = new TestWidget(layoutManager,
                new Size(0, SizeMode.FILL, 0, SizeMode.FILL));
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.layout(plotDims);
        assertEquals(PADDED_RECT, widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void layout_fillSizeWithValue_subtractsValueFromPaddedRect() {
        final Widget widget = new TestWidget(layoutManager,
                new Size(10, SizeMode.FILL, 30, SizeMode.FILL));
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.layout(plotDims);
        assertEquals(new RectF(10, 20, 10 + 200 - 30, 20 + 100 - 10),
                widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void layout_relativeSize_scalesWithPaddedRect() {
        final Widget widget = new TestWidget(layoutManager,
                new Size(0.5f, SizeMode.RELATIVE, 0.25f, SizeMode.RELATIVE));
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.layout(plotDims);
        assertEquals(new RectF(10, 20, 10 + 50, 20 + 50), widget.getWidgetDimensions().canvasRect);

        // resizing the plot re-derives the widget rect:
        widget.layout(new DisplayDimensions(CANVAS_RECT, MARGINATED_RECT,
                new RectF(0, 0, 400, 400)));
        assertEquals(new RectF(0, 0, 100, 200), widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void layout_mixedSizeModes_relativeCenterAnchor() {
        // 50% wide, 20px tall, centered in the padded rect
        final Widget widget = new TestWidget(layoutManager,
                new Size(20, SizeMode.ABSOLUTE, 0.5f, SizeMode.RELATIVE));
        widget.position(0, HorizontalPositioning.RELATIVE_TO_CENTER,
                0, VerticalPositioning.RELATIVE_TO_CENTER, Anchor.CENTER);
        widget.layout(plotDims);
        assertEquals(new RectF(110 - 50, 70 - 10, 110 + 50, 70 + 10),
                widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void setWidthAndHeight_updateSizeMetrics() {
        final Widget widget = newAbsoluteWidget();
        widget.setWidth(70);
        widget.setHeight(60);
        assertEquals(70f, widget.getWidthMetric().getValue(), 0);
        assertEquals(60f, widget.getHeightMetric().getValue(), 0);
        assertEquals(SizeMode.ABSOLUTE, widget.getWidthMetric().getLayoutType());

        widget.setWidth(0.5f, SizeMode.RELATIVE);
        widget.setHeight(0, SizeMode.FILL);
        assertEquals(SizeMode.RELATIVE, widget.getWidthMetric().getLayoutType());
        assertEquals(SizeMode.FILL, widget.getHeightMetric().getLayoutType());
        assertEquals(100f, widget.getWidthPix(200), 0);
        assertEquals(100f, widget.getHeightPix(100), 0);
    }

    @Test
    public void layout_marginsAndPadding_produceMarginatedAndPaddedRects() {
        final Widget widget = newAbsoluteWidget();
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.setMargins(1, 2, 3, 4);
        widget.setPadding(5, 6, 7, 8);
        widget.layout(plotDims);

        final DisplayDimensions dims = widget.getWidgetDimensions();
        assertEquals(new RectF(10, 20, 50, 50), dims.canvasRect);
        assertEquals(new RectF(11, 22, 47, 46), dims.marginatedRect);
        assertEquals(new RectF(16, 28, 40, 38), dims.paddedRect);

        assertEquals(1f, widget.getMarginLeft(), 0);
        assertEquals(2f, widget.getMarginTop(), 0);
        assertEquals(3f, widget.getMarginRight(), 0);
        assertEquals(4f, widget.getMarginBottom(), 0);
        assertEquals(5f, widget.getPaddingLeft(), 0);
        assertEquals(6f, widget.getPaddingTop(), 0);
        assertEquals(7f, widget.getPaddingRight(), 0);
        assertEquals(8f, widget.getPaddingBottom(), 0);
    }

    @Test
    public void individualMarginAndPaddingSetters_delegateToBoxModel() {
        final Widget widget = newAbsoluteWidget();
        widget.setMarginLeft(1);
        widget.setMarginTop(2);
        widget.setMarginRight(3);
        widget.setMarginBottom(4);
        widget.setPaddingLeft(5);
        widget.setPaddingTop(6);
        widget.setPaddingRight(7);
        widget.setPaddingBottom(8);

        final RectF rect = new RectF(100, 100, 200, 200);
        assertEquals(new RectF(101, 102, 197, 196), widget.getMarginatedRect(rect));
        assertEquals(new RectF(105, 106, 193, 192), widget.getPaddedRect(rect));
    }

    @Test
    public void refreshLayout_withoutPositionMetrics_isNoOp() {
        final Widget widget = newAbsoluteWidget();
        assertNull(widget.getPositionMetrics());
        widget.layout(plotDims);
        // untouched default dims:
        assertEquals(new RectF(1, 1, 1, 1), widget.getWidgetDimensions().canvasRect);
    }

    @Test
    public void containsPoint_usesWidgetCanvasRect() {
        final Widget widget = newAbsoluteWidget();
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.layout(plotDims);
        assertTrue(widget.containsPoint(new PointF(10, 20)));
        assertTrue(widget.containsPoint(new PointF(49, 49)));
        assertFalse(widget.containsPoint(new PointF(9, 20)));
        assertFalse(widget.containsPoint(new PointF(50, 50)));
    }

    @Test
    public void calculateCoordinates_offsetsByViewRectOrigin() {
        final PositionMetrics metrics = new PositionMetrics(
                0, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                0, VerticalPositioning.ABSOLUTE_FROM_BOTTOM, Anchor.RIGHT_BOTTOM);
        final PointF coords = Widget.calculateCoordinates(30, 40, PADDED_RECT, metrics);
        assertEquals(PADDED_RECT.right - 40, coords.x, 0);
        assertEquals(PADDED_RECT.bottom - 30, coords.y, 0);
    }

    @Test
    public void getAnchorCoordinates_byOriginAndSize_matchesRectOverload() {
        for (Anchor anchor : Anchor.values()) {
            final PointF fromRect = Widget.getAnchorCoordinates(new RectF(10, 20, 50, 50), anchor);
            final PointF fromCoords = Widget.getAnchorCoordinates(10, 20, 40, 30, anchor);
            assertEquals(anchor.toString(), fromRect, fromCoords);
            final PointF offset = anchorOffset(anchor);
            assertEquals(anchor.toString(), new PointF(10 + offset.x, 20 + offset.y), fromRect);
        }
    }

    @Test
    public void draw_rotationNone_drawsUnrotatedPaddedRect() {
        final Widget widget = layoutRotated(Widget.Rotation.NONE);
        widget.draw(canvas);

        assertEquals(new RectF(10, 20, 50, 50), capturedDrawRect(widget));
        verify(canvas, never()).rotate(anyFloat(), anyFloat(), anyFloat());
        verify(canvas).save();
        verify(canvas).restore();
    }

    @Test
    public void draw_rotation90_swapsDimensionsAboutCenterAndRotatesCanvas() {
        final Widget widget = layoutRotated(Widget.Rotation.NINETY_DEGREES);
        widget.draw(canvas);

        // 40 x 30 rect centered at (30, 35) becomes 30 x 40 about the same center:
        assertEquals(new RectF(15, 15, 45, 55), capturedDrawRect(widget));
        verify(canvas).rotate(90, 30, 35);
    }

    @Test
    public void draw_rotationNegative90_swapsDimensionsAboutCenterAndRotatesCanvas() {
        final Widget widget = layoutRotated(Widget.Rotation.NEGATIVE_NINETY_DEGREES);
        widget.draw(canvas);

        assertEquals(new RectF(15, 15, 45, 55), capturedDrawRect(widget));
        verify(canvas).rotate(-90, 30, 35);
    }

    @Test
    public void draw_rotation180_keepsRectAndRotatesCanvas() {
        final Widget widget = layoutRotated(Widget.Rotation.ONE_HUNDRED_EIGHTY_DEGREES);
        widget.draw(canvas);

        assertEquals(new RectF(10, 20, 50, 50), capturedDrawRect(widget));
        verify(canvas).rotate(180, 30, 35);
    }

    @Test
    public void draw_withBackgroundAndBorderPaint_drawsBoth() {
        final Widget widget = layoutRotated(Widget.Rotation.NINETY_DEGREES);
        final Paint background = new Paint();
        final Paint border = new Paint();
        widget.setBackgroundPaint(background);
        widget.setBorderPaint(border);

        widget.draw(canvas);

        // background covers the unrotated widget rect; border follows the rotated draw rect:
        verify(canvas).drawRect(new RectF(10, 20, 50, 50), background);
        verify(canvas).drawRect(new RectF(15, 15, 45, 55), border);
    }

    @Test
    public void draw_invisible_drawsNothing() {
        final Widget widget = layoutRotated(Widget.Rotation.NONE);
        widget.setBackgroundPaint(new Paint());
        widget.setBorderPaint(new Paint());
        widget.setVisible(false);

        widget.draw(canvas);

        verify(widget, never()).doOnDraw(any(Canvas.class), any(RectF.class));
        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void draw_calledTwiceWithSameRect_invokesOnResizeOnce() {
        final Widget widget = layoutRotated(Widget.Rotation.NONE);
        widget.draw(canvas);
        widget.draw(canvas);
        verify(widget).onResize(any(), any(RectF.class));

        // a new size triggers another resize callback with the previous rect:
        widget.setWidth(80);
        widget.layout(plotDims);
        widget.draw(canvas);
        verify(widget).onResize(eq(new RectF(10, 20, 50, 50)), eq(new RectF(10, 20, 90, 50)));
    }

    @Test
    public void constructor_withSizeMetrics_buildsSize() {
        final Widget widget = new TestWidget(layoutManager,
                new SizeMetric(30, SizeMode.ABSOLUTE), new SizeMetric(0.5f, SizeMode.RELATIVE));
        assertEquals(30f, widget.getHeightMetric().getValue(), 0);
        assertEquals(SizeMode.RELATIVE, widget.getWidthMetric().getLayoutType());
        assertEquals(Widget.Rotation.NONE, widget.getRotation());
        assertFalse(widget.isClippingEnabled());
        widget.setClippingEnabled(true);
        assertTrue(widget.isClippingEnabled());
    }

    private Widget layoutRotated(Widget.Rotation rotation) {
        final Widget widget = spy(newAbsoluteWidget());
        widget.position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        widget.setRotation(rotation);
        widget.layout(plotDims);
        assertEquals(rotation, widget.getRotation());
        return widget;
    }

    private static RectF capturedDrawRect(Widget widget) {
        final ArgumentCaptor<RectF> captor = ArgumentCaptor.forClass(RectF.class);
        verify(widget).doOnDraw(any(Canvas.class), captor.capture());
        return captor.getValue();
    }

    private Widget newAbsoluteWidget() {
        return new TestWidget(layoutManager,
                new Size(WIDGET_H, SizeMode.ABSOLUTE, WIDGET_W, SizeMode.ABSOLUTE));
    }

    static class TestWidget extends Widget {
        TestWidget(@NonNull LayoutManager layoutManager, @NonNull Size size) {
            super(layoutManager, size);
        }

        TestWidget(@NonNull LayoutManager layoutManager, SizeMetric height, SizeMetric width) {
            super(layoutManager, height, width);
        }

        @Override
        protected void doOnDraw(Canvas canvas, RectF widgetRect) {
            // nothing to do
        }
    }
}
