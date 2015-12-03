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

import android.graphics.RectF;
import com.androidplot.util.ValPixConverter;

/**
 * Calculates "stepping" values for a plot.  These values are most commonly used for
 * drawing grid lines on a graph.
 */
public class XYStepCalculator {


    /**
     * Convenience method - wraps other form of getStep().
     * @param plot
     * @param axisType
     * @param rect
     * @param minVal
     * @param maxVal
     * @return
     */
    public static XYStep getStep(XYPlot plot, XYAxisType axisType, RectF rect, Number minVal, Number maxVal) {
        XYStep step = null;
        switch(axisType) {
            case DOMAIN:
                step = getStep(plot.getDomainStepMode(), rect.width(), plot.getDomainStepValue(), minVal, maxVal);
                break;
            case RANGE:
                step = getStep(plot.getRangeStepMode(), rect.height(), plot.getRangeStepValue(), minVal, maxVal);
                break;
        }
        return step;
    }

    public static XYStep getStep(XYStepMode typeXY, float plotPixelSize, double stepValue, Number minVal, Number maxVal) {
        //XYStep step = new XYStep();
        double stepVal = 0;
        float stepPix = 0;
        float stepCount = 0;
        switch(typeXY) {
            case INCREMENT_BY_VAL:
                stepVal = stepValue;
                stepPix = (float)(stepValue/ ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize));
                stepCount = plotPixelSize /stepPix;
                break;
            case INCREMENT_BY_PIXELS:
                stepPix = new Double(stepValue).floatValue();
                stepCount = plotPixelSize /stepPix;
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize)*stepPix;
                break;
            case SUBDIVIDE:
                stepCount = new Double(stepValue).floatValue();
                stepPix = (plotPixelSize /(stepCount-1));
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize)*stepPix;
                break;
        }
        return new XYStep(stepCount, stepPix, stepVal);
    }
}
