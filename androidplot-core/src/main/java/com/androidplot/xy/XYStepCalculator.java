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
    public static Step getStep(XYPlot plot, Axis axisType, RectF rect, Number minVal, Number maxVal) {
        Step step = null;
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

    public static Step getStep(StepMode typeXY, float plotPixelSize, double stepValue, Number minVal, Number maxVal) {
        //XYStep step = new XYStep();
        double stepVal = 0;
        double stepPix = 0;
        double stepCount = 0;
        switch(typeXY) {
            case INCREMENT_BY_VAL:
                stepVal = stepValue;
                stepPix = stepValue / ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize);
                stepCount = plotPixelSize /stepPix;
                break;
            case INCREMENT_BY_PIXELS:
                stepPix = stepValue;
                stepCount = plotPixelSize /stepPix;
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize)*stepPix;
                break;
            case SUBDIVIDE:
                stepCount = stepValue;
                stepPix = (plotPixelSize /(stepCount-1));
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize)*stepPix;
                break;
        }
        return new Step(stepCount, stepPix, stepVal);
    }
}
