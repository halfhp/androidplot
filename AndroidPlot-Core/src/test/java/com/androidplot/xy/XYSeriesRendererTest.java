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
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.ValPixConverter;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({Log.class,View.class,Handler.class,Paint.class,Color.class, Rect.class, RectF.class,
        FontUtils.class, PixelUtils.class, Canvas.class})

public class XYSeriesRendererTest {

    @MockClass(realClass = Context.class)
    public static class MockContext {}

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testDataToGridCorrelation() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        //RectF gridRect = Mockit.setUpMock(new MockRectF());
        //Mockit.setUpMock(RectF.class, new MockRectF());
        //@MockClass(realClass = RectF.class)
        new MockUp<RectF>()
        {
            final float left = 5;
            final float top = 5;
            final float right = 104;
            final float bottom = 104;

            @Mock void $init() {}

            @Mock float width() {return 100;}
            @Mock float height() {return 100;}

        };
        RectF gridRect = new RectF();
        XYPlot plot = new XYPlot(context, "Test");
        plot.setDomainStepMode(XYStepMode.SUBDIVIDE);
        plot.setDomainStepValue(10);
        plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        XYStep domainStep = XYStepCalculator.getStep(plot, XYAxisType.DOMAIN, gridRect, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue());

        float width = gridRect.width();

        int x = 0;
        float val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false) + (gridRect.left);

        assertEquals(val, domainStep.getStepPix()*x);

        x = 1;
        val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false) + (gridRect.left);

        assertEquals(val, domainStep.getStepPix()*x);

        x = 9;
        val = ValPixConverter.valToPix(x, 0, 9, gridRect.width(), false) + (gridRect.left);

        assertEquals(val, domainStep.getStepPix()*x);
    }

}
