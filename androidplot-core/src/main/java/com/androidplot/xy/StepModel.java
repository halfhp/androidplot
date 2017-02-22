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

/**
 * Encapsulates a set of stepping parameters for a single axis.
 */
public class StepModel {

    public StepModel(StepMode mode, double value) {
        setMode(mode);
        setValue(value);
        setSteps(null);
    }

    public StepModel(double[] increments, double numLines) {
        setMode(StepMode.INCREMENT_BY_FIT);
        setValue(numLines);
        setSteps(increments);
    }

    private StepMode mode;
    private double value;   // increment by x value, pixels or number of sub division
    private double[] steps; // for fit mode: possible increments (by value) to choose from

    public double[] getSteps() {
        return steps;
    }

    public void setSteps(double[] steps) {
        this.steps = steps;
    }

    public StepMode getMode() {
        return mode;
    }

    public void setMode(StepMode mode) {
        this.mode = mode;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /*
    depending on the currently displayed range (by value) choose increment so that
    the number of lines is closest to value
     */
    public double getFitValue(double range) {

        // no possible increments where supplied (e.g. switched into this mode without calling setSteps(...)
        // TODO: throw exception this should not be done
        if (steps == null)
            return getValue();

        double curStep = steps[0];

        double oldDistance = Math.abs((range / curStep)-value );

        // determine which step size comes closest to the desired number of steps
        for (double step : steps) {

            double newDistance = Math.abs((range / step)-value );

            // closer than previos stepping?
            if (newDistance < oldDistance){
                curStep = step;
                oldDistance = newDistance;
            }
        }
        return curStep;
    }
}
