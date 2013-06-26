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

package com.androidplot;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.androidplot.mock.MockContext;
import com.androidplot.mock.MockPaint;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Formatter;
//import mockit.*;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.Configurator;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@UsingMocksAndStubs({Log.class, View.class,Handler.class,Paint.class,Color.class,
        RectF.class, Rect.class, FontUtils.class, Canvas.class,
        PixelUtils.class,Context.class})

public class PlotTest {

    static class MockPlotListener implements PlotListener {

        @Override
        public void onBeforeDraw(Plot source, Canvas canvas) {}

        @Override
        public void onAfterDraw(Plot source, Canvas canvas) {}
    }

    static class MockSeries implements Series {
        @Override
        public String getTitle() {
            return null;
        }

    }

    static class MockSeries2 implements Series {
        @Override
        public String getTitle() {
            return null;
        }
    }

    static class MockSeries3 implements Series {
        @Override
        public String getTitle() {
            return null;
        }
    }

    static class MockRenderer1 extends SeriesRenderer {

        public MockRenderer1(Plot plot) {
            super(plot);
        }

        @Override
        public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {

        }

        @Override
        public void doDrawLegendIcon(Canvas canvas, RectF rect, Formatter formatter) {

        }
    }
    static class MockRenderer2 extends SeriesRenderer {

        public MockRenderer2(Plot plot) {
            super(plot);
        }

        @Override
        public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {

        }

        @Override
        public void doDrawLegendIcon(Canvas canvas, RectF rect, Formatter formatter) {

        }
    }

    static class MockFormatter1 extends Formatter<MockPlot> {

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MockRenderer1.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(MockPlot plot) {
            return new MockRenderer1(plot);
        }
    }

    static class MockFormatter2 extends Formatter<MockPlot> {

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MockRenderer2.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(MockPlot plot) {
            return new MockRenderer2(plot);
        }
    }

    //@MockClass(realClass = Plot.class)
    public static class MockPlot extends Plot<MockSeries, Formatter, SeriesRenderer> {
        public MockPlot(Context context, String title) {
            super(context, title);
        }

        @Override
        protected void onPreInit() {

        }

        /*@Override
        protected SeriesRenderer doGetRendererInstance(Class clazz) {
            if(clazz == MockRenderer1.class) {
                return new MockRenderer1(this);
            } else if(clazz == MockRenderer2.class) {
                return new MockRenderer2(this);
            } else {
                return null;
            }
        }*/
    }

    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockPaint.class,MockContext.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddSeries() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        //Plot plot = Mockit.setUpMock(Plot.class, new MockPlot(context, "MockPlot"));
        //Plot plot = Mockit.setUpMock(new MockPlot());
        Plot plot = new MockPlot(context, "MockPlot");

        MockSeries m1 = new MockSeries();
        Class cl = MockRenderer1.class;



        plot.addSeries(m1, new MockFormatter1());

        LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");
        assertEquals(1, registry.size());
        assertEquals(1, registry.get(cl).size());

        plot.addSeries(m1, new MockFormatter1());

        // duplicate Renderer added, registry size should not grow:
        assertEquals(1, registry.size());
        assertEquals(1, registry.get(cl).size());

        MockSeries m2 = new MockSeries();

        plot.addSeries(m2, new MockFormatter1());

        // still should only be one renderer type:
        assertEquals(1, registry.size());

        // we added a new instance of cl to the renderer so there should be 2 in the subregistry:
        assertEquals(2, registry.get(cl).size());


        // lets add another renderer:
        plot.addSeries(m1, new MockFormatter2());

        assertEquals(2, registry.size());
    }

    @Test
    public void testRemoveSeries() throws Exception {

        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter2());
        plot.addSeries(m2, new MockFormatter2());
        plot.addSeries(m3, new MockFormatter2());


        // a quick sanity check:
        assertEquals(2, registry.size());
        assertEquals(3, registry.get(MockRenderer1.class).size());
        assertEquals(3, registry.get(MockRenderer2.class).size());

        plot.removeSeries(m1, MockRenderer1.class);
        assertEquals(2, registry.get(MockRenderer1.class).size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, registry.get(MockRenderer1.class).size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, registry.get(MockRenderer1.class).size());

        plot.removeSeries(m3, MockRenderer1.class);

        // all the elements should be gone from MockRenderer1, thus the renderer should
        // also be gone:
        assertNull(registry.get(MockRenderer1.class));


        // add em all back
        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());


        // a quick sanity check:
        assertEquals(2, registry.size());
        assertEquals(3, registry.get(MockRenderer1.class).size());
        assertEquals(3, registry.get(MockRenderer2.class).size());

        // now lets try removing a series from all renderers:
        plot.removeSeries(m1);
        assertEquals(2, registry.get(MockRenderer1.class).size());
        assertEquals(2, registry.get(MockRenderer2.class).size());

        // and now lets remove the remaining series:
        plot.removeSeries(m2);
        plot.removeSeries(m3);

        // nothing should be left:
        assertNull(registry.get(MockRenderer1.class));
        assertNull(registry.get(MockRenderer2.class));
    }


    @Test
    public void testGetFormatter() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        MockFormatter1 f1 = new MockFormatter1();
        MockFormatter1 f2 = new MockFormatter1();
        MockFormatter2 f3 = new MockFormatter2();

        plot.addSeries(m1, f1);
        plot.addSeries(m2, f2);
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, f3);
        plot.addSeries(m3, new MockFormatter1());

        assertEquals(registry.get(MockRenderer1.class).getFormatter(m1), f1);
        assertEquals(registry.get(MockRenderer1.class).getFormatter(m2), f2);
        assertEquals(registry.get(MockRenderer2.class).getFormatter(m2), f3);

        assertNotSame(registry.get(MockRenderer2.class).getFormatter(m2), f1);

    }

    @Test
    public void testGetSeriesListForRenderer() throws Exception {

        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        //LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        List<MockSeries> m1List = plot.getSeriesListForRenderer(MockRenderer1.class);
        assertEquals(3, m1List.size());
        assertEquals(m1, m1List.get(0));
        assertNotSame(m2, m1List.get(0));
        assertEquals(m2, m1List.get(1));
        assertEquals(m3, m1List.get(2));
    }

    @Test
    public void testGetRendererList() throws Exception {

        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        //LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter2());
        plot.addSeries(m2, new MockFormatter2());
        plot.addSeries(m3, new MockFormatter2());

        List<SeriesRenderer> rList = plot.getRendererList();
        assertEquals(2, rList.size());

        assertEquals(MockRenderer1.class, rList.get(0).getClass());
        assertEquals(MockRenderer2.class, rList.get(1).getClass());
    }

    @Test
    public void testAddListener() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        ArrayList<PlotListener> listeners = Deencapsulation.getField(plot, "listeners");
        //LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        assertEquals(0, listeners.size());

        MockPlotListener pl1 = new MockPlotListener();
        MockPlotListener pl2 = new MockPlotListener();

        plot.addListener(pl1);

        assertEquals(1, listeners.size());

        // should return false on a double entry attempt
        assertFalse(plot.addListener(pl1));

        // make sure the listener wasnt added anyway:
        assertEquals(1, listeners.size());

        plot.addListener(pl2);

        assertEquals(2, listeners.size());
                
    }

    @Test
    public void testRemoveListener() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        ArrayList<PlotListener> listeners = Deencapsulation.getField(plot, "listeners");
        //LinkedHashMap<Class<SeriesRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter1>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        assertEquals(0, listeners.size());

        MockPlotListener pl1 = new MockPlotListener();
        MockPlotListener pl2 = new MockPlotListener();
        MockPlotListener pl3 = new MockPlotListener();

        plot.addListener(pl1);
        plot.addListener(pl2);

        assertEquals(2, listeners.size());

        assertFalse(plot.removeListener(pl3));

        assertTrue(plot.removeListener(pl1));

        assertEquals(1, listeners.size());

        assertFalse(plot.removeListener(pl1));

        assertEquals(1, listeners.size());

        assertTrue(plot.removeListener(pl2));

        assertEquals(0, listeners.size());

    }

    /*@Test
    public void testGuessGetterName() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");

        Method m = Plot.class.getDeclaredMethod("guessGetterMethod", Object.class, String.class);
        assertNotNull(m);
    }

    @Test
    public void testGuessSetterName() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");

        Method m = Plot.class.getDeclaredMethod("guessSetterMethod", Object.class, String.class, Class.class);
        assertNotNull(m);
    }*/



    @Test
    public void testConfigure() throws Exception {
        //Context context = Mockit.setUpMock(new MockContext.MockContext2());
        Context context = new MockContext.MockContext2();
        Plot plot = new MockPlot(context, "MockPlot");

        HashMap<String, String> params = new HashMap<String, String>();
        String param1 = "this is a test.";
        //String param2 = Plot.RenderMode.USE_BACKGROUND_THREAD.toString();
        String param2 = "use_background_thread";
        String param3 = "#FF0000";
        params.put("title", param1);
        params.put("renderMode", param2);
        params.put("backgroundPaint.color", param3);


        //Method m = Plot.class.getDeclaredMethod("configure", params.getClass());
        //m.setAccessible(true);
        //m.invoke(plot, params);
        Configurator.configure(context, plot, params);

        assertEquals(param1, plot.getTitle());
        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, plot.getRenderMode());
        assertEquals(Color.parseColor(param3), plot.getBackgroundPaint().getColor());
    }
}
