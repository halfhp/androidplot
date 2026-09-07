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

    private static boolean redrawerThreadAlive() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if ("Androidplot Redrawer".equals(t.getName()) && t.isAlive()) {
                return true;
            }
        }
        return false;
    }
}
