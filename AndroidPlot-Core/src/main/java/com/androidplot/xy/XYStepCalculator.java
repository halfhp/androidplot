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
