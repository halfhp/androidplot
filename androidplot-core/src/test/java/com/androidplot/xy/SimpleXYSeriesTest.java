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

import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static junit.framework.Assert.assertEquals;

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

    @Test
    public void testResize() throws Exception {
        SimpleXYSeries series = new SimpleXYSeries("series");
        series.resize(10);
        assertEquals(10, series.size());

        series.resize(20);
        assertEquals(20, series.size());

        series.resize(1);
        assertEquals(1, series.size());

        series.resize(0);
        assertEquals(0, series.size());

        series.resize(0);
        assertEquals(0, series.size());
    }

    @Test
    public void setXY_setsXAndY() {
        SimpleXYSeries series = new SimpleXYSeries("series");
        series.resize(5);
        series.setXY(100, 200, 0);

        assertEquals(100, series.getX(0));
        assertEquals(200, series.getY(0));

    }

    @Test(expected = NoSuchElementException.class)
    public void removeFirst_throwsNoSuchElementException_ifEmpty() {
        new SimpleXYSeries("series").removeFirst();
    }

    @Test(expected = NoSuchElementException.class)
    public void removeLast_throwsNoSuchElementException_ifEmpty() {
        new SimpleXYSeries("series").removeLast();
    }

    @Test
    public void setTitle_changesTitle() {
        SimpleXYSeries series = new SimpleXYSeries("series");

        final String newTitle = "newTitle";
        series.setTitle(newTitle);
        assertEquals(newTitle, series.getTitle());
    }

    @Test
    public void clear_removesEverything() {
        SimpleXYSeries series = new SimpleXYSeries(
                Arrays.asList(1, 2, 3, 4, 5),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "series");

        series.clear();
        assertEquals(0, series.size());
    }
}
