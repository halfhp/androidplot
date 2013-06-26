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
import com.androidplot.ui.SeriesRenderer;

/**
 * This is an experimental class and should not currently be used in production apps.
 */
public class BezierLineAndPointFormatter extends LineAndPointFormatter {

    /**
     * Should only be used in conjunction with calls to configure()...
     */
    public BezierLineAndPointFormatter() {
    }

    public BezierLineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor) {
        super(lineColor, vertexColor, fillColor, null);
    }

    public BezierLineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor, FillDirection fillDir) {
        super(lineColor, vertexColor, fillColor, null, fillDir);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return BezierLineAndPointRenderer.class;
    }

    @Override
    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new BezierLineAndPointRenderer(plot);
    }
}
