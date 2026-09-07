// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import androidx.annotation.NonNull;

/**
 * Encapsulates a sizing algorithm and an associated value.
 *
 * The available algorithms list are stored in the {@link SizeMode} enumeration.
 *
 */
public class SizeMetric extends LayoutMetric<SizeMode> {

    public SizeMetric(float value, @NonNull SizeMode layoutType) {
        super(value, layoutType);
    }

    protected void validatePair(float value, @NonNull SizeMode layoutType) {
        switch(layoutType) {
            case RELATIVE:
                if(value < 0 || value > 1) {
                    throw new IllegalArgumentException("SizeMetric Relative and Hybrid layout values must be within the range of 0 to 1.");
                }
            case ABSOLUTE:
            case FILL:
            default:
                break;
        }
    }

    @Override
    public float getPixelValue(float size) {
        switch(getLayoutType()) {
            case ABSOLUTE:
                return getValue();
            case RELATIVE:
                return getValue() * size;
            case FILL:
                return size - getValue();
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + this.getLayoutType());
        }
    }

    @Override
    public void setLayoutType(@NonNull SizeMode layoutType) {
        super.setLayoutType(layoutType);
    }
}
