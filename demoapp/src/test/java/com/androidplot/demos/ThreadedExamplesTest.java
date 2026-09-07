// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

/**
 * ECGExample and DynamicXYPlotActivity each run a data thread and a Redrawer on top of the
 * plot's own render thread.  The lifecycle smoke test already checks those exit on destroy; this
 * checks they were actually started, so that check is not passing vacuously.
 */
@RunWith(RobolectricTestRunner.class)
public class ThreadedExamplesTest {

    @Test
    public void ecgExample_startsAndStopsItsThreads() throws Exception {
        assertStartsAndStopsThreads(ECGExample.class);
    }

    @Test
    public void dynamicXYPlot_startsAndStopsItsThreads() throws Exception {
        assertStartsAndStopsThreads(DynamicXYPlotActivity.class);
    }

    private static void assertStartsAndStopsThreads(Class<? extends Activity> cls)
            throws InterruptedException {
        Set<Thread> before = DemoAppTest.liveThreads();

        ActivityController<? extends Activity> controller = DemoAppTest.launch(cls);
        Set<Thread> started = new HashSet<>(DemoAppTest.liveThreads());
        started.removeAll(before);
        Set<String> names = new HashSet<>();
        for (Thread thread : started) {
            names.add(thread.getName());
        }
        assertTrue("expected a data thread, a Redrawer and a render thread, got " + names,
                started.size() >= 3);
        assertTrue(names.toString(), names.contains("Androidplot Redrawer"));
        assertTrue(names.toString(), names.contains("Androidplot renderThread"));

        DemoAppTest.finish(controller);
        DemoAppTest.assertThreadsExited(before);
        DemoAppTest.assertNoLoggedErrors();
    }
}
