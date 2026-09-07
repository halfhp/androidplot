// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui;

import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Value / validation semantics of the layout metric value classes:
 * {@link HorizontalPosition}, {@link VerticalPosition}, {@link SizeMetric}, {@link Size},
 * {@link PositionMetrics} and {@link BoxModel}.
 */
public class LayoutMetricsTest extends AndroidplotTest {

    private static final float SIZE = 200;

    @Test
    public void horizontalPosition_getPixelValue_perPositioning() {
        assertEquals(30f, new HorizontalPosition(30, HorizontalPositioning.ABSOLUTE_FROM_LEFT)
                .getPixelValue(SIZE), 0);
        assertEquals(170f, new HorizontalPosition(30, HorizontalPositioning.ABSOLUTE_FROM_RIGHT)
                .getPixelValue(SIZE), 0);
        assertEquals(130f, new HorizontalPosition(30, HorizontalPositioning.ABSOLUTE_FROM_CENTER)
                .getPixelValue(SIZE), 0);
        assertEquals(70f, new HorizontalPosition(-30, HorizontalPositioning.ABSOLUTE_FROM_CENTER)
                .getPixelValue(SIZE), 0);
        assertEquals(50f, new HorizontalPosition(0.25f, HorizontalPositioning.RELATIVE_TO_LEFT)
                .getPixelValue(SIZE), 0);
        assertEquals(150f, new HorizontalPosition(-0.25f, HorizontalPositioning.RELATIVE_TO_RIGHT)
                .getPixelValue(SIZE), 0);
        assertEquals(200f, new HorizontalPosition(0, HorizontalPositioning.RELATIVE_TO_RIGHT)
                .getPixelValue(SIZE), 0);
        assertEquals(125f, new HorizontalPosition(0.25f, HorizontalPositioning.RELATIVE_TO_CENTER)
                .getPixelValue(SIZE), 0);
        assertEquals(0f, new HorizontalPosition(-1, HorizontalPositioning.RELATIVE_TO_CENTER)
                .getPixelValue(SIZE), 0);
        assertEquals(200f, new HorizontalPosition(1, HorizontalPositioning.RELATIVE_TO_CENTER)
                .getPixelValue(SIZE), 0);
    }

    @Test
    public void verticalPosition_getPixelValue_perPositioning() {
        assertEquals(30f, new VerticalPosition(30, VerticalPositioning.ABSOLUTE_FROM_TOP)
                .getPixelValue(SIZE), 0);
        assertEquals(170f, new VerticalPosition(30, VerticalPositioning.ABSOLUTE_FROM_BOTTOM)
                .getPixelValue(SIZE), 0);
        assertEquals(130f, new VerticalPosition(30, VerticalPositioning.ABSOLUTE_FROM_CENTER)
                .getPixelValue(SIZE), 0);
        assertEquals(50f, new VerticalPosition(0.25f, VerticalPositioning.RELATIVE_TO_TOP)
                .getPixelValue(SIZE), 0);
        assertEquals(150f, new VerticalPosition(-0.25f, VerticalPositioning.RELATIVE_TO_BOTTOM)
                .getPixelValue(SIZE), 0);
        assertEquals(125f, new VerticalPosition(0.25f, VerticalPositioning.RELATIVE_TO_CENTER)
                .getPixelValue(SIZE), 0);
    }

    @Test
    public void positionMetric_relativeValueOutOfRange_throws() {
        for (HorizontalPositioning hp : new HorizontalPositioning[]{
                HorizontalPositioning.RELATIVE_TO_LEFT,
                HorizontalPositioning.RELATIVE_TO_RIGHT,
                HorizontalPositioning.RELATIVE_TO_CENTER}) {
            assertThrowsIAE(hp + " 1.01", () -> new HorizontalPosition(1.01f, hp));
            assertThrowsIAE(hp + " -1.01", () -> new HorizontalPosition(-1.01f, hp));
            // boundaries are inclusive:
            new HorizontalPosition(1, hp);
            new HorizontalPosition(-1, hp);
        }
        for (VerticalPositioning vp : new VerticalPositioning[]{
                VerticalPositioning.RELATIVE_TO_TOP,
                VerticalPositioning.RELATIVE_TO_BOTTOM,
                VerticalPositioning.RELATIVE_TO_CENTER}) {
            assertThrowsIAE(vp + " 1.01", () -> new VerticalPosition(1.01f, vp));
            assertThrowsIAE(vp + " -1.01", () -> new VerticalPosition(-1.01f, vp));
            new VerticalPosition(1, vp);
            new VerticalPosition(-1, vp);
        }
    }

    @Test
    public void positionMetric_absoluteValues_unrestricted() {
        for (HorizontalPositioning hp : new HorizontalPositioning[]{
                HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                HorizontalPositioning.ABSOLUTE_FROM_CENTER}) {
            new HorizontalPosition(5000, hp);
            new HorizontalPosition(-5000, hp);
        }
        for (VerticalPositioning vp : new VerticalPositioning[]{
                VerticalPositioning.ABSOLUTE_FROM_TOP,
                VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                VerticalPositioning.ABSOLUTE_FROM_CENTER}) {
            new VerticalPosition(5000, vp);
            new VerticalPosition(-5000, vp);
        }
    }

    @Test
    public void positionMetric_setters_revalidateAndUpdate() {
        final HorizontalPosition hpos = new HorizontalPosition(50, HorizontalPositioning.ABSOLUTE_FROM_LEFT);

        // switching to a relative mode with an out of range value must fail and leave state intact:
        assertThrowsIAE("setLayoutType", () -> hpos.setLayoutType(HorizontalPositioning.RELATIVE_TO_LEFT));
        assertEquals(HorizontalPositioning.ABSOLUTE_FROM_LEFT, hpos.getLayoutType());
        assertEquals(50f, hpos.getValue(), 0);

        hpos.setValue(0.5f);
        hpos.setLayoutType(HorizontalPositioning.RELATIVE_TO_LEFT);
        assertEquals(100f, hpos.getPixelValue(SIZE), 0);

        assertThrowsIAE("setValue", () -> hpos.setValue(2));
        assertEquals(0.5f, hpos.getValue(), 0);

        hpos.set(20, HorizontalPositioning.ABSOLUTE_FROM_RIGHT);
        assertEquals(180f, hpos.getPixelValue(SIZE), 0);
        assertThrowsIAE("set", () -> hpos.set(2, HorizontalPositioning.RELATIVE_TO_CENTER));

        final VerticalPosition vpos = new VerticalPosition(50, VerticalPositioning.ABSOLUTE_FROM_TOP);
        assertThrowsIAE("setLayoutType", () -> vpos.setLayoutType(VerticalPositioning.RELATIVE_TO_TOP));
        vpos.setValue(-0.5f);
        vpos.setLayoutType(VerticalPositioning.RELATIVE_TO_BOTTOM);
        assertEquals(100f, vpos.getPixelValue(SIZE), 0);
        assertThrowsIAE("setValue", () -> vpos.setValue(-2));
    }

    @Test
    public void sizeMetric_getPixelValue_perMode() {
        assertEquals(30f, new SizeMetric(30, SizeMode.ABSOLUTE).getPixelValue(SIZE), 0);
        assertEquals(30f, new SizeMetric(30, SizeMode.ABSOLUTE).getPixelValue(0), 0);
        assertEquals(50f, new SizeMetric(0.25f, SizeMode.RELATIVE).getPixelValue(SIZE), 0);
        assertEquals(0f, new SizeMetric(0, SizeMode.RELATIVE).getPixelValue(SIZE), 0);
        assertEquals(200f, new SizeMetric(1, SizeMode.RELATIVE).getPixelValue(SIZE), 0);
        assertEquals(200f, new SizeMetric(0, SizeMode.FILL).getPixelValue(SIZE), 0);
        assertEquals(170f, new SizeMetric(30, SizeMode.FILL).getPixelValue(SIZE), 0);
    }

    @Test
    public void sizeMetric_relativeOutOfRange_throws() {
        assertThrowsIAE("1.01", () -> new SizeMetric(1.01f, SizeMode.RELATIVE));
        assertThrowsIAE("-0.01", () -> new SizeMetric(-0.01f, SizeMode.RELATIVE));

        final SizeMetric metric = new SizeMetric(5, SizeMode.ABSOLUTE);
        assertThrowsIAE("setLayoutType", () -> metric.setLayoutType(SizeMode.RELATIVE));
        assertEquals(SizeMode.ABSOLUTE, metric.getLayoutType());
        metric.setLayoutType(SizeMode.FILL);
        assertEquals(195f, metric.getPixelValue(SIZE), 0);
        assertThrowsIAE("set", () -> metric.set(5, SizeMode.RELATIVE));
        metric.set(0.5f, SizeMode.RELATIVE);
        assertEquals(100f, metric.getPixelValue(SIZE), 0);
        assertThrowsIAE("setValue", () -> metric.setValue(5));

        // absolute and fill accept anything, including negatives:
        new SizeMetric(-5, SizeMode.ABSOLUTE);
        new SizeMetric(-5, SizeMode.FILL);
    }

    @Test
    public void size_getRectF_appliesEachMetricToMatchingCanvasDimension() {
        final RectF canvas = new RectF(50, 60, 250, 160); // 200 x 100
        assertEquals(new RectF(0, 0, 40, 30),
                new Size(30, SizeMode.ABSOLUTE, 40, SizeMode.ABSOLUTE).getRectF(canvas));
        assertEquals(new RectF(0, 0, 100, 25),
                new Size(0.25f, SizeMode.RELATIVE, 0.5f, SizeMode.RELATIVE).getRectF(canvas));
        assertEquals(new RectF(0, 0, 190, 90),
                new Size(10, SizeMode.FILL, 10, SizeMode.FILL).getRectF(canvas));
        assertEquals(new RectF(0, 0, 200, 100), Size.FILL.getRectF(canvas));
    }

    @Test
    public void size_setters_replaceMetrics() {
        final Size size = new Size(1, SizeMode.ABSOLUTE, 2, SizeMode.ABSOLUTE);
        final SizeMetric height = new SizeMetric(0.5f, SizeMode.RELATIVE);
        final SizeMetric width = new SizeMetric(0, SizeMode.FILL);
        size.setHeight(height);
        size.setWidth(width);
        assertSame(height, size.getHeight());
        assertSame(width, size.getWidth());

        final Size fromMetrics = new Size(height, width);
        assertSame(height, fromMetrics.getHeight());
        assertSame(width, fromMetrics.getWidth());
    }

    @Test
    public void positionMetrics_constructorAndSetters() {
        final PositionMetrics metrics = new PositionMetrics(
                10, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                0.5f, VerticalPositioning.RELATIVE_TO_TOP, Anchor.RIGHT_MIDDLE);
        assertEquals(10f, metrics.getXPositionMetric().getValue(), 0);
        assertEquals(HorizontalPositioning.ABSOLUTE_FROM_RIGHT, metrics.getXPositionMetric().getLayoutType());
        assertEquals(0.5f, metrics.getYPositionMetric().getValue(), 0);
        assertEquals(VerticalPositioning.RELATIVE_TO_TOP, metrics.getYPositionMetric().getLayoutType());
        assertEquals(Anchor.RIGHT_MIDDLE, metrics.getAnchor());

        final HorizontalPosition hpos = new HorizontalPosition(1, HorizontalPositioning.ABSOLUTE_FROM_LEFT);
        final VerticalPosition vpos = new VerticalPosition(2, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        metrics.setXPositionMetric(hpos);
        metrics.setYPositionMetric(vpos);
        metrics.setAnchor(Anchor.CENTER);
        assertSame(hpos, metrics.getXPositionMetric());
        assertSame(vpos, metrics.getYPositionMetric());
        assertEquals(Anchor.CENTER, metrics.getAnchor());

        // layer depth is never set, so all instances compare equal:
        assertEquals(0, metrics.compareTo(new PositionMetrics(
                0, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP)));
    }

    @Test
    public void boxModel_defaultConstructor_isZeroEverywhere() {
        final BoxModel model = new BoxModel();
        final RectF rect = new RectF(10, 20, 110, 220);
        assertEquals(rect, model.getMarginatedRect(rect));
        assertEquals(rect, model.getPaddedRect(rect));
    }

    @Test
    public void boxModel_fullConstructor_insetsRects() {
        final BoxModel model = new BoxModel(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(1f, model.getMarginLeft(), 0);
        assertEquals(2f, model.getMarginTop(), 0);
        assertEquals(3f, model.getMarginRight(), 0);
        assertEquals(4f, model.getMarginBottom(), 0);
        assertEquals(5f, model.getPaddingLeft(), 0);
        assertEquals(6f, model.getPaddingTop(), 0);
        assertEquals(7f, model.getPaddingRight(), 0);
        assertEquals(8f, model.getPaddingBottom(), 0);

        final RectF rect = new RectF(10, 20, 110, 220);
        final RectF marginated = model.getMarginatedRect(rect);
        assertEquals(new RectF(11, 22, 107, 216), marginated);
        assertEquals(new RectF(16, 28, 100, 208), model.getPaddedRect(marginated));
    }

    @Test
    public void boxModel_setMarginsAndPadding_orderIsLeftTopRightBottom() {
        final BoxModel model = new BoxModel();
        model.setMargins(1, 2, 3, 4);
        model.setPadding(5, 6, 7, 8);
        assertEquals(1f, model.getMarginLeft(), 0);
        assertEquals(2f, model.getMarginTop(), 0);
        assertEquals(3f, model.getMarginRight(), 0);
        assertEquals(4f, model.getMarginBottom(), 0);
        assertEquals(5f, model.getPaddingLeft(), 0);
        assertEquals(6f, model.getPaddingTop(), 0);
        assertEquals(7f, model.getPaddingRight(), 0);
        assertEquals(8f, model.getPaddingBottom(), 0);
    }

    @Test
    public void insets_constructorOrderIsTopBottomLeftRight() {
        final Insets insets = new Insets(1, 2, 3, 4);
        assertEquals(1f, insets.getTop(), 0);
        assertEquals(2f, insets.getBottom(), 0);
        assertEquals(3f, insets.getLeft(), 0);
        assertEquals(4f, insets.getRight(), 0);
    }

    private static void assertThrowsIAE(String message, Runnable runnable) {
        try {
            runnable.run();
            fail("expected IllegalArgumentException: " + message);
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }
}
