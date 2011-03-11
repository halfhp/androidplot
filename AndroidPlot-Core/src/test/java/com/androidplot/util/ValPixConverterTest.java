/*
Copyright 2010 Nick Fellows. All rights reserved.

Redistribution and use in source and binary forms, without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Nick Fellows ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NICK FELLOWS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Nick Fellows.
*/

package com.androidplot.util;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: halfhp
 * Date: 11/6/10
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
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
