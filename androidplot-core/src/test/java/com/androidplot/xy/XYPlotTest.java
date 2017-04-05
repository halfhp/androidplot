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
import static org.mockito.Mockito.mock;

public class XYPlotTest extends AndroidplotTest {

    XYPlot plot;  // testing
    
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series0To100;

    @Before
    public void setUp() throws Exception {

        plot = new XYPlot(getContext(), "test");
        numList1 = Arrays.asList(0, 1, 3, 5, 10, 15, 25, 50, 75, 100); // 10 elements
        numList2 = Arrays.asList(-100, 0, 1, 3, 5, 10, 15, 25, 50, 75, 100, 200); // 12 elements
        series0To100 = new SimpleXYSeries(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOriginFixedMode() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, 2, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();


        assertEquals(3.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(7.0, plot.getBounds().getMaxX().doubleValue(), 0);
    }

    @Test
    public void testOriginAutoMode() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5);
        plot.calculateMinMaxVals();

        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0); // symmetry is @ 10, not 9
        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);

        plot.centerOnRangeOrigin(50);
        plot.calculateMinMaxVals();

        assertEquals(100.0, plot.getBounds().getMaxY().doubleValue(), 0);
        assertEquals(0.0, plot.getBounds().getMinY().doubleValue(), 0);

    }

    @Test
    public void testOriginGrowMode() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);

        // introduce a larger domain set.  boundaries should change
        series0To100.setModel(numList2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11.0, plot.getBounds().getMaxX().doubleValue(), 0);

        // revert series model back to the previous set.  boundaries should remain the same
        series0To100.setModel(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(-1.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11.0, plot.getBounds().getMaxX().doubleValue(), 0);
    }

    @Test
    public void testOriginShrinkMode() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);

        // update with more extreme values...nothing should change in shrink mode:
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);
                
    }

    @Test(expected = IllegalArgumentException.class)
    public void centerOnRangeOrigin_throwsIllegalArgumentException_ifNullOrigin() {
        plot.centerOnRangeOrigin(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void centerOnDomainOrigin_throwsIllegalArgumentException_ifNullOrigin() {
        plot.centerOnDomainOrigin(null);
    }

 // Ifor not sure about filling in test stubs just going to do my own stuff instead.
    @Test
    public void testsetDomainBoundaries() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so run them
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);

        plot.setDomainBoundaries(2, BoundaryMode.FIXED, 8, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(2, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(8, plot.getBounds().getMaxX().doubleValue(), 0);

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // run again
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);
        
        // we are not testing MinY well with this dataset.
        // try grow
        plot.setDomainBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // run inital
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);
        
        // update with more extreme values...
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);

        // back to previous
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);

        // back to big
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setDomainBoundaries(2, BoundaryMode.SHRINK, 8, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        // run inital
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);
        
        // now small
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);

        // back to previous
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);

        // back to auto
        plot.setDomainBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);
    }
    
    @Test
    public void setRangeBoundaries_calculatesCorrectMinMaxVals() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so run them
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);

        plot.setRangeBoundaries(5, BoundaryMode.FIXED, 80, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();

        // fixed
        assertEquals(5, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(80, plot.getBounds().getMaxY().doubleValue(), 0);

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();

        // run again
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);
        
        // try grow
        plot.setRangeBoundaries(2, BoundaryMode.GROW, 8, BoundaryMode.GROW);
        plot.calculateMinMaxVals();

        // run inital
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);
        
        // update with more extreme values...
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after growing
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);

        // back to previous
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);

        // back to big
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        
        plot.setRangeBoundaries(2, BoundaryMode.SHRINK, 8, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        // run inital
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);
        
        // now small
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // after shrinking
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);

        // back to previous
        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // should not of changed.
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);

        // back to auto
        plot.setRangeBoundaries(2, BoundaryMode.AUTO, 8, BoundaryMode.AUTO);
        plot.calculateMinMaxVals();
        
        // should of changed.
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);
    }
    
    @Test
    public void testSetDomainRightMinMax() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so run them
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);

        plot.getOuterLimits().setMaxX(10);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);

        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on RightMax
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10, plot.getBounds().getMaxX().doubleValue(), 0);

        plot.getOuterLimits().setMaxX(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);
        
        // now the RightMin
        plot.getInnerLimits().setMaxX(10);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(11, plot.getBounds().getMaxX().doubleValue(), 0);

        // small list
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on RightMin
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10, plot.getBounds().getMaxX().doubleValue(), 0);

        // now off again
        plot.getInnerLimits().setMaxX(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(9, plot.getBounds().getMaxX().doubleValue(), 0);
    }
    
    @Test
    public void testSetRangeTopBottomMinMax() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.calculateMinMaxVals();

        // default to auto so run them
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);

        plot.getOuterLimits().setMaxY(110);
        plot.getOuterLimits().setMinY(-50);
        plot.calculateMinMaxVals();

        // same values.
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);

        series0To100.setModel(numList2,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        // on Limits
        assertEquals(-50, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(110, plot.getBounds().getMaxY().doubleValue(), 0);

        plot.getOuterLimits().setMaxY(null);
        plot.getOuterLimits().setMinY(null);
        plot.calculateMinMaxVals();

        // back to full
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);
        
        // now the Min
        plot.getInnerLimits().setMaxY(150);
        plot.getInnerLimits().setMinY(-60);
        plot.calculateMinMaxVals();

        // still to full
        assertEquals(-100, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(200, plot.getBounds().getMaxY().doubleValue(), 0);

        // small list
        series0To100.setModel(numList1,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();
        
        // on Limits
        assertEquals(-60, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(150, plot.getBounds().getMaxY().doubleValue(), 0);

        // now off again
        plot.getInnerLimits().setMaxY(null);
        plot.getInnerLimits().setMinY(null);
        plot.calculateMinMaxVals();

        // small values.
        assertEquals(0, plot.getBounds().getMinY().doubleValue(), 0);
        assertEquals(100, plot.getBounds().getMaxY().doubleValue(), 0);
    }
    
    @Test
    public void setDomainUpperBoundary_overridesCalculatedBoundary() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.setDomainUpperBoundary(350, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertEquals(350, plot.getBounds().getMaxX().intValue());
    }

    @Test
    public void testSetDomainLowerBoundary() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.setDomainLowerBoundary(-350, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertEquals(-350, plot.getBounds().getMinX().intValue());
    }

    @Test
    public void testSetRangeUpperBoundary() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.setRangeUpperBoundary(350, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertEquals(350, plot.getBounds().getMaxY().intValue());
    }

    @Test
    public void testSetRangeLowerBoundary() throws Exception {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.setRangeLowerBoundary(-350, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertEquals(-350, plot.getBounds().getMinY().intValue());
    }

    @Test
    public void testSetDomainOrigin() throws Exception {
        // TODO
    }

    @Test
    public void testSetRangeOrigin() throws Exception {
        // TODO
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

    @Test
    public void removeMarker_withXMarker_removesExpectedXMarkerOnly() {

        XValueMarker xMarker1 = new XValueMarker(1, "x1");
        XValueMarker xMarker2 = new XValueMarker(2, "x2");
        XValueMarker xMarker3 = new XValueMarker(2, "x2");
        XValueMarker xMarker4 = new XValueMarker(2, "x2");
        XValueMarker xMarker5 = new XValueMarker(2, "x2");

        plot.addMarker(xMarker1);
        plot.addMarker(xMarker2);
        plot.addMarker(xMarker3);
        plot.addMarker(xMarker4);
        plot.addMarker(xMarker5);

        assertEquals(5, plot.getXValueMarkers().size());

        assertEquals(xMarker3, plot.removeMarker(xMarker3));
        assertEquals(4, plot.getXValueMarkers().size());
    }

    @Test
    public void removeMarker_withYMarker_removesExpectedYMarkerOnly() {

        YValueMarker YMarker1 = new YValueMarker(1, "Y1");
        YValueMarker YMarker2 = new YValueMarker(2, "Y2");
        YValueMarker YMarker3 = new YValueMarker(2, "Y2");
        YValueMarker YMarker4 = new YValueMarker(2, "Y2");
        YValueMarker YMarker5 = new YValueMarker(2, "Y2");

        plot.addMarker(YMarker1);
        plot.addMarker(YMarker2);
        plot.addMarker(YMarker3);
        plot.addMarker(YMarker4);
        plot.addMarker(YMarker5);

        assertEquals(5, plot.getYValueMarkers().size());

        assertEquals(YMarker3, plot.removeMarker(YMarker3));
        assertEquals(4, plot.getYValueMarkers().size());
    }
    
    @Test
    public void removeMarkers_removesAllXAndYMarkers() {

        XValueMarker xMarker1 = new XValueMarker(1, "x1");
        XValueMarker xMarker2 = new XValueMarker(2, "x2");
        XValueMarker xMarker3 = new XValueMarker(2, "x2");
        XValueMarker xMarker4 = new XValueMarker(2, "x2");
        XValueMarker xMarker5 = new XValueMarker(2, "x2");

        plot.addMarker(xMarker1);
        plot.addMarker(xMarker2);
        plot.addMarker(xMarker3);
        plot.addMarker(xMarker4);
        plot.addMarker(xMarker5);

        YValueMarker YMarker1 = new YValueMarker(1, "Y1");
        YValueMarker YMarker2 = new YValueMarker(2, "Y2");
        YValueMarker YMarker3 = new YValueMarker(2, "Y2");
        YValueMarker YMarker4 = new YValueMarker(2, "Y2");
        YValueMarker YMarker5 = new YValueMarker(2, "Y2");

        plot.addMarker(YMarker1);
        plot.addMarker(YMarker2);
        plot.addMarker(YMarker3);
        plot.addMarker(YMarker4);
        plot.addMarker(YMarker5);

        assertEquals(5, plot.getXValueMarkers().size());
        assertEquals(5, plot.getYValueMarkers().size());

        plot.removeMarkers();
        assertEquals(0, plot.getXValueMarkers().size());
        assertEquals(0, plot.getYValueMarkers().size());
    }
}
