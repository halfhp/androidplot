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

import com.androidplot.*;

/**
 * Calculates "stepping" values for a plot.  These values are most commonly used for
 * drawing grid lines on a graph.
 */
public class XYStepCalculator {


    /**
     * Convenience method - wraps other form of getStep().
     * @param plot
     * @param axisType
     * @param pixRect
     * @return
     */
    public static Step getStep(XYPlot plot, Axis axisType, RectF pixRect) {
        Step step = null;
        switch(axisType) {
            case DOMAIN:
                step = getStep(plot.getDomainStepMode(),
                        plot.getDomainStepValue(),
                        plot.getBounds().getxRegion(),
                        new Region(pixRect.left, pixRect.right));
                break;
            case RANGE:
                step = getStep(plot.getRangeStepMode(),
                        plot.getRangeStepValue(),
                        plot.getBounds().getyRegion(),
                        new Region(pixRect.top, pixRect.bottom));
                break;
        }
        return step;
    }

    public static Step getStep(StepMode typeXY, double stepValue, Region realBounds, Region pixelBounds) {
        double stepVal = 0;
        double stepPix = 0;
        double stepCount = 0;
        switch(typeXY) {
            case INCREMENT_BY_VAL:
            case INCREMENT_BY_FIT:
                stepVal = stepValue;
                stepPix = stepValue / realBounds.ratio(pixelBounds).doubleValue();
                stepCount = pixelBounds.length().doubleValue() / stepPix;
                break;
            case INCREMENT_BY_PIXELS:
                stepPix = stepValue;
                stepVal = realBounds.ratio(pixelBounds).doubleValue() * stepPix;
                stepCount = pixelBounds.length().doubleValue() / stepPix;
                break;
            case SUBDIVIDE:
                stepCount = stepValue;
                stepPix = pixelBounds.length().doubleValue() / (stepCount - 1);
                stepVal = realBounds.ratio(pixelBounds).doubleValue() * stepPix;
                break;
        }
        return new Step(stepCount, stepPix, stepVal);
    }
}
