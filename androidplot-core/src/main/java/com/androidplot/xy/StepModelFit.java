package com.androidplot.xy;

import com.androidplot.Region;

import java.util.Arrays;

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

        // sanity checks: no null, 0 or negative
        if (steps == null || steps.length == 0)
            return;

        for (double step : steps) {
            if (step <= 0.0d)
                return;
        }

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

        // no possible increments where supplied
        // or no region defined
        if (steps == null || scale == null || !scale.isDefined())
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

    @Override
    public String toString() {
        return "StepModelFit{" +
                "steps=" + Arrays.toString(steps) +
                ", scale=" + scale +
                ", current stepping=" + getValue() +
                '}';
    }
}
