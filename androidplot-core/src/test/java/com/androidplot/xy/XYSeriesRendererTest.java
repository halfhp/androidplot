/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.util.ValPixConverter;
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
        Step domainStep = XYStepCalculator.getStep(plot, Axis.DOMAIN, gridRect, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue());

        int x = 0;
        double val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false);

        assertEquals(val, domainStep.getStepPix()*x);

        x = 1;
        val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false);

        assertEquals(val, domainStep.getStepPix()*x);

        x = 9;
        val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false);

        assertEquals(val, domainStep.getStepPix()*x);
    }

}
