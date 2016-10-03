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


package com.androidplot;

/**
 * A one dimensional region represented by a starting and ending value.
 */
public class Region {
    private Number min;
    private Number max;
    private Region defaults = this;

    public Region() {}

    public static Region withDefaults(Region defaults) {
        if(defaults == null || !defaults.isDefined()) {
            throw new IllegalArgumentException("When specifying default min and max must both be non-null values");
        }
        Region r = new Region();
        r.defaults = defaults;
        return r;
    }

    public Region(Number v1, Number v2) {
        if (v1 != null && v2 != null && v1.doubleValue() < v2.doubleValue()) {
            this.setMin(v1);
            this.setMax(v2);
        } else {
            this.setMin(v2);
            this.setMax(v1);
        }
    }

    /**
     *
     * @param v1
     * @param v2
     * @return The distance between val1 and val2 or null if either parameters are null.
     * @since 0.9.7
     */
    public static Number measure(Number v1, Number v2) {
        return new Region(v1, v2).length();
    }

    public Number length() {
        return getMax() == null || getMin() == null ?
               null : getMax().doubleValue() - getMin().doubleValue();
    }

    /**
     * Tests whether a value is within the given range
     * @param value
     * @return
     */
    public boolean contains(Number value) {
        return value.doubleValue() >= getMin().doubleValue() && value.doubleValue() <= getMax().doubleValue();
    }

    public boolean intersects(Region region) {
        return intersects(region.getMin(), region.getMax());
    }

    /**
     *
     * @return Middle value within this region
     */
    public Number center() {
        return getMax().doubleValue() - (length().doubleValue() / 2);
    }

    /**
     * Transform a value relative to this region into it's corresponding value relative to the
     * specified region.
     * @param value
     * @param region2
     * @return
     */
    public Number transform(double value, Region region2) {
        return transform(value, region2, false);
    }

    public Number transform(double value, Region region2, boolean flip) {
        return transform(value, region2.min.doubleValue(), region2.max.doubleValue(), flip);
    }

    public double transform(double value, double min, double max, boolean flip) {
        double range = length().doubleValue();
        final double r2 = max - min;

        // TODO: refactor to use ratio here
        final double scale = r2 / range;
        if(!flip) {
            return min + (scale * (value - this.getMin().doubleValue()));
        } else {
            return  max - (scale * (value - this.getMin().doubleValue()));
        }
    }

    public Number ratio(Region r2) {
        return ratio(r2.min.doubleValue(), r2.max.doubleValue());
    }

    public double ratio(double min, double max) {
        return length().doubleValue() / (max - min);
    }

    /**
     * Compares the input bounds min/max against this instance's current min/max.
     * If the input.min is less than this.min then this.min will be set to input.min.
     * If the input.max is greater than this.max then this.max will be set to input.max
     *
     * The result of a union will always be an equal or larger size region.
     * @param input
     */
    public void union(Region input) {
        if(getMin() == null || input.min != null &&
                input.min.doubleValue() < getMin().doubleValue()) {
            setMin(input.min);
        }
        if(getMax() == null || input.max != null && input.max.doubleValue() >
                getMax().doubleValue()) {
            setMax(input.max);
        }
    }

    /**
     * The result of an intersect will always be an equal or smaller size region.
     * @param input
     */
    public void intersect(Region input) {
        if(getMin().doubleValue() < input.getMin().doubleValue()) {
            setMin(input.getMin());
        }

        if(getMax().doubleValue() > input.getMax().doubleValue()) {
            setMax(input.getMax());
        }
    }

     /**
     * Tests whether this segment intersects another
     * @param line2Min
     * @param line2Max
     * @return
     */
    public  boolean intersects(Number line2Min, Number line2Max) {

        // is this line completely within line2?
        if(line2Min.doubleValue() <= getMin().doubleValue() && line2Max.doubleValue() >= getMax().doubleValue()) {
            return true;
        // is line1 partially within line2
        } else return contains(line2Min) || contains(line2Max);
    }

    public boolean isMinSet() {
        return min != null;
    }

    public Number getMin() {
        return isMinSet() ? min : defaults.min;
    }

    public void setMin(Number min) {
        if(min == null && defaults == null) {
            throw new NullPointerException("Region values cannot be null unless defaults have been set.");
        }
        this.min = min;
    }

    public boolean isMaxSet() {
        return max != null;
    }

    public Number getMax() {
        return isMaxSet() ? max : defaults.max;
    }

    public void setMax(Number max) {
        if(max == null && defaults == null) {
            throw new NullPointerException("Region values can never be null unless defaults have been set.");
        }
        this.max = max;
    }

    /**
     *
     * @return True if both min and max values are non-null, false otherwise.  Does *not* consider defaults.
     */
    public boolean isDefined() {
        return min != null && max != null;
    }
}
