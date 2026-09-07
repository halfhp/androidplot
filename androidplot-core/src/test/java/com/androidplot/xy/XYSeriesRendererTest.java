// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.RectF;

import com.androidplot.*;
import com.androidplot.test.AndroidplotTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;

public class XYSeriesRendererTest extends AndroidplotTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDataToGridCorrelation() throws Exception {
        RectF gridRect = new RectF(5, 5, 105, 105);
        XYPlot plot = new XYPlot(RuntimeEnvironment.application, "Test");
        plot.setDomainStepMode(StepMode.SUBDIVIDE);
        plot.setDomainStepValue(10);
        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        Step domainStep = XYStepCalculator.getStep(plot, Axis.DOMAIN, gridRect);

        Region xBounds = new Region(0, 9);

        int x = 0;
        double val = xBounds.transform(x, gridRect.left, gridRect.right, false);
        assertEquals(val, (domainStep.getStepPix()*x) + gridRect.left);

        x = 1;
        val = xBounds.transform(x, gridRect.left, gridRect.right, false);
        assertEquals(val, (domainStep.getStepPix()*x) + gridRect.left);

        x = 9;
        val = xBounds.transform(x, gridRect.left, gridRect.right, false);
        assertEquals(val, (domainStep.getStepPix()*x) + gridRect.left);
    }

}
