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
import android.view.View;
import com.androidplot.Plot;
import com.androidplot.PlotEvent;
import com.androidplot.PlotListener;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.DataRenderer;
import com.androidplot.ui.Formatter;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@UsingMocksAndStubs({View.class,Handler.class,Paint.class,Color.class,RectF.class})

public class PlotTest {

    @MockClass(realClass = Context.class)
    public static class MockContext {}

    /*@MockClass(realClass = Plot.class)
    public static class MockPlot {}*/

    static class MockPlotListener implements PlotListener {

        @Override
        public void onPlotUpdate(PlotEvent event) {}

        @Override
        public void onBeforeDraw(Plot source, Canvas canvas) {}

        @Override
        public void onAfterDraw(Plot source, Canvas canvas) {}
    }

    //@MockClass(realClass = Series.class)
    static class MockSeries implements Series {
        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        /*@Override
        public void onReadBegin() {

        }

        @Override
        public void onReadEnd() {

        }*/
    }

    static class MockSeries2 implements Series {
        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        /*@Override
        public void onReadBegin() {

        }

        @Override
        public void onReadEnd() {

        }*/
    }

    static class MockSeries3 implements Series {
        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        /*@Override
        public void onReadBegin() {

        }

        @Override
        public void onReadEnd() {

        }*/
    }

    static class MockRenderer1 extends DataRenderer {

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
    static class MockRenderer2 extends DataRenderer {

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

    static class MockFormatter {

    }

    //@MockClass(realClass = Plot.class)
    public static class MockPlot extends Plot<MockSeries, Formatter, DataRenderer> {
        public MockPlot(Context context, String title) {
            super(context, title);
        }

        @Override
        protected DataRenderer doGetRendererInstance(Class clazz) {
            if(clazz == MockRenderer1.class) {
                return new MockRenderer1(this);
            } else if(clazz == MockRenderer2.class) {
                return new MockRenderer2(this);
            } else {
                return null;
            }
        }

        @Override
        protected void doBeforeDraw() {

        }

        @Override
        protected void doAfterDraw() {

        }

    }

    @Before
    public void setUp() throws Exception {

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



        plot.addSeries(m1, cl, new MockFormatter());

        LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");
        assertEquals(1, registry.size());
        assertEquals(1, registry.get(cl).size());

        plot.addSeries(m1, cl, new MockFormatter());

        // duplicate Renderer added, registry size should not grow:
        assertEquals(1, registry.size());
        assertEquals(1, registry.get(cl).size());

        MockSeries m2 = new MockSeries();

        plot.addSeries(m2, cl, new MockFormatter());

        // still should only be one renderer type:
        assertEquals(1, registry.size());

        // we added a new instance of cl to the renderer so there should be 2 in the subregistry:
        assertEquals(2, registry.get(cl).size());

        // now let's introduce a new renderer:
        Class cl2 = MockRenderer2.class;

        // and add m1 to it:
        plot.addSeries(m1, cl2, new MockFormatter());

        assertEquals(2, registry.size());
    }

    @Test
    public void testRemoveSeries() throws Exception {

        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer1.class, new MockFormatter());

        plot.addSeries(m1, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer2.class, new MockFormatter());


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
        plot.addSeries(m1, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer1.class, new MockFormatter());

        plot.addSeries(m1, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer2.class, new MockFormatter());


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
        LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        MockFormatter f1 = new MockFormatter();
        MockFormatter f2 = new MockFormatter();
        MockFormatter f3 = new MockFormatter();

        plot.addSeries(m1, MockRenderer1.class, f1);
        plot.addSeries(m2, MockRenderer1.class, f2);
        plot.addSeries(m3, MockRenderer1.class, new MockFormatter());

        plot.addSeries(m1, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer2.class, f3);
        plot.addSeries(m3, MockRenderer2.class, new MockFormatter());

        assertEquals(registry.get(MockRenderer1.class).getFormatter(m1), f1);
        assertEquals(registry.get(MockRenderer1.class).getFormatter(m2), f2);
        assertEquals(registry.get(MockRenderer2.class).getFormatter(m2), f3);

        assertNotSame(registry.get(MockRenderer2.class).getFormatter(m2), f1);

    }

    @Test
    public void testGetSeriesListForRenderer() throws Exception {

        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        //LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer1.class, new MockFormatter());

        plot.addSeries(m1, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer2.class, new MockFormatter());

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
        //LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer1.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer1.class, new MockFormatter());

        plot.addSeries(m1, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m2, MockRenderer2.class, new MockFormatter());
        plot.addSeries(m3, MockRenderer2.class, new MockFormatter());

        List<DataRenderer> rList = plot.getRendererList();
        assertEquals(2, rList.size());

        assertEquals(MockRenderer1.class, rList.get(0).getClass());
        assertEquals(MockRenderer2.class, rList.get(1).getClass());
    }

    @Test
    public void testAddListener() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        Plot plot = new MockPlot(context, "MockPlot");
        ArrayList<PlotListener> listeners = Deencapsulation.getField(plot, "listeners");
        //LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

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
        //LinkedHashMap<Class<DataRenderer>, SeriesAndFormatterList<MockSeries,MockFormatter>> registry = Deencapsulation.getField(plot, "seriesRegistry");

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
}
