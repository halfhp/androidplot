// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.XYPlot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class TouchZoomExampleActivityTest {

    private static final double DELTA = 0.0001;

    @Test
    @SuppressWarnings("deprecation") // the typed getSerializable needs API 33 but the app supports 23
    public void savesPanZoomState() {
        ActivityController<TouchZoomExampleActivity> controller =
                DemoAppTest.launch(TouchZoomExampleActivity.class);

        Bundle bundle = new Bundle();
        controller.saveInstanceState(bundle);
        assertTrue(bundle.getSerializable("pan-zoom-state") instanceof PanZoom.State);

        DemoAppTest.finish(controller);
    }

    @Test
    public void recreate_restoresPanZoomState() {
        ActivityController<TouchZoomExampleActivity> controller =
                DemoAppTest.launch(TouchZoomExampleActivity.class);
        TouchZoomExampleActivity first = controller.get();
        XYPlot plot = first.findViewById(R.id.plot);

        // zoom in to a window the user could have panned / pinched to
        plot.setDomainBoundaries(1000, 1500, BoundaryMode.FIXED);
        plot.setRangeBoundaries(200, 600, BoundaryMode.FIXED);
        plot.redraw();

        // saves the instance state, destroys the activity and creates a new one from that state
        controller.recreate();
        DemoAppTest.idle();
        TouchZoomExampleActivity second = controller.get();
        assertNotSame(first, second);
        XYPlot restored = second.findViewById(R.id.plot);
        assertNotNull(restored);
        assertNotSame(plot, restored);

        restored.calculateMinMaxVals();
        assertEquals(1000, restored.getBounds().getMinX().doubleValue(), DELTA);
        assertEquals(1500, restored.getBounds().getMaxX().doubleValue(), DELTA);
        assertEquals(200, restored.getBounds().getMinY().doubleValue(), DELTA);
        assertEquals(600, restored.getBounds().getMaxY().doubleValue(), DELTA);
        assertEquals(BoundaryMode.FIXED, restored.getDomainLowerBoundaryMode());
        assertEquals(BoundaryMode.FIXED, restored.getRangeUpperBoundaryMode());

        DemoAppTest.finish(controller);
        DemoAppTest.assertNoLoggedErrors();
    }

    @Test
    public void resetButton_restoresOuterLimits() {
        ActivityController<TouchZoomExampleActivity> controller =
                DemoAppTest.launch(TouchZoomExampleActivity.class);
        XYPlot plot = controller.get().findViewById(R.id.plot);
        plot.setDomainBoundaries(1000, 1500, BoundaryMode.FIXED);

        controller.get().findViewById(R.id.resetButton).performClick();
        DemoAppTest.idle();

        plot.calculateMinMaxVals();
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), DELTA);
        assertEquals(3000, plot.getBounds().getMaxX().doubleValue(), DELTA);

        DemoAppTest.finish(controller);
    }
}
