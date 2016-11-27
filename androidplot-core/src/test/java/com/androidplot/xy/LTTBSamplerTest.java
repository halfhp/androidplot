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

import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class LTTBSamplerTest extends AndroidplotTest {

    @Test
    public void testSomething() throws Exception {

    }

//    @Test
//    public void testDownsample() throws Exception {
//
//        Number[][] rawNumbers = new Number[][] { {1, 2}, {3, 4}, {5, 6}, {7, 8}, {9, 10}, {11, 12}, {13, 14}, {15, 16}, {17, 18}, {19, 20} };
//        XYSeries rawSeries = new SimpleXYSeries(
//                Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19),
//                Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20), "raw");
//        compareSeriesToRaw(rawNumbers, rawSeries);
//
//        LTTBDownsampler downsampler = new LTTBDownsampler();
//        SimpleXYSeries sampled = new SimpleXYSeries(
//                Arrays.asList(new Number[]{0, 0, 0, 0, 0}),
//                Arrays.asList(new Number[]{0, 0, 0, 0, 0}), "sampled");
//        downsampler.downsample(rawSeries, sampled);
//
//        Number[][] downsampled = LTTBDownsampler.downsample(rawNumbers, 5);
//        compareSeriesToRaw(downsampled, sampled);
//    }
//
//    protected void compareSeriesToRaw(Number[][] raw, XYSeries series) {
//        assertEquals(raw.length, series.size());
//        int i = 0;
//        for(Number[] xy : raw) {
//            assertEquals(xy[0], series.getX(i));
//            assertEquals(xy[1], series.getY(i));
//            i++;
//        }
//    }
}
