/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.xy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;
import com.androidplot.util.FontUtils;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.ValPixConverter;
import mockit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

@UsingMocksAndStubs({View.class,Handler.class,Paint.class,Color.class, Rect.class, RectF.class, FontUtils.class, PixelUtils.class})

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
