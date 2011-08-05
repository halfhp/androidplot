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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({View.class,Context.class,Handler.class,Paint.class,Color.class, Rect.class, RectF.class,FontUtils.class, PixelUtils.class})

public class XYPlotTest {

    XYPlot plot;  // testing
    
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series1;

    @Before
    public void setUp() throws Exception {
        new MockUp<View>() {
            @Mock int getWidth() { return 100;}
            @Mock int getHeight() { return 100;}

        };

        plot = new XYPlot(null, "test");
        numList1 = Arrays.asList(0, 1, 3, 5, 10, 15, 25, 50, 75, 100); // 10 elements
        numList2 = Arrays.asList(-100, 0, 1, 3, 5, 10, 15, 25, 50, 75, 100, 200); // 12 elements
        series1 = new SimpleXYSeries(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOriginFixedMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, 2, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();


        assertEquals(3.0, plot.getCalculatedMinX());
        assertEquals(7.0, plot.getCalculatedMaxX());
    }

    @Test
    public void testOriginAutoMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5);
        plot.calculateMinMaxVals();
        //plot.updateMinMaxVals();

        assertEquals(10.0, plot.getCalculatedMaxX()); // symmetry is @ 10, not 9
        assertEquals(0.0, plot.getCalculatedMinX());

        plot.centerOnRangeOrigin(50);
        plot.calculateMinMaxVals();

        assertEquals(100.0, plot.getCalculatedMaxY());
        assertEquals(0.0, plot.getCalculatedMinY());

    }

    @Test
    public void testOriginGrowMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getCalculatedMinX());
        assertEquals(10.0, plot.getCalculatedMaxX());

        // introduce a larger domain set.  boundaries should change
        series1.setModel(numList2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getCalculatedMinX());
        assertEquals(11.0, plot.getCalculatedMaxX());

        // revert series model back to the previous set.  boundaries should remain the same
        series1.setModel(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getCalculatedMinX());
        assertEquals(11.0, plot.getCalculatedMaxX());
    }

    @Test
    public void testOriginShrinkMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.SHRINNK);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getCalculatedMinX());
        assertEquals(10.0, plot.getCalculatedMaxX());

        // update with more extreme values...nothing should change in shrink mode:
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        assertEquals(0.0, plot.getCalculatedMinX());
        assertEquals(10.0, plot.getCalculatedMaxX());
                
    }

    @Test
    public void testSetDomainUpperBoundary() throws Exception {

    }

    @Test
    public void testSetDomainLowerBoundary() throws Exception {

    }

    @Test
    public void testSetRangeUpperBoundary() throws Exception {

    }

    @Test
    public void testSetRangeLowerBoundary() throws Exception {

    }

    @Test
    public void testSetDomainOrigin() throws Exception {

    }

    @Test
    public void testSetRangeOrigin() throws Exception {

    }
}
