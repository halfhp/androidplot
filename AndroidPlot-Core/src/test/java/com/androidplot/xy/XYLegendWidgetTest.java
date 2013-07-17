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
import android.util.Pair;
import android.view.View;
import com.androidplot.Plot;
import com.androidplot.mock.*;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.ValPixConverter;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

@UsingMocksAndStubs({Log.class,View.class,Handler.class,Paint.class,Color.class, Rect.class,
        FontUtils.class, Paint.FontMetrics.class,Bitmap.class, Pair.class})

public class XYLegendWidgetTest {

    @MockClass(realClass = Context.class)
    public static class MockContext {}

    @MockClass(realClass = TextLabelWidget.class)
    public static class MockTextLabelWidget {
        @Mock
        public void doOnDraw(Canvas canvas, RectF widgetRect) {}
    }

    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockCanvas.class, MockRectF.class, MockSizeMetrics.class,
                MockPointF.class, MockLooper.class, MockPixelUtils.class, MockTextLabelWidget.class);
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testDoOnDraw() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        XYPlot plot = new XYPlot(context, "Test", Plot.RenderMode.USE_MAIN_THREAD);

        SimpleXYSeries s1 = new SimpleXYSeries((Arrays.asList(1, 2, 3)),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1");

        plot.addSeries(s1, new LineAndPointFormatter(
                Color.RED, Color.GREEN, Color.BLUE, (PointLabelFormatter)null));

        assertEquals(1, plot.getSeriesSet().size());

        Deencapsulation.invoke(plot, "onSizeChanged", 100, 100, 100, 100);
        plot.redraw();
        // have to manually invoke this because the invalidate()
        // invoked by redraw() is a stub and will not result in onDraw being called.
        Deencapsulation.invoke(plot, "onDraw", new Canvas());

        plot.removeSeries(s1);
        assertEquals(0, plot.getSeriesSet().size());
        plot.addSeries(s1, new BarFormatter(Color.RED, Color.GREEN));
        plot.redraw();

        // throws NullPointerException before fix
        // for ANDROIDPLOT-166 was applied.
        Deencapsulation.invoke(plot, "onDraw", new Canvas());

    }

}
