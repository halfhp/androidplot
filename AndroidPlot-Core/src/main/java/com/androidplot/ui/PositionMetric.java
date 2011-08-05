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
