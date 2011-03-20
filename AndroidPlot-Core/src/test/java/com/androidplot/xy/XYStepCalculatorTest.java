package com.androidplot.xy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class XYStepCalculatorTest {
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
        float plotSize = 100;
        double minVal = 0;
        double maxVal = 100;
        XYStep step = XYStepCalculator.getStep(XYStepMode.SUBDIVIDE, plotSize, numSegments, minVal, maxVal);

        assertEquals(plotSize/(numSegments-1), step.getStepPix());
        //assertEquals(10, step.getStepVal());

        // make sure large values dont break anything:
        minVal = 1000000000;
        maxVal = 2000000000;
        step = XYStepCalculator.getStep(XYStepMode.SUBDIVIDE, plotSize, numSegments, minVal, maxVal);
        assertEquals(plotSize/(numSegments-1), step.getStepPix());

    }
    
    @Test
    public void testIncrementByVal() throws Exception {

    }

    @Test
    public void testIncrementByPixels() throws Exception {

    }
}
