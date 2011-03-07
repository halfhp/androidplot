package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import com.androidplot.util.FontUtils;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({View.class,Handler.class,Paint.class,Color.class, Rect.class, FontUtils.class})


public class XYPlotTest {

    XYPlot plot;  // testing
    
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series1;
    int x;


    @MockClass(realClass=View.class)
    public static class MockXYPlot {

        @Mock
        public int getWidth() { return 100;}
    }

    //@Mocked XYPlot plot;


    //@MockClass(realClass = Context.class)
    //public static class MockContext {}

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

        //plot.addSeries(series1, new LineAndPointFormatter(null, null));
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
