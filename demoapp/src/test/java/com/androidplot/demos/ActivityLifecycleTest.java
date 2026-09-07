// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

/**
 * Runs every example activity through its full lifecycle: this is the automated version of the
 * "open each example on an emulator and make sure it doesn't crash" smoke test.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class ActivityLifecycleTest {

    @Parameters(name = "{0}")
    public static List<Object[]> activities() {
        List<Object[]> params = new ArrayList<>();
        for (Class<? extends Activity> cls : DemoActivities.ALL) {
            params.add(new Object[] {cls.getSimpleName(), cls});
        }
        return params;
    }

    private final Class<? extends Activity> activityClass;

    public ActivityLifecycleTest(String name, Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    @Test
    public void fullLifecycle_noExceptionsAndNoLeakedThreads() throws Exception {
        Set<Thread> before = DemoAppTest.liveThreads();
        List<Throwable> uncaught = DemoAppTest.recordUncaughtExceptions();

        ActivityController<? extends Activity> controller =
                Robolectric.buildActivity(activityClass).create();
        View decor = controller.get().getWindow().getDecorView();
        if (controller.get().isFinishing()) {
            // finished during onCreate (eg. OrientationSensorExampleActivity when the device has
            // no sensor): the framework would go straight to onDestroy without resuming.
            controller.destroy();
        } else {
            controller.start().postCreate(null).resume().visible();
            DemoAppTest.idle();
            assertTrue(decor.isAttachedToWindow());

            // Robolectric attaches and lays the window out but never draws it, so draw the whole
            // screen once: this runs every main-thread plot's renderers.  (Background-thread
            // plots render on their own thread as soon as they have a size; an exception there
            // would surface via the uncaught exception handler.)
            DemoAppTest.draw(decor);

            DemoAppTest.finish(controller);
        }
        assertFalse(decor.isAttachedToWindow());

        DemoAppTest.assertThreadsExited(before);
        assertEquals("uncaught exceptions on background threads: " + uncaught,
                0, uncaught.size());
        DemoAppTest.assertNoLoggedErrors();
    }
}
