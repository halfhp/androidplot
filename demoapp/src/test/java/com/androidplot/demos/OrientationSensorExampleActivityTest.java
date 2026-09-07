// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowSensor;
import org.robolectric.shadows.ShadowSensorManager;

@RunWith(RobolectricTestRunner.class)
public class OrientationSensorExampleActivityTest {

    @Test
    public void noOrientationSensor_finishesCleanly() throws Exception {
        Set<Thread> before = DemoAppTest.liveThreads();

        // Robolectric's SensorManager has no sensors unless some are added
        ActivityController<OrientationSensorExampleActivity> controller =
                Robolectric.buildActivity(OrientationSensorExampleActivity.class).create();
        assertTrue(controller.get().isFinishing());

        // the Redrawer created in onCreate must still be shut down by onDestroy
        controller.destroy();
        DemoAppTest.assertThreadsExited(before);
    }

    @Test
    public void rotationVectorSensor_readingsArePlotted() throws Exception {
        ActivityController<OrientationSensorExampleActivity> controller =
                Robolectric.buildActivity(OrientationSensorExampleActivity.class);
        // the activity looks its SensorManager up through its own context, so add the sensor
        // to that instance
        SensorManager sensorManager = controller.get().getSystemService(SensorManager.class);
        ShadowSensorManager shadowSensorManager = shadowOf(sensorManager);
        shadowSensorManager.addSensor(ShadowSensor.newInstance(Sensor.TYPE_ROTATION_VECTOR));

        controller.setup();
        DemoAppTest.idle();
        OrientationSensorExampleActivity activity = controller.get();
        assertFalse(activity.isFinishing());
        assertTrue(shadowSensorManager.hasListener(activity));

        XYPlot historyPlot = activity.findViewById(R.id.aprHistoryPlot);
        XYPlot levelsPlot = activity.findViewById(R.id.aprLevelsPlot);
        assertEquals(3, historyPlot.getRegistry().getSeriesList().size());
        for (XYSeries series : historyPlot.getRegistry().getSeriesList()) {
            assertEquals(0, series.size());
        }

        // a rotation of -90 degrees about the z axis as a unit quaternion (x, y, z, w), which
        // SensorManager.getOrientation reports as an azimuth of +90 degrees
        SensorEvent event = ShadowSensorManager.createSensorEvent(4, Sensor.TYPE_ROTATION_VECTOR);
        float halfAngle = (float) Math.toRadians(-45);
        event.values[0] = 0;
        event.values[1] = 0;
        event.values[2] = (float) Math.sin(halfAngle);
        event.values[3] = (float) Math.cos(halfAngle);
        shadowSensorManager.sendSensorEventToListeners(event);

        // one sample of azimuth, pitch and roll each in the history and the levels
        for (XYSeries series : historyPlot.getRegistry().getSeriesList()) {
            assertEquals(1, series.size());
        }
        for (XYSeries series : levelsPlot.getRegistry().getSeriesList()) {
            assertEquals(1, series.size());
        }
        XYSeries azimuth = historyPlot.getRegistry().getSeriesList().get(0);
        assertEquals("Az.", azimuth.getTitle());
        assertEquals(90, azimuth.getY(0).floatValue(), 0.01f);

        shadowSensorManager.sendSensorEventToListeners(event);
        assertEquals(2, azimuth.size());

        // the listener is only registered while resumed
        controller.pause();
        assertFalse(shadowSensorManager.hasListener(activity));
        controller.stop().destroy();
        DemoAppTest.awaitAndroidplotThreads();
        DemoAppTest.assertNoLoggedErrors();
    }
}
