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

package com.androidplot;

import android.content.res.TypedArray;
import android.graphics.*;
import android.util.*;

import com.androidplot.exception.PlotRenderException;
import com.androidplot.test.*;
import com.androidplot.ui.*;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.halfhp.fig.*;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PlotTest extends AndroidplotTest {

    @Mock
    SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> mockSeriesRegistry;

    @Test
    public void testInit_withoutAttrs() throws Exception {
        Plot plot = mock(Plot.class);
        plot.init(RuntimeEnvironment.application, null, 0);

        verify(plot, times(1)).onPreInit();
        verify(plot, times(1)).onAfterConfig();
    }

    @Test
    public void testInit_withAttrs() throws Exception {
        Plot plot = new MockPlot("MockPlot");
        AttributeSet attrs = mock(AttributeSet.class);
        plot.init(RuntimeEnvironment.application, attrs, 0);
        // TODO: verifications
    }

    @Test
    public void testAddSeries() throws Exception {
        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        Class cl = MockRenderer1.class;

        plot.addSeries(m1, new MockFormatter1());
        assertEquals(1, plot.getRegistry().size());

        // a new copy of m1 is added:
        plot.addSeries(m1, new MockFormatter1());

        assertEquals(1, plot.getRenderers().size());
        assertEquals(2, plot.getRenderer(cl).getSeriesList().size());

        MockSeries m2 = new MockSeries();

        plot.addSeries(m2, new MockFormatter1());

        // still should only be one renderer type:
        assertEquals(1, plot.getRendererList().size());

        // we added a new instance of cl to the renderer so there should be 3 in the subregistry:
        assertEquals(3, plot.getRenderer(cl).getSeriesList().size());


        // lets add another renderer:
        plot.addSeries(m1, new MockFormatter2());

        assertEquals(2, plot.getRendererList().size());
    }

    @Test
    public void testRemoveSeries() throws Exception {

        Plot plot = new MockPlot("MockPlot");

        MockSeries m1 = new MockSeries();
        MockSeries m2 = new MockSeries();
        MockSeries m3 = new MockSeries();

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter2());
        plot.addSeries(m2, new MockFormatter2());
        plot.addSeries(m3, new MockFormatter2());


        // a quick sanity run:
        assertEquals(2, plot.getRendererList().size());
        assertEquals(3, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(3, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        plot.removeSeries(m1, MockRenderer1.class);
        assertEquals(2, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m2, MockRenderer1.class);
        assertEquals(1, plot.getRenderer(MockRenderer1.class).getSeriesList().size());

        plot.removeSeries(m3, MockRenderer1.class);

        // add em all back
        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());

        plot.addSeries(m1, new MockFormatter1());
        plot.addSeries(m2, new MockFormatter1());
        plot.addSeries(m3, new MockFormatter1());


        // a quick sanity run:
        assertEquals(2, plot.getRendererList().size());
        assertEquals(6, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(3, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        // now lets try removing a series from all renderers:
        plot.removeSeries(m1);
        assertEquals(4, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertEquals(2, plot.getRenderer(MockRenderer2.class).getSeriesList().size());

        // and now lets remove the remaining series:
        plot.removeSeries(m2);
        plot.removeSeries(m3);
    }


    @Test
    public void testGetFormatter() throws Exception {
        Plot plot = new MockPlot("MockPlot");

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

        assertEquals(plot.getRenderer(MockRenderer1.class).getFormatter(m1), f1);
        assertEquals(plot.getRenderer(MockRenderer1.class).getFormatter(m2), f2);
        assertEquals(plot.getRenderer(MockRenderer2.class).getFormatter(m2), f3);

        assertNotSame(plot.getRenderer(MockRenderer2.class).getFormatter(m2), f1);

    }

    @Test
    public void testGetRendererList() throws Exception {

        Plot plot = new MockPlot("MockPlot");

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
    }

    @Test
    public void testAddListener() throws Exception {
        Plot plot = new MockPlot("MockPlot");
        ArrayList<PlotListener> listeners = plot.getListeners();

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
        Plot plot = new MockPlot("MockPlot");
        ArrayList<PlotListener> listeners = plot.getListeners();

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

    @Test
    public void testConfigure() throws Exception {
        Plot plot = new MockPlot("MockPlot");

        HashMap<String, String> params = new HashMap<String, String>();
        String param1 = "this is a test.";
        String param2 = "use_background_thread";
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
    public void setTitle_setsTitle() {
        Plot plot = new MockPlot("foo");
        plot.setTitle("bar");
        assertEquals("bar", plot.getTitle().getText());
    }

    @Test
    public void clear_unregistersAllPlotListeners() {
        Plot plot = new MockPlot("MockPlot");
        plot.addSeries(new MockSeries(), new MockFormatter1());
        plot.addSeries(new MockSeries(), new MockFormatter1());
        plot.addSeries(new MockSeries(), new MockFormatter1());
        assertEquals(3, plot.getListeners().size());

        plot.clear();
        assertEquals(0, plot.getListeners().size());
    }

    @Test
    public void clear_clearsRegistry() {
        Plot plot = new MockPlot("MockPlot");
        plot.setRegistry(mockSeriesRegistry);

        plot.clear();
        verify(mockSeriesRegistry).clear();
    }

    @Test
    public void setPlotMargins_updatesMargins() {
        Plot plot = new MockPlot("MockPlot");
        plot.setPlotMargins(11, 22, 33, 44);

        assertEquals(11f, plot.getPlotMarginLeft());
        assertEquals(22f, plot.getPlotMarginTop());
        assertEquals(33f, plot.getPlotMarginRight());
        assertEquals(44f, plot.getPlotMarginBottom());
    }

    @Test
    public void setPlotPadding_updatesPadding() {
        Plot plot = new MockPlot("MockPlot");
        plot.setPlotPadding(11, 22, 33, 44);

        assertEquals(11f, plot.getPlotPaddingLeft());
        assertEquals(22f, plot.getPlotPaddingTop());
        assertEquals(33f, plot.getPlotPaddingRight());
        assertEquals(44f, plot.getPlotPaddingBottom());
    }

    static class MockPlotListener implements PlotListener {

        public void onBeforeDraw(Plot source, Canvas canvas) {
        }

        public void onAfterDraw(Plot source, Canvas canvas) {
        }
    }

    static class MockSeries implements Series, PlotListener {

        public String getTitle() {
            return null;
        }

        @Override
        public void onBeforeDraw(Plot source, Canvas canvas) {

        }

        @Override
        public void onAfterDraw(Plot source, Canvas canvas) {

        }
    }

    static class MockSeries2 implements Series {

        public String getTitle() {
            return null;
        }
    }

    static class MockSeries3 implements Series {

        public String getTitle() {
            return null;
        }
    }

    static class MockRenderer1 extends SeriesRenderer {

        public MockRenderer1(Plot plot) {
            super(plot);
        }

        @Override
        public void onRender(Canvas canvas, RectF plotArea, Series series, Formatter formatter, RenderStack stack) throws PlotRenderException {

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
        public void onRender(Canvas canvas, RectF plotArea, Series series, Formatter formatter, RenderStack stack) throws PlotRenderException {

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
        public SeriesRenderer doGetRendererInstance(MockPlot plot) {
            return new MockRenderer1(plot);
        }
    }

    static class MockFormatter2 extends Formatter<MockPlot> {

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MockRenderer2.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(MockPlot plot) {
            return new MockRenderer2(plot);
        }
    }

    public static class MockSeriesBundle extends SeriesBundle<MockSeries, Formatter> {

        public MockSeriesBundle(MockSeries series, Formatter formatter) {
            super(series, formatter);
        }
    }

    public static class MockPlot extends Plot<MockSeries, Formatter, SeriesRenderer, MockSeriesBundle, SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>> {
        public MockPlot(String title) {
            super(RuntimeEnvironment.application, title);
        }

        @Override
        protected SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> getRegistryInstance() {
            return new SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>() {
                @Override
                protected MockSeriesBundle newSeriesBundle(
                        MockSeries series, Formatter formatter) {
                    return new MockSeriesBundle(series, formatter);
                }
            };
        }

        @Override
        protected void onPreInit() {

        }

        @Override
        protected void processAttrs(TypedArray attrs) {

        }
    }
}
