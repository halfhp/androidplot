/*
 * Copyright 2012 AndroidPlot.com
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

/**
 * Encapsulates a sizing algorithm and an associated value.
 *
 * The available algorithms list are stored in the {@link SizeLayoutType} enumeration.
 *
 */
public class SizeMetric extends LayoutMetric<SizeLayoutType> {

    public SizeMetric(float value, SizeLayoutType layoutType) {
        super(value, layoutType);
    }

    protected void validatePair(float value, SizeLayoutType layoutType) {
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
        //switch(layoutType)
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

}
