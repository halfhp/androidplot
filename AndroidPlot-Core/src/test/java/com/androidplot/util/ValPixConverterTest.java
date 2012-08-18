/*
 * Copyright 2012 AndroidPlot.com
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

package com.androidplot.util;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ValPixConverterTest {
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    /*
    @org.junit.Test
    public void testIndexToPix() throws Exception {
        int sizeInPix = 100;
        int itemCount = 10;
        assertEquals(10.0f, ValPixConverter.indexToPix(1, itemCount, sizeInPix));

        try {
           ValPixConverter.indexToPix(100, 10, 100);
            fail("IndexOutOfBoundsException expected.");
        } catch(IndexOutOfBoundsException ex) {

        }

    }
    */

    @org.junit.Test
    public void testValToPix() throws Exception {
        int sizeInPix = 100;
        int min = 0;
        int max = 100;

        int value = 50;

        assertEquals(50.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be closer to the top:
        // (remember that 0,0 is the top left pixel)
        value = 75;
        assertEquals(25.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be at the very top:
        value = 100;
        assertEquals(0.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be at the very top:
        value = 0;
        assertEquals(100.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be smack in the middle:
        min = -100;
        value = 0;
        assertEquals(50.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be at the very bottom:
        value = 100;
        assertEquals(0.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        // should be in the middle:
        value = 0;
        assertEquals(50.0f, ValPixConverter.valToPix(value, min, max, sizeInPix, true));

        min = -100;
        max = 100;
        sizeInPix = 200;
        assertEquals(0f, ValPixConverter.valToPix(-100, min, max, sizeInPix, false));
        assertEquals(200f, ValPixConverter.valToPix(100, min, max, sizeInPix, false));
    }

    @org.junit.Test
    public void testpixToVal() throws Exception {
        int sizeInPix = 100;
        int min = 0;
        int max = 100;

        double value = 50;

        float pixel = ValPixConverter.valToPix(value, min, max, sizeInPix, true);
        assertEquals(value, ValPixConverter.pixToVal(pixel, min, max, sizeInPix, true));

        value = 75;
        pixel = ValPixConverter.valToPix(value, min, max, sizeInPix, true);
        assertEquals(value, ValPixConverter.pixToVal(pixel, min, max, sizeInPix, true));


        min = -100;
        value = 50;
        pixel = ValPixConverter.valToPix(value, min, max, sizeInPix, true);
        assertEquals(value, ValPixConverter.pixToVal(pixel, min, max, sizeInPix, true));

        min = -100;
        value = 60;
        pixel = ValPixConverter.valToPix(value, min, max, sizeInPix, true);
        assertEquals(value, ValPixConverter.pixToVal(pixel, min, max, sizeInPix, true));

        min =  20;
        value = 50;
        pixel = ValPixConverter.valToPix(value, min, max, sizeInPix, true);
        assertEquals(value, ValPixConverter.pixToVal(pixel, min, max, sizeInPix, true));

        try {
            ValPixConverter.pixToVal(-5, 0, 0, 0, true);
            fail("IllegalArgumentException expected.");
        } catch(IllegalArgumentException ex) {
            
        }
        

    }

    
    @Test
    public void testValPerPix() {
        //double result = ;
        assertEquals(1.0, ValPixConverter.valPerPix(0, 100, 100));
        double expected = 200d/100;
        assertEquals(expected, ValPixConverter.valPerPix(100, 300, 100));
        expected = 50d/100;
        assertEquals(expected, ValPixConverter.valPerPix(0, 50, 100));
        expected = 200d/100;
        assertEquals(expected, ValPixConverter.valPerPix(-100, 100, 100));
    }
}
