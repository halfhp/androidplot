package com.androidplot.xy;

import android.graphics.RectF;
import com.androidplot.util.ValPixConverter;


public class XYStepCalculator {


    //public static XYStep getStep(XYPlot plot, XYAxisType axisType, RectF rect, double minVal, double maxVal) {
     //   return getStep(plot, axisType, new Dimension(rect.width(), rect.height()), minVal, maxVal);
    //}
    public static XYStep getStep(XYPlot plot, XYAxisType axisType, RectF rect, double minVal, double maxVal) {
        XYStep step = null;
        switch(axisType) {
            case DOMAIN:
                step = getStep(plot, plot.getDomainStepMode(), rect.width(), plot.getDomainStepValue(), minVal, maxVal);
                break;
            case RANGE:
                step = getStep(plot, plot.getRangeStepMode(), rect.height(), plot.getRangeStepValue(), minVal, maxVal);
                break;
        }
        return step;
    }

    private static XYStep getStep(XYPlot plot, XYStepMode typeXY, float numPix, double stepValue, double minVal, double maxVal) {
        XYStep step = new XYStep();
        switch(typeXY) {
            case INCREMENT_BY_VAL:
                //float sv = (float) stepValue;
                step.setStepVal(stepValue);
                step.setStepPix((float)(stepValue/ ValPixConverter.valPerPix(minVal, maxVal, numPix)));
                step.setStepCount(numPix/step.getStepPix());
                break;
            case INCREMENT_BY_PIXELS:
                step.setStepPix((float)stepValue);
                step.setStepCount(numPix/step.getStepPix());
                step.setStepVal(ValPixConverter.valPerPix(minVal, maxVal, numPix)*step.getStepPix());
                break;
            case SUBDIVIDE:
                step.setStepCount(stepValue);
                step.setStepPix((float)(numPix/(step.getStepCount()-1)));
                step.setStepVal(ValPixConverter.valPerPix(minVal, maxVal, numPix)*step.getStepPix());
                break;
        }
        return step;
    }
}
