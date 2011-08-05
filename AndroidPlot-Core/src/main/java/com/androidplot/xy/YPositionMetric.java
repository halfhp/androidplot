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

package com.androidplot.xy;

import com.androidplot.ui.PositionMetric;

public class YPositionMetric extends PositionMetric<YLayoutStyle> {
    /*
    public enum YLayoutStyle {
        ABSOLUTE_FROM_TOP,
        ABSOLUTE_FROM_BOTTOM,
        ABSOLUTE_FROM_CENTER,
        RELATIVE_TO_TOP,
        RELATIVE_TO_BOTTOM,
        RELATIVE_TO_CENTER
    }
    */

    //private YLayoutStyle layoutType;

    public YPositionMetric(float value, YLayoutStyle layoutStyle) {
        super(value, layoutStyle);
        //this.layoutStyle = layoutStyle;


    }

    /*
    @Override
    public void set(float value, YLayoutStyle layoutType) {
        validatePair(value, layoutType);
        super.set(value, layoutType);
    }

    @Override
    public void setLayoutType(YLayoutStyle layoutType) {
        validatePair(getValue(), layoutType);
        super.setLayoutType(layoutType);
    }

    @Override
    public void setValue(float value) {
        validatePair(value, getLayoutType());
        super.setValue(value);
    }
    */

    /**
     * Throws IllegalArgumentException if there is a problem.
     * @param value
     */
    protected void validatePair(float value, YLayoutStyle layoutStyle) {
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
}
