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
import com.androidplot.Plot;
import com.androidplot.test.AndroidplotTest;
import com.halfhp.fig.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class XYPlotTest extends AndroidplotTest {

    XYPlot plot;  // testing
    
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series1;

    @Before
    public void setUp() throws Exception {

        plot = new XYPlot(getContext(), "test");
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


        assertEquals(3.0, plot.getBounds().getMinX());
        assertEquals(7.0, plot.getBounds().getMaxX());
    }

    @Test
    public void testOriginAutoMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5);
        plot.calculateMinMaxVals();

        assertEquals(10.0, plot.getBounds().getMaxX()); // symmetry is @ 10, not 9
        assertEquals(0.0, plot.getBounds().getMinX());

        plot.centerOnRangeOrigin(50);
        plot.calculateMinMaxVals();

        assertEquals(100.0, plot.getBounds().getMaxY());
        assertEquals(0.0, plot.getBounds().getMinY());

    }

    @Test
    public void testOriginGrowMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX());
        assertEquals(10.0, plot.getBounds().getMaxX());

        // introduce a larger domain set.  boundaries should change
        series1.setModel(numList2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getBounds().getMinX());
        assertEquals(11.0, plot.getBounds().getMaxX());

        // revert series model back to the previous set.  boundaries should remain the same
        series1.setModel(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getBounds().getMinX());
        assertEquals(11.0, plot.getBounds().getMaxX());
    }

    @Test
    public void testOriginShrinkMode() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX());
        assertEquals(10.0, plot.getBounds().getMaxX());

        // update with more extreme values...nothing should change in shrink mode:
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        assertEquals(0.0, plot.getBounds().getMinX());
        assertEquals(10.0, plot.getBounds().getMaxX());
                
    }


 // Ifor not sure about filling in test stubs just going to do my own stuff instead.
    @Test
    public void testsetDomainBoundaries() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());

        plot.setDomainBoundaries(2, BoundaryMode.FIXED, 8, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(2, plot.getBounds().getMinX());
        assertEquals(8, plot.getBounds().getMaxX());

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // check again
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());
        
        // we are not testing MinY well with this dataset.
        // try grow
        plot.setDomainBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());
        
        // update with more extreme values...
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());

        // back to previous
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());

        // back to big
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setDomainBoundaries(2, BoundaryMode.SHRINK, 8, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());
        
        // now small
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());

        // back to previous
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());
    }
    
    @Test
    public void testsetRangeBoundaries() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());

        plot.setRangeBoundaries(5, BoundaryMode.FIXED, 80, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(5, plot.getBounds().getMinY());
        assertEquals(80, plot.getBounds().getMaxY());

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // check again
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());
        
        // try grow
        plot.setRangeBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());
        
        // update with more extreme values...
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());

        // back to previous
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());

        // back to big
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setRangeBoundaries(2, BoundaryMode.SHRINK, 8, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());
        
        // now small
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());

        // back to previous
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());
    }
    
    @Test
    public void testSetDomainRightMinMax() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());
        
        plot.setDomainRightMax(10);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());

        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on RightMax
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(10, plot.getBounds().getMaxX());

        plot.setDomainRightMax(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());
        
        // now the RightMin
        plot.setDomainRightMin(10);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(11, plot.getBounds().getMaxX());

        // small list
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on RightMin
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(10, plot.getBounds().getMaxX());

        // now off again
        plot.setDomainRightMin(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getBounds().getMinX());
        assertEquals(9, plot.getBounds().getMaxX());
    }
    
    @Test
    public void testSetRangeTopBottomMinMax() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());
        
        plot.setRangeTopMax(110);
        plot.setRangeBottomMin(-50);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());

        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on Limits
        assertEquals(-50, plot.getBounds().getMinY());
        assertEquals(110, plot.getBounds().getMaxY());

        plot.setRangeTopMax(null);
        plot.setRangeBottomMin(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());
        
        // now the Min
        plot.setRangeTopMin(150);
        plot.setRangeBottomMax(-60);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(-100, plot.getBounds().getMinY());
        assertEquals(200, plot.getBounds().getMaxY());

        // small list
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on Limits
        assertEquals(-60, plot.getBounds().getMinY());
        assertEquals(150, plot.getBounds().getMaxY());

        // now off again
        plot.setRangeTopMin(null);
        plot.setRangeBottomMax(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getBounds().getMinY());
        assertEquals(100, plot.getBounds().getMaxY());
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

    @Test
    public void testConfigure() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        String param1 = "this is a test.";
        String param2 = Plot.RenderMode.USE_BACKGROUND_THREAD.toString();
        String param3 = "#FF0000";
        params.put("title.text", param1);
        params.put("renderMode", param2);
        params.put("backgroundPaint.color", param3);

        Fig.configure(RuntimeEnvironment.application, plot, params);
        assertEquals(param1, plot.getTitle().getText());
        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, plot.getRenderMode());
        assertEquals(Color.parseColor(param3), plot.getBackgroundPaint().getColor());
    }
}
