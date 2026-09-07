// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.RectF;

import com.androidplot.*;
import androidx.annotation.NonNull;

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
    @NonNull
    public static Step getStep(@NonNull XYPlot plot, @NonNull Axis axisType, @NonNull RectF pixRect) {
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

    @NonNull
    public static Step getStep(@NonNull StepMode typeXY, double stepValue, @NonNull Region realBounds, @NonNull Region pixelBounds) {
        double stepVal = 0;
        double stepPix = 0;
        double stepCount = 0;
        switch(typeXY) {
            case INCREMENT_BY_VAL:
            case INCREMENT_BY_FIT:
                stepVal = stepValue;
                stepPix = stepValue / realBounds.ratio(pixelBounds).doubleValue();
                if (stepPix < 0) {
                    // the axis is inverted (min > max), so a positive value step runs against
                    // the pixel direction.  Grid drawing walks the axis in pixel order, so keep
                    // the pixel step positive and let the value step carry the sign, which is
                    // how the other step modes already come out for inverted bounds.  (#125)
                    stepPix = -stepPix;
                    stepVal = -stepVal;
                }
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
