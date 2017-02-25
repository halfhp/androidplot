package com.androidplot.xy;

import com.androidplot.Region;

/**
 * Subclass of StepModel that chooses from predefined step values. Depending on the currently
 * displayed range (by value) choose increment so that the number of lines
 * is closest to StepModel.value
 */
public class StepModelFit extends StepModel {

    private double[] steps; // list of steps to choose from
    private Region scale;   // axis region on display

    public StepModelFit(Region axisRegion, double[] increments, double numLines) {
        super(StepMode.INCREMENT_BY_FIT, numLines);

        setSteps(increments);
        setScale(axisRegion);
    }

    public double[] getSteps() {
        return steps;
    }

    public void setSteps(double[] steps) {
        this.steps = steps;
    }

    public Region getScale() {
        return scale;
    }

    public void setScale(Region scale) {
        this.scale = scale;
    }

    // does not return StepModel.value instead calculates best fit
    @Override
    public double getValue() {

        // no possible increments where supplied (e.g. switched into this mode without calling setSteps(...)
        if (steps == null)
            return super.getValue();

        double curStep = steps[0];
        double oldDistance = Math.abs((scale.length().doubleValue() / curStep)-super.getValue() );

        // determine which step size comes closest to the desired number of steps
        // since steps[] is a small array brute force search is ok
        for (double step : steps) {

            double newDistance = Math.abs((scale.length().doubleValue() / step)-super.getValue() );

            // closer than previous stepping?
            if (newDistance < oldDistance){
                curStep = step;
                oldDistance = newDistance;
            }
        }
        return curStep;
    }
}
