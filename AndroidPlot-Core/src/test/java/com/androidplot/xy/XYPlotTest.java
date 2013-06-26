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

package com.androidplot.xy;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.androidplot.Plot;
import com.androidplot.PlotTest;
import com.androidplot.mock.MockContext;
import com.androidplot.mock.MockPaint;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.Configurator;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({Log.class,View.class,Context.class,Handler.class,Paint.class,Color.class,
        Rect.class, RectF.class,FontUtils.class, PixelUtils.class, Canvas.class})

public class XYPlotTest {

    XYPlot plot;  // testing
    
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series1;

    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockPaint.class,MockContext.class);
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


 // Ifor not sure about filling in test stubs just going to do my own stuff instead.
    @Test
    public void testsetDomainBoundaries() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());

        plot.setDomainBoundaries(2, BoundaryMode.FIXED, 8, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(2, plot.getCalculatedMinX());
        assertEquals(8, plot.getCalculatedMaxX());

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // check again
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());
        
        // we are not testing MinY well with this dataset.
        // try grow
        plot.setDomainBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());
        
        // update with more extreme values...
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());

        // back to previous
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());

        // back to big
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setDomainBoundaries(2, BoundaryMode.SHRINNK, 8, BoundaryMode.SHRINNK);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());
        
        // now small
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());

        // back to previous
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());
    }
    
    @Test
    public void testsetRangeBoundaries() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());

        plot.setRangeBoundaries(5, BoundaryMode.FIXED, 80, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(5, plot.getCalculatedMinY());
        assertEquals(80, plot.getCalculatedMaxY());

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // check again
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());
        
        // try grow
        plot.setRangeBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());
        
        // update with more extreme values...
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());

        // back to previous
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());

        // back to big
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setRangeBoundaries(2, BoundaryMode.SHRINNK, 8, BoundaryMode.SHRINNK);
        plot.calculateMinMaxVals();

        // check inital
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());
        
        // now small
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());

        // back to previous
        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());
    }
    
    @Test
    public void testSetDomainRightMinMax() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());
        
        plot.setDomainRightMax(10);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());

        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on RightMax
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(10, plot.getCalculatedMaxX());

        plot.setDomainRightMax(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());
        
        // now the RightMin
        plot.setDomainRightMin(10);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(11, plot.getCalculatedMaxX());

        // small list
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on RightMin
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(10, plot.getCalculatedMaxX());

        // now off again
        plot.setDomainRightMin(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getCalculatedMinX());
        assertEquals(9, plot.getCalculatedMaxX());
    }
    
    @Test
    public void testSetRangeTopBottomMinMax() throws Exception {
        plot.addSeries(series1, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so check them
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());
        
        plot.setRangeTopMax(110);
        plot.setRangeBottomMin(-50);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());

        series1.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on Limits
        assertEquals(-50, plot.getCalculatedMinY());
        assertEquals(110, plot.getCalculatedMaxY());

        plot.setRangeTopMax(null);
        plot.setRangeBottomMin(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());
        
        // now the Min
        plot.setRangeTopMin(150);
        plot.setRangeBottomMax(-60);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(-100, plot.getCalculatedMinY());
        assertEquals(200, plot.getCalculatedMaxY());

        // small list
        series1.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on Limits
        assertEquals(-60, plot.getCalculatedMinY());
        assertEquals(150, plot.getCalculatedMaxY());

        // now off again
        plot.setRangeTopMin(null);
        plot.setRangeBottomMax(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getCalculatedMinY());
        assertEquals(100, plot.getCalculatedMaxY());
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
        //Context context = Mockit.setUpMock(new MockContext());
        Context context = new MockContext.MockContext2();
        HashMap<String, String> params = new HashMap<String, String>();
        String param1 = "this is a test.";
        String param2 = Plot.RenderMode.USE_BACKGROUND_THREAD.toString();
        String param3 = "#FF0000";
        params.put("title", param1);
        params.put("renderMode", param2);
        params.put("backgroundPaint.color", param3);
        params.put("graphWidget.domainLabelPaint.color", param3);

        Configurator.configure(context, plot, params);
        assertEquals(param1, plot.getTitle());
        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, plot.getRenderMode());
        assertEquals(Color.parseColor(param3), plot.getBackgroundPaint().getColor());
        assertEquals(Color.parseColor(param3), plot.getGraphWidget().getDomainLabelPaint().getColor());
    }
}
