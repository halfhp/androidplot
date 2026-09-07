// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

/**
 * Encapsulates a set of stepping parameters for a single axis.
 */
public class StepModel {

    public StepModel(StepMode mode, double value) {
        setMode(mode);
        setValue(value);
    }

    private StepMode mode;
    private double value;

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
}
