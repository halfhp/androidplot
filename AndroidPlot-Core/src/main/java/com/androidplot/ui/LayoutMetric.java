/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
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
