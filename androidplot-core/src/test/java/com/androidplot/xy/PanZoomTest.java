// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.res.*;
import android.graphics.*;
import android.view.*;

import com.androidplot.Region;
import com.androidplot.test.*;
import com.androidplot.ui.*;
import com.androidplot.util.*;

import org.junit.*;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PanZoomTest extends AndroidplotTest {

    @Mock
    LayoutManager layoutManager;

    @Mock
    XYPlot xyPlot;

    @Mock
    TypedArray typedArray;

    @Mock
    XYSeriesRegistry seriesRegistry;

    RectRegion bounds = new RectRegion(0, 100, 0, 100);

    @Before
    public void setUp() throws Exception {
        when(xyPlot.getRegistry()).thenReturn(seriesRegistry);
        when(xyPlot.getBounds()).thenReturn(bounds);
        when(xyPlot.getInnerLimits()).thenReturn(new RectRegion());
        when(xyPlot.getOuterLimits()).thenReturn(new RectRegion());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnTouch_notifiesOnTouchListener() throws Exception {
        PanZoom panZoom = new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE);

        View.OnTouchListener listener = mock(View.OnTouchListener.class);
        panZoom.setDelegate(listener);

        MotionEvent motionEvent = mock(MotionEvent.class);
        panZoom.onTouch(xyPlot, motionEvent);

        verify(listener, times(1)).onTouch(xyPlot, motionEvent);
    }

    @Test
    public void testOnTouch_oneFingerMovePansButDoesNotZoom() throws Exception {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));

        View.OnTouchListener listener = mock(View.OnTouchListener.class);
        panZoom.setDelegate(listener);

        MotionEvent moveEvent = mock(MotionEvent.class);

        doNothing().when(panZoom).calculatePan(
                any(PointF.class), any(Region.class), anyBoolean());

        when(moveEvent.getAction())
                .thenReturn(MotionEvent.ACTION_DOWN)
                .thenReturn(MotionEvent.ACTION_MOVE)
                .thenReturn(MotionEvent.ACTION_UP);

        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_MOVE
        panZoom.onTouch(xyPlot, moveEvent); // fires ACTION_UP

        verify(panZoom).pan(moveEvent);
        verify(panZoom, never()).zoom(moveEvent);
        verify(panZoom).reset();
    }

    @Test
    public void testOnTouch_twoFingersZoom() throws Exception {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));
        MotionEvent moveEvent = mock(MotionEvent.class);

        // simulate a zoom gesture sequence:
        when(moveEvent.getAction())
                .thenReturn(MotionEvent.ACTION_DOWN)
                .thenReturn(MotionEvent.ACTION_POINTER_DOWN)
                .thenReturn(MotionEvent.ACTION_MOVE)
                .thenReturn(MotionEvent.ACTION_UP);

        when(panZoom.fingerDistance(moveEvent))
                .thenReturn(new RectF(0, 0, 10, 10))
                .thenReturn(new RectF(0, 0, 11, 11))
                .thenReturn(new RectF(0, 0, 12, 12))
                .thenReturn(new RectF(0, 0, 13, 13));

        panZoom.onTouch(xyPlot, moveEvent); // ACTION_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_POINTER_DOWN
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_MOVE
        panZoom.onTouch(xyPlot, moveEvent); // ACTION_UP

        verify(xyPlot).redraw();
        verify(panZoom, never()).pan(any(MotionEvent.class));
        verify(panZoom).zoom(moveEvent);
        verify(panZoom).reset();
    }

    @Test
    public void testZoom() {
        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        xyPlot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.redraw();

        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));

        // cap our pan/zoom boundaries:
        xyPlot.getOuterLimits().set(0, 100, 0, 100);

        panZoom.setFingersRect(new RectF(0, 0, 20, 20));

        InOrder inOrder = inOrder(xyPlot);
        inOrder.verify(xyPlot).setDomainBoundaries(0, 100, BoundaryMode.FIXED);

        // should result in a 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 40, 40));
        inOrder.verify(xyPlot).setDomainBoundaries(25.0, 75.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(25.0, 75.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // should result in another 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 80, 80));
        inOrder.verify(xyPlot).setDomainBoundaries(37.5, 62.5, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(37.5, 62.5, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // should zoom out and take us back to the original bounds:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 20, 20));
        inOrder.verify(xyPlot).setDomainBoundaries(0.0, 100.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(0.0, 100.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // zooming out past capped bounds should not result in any change:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 1, 1));
        inOrder.verify(xyPlot).setDomainBoundaries(0.0, 100.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(0.0, 100.0, BoundaryMode.FIXED);
        // TODO: if nothing changed, then why bother redrawing??
        inOrder.verify(xyPlot).redraw();

        // redraw should not be called again
        inOrder.verify(xyPlot, never()).redraw();

        // make sure no panning took place during these zoom ops:
        verify(panZoom, never()).pan(any(MotionEvent.class));

    }

    @Test
    public void testLimitZoom() {
        double[] inc_domain = new double[]{10,50,100};
        double[] inc_range = new double[]{20,50};

        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        xyPlot.setDomainBoundaries(0, 20, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 30, BoundaryMode.FIXED);
        xyPlot.setDomainStepModel(new StepModelFit(xyPlot.getBounds().getxRegion(), inc_domain, 5));
        xyPlot.setRangeStepModel(new StepModelFit(xyPlot.getBounds().getyRegion(), inc_range, 5));
        xyPlot.redraw();

        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE, PanZoom.ZoomLimit.MIN_TICKS));

        // cap our pan/zoom boundaries:
        xyPlot.getOuterLimits().set(0, 20, 0, 30);

        panZoom.setFingersRect(new RectF(0, 0, 20, 20));

        InOrder inOrder = inOrder(xyPlot);
        inOrder.verify(xyPlot).setDomainBoundaries(0, 20, BoundaryMode.FIXED);

        // should NOT result in a 2x zoom on domain centerpoint, but in a zoom to
        // the minimum spacing 10 and 20 respectively
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 40, 40));
        inOrder.verify(xyPlot).setDomainBoundaries(5.0, 15.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(5.0, 25.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // to zoom in beyond min limits
        panZoom.setZoomLimit(PanZoom.ZoomLimit.OUTER);

        // should result in another 2x zoom on domain centerpoint:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 80, 80));
        inOrder.verify(xyPlot).setDomainBoundaries(7.5, 12.5, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(10.0, 20.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // back to limited zoom
        panZoom.setZoomLimit(PanZoom.ZoomLimit.MIN_TICKS);

        // try to zoom in further, should snap back to min limit:
        panZoom.zoom(TestUtils.newPointerDownEvent(0, 0, 90, 90));
        inOrder.verify(xyPlot).setDomainBoundaries(5.0, 15.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).setRangeBoundaries(5.0, 25.0, BoundaryMode.FIXED);
        inOrder.verify(xyPlot).redraw();

        // redraw should not be called again
        inOrder.verify(xyPlot, never()).redraw();

        // make sure no panning took place during these zoom ops:
        verify(panZoom, never()).pan(any(MotionEvent.class));

    }

    /**
     * A 1000px wide plot showing 60 seconds of epoch milliseconds.  Dragging one finger 10px to the
     * left must shift the domain window right by exactly 600ms; float arithmetic cannot represent
     * that offset at this magnitude (the float ulp of 1.7e12 is 131072).
     */
    @Test
    public void testPan_keepsPrecisionOnLargeMagnitudeDomain() {
        final double minX = 1.7e12;
        final double maxX = minX + 60000;
        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        doReturn(1000).when(xyPlot).getWidth();
        doReturn(500).when(xyPlot).getHeight();
        xyPlot.setDomainBoundaries(minX, maxX, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.redraw();

        PanZoom panZoom = new PanZoom(xyPlot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);
        panZoom.onTouch(xyPlot, MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100, 50, 0));
        panZoom.onTouch(xyPlot, MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 90, 50, 0));

        ArgumentCaptor<Number> lower = ArgumentCaptor.forClass(Number.class);
        ArgumentCaptor<Number> upper = ArgumentCaptor.forClass(Number.class);
        verify(xyPlot, times(2)).setDomainBoundaries(
                lower.capture(), upper.capture(), eq(BoundaryMode.FIXED));
        assertEquals(minX + 600, lower.getValue().doubleValue(), 1);
        assertEquals(maxX + 600, upper.getValue().doubleValue(), 1);
    }

    /**
     * Outer limits defining only a range (y) constraint must not clamp a horizontal pan.
     */
    @Test
    public void testPan_horizontalIgnoresRangeOnlyOuterLimits() {
        xyPlot = spy(new InstrumentedXYPlot(getContext()));
        doReturn(1000).when(xyPlot).getWidth();
        doReturn(500).when(xyPlot).getHeight();
        xyPlot.setDomainBoundaries(1000, 1100, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        xyPlot.redraw();

        // only y limits are defined; the domain window lies entirely outside of them:
        xyPlot.getOuterLimits().setMinY(0);
        xyPlot.getOuterLimits().setMaxY(100);

        PanZoom panZoom = new PanZoom(xyPlot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);
        panZoom.onTouch(xyPlot, MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100, 50, 0));
        panZoom.onTouch(xyPlot, MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 90, 50, 0));

        ArgumentCaptor<Number> lower = ArgumentCaptor.forClass(Number.class);
        ArgumentCaptor<Number> upper = ArgumentCaptor.forClass(Number.class);
        verify(xyPlot, times(2)).setDomainBoundaries(
                lower.capture(), upper.capture(), eq(BoundaryMode.FIXED));
        assertEquals(1001d, lower.getValue().doubleValue(), 0.0001);
        assertEquals(1101d, upper.getValue().doubleValue(), 0.0001);
    }

    @Test
    public void testFingerDistance() {
        PanZoom panZoom = spy(new PanZoom(xyPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.SCALE));
        RectF distance = panZoom.fingerDistance(TestUtils.newPointerDownEvent(0, 0, 10, 10));
        assertEquals(0f, distance.left);
        assertEquals(0f, distance.top);
        assertEquals(10f, distance.right);
        assertEquals(10f, distance.bottom);

        // no matter what order the coords are supplied, make sure the same rect is calculated:
        distance = panZoom.fingerDistance(10, 10, 0, 0);
        assertEquals(0f, distance.left);
        assertEquals(0f, distance.top);
        assertEquals(10f, distance.right);
        assertEquals(10f, distance.bottom);
    }

    private XYPlot newRealPlot() {
        XYPlot plot = new XYPlot(getContext(), "test");
        plot.addSeries(TestUtils.generateXYSeries("series", 10, 0, 100), new LineAndPointFormatter());
        return plot;
    }

    @Test
    public void setState_withUnpopulatedState_leavesPlotBoundariesIntact() {
        XYPlot plot = newRealPlot();
        plot.setDomainBoundaries(2, 8, BoundaryMode.FIXED);
        plot.setRangeBoundaries(-1, 1, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // a partial config (no vertical pan, no zoom) never touches the range axis:
        PanZoom panZoom = PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.NONE);

        // simulates restoring a state that was captured before any pan / zoom gesture:
        panZoom.setState(new PanZoom.State());

        // this is what the render thread does on the next frame:
        plot.calculateMinMaxVals();

        assertEquals(2.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(8.0, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(-1.0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(1.0, plot.getBounds().getMaxY().doubleValue(), 0);
    }

    @Test
    public void getState_setState_roundTrip_preservesFixedBoundaries() {
        XYPlot plot = newRealPlot();
        plot.setDomainBoundaries(2, 8, BoundaryMode.FIXED);
        plot.setRangeBoundaries(-1, 1, BoundaryMode.FIXED);
        PanZoom panZoom = PanZoom.attach(plot);

        // capture before any gesture, as an Activity would in onSaveInstanceState:
        PanZoom.State state = panZoom.getState();

        // boundaries change (eg. a new Activity instance is created with different defaults)...
        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);

        // ...and restoring the state brings the originals back:
        panZoom.setState(state);
        plot.calculateMinMaxVals();

        assertEquals(2.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(8.0, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(-1.0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(1.0, plot.getBounds().getMaxY().doubleValue(), 0);
    }

    @Test
    public void state_serialVersionUID_matchesOriginalClass() {
        // States serialized by older versions of the library must keep deserializing; see the
        // comment on PanZoom.State.serialVersionUID.
        assertEquals(-4221152827129598653L,
                ObjectStreamClass.lookup(PanZoom.State.class).getSerialVersionUID());
    }

    @Test
    public void state_javaSerialization_roundTrip_preservesBoundaries() throws Exception {
        XYPlot plot = newRealPlot();
        plot.setDomainBoundaries(2, BoundaryMode.FIXED, 8, BoundaryMode.AUTO);
        plot.setRangeBoundaries(-1, BoundaryMode.AUTO, 1, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        final double autoMaxX = plot.getBounds().getMaxX().doubleValue();
        final double autoMinY = plot.getBounds().getMinY().doubleValue();
        PanZoom panZoom = PanZoom.attach(plot);

        // serialize as a Bundle would when saving instance state:
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(panZoom.getState());
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        PanZoom.State restored = (PanZoom.State) in.readObject();
        in.close();

        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);

        panZoom.setState(restored);
        plot.calculateMinMaxVals();

        assertEquals(2.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(autoMaxX, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(autoMinY, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(1.0, plot.getBounds().getMaxY().doubleValue(), 0);
    }

    /**
     * A State serialized by the library as it was before per-edge boundary modes were added
     * (domain 2..8 FIXED, range -1..1 FIXED), captured from the compiled class on master.
     */
    private static final String LEGACY_STATE_HEX =
            "aced000573720020636f6d2e616e64726f6964706c6f742e78792e50616e5a6f6f6d245374617465c56b73de4c4ded43" +
            "0200064c0012646f6d61696e426f756e646172794d6f64657400214c636f6d2f616e64726f6964706c6f742f78792f42" +
            "6f756e646172794d6f64653b4c0013646f6d61696e4c6f776572426f756e646172797400124c6a6176612f6c616e672f" +
            "4e756d6265723b4c0013646f6d61696e5570706572426f756e6461727971007e00024c001172616e6765426f756e6461" +
            "72794d6f646571007e00014c001272616e67654c6f776572426f756e6461727971007e00024c001272616e6765557070" +
            "6572426f756e6461727971007e000278707e72001f636f6d2e616e64726f6964706c6f742e78792e426f756e64617279" +
            "4d6f646500000000000000001200007872000e6a6176612e6c616e672e456e756d000000000000000012000078707400" +
            "054649584544737200116a6176612e6c616e672e496e746567657212e2a0a4f781873802000149000576616c75657872" +
            "00106a6176612e6c616e672e4e756d62657286ac951d0b94e08b0200007870000000027371007e00080000000871007e" +
            "00067371007e0008ffffffff7371007e000800000001";

    @Test
    public void state_serializedByOlderVersion_deserializesAndAppliesAsNoOp() throws Exception {
        byte[] bytes = new byte[LEGACY_STATE_HEX.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(LEGACY_STATE_HEX.substring(i * 2, i * 2 + 2), 16);
        }
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        PanZoom.State legacy = (PanZoom.State) in.readObject();
        in.close();

        // the legacy stream carries no per-edge modes, so applying it must leave the plot alone:
        XYPlot plot = newRealPlot();
        plot.setDomainBoundaries(10, 20, BoundaryMode.FIXED);
        plot.setRangeBoundaries(30, 40, BoundaryMode.FIXED);
        PanZoom.attach(plot).setState(legacy);
        plot.calculateMinMaxVals();

        assertEquals(10.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(20.0, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(30.0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(40.0, plot.getBounds().getMaxY().doubleValue(), 0);
    }

    @Test
    public void getState_setState_roundTrip_preservesMixedBoundaryModes() {
        XYPlot plot = newRealPlot();
        plot.setDomainBoundaries(2, BoundaryMode.FIXED, 8, BoundaryMode.AUTO);
        plot.setRangeBoundaries(-1, BoundaryMode.AUTO, 1, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        final double autoMaxX = plot.getBounds().getMaxX().doubleValue();
        final double autoMinY = plot.getBounds().getMinY().doubleValue();
        PanZoom panZoom = PanZoom.attach(plot);

        PanZoom.State state = panZoom.getState();

        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);

        panZoom.setState(state);
        plot.calculateMinMaxVals();

        assertEquals(2.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(autoMaxX, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(autoMinY, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(1.0, plot.getBounds().getMaxY().doubleValue(), 0);
    }
}
