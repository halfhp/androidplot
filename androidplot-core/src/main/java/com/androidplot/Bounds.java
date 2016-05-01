/*
 * Copyright 2016 AndroidPlot.com
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

package com.androidplot;

/**
 * Defines simple min/max boundary.  Differs from {@link LineRegion} in that it accepts null values.
 * @since 0.9.7
 */
public class Bounds {

    private Number min;
    private Number max;

    public Bounds() {
        this(null, null);
    }

    public Bounds(Number min, Number max) {
        this.min = min;
        this.max = max;
    }

    public Number getMin() {
        return min;
    }

    public void setMin(Number min) {
        this.min = min;
    }

    public Number getMax() {
        return max;
    }

    public void setMax(Number max) {
        this.max = max;
    }

    /**
     * Compares the input bounds min/max against this instance's current min/max.
     * If the input.min is less than this.min then this.min will be set to input.min.
     * If the input.max is greater than this.max then this.max will be set to input.max
     * @param input
     */
    public void union(Bounds input) {
        if(this.min == null || input.min != null &&
                input.min.doubleValue() < this.min.doubleValue()) {
            this.min = input.min;
        }
        if(this.max == null || input.max != null && input.max.doubleValue() >
                this.max.doubleValue()) {
            this.max = input.max;
        }
    }

    /**
     * Inverse of {@link #union(Bounds)}.
     * @param input
     */
    public void intersect(Bounds input) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
