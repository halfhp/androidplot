// SPDX-License-Identifier: Apache-2.0
package com.androidplot.util;

import com.androidplot.Plot;
import com.androidplot.PlotTest;
import com.androidplot.test.AndroidplotTest;

import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class RedrawerTest extends AndroidplotTest {

    /**
     * https://github.com/halfhp/androidplot/issues/96: plots are held weakly, so one that has
     * been garbage collected must be skipped rather than dereferenced.
     */
    @Test
    public void redrawAll_skipsAndDropsCollectedPlots() {
        Plot live = mock(Plot.class);
        WeakReference<Plot> collected = new WeakReference<>(mock(Plot.class));
        collected.clear();
        List<WeakReference<Plot>> plots = new ArrayList<>();
        plots.add(collected);
        plots.add(new WeakReference<>(live));

        assertTrue(Redrawer.redrawAll(plots));
        verify(live).redraw();
        assertEquals(1, plots.size());
    }

    @Test
    public void redrawAll_reportsWhenNoPlotsRemain() {
        WeakReference<Plot> collected = new WeakReference<>(mock(Plot.class));
        collected.clear();
        List<WeakReference<Plot>> plots = new ArrayList<>();
        plots.add(collected);

        assertFalse(Redrawer.redrawAll(plots));
        assertTrue(plots.isEmpty());
    }

    @Test
    public void run_redrawsPlotAtRefreshRate() throws Exception {
        Plot plot = mock(Plot.class);
        Redrawer redrawer = new Redrawer(Collections.singletonList(plot), 100, true);
        try {
            verify(plot, timeout(2000).atLeast(3)).redraw();
        } finally {
            redrawer.finish();
        }
    }

    @Test
    public void run_exitsCleanlyOnceAllPlotsAreCollected() throws Exception {
        // an uncaught exception on the redrawer thread would crash a real app; capture it here
        final List<Throwable> uncaught = Collections.synchronizedList(new ArrayList<Throwable>());
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> uncaught.add(e));
        try {
            // a real plot rather than a mock: Mockito keeps strong references to its mocks,
            // which would prevent collection.
            Plot plot = new PlotTest.MockPlot("collectable");
            new Redrawer(Collections.singletonList(plot), 100, true);
            assertTrue(redrawerThreadAlive());

            // simulate the plot being garbage collected while the redrawer keeps running
            plot = null;
            for (int i = 0; i < 20 && redrawerThreadAlive(); i++) {
                System.gc();
                Thread.sleep(100);
            }
            assertFalse("redrawer thread kept running with no plots left", redrawerThreadAlive());
            assertTrue("redrawer thread died with " + uncaught, uncaught.isEmpty());
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previous);
        }
    }

    /**
     * finish() may be called before the redrawer's thread has been scheduled (eg. an activity
     * created and destroyed back to back on a slow device). The thread must still exit rather
     * than un-doing the finish and parking forever.
     */
    @Test
    public void finish_beforeThreadRuns_stillExits() throws Exception {
        Plot plot = mock(Plot.class);
        Redrawer redrawer = new Redrawer(Collections.singletonList(plot), 100, false);
        // the constructor already started the real thread; finish it, then replay the race by
        // invoking run() again as if the thread were only now getting scheduled.
        redrawer.finish();
        Thread replay = new Thread(redrawer, "replayed redrawer");
        replay.start();
        replay.join(2000);
        assertFalse("run() invoked after finish() must return immediately", replay.isAlive());
    }

    @Test
    public void singlePlotConstructor_notStarted_parksUntilStarted() throws Exception {
        Plot plot = mock(Plot.class);
        Redrawer redrawer = new Redrawer(plot, 100, false);
        try {
            Thread.sleep(300);
            verify(plot, never()).redraw();

            redrawer.start();
            verify(plot, timeout(2000).atLeast(2)).redraw();
        } finally {
            redrawer.finish();
        }
    }

    @Test
    public void pause_stopsRedrawingUntilStarted() throws Exception {
        Plot plot = mock(Plot.class);
        Redrawer redrawer = new Redrawer(plot, 100, true);
        try {
            verify(plot, timeout(2000).atLeast(2)).redraw();

            redrawer.pause();
            // let any in-flight redraw complete, then confirm no further ones arrive:
            Thread.sleep(100);
            int redrawsAtPause = mockingDetails(plot).getInvocations().size();
            Thread.sleep(300);
            assertEquals(redrawsAtPause, mockingDetails(plot).getInvocations().size());

            redrawer.start();
            verify(plot, timeout(2000).atLeast(redrawsAtPause + 2)).redraw();
        } finally {
            redrawer.finish();
        }
    }

    @Test
    public void setMaxRefreshRate_changesRedrawInterval() throws Exception {
        Plot plot = mock(Plot.class);
        // 2 Hz: at most a couple of redraws in half a second
        Redrawer redrawer = new Redrawer(plot, 2, true);
        try {
            Thread.sleep(500);
            int slowRedraws = mockingDetails(plot).getInvocations().size();
            assertTrue("expected at most 3 redraws at 2Hz, got " + slowRedraws, slowRedraws <= 3);

            redrawer.setMaxRefreshRate(200);
            verify(plot, timeout(2000).atLeast(slowRedraws + 10)).redraw();
        } finally {
            redrawer.finish();
        }
    }

    @Test
    public void interruptingTheThread_exitsQuietly() throws Exception {
        final List<Throwable> uncaught = Collections.synchronizedList(new ArrayList<Throwable>());
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> uncaught.add(e));
        try {
            List<Thread> before = redrawerThreads();
            Plot plot = mock(Plot.class);
            new Redrawer(plot, 100, false);
            Thread thread = null;
            for (int i = 0; i < 50 && thread == null; i++) {
                for (Thread t : redrawerThreads()) {
                    if (!before.contains(t)) {
                        thread = t;
                    }
                }
                Thread.sleep(10);
            }
            assertTrue("redrawer thread not found", thread != null);

            thread.interrupt();
            thread.join(2000);
            assertFalse("interrupted redrawer thread did not exit", thread.isAlive());
            assertTrue("redrawer thread died with " + uncaught, uncaught.isEmpty());
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previous);
        }
    }

    private static List<Thread> redrawerThreads() {
        List<Thread> threads = new ArrayList<>();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if ("Androidplot Redrawer".equals(t.getName()) && t.isAlive()) {
                threads.add(t);
            }
        }
        return threads;
    }

    /**
     * Hammers pause/start/finish from another thread while the redrawer runs. Before the loop
     * re-checked its flags under the monitor, a notify() could land between the thread deciding
     * to park and actually parking, leaving it waiting forever.
     */
    @Test
    public void pauseStartFinish_underContention_alwaysExits() throws Exception {
        for (int i = 0; i < 50; i++) {
            Plot plot = mock(Plot.class);
            Redrawer redrawer = new Redrawer(Collections.singletonList(plot), 1000, i % 2 == 0);
            for (int j = 0; j < 20; j++) {
                redrawer.pause();
                redrawer.start();
            }
            redrawer.finish();
            long deadline = System.currentTimeMillis() + 2000;
            while (redrawerThreadAlive() && System.currentTimeMillis() < deadline) {
                Thread.sleep(5);
            }
            assertFalse("redrawer thread " + i + " never exited", redrawerThreadAlive());
        }
    }

    private static boolean redrawerThreadAlive() {
        return !redrawerThreads().isEmpty();
    }
}
