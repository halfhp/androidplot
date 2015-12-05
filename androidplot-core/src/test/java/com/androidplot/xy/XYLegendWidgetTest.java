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
import mockit.*;
import org.junit.After;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import java.util.Arrays;
import static junit.framework.Assert.assertEquals;

public class XYLegendWidgetTest extends AndroidplotTest {

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testDoOnDraw() throws Exception {
        XYPlot plot = new XYPlot(RuntimeEnvironment.application, "Test",
                Plot.RenderMode.USE_MAIN_THREAD);

        SimpleXYSeries s1 = new SimpleXYSeries((Arrays.asList(1, 2, 3)),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s1");

        plot.addSeries(s1, new LineAndPointFormatter(
                Color.RED, Color.GREEN, Color.BLUE, null));

        assertEquals(1, plot.getSeriesRegistry().size());

        Deencapsulation.invoke(plot, "onSizeChanged", 100, 100, 100, 100);
        plot.redraw();
        // have to manually invoke this because the invalidate()
        // invoked by redraw() is a stub and will not result in onDraw being called.
        Deencapsulation.invoke(plot, "onDraw", new Canvas());

        plot.removeSeries(s1);
        assertEquals(0, plot.getSeriesRegistry().size());
        plot.addSeries(s1, new BarFormatter(Color.RED, Color.GREEN));
        plot.redraw();

        // throws NullPointerException before fix
        // for ANDROIDPLOT-166 was applied.
        Deencapsulation.invoke(plot, "onDraw", new Canvas());
    }

}
