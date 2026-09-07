// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import androidx.annotation.NonNull;



abstract class LayoutMetric<LayoutType extends Enum> {

    private LayoutType layoutType;

    //private LayoutType layoutType;
    private float value;
    //private float lastRow;

    public LayoutMetric(float value, @NonNull LayoutType layoutType) {
        validatePair(value, layoutType);
        set(value, layoutType);
        //setLayoutType(layoutType);
        //setValue(value);
        //setLayoutType(layoutType);
    }

    /**
     * Verifies that the values passed in are valid for the layout algorithm being used.
     * @param value 
     * @param layoutType
     */
    protected abstract void validatePair(float value, @NonNull LayoutType layoutType);

    public void set(float value, @NonNull LayoutType layoutType) {
        validatePair(value, layoutType);
        this.value = value;
        this.layoutType = layoutType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        validatePair(value, layoutType);
        this.value = value;
    }

    public abstract float getPixelValue(float size);

    @NonNull
    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(@NonNull LayoutType layoutType) {
        validatePair(value, layoutType);
        this.layoutType = layoutType;
    }
}
