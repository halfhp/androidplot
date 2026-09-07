// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import android.content.res.TypedArray;
import android.graphics.*;
import android.util.*;
import android.view.View;

import com.androidplot.test.*;
import com.androidplot.ui.*;
import com.androidplot.util.fig.*;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

public class PlotTest extends AndroidplotTest {

    @Mock
    SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> mockSeriesRegistry;

    @Test
    public void testInit_withoutAttrs() {
        //Plot plot = mock(Plot.class);
        Plot plot = spy(new MockPlot("MockPlot"));
        plot.init(RuntimeEnvironment.application, null, 0);

        verify(plot, times(1)).onPreInit();
        verify(plot, times(1)).onAfterConfig();
    }

    @Test
    public void testInit_withAttrs() {
        Plot plot = spy(new MockPlot("MockPlot"));
        AttributeSet attrs = Robolectric.buildAttributeSet().build();
        plot.init(RuntimeEnvironment.application, attrs, 0);

        verify(plot, times(1)).onPreInit();
        verify(plot, times(1)).onAfterConfig();
    }

    @Test
    public void testAddSeries() {
        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        Class cl = MockRenderer1.class;

        plot.addSeries(m1, new MockFormatter1());
        assertEquals(1, plot.getRegistry().size());

        // a new copy of m1 is added:
        plot.addSeries(m1, new MockFormatter1());

        assertEquals(1, plot.getRenderers().size());
        assertEquals(2, plot.getRenderer(cl).getSeriesList().size());

        MockSeries m2 = new MockSeries();

        plot.addSeries(m2, new MockFormatter1());

        // still should only be one renderer type:
        assertEquals(1, plot.getRendererList().size());

        // we added a new instance of cl to the renderer so there should be 3 in the subregistry:
        assertEquals(3, plot.getRenderer(cl).getSeriesList().size());


        // lets add another renderer:
        plot.addSeries(m1, new MockFormatter2());

        assertEquals(2, plot.getRendererList().size());
    }

    @Test
    public void testRemoveSeries() {

        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter2());
        plot.addSeries(m2, new MockFormatter2());
        plot.addSeries(m3, new MockFormatter2());


        // a quick sanity run:
        assertEquals(2, plot.getRendererList().size());
        assertEquals(3, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(3, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        plot.removeSeries(m1, MockRenderer1.class);
        assertEquals(2, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m3, MockRenderer1.class);

        // add em all back
        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());


        // a quick sanity run:
        assertEquals(2, plot.getRendererList().size());
        assertEquals(6, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(3, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        // now lets try removing a series from all renderers:
        plot.removeSeries(m1);
        assertEquals(4, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(2, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        // and now lets remove the remaining series:
        plot.removeSeries(m2);
        plot.removeSeries(m3);
    }


    @Test
    public void testGetFormatter() {
        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        MockFormatter1 f1 = new MockFormatter1();
        MockFormatter1 f2 = new MockFormatter1();
        MockFormatter2 f3 = new MockFormatter2();

        plot.addSeries(m1, f1);
        plot.addSeries(m2, f2);
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, f3);
        plot.addSeries(m3, new MockFormatter1());

        assertEquals(plot.getRenderer(MockRenderer1.class).getFormatter(m1), f1);
        assertEquals(plot.getRenderer(MockRenderer1.class).getFormatter(m2), f2);
        assertEquals(plot.getRenderer(MockRenderer2.class).getFormatter(m2), f3);

        assertNotSame(plot.getRenderer(MockRenderer2.class).getFormatter(m2), f1);

    }

    @Test
    public void testGetRendererList() {

        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter2());
        plot.addSeries(m2, new MockFormatter2());
        plot.addSeries(m3, new MockFormatter2());

        List<SeriesRenderer> rList = plot.getRendererList();
        assertEquals(2, rList.size());
    }

    @Test
    public void testAddListener() {
        Plot plot = new MockPlot("MockPlot");
        List<PlotListener> listeners = plot.getListeners();

        assertEquals(0, listeners.size());

        MockPlotListener pl1 = new MockPlotListener();
        MockPlotListener pl2 = new MockPlotListener();

        plot.addListener(pl1);

        assertEquals(1, listeners.size());

        // should return false on a double entry attempt
        assertFalse(plot.addListener(pl1));

        // make sure the listener wasnt added anyway:
        assertEquals(1, listeners.size());

        plot.addListener(pl2);

        assertEquals(2, listeners.size());

    }

    @Test
    public void testRemoveListener() {
        Plot plot = new MockPlot("MockPlot");
        List<PlotListener> listeners = plot.getListeners();

        assertEquals(0, listeners.size());

        MockPlotListener pl1 = new MockPlotListener();
        MockPlotListener pl2 = new MockPlotListener();
        MockPlotListener pl3 = new MockPlotListener();

        plot.addListener(pl1);
        plot.addListener(pl2);

        assertEquals(2, listeners.size());

        assertFalse(plot.removeListener(pl3));

        assertTrue(plot.removeListener(pl1));

        assertEquals(1, listeners.size());

        assertFalse(plot.removeListener(pl1));

        assertEquals(1, listeners.size());

        assertTrue(plot.removeListener(pl2));

        assertEquals(0, listeners.size());

    }

    @Test
    public void testConfigure() throws Exception {
        Plot plot = new MockPlot("MockPlot");

        HashMap<String, String> params = new HashMap<String, String>();
        String param1 = "this is a test.";
        String param2 = "use_background_thread";
        String param3 = "#FF0000";
        params.put("title.text", param1);
        params.put("renderMode", param2);
        params.put("backgroundPaint.color", param3);

        Fig.configure(RuntimeEnvironment.application, plot, params);

        assertEquals(param1, plot.getTitle().getText());
        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, plot.getRenderMode());
        assertEquals(Color.parseColor(param3), plot.getBackgroundPaint().getColor());
    }

    @Test
    public void setTitle_setsTitle() {
        Plot plot = new MockPlot("foo");
        plot.setTitle("bar");
        assertEquals("bar", plot.getTitle().getText());
    }

    /** Counts onAfterDraw invocations. */
    static class CountingPlotListener extends MockPlotListener {
        int afterDrawCount;

        @Override
        public void onAfterDraw(Plot source, Canvas canvas) {
            afterDrawCount++;
        }
    }

    @Test
    public void renderOnCanvas_listenerRemovingItselfDuringDraw_doesNotThrow() {
        final Plot plot = new MockPlot("MockPlot", Plot.RenderMode.USE_MAIN_THREAD);
        final PlotListener selfRemovingListener = new PlotListener() {
            @Override
            public void onBeforeDraw(Plot source, Canvas canvas) {
            }

            @Override
            public void onAfterDraw(Plot source, Canvas canvas) {
                source.removeListener(this);
            }
        };
        CountingPlotListener otherListener = new CountingPlotListener();
        plot.addListener(otherListener);
        plot.addListener(selfRemovingListener);

        plot.renderOnCanvas(new Canvas());

        assertEquals(1, otherListener.afterDrawCount);
        assertEquals(1, plot.getListeners().size());
        assertTrue(plot.getListeners().contains(otherListener));
    }

    @Test
    public void renderOnCanvas_listenerRemovingItselfDuringDraw_stillNotifiesRemainingListeners() {
        final Plot plot = new MockPlot("MockPlot", Plot.RenderMode.USE_MAIN_THREAD);
        final PlotListener selfRemovingListener = new PlotListener() {
            @Override
            public void onBeforeDraw(Plot source, Canvas canvas) {
            }

            @Override
            public void onAfterDraw(Plot source, Canvas canvas) {
                source.removeListener(this);
            }
        };
        CountingPlotListener otherListener = new CountingPlotListener();
        plot.addListener(selfRemovingListener);
        plot.addListener(otherListener);

        plot.renderOnCanvas(new Canvas());

        assertEquals(1, otherListener.afterDrawCount);
        assertEquals(1, plot.getListeners().size());
        assertTrue(plot.getListeners().contains(otherListener));
    }

    @Test
    public void renderOnCanvas_seriesRemovingItselfDuringDraw_doesNotThrow() {
        final Plot plot = new MockPlot("MockPlot", Plot.RenderMode.USE_MAIN_THREAD);
        // series implementing PlotListener are auto-registered as listeners:
        final MockSeries selfRemovingSeries = new MockSeries() {
            @Override
            public void onAfterDraw(Plot source, Canvas canvas) {
                source.removeSeries(this);
            }
        };
        plot.addSeries(new MockSeries(), new MockFormatter1());
        plot.addSeries(selfRemovingSeries, new MockFormatter1());
        assertEquals(2, plot.getListeners().size());

        plot.renderOnCanvas(new Canvas());

        assertEquals(1, plot.getListeners().size());
        assertEquals(1, plot.getRegistry().size());
    }

    @Test
    public void clear_unregistersAllPlotListeners() {
        Plot plot = new MockPlot("MockPlot");
        plot.addSeries(new MockSeries(), new MockFormatter1());
        plot.addSeries(new MockSeries(), new MockFormatter1());
        plot.addSeries(new MockSeries(), new MockFormatter1());
        assertEquals(3, plot.getListeners().size());

        plot.clear();
        assertEquals(0, plot.getListeners().size());
    }

    @Test
    public void clear_clearsRegistry() {
        Plot plot = new MockPlot("MockPlot");
        plot.setRegistry(mockSeriesRegistry);

        plot.clear();
        verify(mockSeriesRegistry).clear();
    }

    @Test
    public void setPlotMargins_updatesMargins() {
        Plot plot = new MockPlot("MockPlot");
        plot.setPlotMargins(11, 22, 33, 44);

        assertEquals(11f, plot.getPlotMarginLeft());
        assertEquals(22f, plot.getPlotMarginTop());
        assertEquals(33f, plot.getPlotMarginRight());
        assertEquals(44f, plot.getPlotMarginBottom());
    }

    @Test
    public void setPlotPadding_updatesPadding() {
        Plot plot = new MockPlot("MockPlot");
        plot.setPlotPadding(11, 22, 33, 44);

        assertEquals(11f, plot.getPlotPaddingLeft());
        assertEquals(22f, plot.getPlotPaddingTop());
        assertEquals(33f, plot.getPlotPaddingRight());
        assertEquals(44f, plot.getPlotPaddingBottom());
    }

    /**
     * Regression tests for https://github.com/halfhp/androidplot/issues/120: a background-mode
     * plot whose first layout pass gives it a zero-sized dimension used to leave its render thread
     * permanently un-wakeable, so it never drew once it was given a real size.
     */
    @Test
    public void backgroundRender_afterZeroSizedLayout_rendersOnResize() throws Exception {
        RenderCountingPlot plot = new RenderCountingPlot();
        try {
            plot.onSizeChanged(100, 0, 0, 0);
            plot.awaitRenderThreadParked();
            assertEquals(0, plot.rendersOnCanvas.get());

            plot.onSizeChanged(100, 100, 100, 0);
            assertTrue("plot never rendered after being resized to a non-zero size",
                    plot.rendered.await(5, TimeUnit.SECONDS));
        } finally {
            plot.onDetachedFromWindow();
        }
    }

    @Test
    public void backgroundRender_afterZeroSizedLayout_rendersOnRedraw() throws Exception {
        RenderCountingPlot plot = new RenderCountingPlot();
        try {
            plot.onSizeChanged(100, 0, 0, 0);
            plot.awaitRenderThreadParked();
            plot.onSizeChanged(100, 100, 100, 0);
            plot.redraw();
            assertTrue("plot never rendered after redraw() following a resize",
                    plot.rendered.await(5, TimeUnit.SECONDS));
        } finally {
            plot.onDetachedFromWindow();
        }
    }

    /**
     * A redraw() issued while the render thread is busy drawing must not be dropped: the
     * thread has to render again afterwards so the latest data reaches the screen.
     */
    @Test
    public void backgroundRender_redrawDuringRender_rendersAgain() throws Exception {
        RenderCountingPlot plot = new RenderCountingPlot();
        plot.holdRenderIndex = 0;
        try {
            plot.onSizeChanged(100, 100, 0, 0);
            assertTrue(plot.heldRenderStarted.await(5, TimeUnit.SECONDS));

            // render thread is now blocked inside its first render
            plot.redraw();
            plot.redraw();
            plot.releaseHeldRender.countDown();

            assertTrue("redraw() issued during a render was dropped",
                    plot.renderedTwice.await(5, TimeUnit.SECONDS));
            // several requests made during one render coalesce into a single extra pass
            plot.awaitRenderThreadParked();
            assertEquals(2, plot.rendersOnCanvas.get());
        } finally {
            plot.releaseHeldRender.countDown();
            plot.onDetachedFromWindow();
        }
    }

    /**
     * Detaching and re-attaching a plot before its render thread has finished exiting (as a
     * RecyclerView does when scrolling) must hand over cleanly: the replacement thread renders,
     * and the exiting thread must not recycle the buffers out from under it.
     */
    @Test
    public void backgroundRender_reattachWhileOldThreadExiting_keepsRendering() throws Exception {
        RenderCountingPlot plot = new RenderCountingPlot();
        try {
            plot.onSizeChanged(100, 100, 0, 0);
            plot.awaitRenders(1);
            plot.awaitRenderThreadParked();

            // park the old thread inside a render, then detach and re-attach while it's stuck
            plot.holdRenderIndex = 1;
            plot.redraw();
            assertTrue(plot.heldRenderStarted.await(5, TimeUnit.SECONDS));
            plot.onDetachedFromWindow();
            plot.onAttachedToWindow();
            plot.releaseHeldRender.countDown();

            // the replacement thread renders on start, and again on request
            plot.awaitRenders(3);
            plot.awaitRenderThreadParked();
            plot.redraw();
            plot.awaitRenders(4);

            // the buffers survived the old thread's exit: onDraw still has a bitmap to draw
            Canvas canvas = spy(new Canvas());
            plot.onDraw(canvas);
            verify(canvas).drawBitmap(any(Bitmap.class), eq(0f), eq(0f), (Paint) isNull());
        } finally {
            plot.releaseHeldRender.countDown();
            plot.onDetachedFromWindow();
        }
    }

    /**
     * https://github.com/halfhp/androidplot/issues/96: in background mode the view only ever
     * draws a bitmap, so it must not be forced onto a software layer, which would add a
     * full-size CPU copy of the view on every frame.
     */
    @Test
    public void onSizeChanged_backgroundMode_doesNotForceSoftwareLayer() {
        MockPlot plot = spy(new MockPlot("bg", Plot.RenderMode.USE_BACKGROUND_THREAD));
        doReturn(true).when(plot).isHardwareAccelerated();
        try {
            plot.onSizeChanged(100, 100, 0, 0);
            verify(plot, never()).setLayerType(eq(View.LAYER_TYPE_SOFTWARE), any());
        } finally {
            plot.onDetachedFromWindow();
        }
    }

    @Test
    public void onSizeChanged_mainThreadMode_forcesSoftwareLayer() {
        MockPlot plot = spy(new MockPlot("main", Plot.RenderMode.USE_MAIN_THREAD));
        doReturn(true).when(plot).isHardwareAccelerated();
        plot.onSizeChanged(100, 100, 0, 0);
        verify(plot).setLayerType(eq(View.LAYER_TYPE_SOFTWARE), any());
    }

    /** A background-mode plot that reports when it has rendered onto a real canvas. */
    static class RenderCountingPlot extends MockPlot {
        final CountDownLatch rendered = new CountDownLatch(1);
        final CountDownLatch renderedTwice = new CountDownLatch(2);
        final AtomicInteger rendersOnCanvas = new AtomicInteger();

        /** zero-based index of the render to block inside until releaseHeldRender fires */
        volatile int holdRenderIndex = -1;
        final CountDownLatch heldRenderStarted = new CountDownLatch(1);
        final CountDownLatch releaseHeldRender = new CountDownLatch(1);

        RenderCountingPlot() {
            super("RenderCountingPlot", RenderMode.USE_BACKGROUND_THREAD);
        }

        @Override
        protected synchronized void renderOnCanvas(Canvas canvas) {
            super.renderOnCanvas(canvas);
            if (canvas != null) {
                if (rendersOnCanvas.get() == holdRenderIndex) {
                    heldRenderStarted.countDown();
                    try {
                        releaseHeldRender.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                rendersOnCanvas.incrementAndGet();
                rendered.countDown();
                renderedTwice.countDown();
            }
        }

        /** Blocks until at least n renders onto a real canvas have completed. */
        void awaitRenders(int n) throws Exception {
            long deadline = System.currentTimeMillis() + 5000;
            while (rendersOnCanvas.get() < n) {
                if (System.currentTimeMillis() > deadline) {
                    fail("expected " + n + " renders, got " + rendersOnCanvas.get());
                }
                Thread.sleep(10);
            }
        }

        /**
         * Blocks until the render thread is waiting for a redraw request, so that a subsequent
         * notify cannot be lost by arriving before the thread has started waiting.
         */
        void awaitRenderThreadParked() throws Exception {
            long deadline = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < deadline) {
                boolean anyLive = false;
                boolean allParked = true;
                for (Thread t : Thread.getAllStackTraces().keySet()) {
                    if ("Androidplot renderThread".equals(t.getName())) {
                        anyLive = true;
                        if (t.getState() != Thread.State.WAITING) {
                            allParked = false;
                        }
                    }
                }
                if (anyLive && allParked) {
                    return;
                }
                Thread.sleep(10);
            }
            fail("render thread never parked");
        }
    }

    static class MockPlotListener implements PlotListener {

        public void onBeforeDraw(Plot source, Canvas canvas) {
        }

        public void onAfterDraw(Plot source, Canvas canvas) {
        }
    }

    static class MockSeries implements Series, PlotListener {

        public String getTitle() {
            return null;
        }

        @Override
        public void onBeforeDraw(Plot source, Canvas canvas) {

        }

        @Override
        public void onAfterDraw(Plot source, Canvas canvas) {

        }
    }

    static class MockSeries2 implements Series {

        public String getTitle() {
            return null;
        }
    }

    static class MockSeries3 implements Series {

        public String getTitle() {
            return null;
        }
    }

    static class MockRenderer1 extends SeriesRenderer {

        public MockRenderer1(Plot plot) {
            super(plot);
        }

        @Override
        public void onRender(Canvas canvas, RectF plotArea, Series series, Formatter formatter, RenderStack stack) {

        }

        @Override
        public void doDrawLegendIcon(Canvas canvas, RectF rect, Formatter formatter) {

        }
    }

    static class MockRenderer2 extends SeriesRenderer {

        public MockRenderer2(Plot plot) {
            super(plot);
        }

        @Override
        public void onRender(Canvas canvas, RectF plotArea, Series series, Formatter formatter, RenderStack stack) {

        }

        @Override
        public void doDrawLegendIcon(Canvas canvas, RectF rect, Formatter formatter) {

        }
    }

    static class MockFormatter1 extends Formatter<MockPlot> {

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MockRenderer1.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(MockPlot plot) {
            return new MockRenderer1(plot);
        }
    }

    static class MockFormatter2 extends Formatter<MockPlot> {

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MockRenderer2.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(MockPlot plot) {
            return new MockRenderer2(plot);
        }
    }

    public static class MockSeriesBundle extends SeriesBundle<MockSeries, Formatter> {

        public MockSeriesBundle(MockSeries series, Formatter formatter) {
            super(series, formatter);
        }
    }

    public static class MockPlot extends Plot<MockSeries, Formatter, SeriesRenderer, MockSeriesBundle, SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>> {
        public MockPlot(String title) {
            super(RuntimeEnvironment.application, title);
        }

        public MockPlot(String title, RenderMode mode) {
            super(RuntimeEnvironment.application, title, mode);
        }

        @Override
        protected SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> getRegistryInstance() {
            return new SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>() {
                @Override
                protected MockSeriesBundle newSeriesBundle(
                        MockSeries series, Formatter formatter) {
                    return new MockSeriesBundle(series, formatter);
                }
            };
        }

        @Override
        protected void onPreInit() {

        }

        @Override
        protected void processAttrs(TypedArray attrs) {

        }
    }
}
