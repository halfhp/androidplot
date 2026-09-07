// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLog;

/**
 * Helpers shared by the demoapp tests.
 *
 * <p>Sizing: {@link ActivityController#setup()} ends with {@code visible()}, which attaches the
 * activity's decor view to a window and lays it out at the emulated display size (320x470 by
 * default), so every plot in the layout has real dimensions, widget rects and grid rects after
 * {@link #launch}; nothing needs to be measured or laid out by hand.  Only a view that is not in
 * the window (eg. a list row that has not been created yet) needs {@link #layOut}.
 *
 * <p>Threads: {@code destroy()} removes the decor view from the window, which is what stops each
 * plot's render thread; the examples' Redrawer and data threads are stopped by the activities
 * themselves.  {@link #assertThreadsExited} checks that all of them are gone afterwards.
 */
final class DemoAppTest {

    /** How long a background thread may take to notice it has been stopped. */
    private static final long THREAD_EXIT_TIMEOUT_MS = 2000;

    private DemoAppTest() {}

    /** Creates, starts, resumes and makes the activity visible, then drains the main looper. */
    static <T extends Activity> ActivityController<T> launch(Class<T> cls) {
        ActivityController<T> controller = Robolectric.buildActivity(cls).setup();
        idle();
        return controller;
    }

    /**
     * Runs the activity through pause, stop and destroy, drains the main looper and waits for
     * the plots' render threads to exit.  The wait matters: Robolectric resets its native
     * graphics state between tests, and a render thread still mid-frame at that point fails
     * with a confusing NPE inside Canvas.
     */
    static void finish(ActivityController<?> controller) {
        controller.pause().stop().destroy();
        idle();
        awaitAndroidplotThreads();
    }

    /** Waits (bounded) for every "Androidplot ..." thread to exit; does not fail if one lingers. */
    static void awaitAndroidplotThreads() {
        long deadline = System.currentTimeMillis() + THREAD_EXIT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline && !androidplotThreads().isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static List<Thread> androidplotThreads() {
        List<Thread> threads = new ArrayList<>();
        for (Thread thread : liveThreads()) {
            if (thread.getName().startsWith("Androidplot")) {
                threads.add(thread);
            }
        }
        return threads;
    }

    /** Runs everything currently queued on the main looper (Robolectric's default PAUSED mode). */
    static void idle() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    /** Measures and lays a detached view out at exactly the given size. */
    static void layOut(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    /** Draws a laid-out view (and its children) onto a bitmap of its own size. */
    static void draw(View view) {
        assertTrue("view has no size: " + view, view.getWidth() > 0 && view.getHeight() > 0);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        try {
            view.draw(new Canvas(bitmap));
        } finally {
            bitmap.recycle();
        }
    }

    /**
     * Installs a default uncaught exception handler for the rest of the test (Robolectric resets
     * it between tests) and returns the list it records exceptions from other threads into.
     */
    static List<Throwable> recordUncaughtExceptions() {
        List<Throwable> uncaught = Collections.synchronizedList(new ArrayList<>());
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> uncaught.add(throwable));
        return uncaught;
    }

    /**
     * Fails if anything logged an exception at error level.  Plot catches and logs exceptions
     * thrown while rendering rather than letting them propagate, so this is how a broken
     * renderer shows up.
     */
    static void assertNoLoggedErrors() {
        for (ShadowLog.LogItem item : ShadowLog.getLogs()) {
            if (item.type >= Log.ERROR && item.throwable != null) {
                throw new AssertionError("error logged by " + item.tag + ": " + item.msg,
                        item.throwable);
            }
        }
    }

    /** Delivers a down/up tap at the given view-relative point. */
    static void tap(View view, PointF point) {
        long now = SystemClock.uptimeMillis();
        MotionEvent down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, point.x, point.y, 0);
        MotionEvent up = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, point.x, point.y, 0);
        try {
            view.dispatchTouchEvent(down);
            view.dispatchTouchEvent(up);
        } finally {
            down.recycle();
            up.recycle();
        }
        idle();
    }

    /**
     * @return the non-daemon threads that are alive right now.  Daemon threads are left out
     * because the JVM, AWT and Robolectric start some lazily (eg. on the first bitmap draw) and
     * never stop them; every thread the plots and examples start is a regular thread.
     */
    static Set<Thread> liveThreads() {
        Set<Thread> threads = new HashSet<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.isAlive() && !thread.isDaemon()) {
                threads.add(thread);
            }
        }
        return threads;
    }

    /**
     * Waits (briefly) for every thread started since {@code before} was taken to exit, and fails
     * naming the survivors if any are still alive.  Covers Androidplot's named render and
     * Redrawer threads as well as the examples' own unnamed data threads.
     */
    static void assertThreadsExited(Set<Thread> before) throws InterruptedException {
        long deadline = System.currentTimeMillis() + THREAD_EXIT_TIMEOUT_MS;
        List<Thread> survivors;
        while (true) {
            survivors = new ArrayList<>();
            for (Thread thread : liveThreads()) {
                if (!before.contains(thread)) {
                    survivors.add(thread);
                }
            }
            if (survivors.isEmpty() || System.currentTimeMillis() > deadline) {
                break;
            }
            Thread.sleep(10);
        }
        if (!survivors.isEmpty()) {
            StringBuilder names = new StringBuilder();
            for (Thread thread : survivors) {
                names.append(names.length() == 0 ? "" : ", ").append(thread.getName());
            }
            fail("Threads still alive after destroy: " + names);
        }
        for (Thread thread : androidplotThreads()) {
            fail("Androidplot thread survived destroy: " + thread.getName());
        }
    }
}
