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



abstract class LayoutMetric<LayoutType extends Enum> {

    private LayoutType layoutType;

    //private LayoutType layoutType;
    private float value;
    //private float lastRow;

    public LayoutMetric(float value, LayoutType layoutType) {
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
    protected abstract void validatePair(float value, LayoutType layoutType);

    public void set(float value, LayoutType layoutType) {
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

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        validatePair(value, layoutType);
        this.layoutType = layoutType;
    }
}
