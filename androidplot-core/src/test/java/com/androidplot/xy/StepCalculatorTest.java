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

import com.androidplot.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StepCalculatorTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetStep() throws Exception {

    }

    @Test
    public void testSubdivide() throws Exception {
        int numSegments = 10;

        Region pixBounds = new Region(0, 100);
        Region realBounds = new Region(0, 100);
        Step step = XYStepCalculator.getStep(StepMode.SUBDIVIDE,numSegments, realBounds, pixBounds);

        assertEquals(pixBounds.length().doubleValue()/(numSegments-1), step.getStepPix());

        // make sure large values dont break anything:
        realBounds.setMin(1000000000);
        realBounds.setMax(2000000000);
        step = XYStepCalculator.getStep(StepMode.SUBDIVIDE,
                numSegments, realBounds, pixBounds);

        assertEquals(pixBounds.length().doubleValue()/(numSegments-1), step.getStepPix());
    }
    
    @Test
    public void testIncrementByVal() throws Exception {
        Region pixBounds = new Region(50, 150);
        Region realBounds = new Region(100, 200);
        Step step = XYStepCalculator.getStep(StepMode.INCREMENT_BY_VAL, 1, realBounds, pixBounds);
        assertEquals(1.0, step.getStepPix());
    }

    @Test
    public void testIncrementByPixels() throws Exception {
        Region pixBounds = new Region(50, 150);
        Region realBounds = new Region(100, 200);
        Step step = XYStepCalculator.getStep(StepMode.INCREMENT_BY_PIXELS, 1, realBounds, pixBounds);
        assertEquals(1.0, step.getStepPix());
    }
}
