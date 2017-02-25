package com.androidplot.xy;

import com.androidplot.Region;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StepModelFitTest {

    Region regionSmall = new Region(0,11);
    Region regionBig = new Region(-111,420);
    Region regionZero = new Region(0, 0);
    Region regionUndef = new Region(0, null);

    double[] stpSmall = {1,2,5}, stpBig = {1,10,100}, nonsense = {0};

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getValue() throws Exception {

        StepModelFit model = new StepModelFit(regionSmall,stpSmall,3);

        assertEquals(5.0, model.getValue(), 0.0);
        model.setValue(5.0);
        assertEquals(2.0, model.getValue(), 0.0);
        model.setValue(7.0);
        assertEquals(2.0, model.getValue(), 0.0);

        model.setSteps(stpBig);
        assertEquals(1.0, model.getValue(), 0.0);

        model.setScale(regionBig);
        assertEquals(100.0, model.getValue(), 0.0);
        model.setValue(1000.0);
        assertEquals(1.0, model.getValue(), 0.0);

        // bad parameters
        model.setSteps(nonsense);
        assertArrayEquals(stpBig,model.getSteps(), 0.0);

        model.setScale(regionZero);
        assertEquals(stpBig[0], model.getValue(), 0.0);

        model.setScale(regionUndef);
        model.setValue(1.1);
        assertEquals(1.1, model.getValue(), 0.0);
    }

}