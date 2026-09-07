// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import androidx.annotation.NonNull;

/**
 * Encapsulates a set of stepping parameters for a single axis.
 */
public class StepModel {

    public StepModel(@NonNull StepMode mode, double value) {
        setMode(mode);
        setValue(value);
    }

    private StepMode mode;
    private double value;

    @NonNull
    public StepMode getMode() {
        return mode;
    }

    public void setMode(@NonNull StepMode mode) {
        this.mode = mode;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
