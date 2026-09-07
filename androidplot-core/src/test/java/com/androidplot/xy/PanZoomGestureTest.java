// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import android.annotation.SuppressLint;
import android.view.InputDevice;
import android.view.MotionEvent;

import com.androidplot.test.AndroidplotTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Drives {@link PanZoom} with real {@link MotionEvent} sequences against a real, laid out
 * {@link XYPlot} and checks the resulting plot bounds.
 *
 * The plot is 1000x500 px showing a domain of 0..100 and a range of 0..50, so one pixel of drag
 * corresponds to 0.1 units on either axis.  Note that {@link XYPlot#getBounds()} only reflects
 * boundary changes after {@link XYPlot#calculateMinMaxVals()}, which the render pass normally
 * does; the gesture helpers here call it explicitly after each gesture.
 */
public class PanZoomGestureTest extends AndroidplotTest {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 500;
    private static final double DELTA = 1e-6;

    private static final int SECOND_POINTER_DOWN =
            MotionEvent.ACTION_POINTER_DOWN | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
    private static final int SECOND_POINTER_UP =
            MotionEvent.ACTION_POINTER_UP | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT);

    private XYPlot plot;
    private PanZoom panZoom;

    @Before
    public void setUp() {
        plot = new XYPlot(getContext(), "p");
        plot.layout(0, 0, WIDTH, HEIGHT);
        // the FIXED boundaries make the data irrelevant to the bounds, but a plot with no series
        // skips the outer-limit clamp on its range axis in calculateMinMaxVals:
        plot.addSeries(new SimpleXYSeries(Arrays.asList(1, 4, 2, 8, 5),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "series"), new LineAndPointFormatter());
        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 50, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
    }

    private PanZoom attach(PanZoom.Pan pan, PanZoom.Zoom zoom) {
        panZoom = PanZoom.attach(plot, pan, zoom);
        return panZoom;
    }

    private PanZoom attach(PanZoom.Pan pan, PanZoom.Zoom zoom, PanZoom.ZoomLimit limit) {
        panZoom = PanZoom.attach(plot, pan, zoom, limit);
        return panZoom;
    }

    // ---------------------------------------------------------------------------------------
    // pan
    // ---------------------------------------------------------------------------------------

    @Test
    public void horizontalPan_movesDomainByPixelsTimesSpanOverWidth() {
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);

        // dragging the finger 100px to the left pulls the content left, revealing higher x:
        drag(500, 250, 400, 250);
        assertBounds(10, 110, 0, 50);

        // and back to the right, a different distance:
        drag(400, 250, 650, 250);
        assertBounds(-15, 85, 0, 50);
    }

    @Test
    public void horizontalPan_ignoresVerticalMovement() {
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);
        drag(500, 250, 500, 100);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void verticalPan_movesRangeByPixelsTimesSpanOverHeight() {
        attach(PanZoom.Pan.VERTICAL, PanZoom.Zoom.NONE);

        // dragging the finger 50px down pulls the content down, revealing higher y:
        drag(500, 250, 500, 300);
        assertBounds(0, 100, 5, 55);

        // horizontal movement is ignored:
        drag(500, 300, 100, 300);
        assertBounds(0, 100, 5, 55);
    }

    @Test
    public void panBoth_movesBothAxes() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.NONE);
        drag(500, 250, 400, 300);
        assertBounds(10, 110, 5, 55);
    }

    @Test
    public void panNone_ignoresDrags() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.NONE);
        drag(500, 250, 100, 100);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void pan_accumulatesOverMultipleMoveEvents() {
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);
        touch(down(500, 250));
        touch(move(450, 250));
        plot.calculateMinMaxVals();
        assertBounds(5, 105, 0, 50);

        touch(move(400, 250));
        plot.calculateMinMaxVals();
        assertBounds(10, 110, 0, 50);

        touch(up(400, 250));
        plot.calculateMinMaxVals();
        assertBounds(10, 110, 0, 50);
    }

    @Test
    public void pan_isClampedToOuterLimitsAndPreservesWindowWidth() {
        plot.setDomainBoundaries(20, 40, BoundaryMode.FIXED);
        plot.setRangeBoundaries(10, 20, BoundaryMode.FIXED);
        plot.getOuterLimits().set(0, 50, 0, 25);
        plot.calculateMinMaxVals();
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.NONE);

        // an unclamped drag first, to prove the limits are not interfering:
        drag(500, 250, 400, 300);
        assertBounds(22, 42, 11, 21);

        // a drag far past the upper limits on both axes (finger left and down):
        drag(1000, 0, 0, 500);
        assertBounds(30, 50, 15, 25);

        // and far past the lower limits (the finger may leave the view):
        drag(0, 500, 2000, -500);
        assertBounds(0, 20, 0, 10);
    }

    @Test
    public void pan_withWindowWiderThanOuterLimits_snapsToLimitsWithoutThrowing() {
        // the demo app's old default: outer limits narrower than the FIXED window
        plot.getOuterLimits().set(20, 80, 10, 40);
        plot.calculateMinMaxVals();
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.NONE);

        // PanZoom snaps the upper edge to the limit (the window is too wide to satisfy both
        // edges) and the plot then clamps the overshooting lower edge on its next pass:
        drag(500, 250, 400, 300);
        assertBounds(20, 80, 10, 40);

        // and again, now that the window matches the limits exactly:
        drag(400, 300, 500, 250);
        assertBounds(20, 80, 10, 40);
    }

    @Test
    public void pan_onLargeMagnitudeDomain_keepsPrecision() {
        final double minX = 1.7e12;
        final double maxX = minX + 60000;
        plot.setDomainBoundaries(minX, maxX, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);

        // 60 units per pixel; 10px must move the window by exactly 600, an offset that float
        // arithmetic cannot represent at this magnitude (the float ulp of 1.7e12 is 131072).
        drag(500, 250, 490, 250);
        assertEquals(minX + 600, plot.getBounds().getMinX().doubleValue(), 1);
        assertEquals(maxX + 600, plot.getBounds().getMaxX().doubleValue(), 1);
        assertEquals(60000, plot.getBounds().getxRegion().length().doubleValue(), 1);
    }

    // ---------------------------------------------------------------------------------------
    // zoom
    // ---------------------------------------------------------------------------------------

    @Test
    public void stretchHorizontal_pinchOutNarrowsDomainOnly() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_HORIZONTAL);
        pinch(400, 250, 600, 250, 300, 250, 700, 250);
        assertBounds(25, 75, 0, 50);
    }

    @Test
    public void stretchHorizontal_pinchInWidensDomainOnly() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_HORIZONTAL);
        pinch(300, 250, 700, 250, 400, 250, 600, 250);
        assertBounds(-50, 150, 0, 50);
    }

    @Test
    public void stretchHorizontal_ignoresVerticalFingerMovement() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_HORIZONTAL);
        pinch(400, 200, 600, 300, 400, 100, 600, 400);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void stretchVertical_pinchOutNarrowsRangeOnly() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_VERTICAL);
        pinch(400, 200, 600, 300, 400, 150, 600, 350);
        assertBounds(0, 100, 12.5, 37.5);
    }

    @Test
    public void stretchVertical_pinchInWidensRangeOnly() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_VERTICAL);
        pinch(400, 150, 600, 350, 400, 200, 600, 300);
        assertBounds(0, 100, -25, 75);
    }

    @Test
    public void stretchBoth_scalesEachAxisByItsOwnFingerDistance() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_BOTH);

        // x distance doubles, y distance quadruples:
        pinch(400, 200, 600, 300, 300, 50, 700, 450);
        assertBounds(25, 75, 18.75, 31.25);
    }

    @Test
    public void stretchBoth_pinchInWidensBothAxes() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.STRETCH_BOTH);
        pinch(300, 150, 700, 350, 400, 200, 600, 300);
        assertBounds(-50, 150, -25, 75);
    }

    @Test
    public void scale_pinchOutNarrowsBothAxesByTheSameFactor() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE);

        // a purely horizontal pinch tripling the finger distance still scales the range:
        pinch(400, 250, 600, 250, 200, 250, 800, 250);
        assertBounds(100 / 3.0, 200 / 3.0, 50 / 3.0, 100 / 3.0);
    }

    @Test
    public void scale_pinchInWidensBothAxesByTheSameFactor() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE);
        pinch(200, 250, 800, 250, 400, 250, 600, 250);
        assertBounds(-100, 200, -50, 100);
    }

    @Test
    public void zoomNone_ignoresPinch() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.NONE);
        pinch(400, 250, 600, 250, 300, 250, 700, 250);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void zoom_isClampedToOuterLimits() {
        plot.getOuterLimits().set(-20, 120, 0, 50);
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE);
        pinch(400, 250, 600, 250, 480, 250, 520, 250);
        assertBounds(-20, 120, 0, 50);
    }

    @Test
    public void zoomLimitMinTicks_stopsZoomInAtOneStep() {
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 10);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 4);
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE, PanZoom.ZoomLimit.MIN_TICKS);

        // a 50x pinch-out would leave a 2 wide domain and a 1 tall range; instead each axis
        // stops at exactly one step centered on its midpoint:
        pinch(490, 250, 510, 250, 0, 250, 1000, 250);
        assertBounds(45, 55, 23, 27);

        // a further pinch-out stays there:
        pinch(400, 250, 600, 250, 100, 250, 900, 250);
        assertBounds(45, 55, 23, 27);

        // zooming out is unaffected:
        pinch(400, 250, 600, 250, 450, 250, 550, 250);
        assertBounds(40, 60, 21, 29);
    }

    @Test
    public void zoomLimitOuter_allowsZoomInPastOneStep() {
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 10);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 4);
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE, PanZoom.ZoomLimit.OUTER);
        pinch(490, 250, 510, 250, 0, 250, 1000, 250);
        assertBounds(49, 51, 24.5, 25.5);
    }

    @Test
    public void pinch_withFingersTooCloseTogether_doesNotZoom() {
        attach(PanZoom.Pan.NONE, PanZoom.Zoom.SCALE);
        // second finger lands within MIN_DIST_2_FING horizontally, so no zoom state is entered:
        pinch(500, 250, 503, 250, 100, 250, 900, 250);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void oneFingerDragAfterPinch_pansFromReleasedPosition() {
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        pinch(400, 250, 600, 250, 300, 250, 700, 250);
        assertBounds(25, 75, 0, 50);

        // a new one finger gesture pans the zoomed window; 100px is now 5 units:
        drag(500, 250, 400, 250);
        assertBounds(30, 80, 0, 50);
    }

    // ---------------------------------------------------------------------------------------
    // state and delegation
    // ---------------------------------------------------------------------------------------

    @Test
    public void getState_setState_roundTripAfterGesture() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);
        drag(500, 250, 400, 300);
        pinch(400, 250, 600, 250, 200, 250, 800, 250);
        assertBounds(60 - 100 / 6.0, 60 + 100 / 6.0, 30 - 25 / 3.0, 30 + 25 / 3.0);
        PanZoom.State state = panZoom.getState();

        plot.setDomainBoundaries(-1000, 1000, BoundaryMode.FIXED);
        plot.setRangeBoundaries(-1000, 1000, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertBounds(-1000, 1000, -1000, 1000);

        panZoom.setState(state);
        plot.calculateMinMaxVals();
        assertBounds(60 - 100 / 6.0, 60 + 100 / 6.0, 30 - 25 / 3.0, 30 + 25 / 3.0);
    }

    @Test
    public void delegate_consumingEvents_blocksPanning() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);
        final List<MotionEvent> seen = new ArrayList<>();
        panZoom.setDelegate((v, event) -> {
            seen.add(event);
            return true;
        });

        drag(500, 250, 400, 300);
        assertBounds(0, 100, 0, 50);
        pinch(400, 250, 600, 250, 300, 250, 700, 250);
        assertBounds(0, 100, 0, 50);
        assertEquals(3 + 5, seen.size());
    }

    @Test
    public void delegate_notConsumingEvents_allowsPanning() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);
        final List<MotionEvent> seen = new ArrayList<>();
        panZoom.setDelegate((v, event) -> {
            seen.add(event);
            return false;
        });

        drag(500, 250, 400, 300);
        assertBounds(10, 110, 5, 55);
        assertEquals(3, seen.size());
    }

    @Test
    public void disabled_ignoresGestures() {
        attach(PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);
        panZoom.setEnabled(false);
        drag(500, 250, 400, 300);
        pinch(400, 250, 600, 250, 300, 250, 700, 250);
        assertBounds(0, 100, 0, 50);
    }

    @Test
    public void attach_registersAsThePlotsTouchListener() {
        attach(PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);
        assertTrue(plot.dispatchTouchEvent(down(500, 250)));
        assertTrue(plot.dispatchTouchEvent(move(400, 250)));
        assertTrue(plot.dispatchTouchEvent(up(400, 250)));
        plot.calculateMinMaxVals();
        assertBounds(10, 110, 0, 50);
    }

    // ---------------------------------------------------------------------------------------
    // gesture helpers
    // ---------------------------------------------------------------------------------------

    private void touch(MotionEvent event) {
        panZoom.onTouch(plot, event);
    }

    /** A complete one finger drag: down at (fromX, fromY), one move to (toX, toY), up. */
    private void drag(float fromX, float fromY, float toX, float toY) {
        touch(down(fromX, fromY));
        touch(move(toX, toY));
        touch(up(toX, toY));
        plot.calculateMinMaxVals();
    }

    /**
     * A complete two finger gesture: the first finger lands at (x1, y1), the second at (x2, y2),
     * both move to (x1b, y1b) / (x2b, y2b), then lift.
     */
    private void pinch(float x1, float y1, float x2, float y2,
                       float x1b, float y1b, float x2b, float y2b) {
        touch(down(x1, y1));
        touch(twoFingers(SECOND_POINTER_DOWN, x1, y1, x2, y2));
        touch(twoFingers(MotionEvent.ACTION_MOVE, x1b, y1b, x2b, y2b));
        touch(twoFingers(SECOND_POINTER_UP, x1b, y1b, x2b, y2b));
        touch(up(x1b, y1b));
        plot.calculateMinMaxVals();
    }

    private static MotionEvent down(float x, float y) {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, x, y, 0);
    }

    private static MotionEvent move(float x, float y) {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, x, y, 0);
    }

    private static MotionEvent up(float x, float y) {
        return MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, x, y, 0);
    }

    @SuppressLint("NewApi")
    private static MotionEvent twoFingers(int action, float x1, float y1, float x2, float y2) {
        MotionEvent.PointerProperties[] properties = {pointer(0), pointer(1)};
        MotionEvent.PointerCoords[] coords = {coords(x1, y1), coords(x2, y2)};
        return MotionEvent.obtain(0, 0, action, 2, properties, coords,
                0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
    }

    @SuppressLint("NewApi")
    private static MotionEvent.PointerProperties pointer(int id) {
        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
        properties.id = id;
        properties.toolType = MotionEvent.TOOL_TYPE_FINGER;
        return properties;
    }

    @SuppressLint("NewApi")
    private static MotionEvent.PointerCoords coords(float x, float y) {
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.x = x;
        coords.y = y;
        coords.pressure = 1f;
        coords.size = 1f;
        return coords;
    }

    private void assertBounds(double minX, double maxX, double minY, double maxY) {
        RectRegion bounds = plot.getBounds();
        assertEquals("minX", minX, bounds.getMinX().doubleValue(), DELTA);
        assertEquals("maxX", maxX, bounds.getMaxX().doubleValue(), DELTA);
        assertEquals("minY", minY, bounds.getMinY().doubleValue(), DELTA);
        assertEquals("maxY", maxY, bounds.getMaxY().doubleValue(), DELTA);
    }
}
