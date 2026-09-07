// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import androidx.annotation.NonNull;

public class VerticalPosition extends PositionMetric<VerticalPositioning> {

    public VerticalPosition(float value, @NonNull VerticalPositioning layoutStyle) {
        super(value, layoutStyle);
    }

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     */
    protected void validatePair(float value, @NonNull VerticalPositioning layoutStyle) {
        switch(layoutStyle) {
            case ABSOLUTE_FROM_TOP:
            case ABSOLUTE_FROM_BOTTOM:
            case ABSOLUTE_FROM_CENTER:
                validateValue(value, PositionMetric.LayoutMode.ABSOLUTE);
                break;
            case RELATIVE_TO_TOP:
            case RELATIVE_TO_BOTTOM:
            case RELATIVE_TO_CENTER:
                validateValue(value, PositionMetric.LayoutMode.RELATIVE);
        }
    }

    @Override
    public float getPixelValue(float size) {
        switch(getLayoutType()) {
            case ABSOLUTE_FROM_TOP:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_BEGINING);
            case ABSOLUTE_FROM_BOTTOM:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_END);
            case ABSOLUTE_FROM_CENTER:
                return this.getAbsolutePosition(size, PositionMetric.Origin.FROM_CENTER);
            case RELATIVE_TO_TOP:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_BEGINING);
            case RELATIVE_TO_BOTTOM:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_END);
            case RELATIVE_TO_CENTER:
                return this.getRelativePosition(size, PositionMetric.Origin.FROM_CENTER);
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + this.getLayoutType());
        }
    }

    @Override
    public void setLayoutType(@NonNull VerticalPositioning layoutType) {
        super.setLayoutType(layoutType);
    }
}
