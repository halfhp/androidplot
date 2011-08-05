/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.xy;

import android.util.Pair;
import mockit.UsingMocksAndStubs;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({Pair.class})

public class SimpleXYSeriesTest {

    @Test
    public void testYValsOnlyConstructor() throws Exception {
        Number[] yVals = {5, 6, 7, 8, 9};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "test");

        assertEquals(yVals[0], series.getY(0));
        assertEquals(yVals[1], series.getY(1));
        assertEquals(yVals[2], series.getY(2));
        assertEquals(yVals[3], series.getY(3));
        assertEquals(yVals[4], series.getY(4));

        assertEquals(0, series.getX(0));
        assertEquals(1, series.getX(1));
        assertEquals(2, series.getX(2));
        assertEquals(3, series.getX(3));
        assertEquals(4, series.getX(4));
    }

    @Test
    public void testXYInterleavedConstructor() throws Exception {
        Number[] yVals = {55, 5, 66, 6, 77, 7, 88, 8, 99, 9};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "test");

        assertEquals(5, series.getY(0));
        assertEquals(6, series.getY(1));
        assertEquals(7, series.getY(2));
        assertEquals(8, series.getY(3));
        assertEquals(9, series.getY(4));

        assertEquals(55, series.getX(0));
        assertEquals(66, series.getX(1));
        assertEquals(77, series.getX(2));
        assertEquals(88, series.getX(3));
        assertEquals(99, series.getX(4));
    }

    @Test
    public void testTwoListConstructor() throws Exception {
        Number[] yVals = {5, 6, 7, 8, 9};
        Number[] xVals = {1, 2, 3, 4, 5};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "test");

        assertEquals(5, series.getY(0));
        assertEquals(6, series.getY(1));
        assertEquals(7, series.getY(2));
        assertEquals(8, series.getY(3));
        assertEquals(9, series.getY(4));

        assertEquals(1, series.getX(0));
        assertEquals(2, series.getX(1));
        assertEquals(3, series.getX(2));
        assertEquals(4, series.getX(3));
        assertEquals(5, series.getX(4));
    }

    @Test
    public void testPushPopStuff() throws Exception {
        Number[] yVals = {5, 6, 7, 8, 9};
        Number[] xVals = {1, 2, 3, 4, 5};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "test");

        // chop off the tail:
        series.removeLast();
        assertEquals(8, series.getY(series.size()-1));
        assertEquals(4, series.getX(series.size()-1));

        // chop off the head:
        series.removeFirst();
        assertEquals(6, series.getY(0));
        assertEquals(2, series.getX(0));

        // add to the tail:
        series.addLast(22, 33);
        assertEquals(33, series.getY(series.size()-1));
        assertEquals(22, series.getX(series.size()-1));

        // add to the head:
        series.addFirst(55, 66);
        assertEquals(66, series.getY(0));
        assertEquals(55, series.getX(0));
    }

    @Test
    public void testSet() throws Exception {
        Number[] yVals = {5, 6, 7, 8, 9};
        Number[] xVals = {1, 2, 3, 4, 5};
        SimpleXYSeries series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "test");

        int size = series.size();

        series.setX(22, 2);
        assertEquals(22, series.getX(2));

        // make sure size has not changed:
        assertEquals(size, series.size());

        series.setY(23, 2);
        assertEquals(23, series.getY(2));

        // make sure size has not changed:
        assertEquals(size, series.size());
    }

}
