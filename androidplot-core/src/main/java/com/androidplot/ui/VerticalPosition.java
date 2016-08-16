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

package com.androidplot.ui;

public class VerticalPosition extends PositionMetric<VerticalPositioning> {

    public VerticalPosition(float value, VerticalPositioning layoutStyle) {
        super(value, layoutStyle);
    }

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     */
    protected void validatePair(float value, VerticalPositioning layoutStyle) {
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
    public void setLayoutType(VerticalPositioning layoutType) {
        super.setLayoutType(layoutType);
    }
}
