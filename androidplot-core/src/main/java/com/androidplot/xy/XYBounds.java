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

package com.androidplot.xy;

import com.androidplot.Bounds;

/**
 * Defines a rectangle using xy min/max values.  XYBounds differs from {@link RectRegion} in that
 * it accepts null values.
 * @since 0.9.7
 */
public class XYBounds {

    private Bounds xBounds;
    private Bounds yBounds;

    public XYBounds() {
        this(null, null, null, null);
    }

    public XYBounds(Number minX, Number maxX, Number minY, Number maxY) {
        xBounds = new Bounds(minX, maxX);
        yBounds = new Bounds(minY, maxY);
    }

    /**
     * Compares the input bounds xy min/max vals against this instance's current xy min/max vals.
     * If the input.min is less than this.min then this.min will be set to input.min.
     * If the input.max is greater than this.max then this.max will be set to input.max
     * @param input
     */
    public void union(XYBounds input) {
        xBounds.union(input.getXBounds());
        yBounds.union(input.getYBounds());
    }

    public void setXBounds(Bounds xBounds) {
        this.xBounds = xBounds;
    }

    public void setYBounds(Bounds yBounds) {
        this.yBounds = yBounds;
    }

    public Bounds getXBounds() {
        return xBounds;
    }

    public Bounds getYBounds() {
        return yBounds;
    }

    public Number getMinX() {
        return xBounds.getMin();
    }

    public void setMinX(Number minX) {
        xBounds.setMin(minX);
    }

    public Number getMaxX() {
        return xBounds.getMax();
    }

    public void setMaxX(Number maxX) {
        xBounds.setMax(maxX);
    }

    public Number getMinY() {
        return yBounds.getMin();
    }

    public void setMinY(Number minY) {
        yBounds.setMin(minY);
    }

    public Number getMaxY() {
        return yBounds.getMax();
    }

    public void setMaxY(Number maxY) {
        yBounds.setMax(maxY);
    }
}
