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

public abstract class PositionMetric<LayoutType extends Enum> extends LayoutMetric<LayoutType> {

    protected enum Origin {
        FROM_BEGINING,
        FROM_CENTER,
        FROM_END
    }

    protected enum LayoutMode {
        ABSOLUTE,
        RELATIVE
    }

    public PositionMetric(float value, LayoutType layoutType) {
        super(value, layoutType);
    }

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     * @param layoutMode
     * @throws IllegalArgumentException
     */
    protected static void validateValue(float value, LayoutMode layoutMode) throws IllegalArgumentException {
        switch(layoutMode) {
            case ABSOLUTE:
                break;
            case RELATIVE:
                if(value < -1 || value > 1) {
                    throw new IllegalArgumentException("Relative layout values must be within the range of -1 to 1.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown LayoutMode: " + layoutMode);
        }

    }

    protected float getAbsolutePosition(float size, Origin origin) {
        switch(origin) {
            case FROM_BEGINING:
                return getValue();
            case FROM_CENTER:
                return (size/2f) + getValue();
            case FROM_END:
                return size - getValue();
            default:
                 throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }
    }

    protected float getRelativePosition(float size, Origin origin) {
        //throw new UnsupportedOperationException("Not yet implemented.");

        switch(origin) {
            case FROM_BEGINING:
                return size * getValue();
            case FROM_CENTER:
                return (size/2f) + ((size/2f) * getValue());
            case FROM_END:
                return size + (size*getValue());
            default:
                 throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }

    }


}
