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

import com.androidplot.util.FastNumber;

/**
 * A one dimensional region represented by a starting and ending value.
 */
public class Region {
    private FastNumber min;
    private FastNumber max;
    private FastNumber cachedLength;

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

    public void setMinMax(Region region) {
        setMin(region.getMin());
        setMax(region.getMax());
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
        if(cachedLength == null) {
            Number l = getMax() == null || getMin() == null ?
                   null : getMax().doubleValue() - getMin().doubleValue();
            if(l != null) {
                cachedLength = FastNumber.orNull(l);
            }
        }
        return cachedLength;
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
        return transform(value, region2.getMin().doubleValue(), region2.getMax().doubleValue(), flip);
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
        return ratio(r2.getMin().doubleValue(), r2.getMax().doubleValue());
    }

    /**
     *
     * @param min
     * @param max
     * @return length of this series divided by the length of the distance between min and max.
     */
    public double ratio(double min, double max) {
        return length().doubleValue() / (max - min);
    }


    public void union(Number value) {
        if(value == null) {
            return;
        }
        double val = value.doubleValue();
        if(getMin() == null ||
                val < getMin().doubleValue()) {
            setMin(value);
        }
        if(getMax() == null || val >
                getMax().doubleValue()) {
            setMax(value);
        }
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
        union(input.getMin());
        union(input.getMax());
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
        cachedLength = null;
        if(min == null) {
            if(defaults == null) {
                throw new NullPointerException(
                        "Region values cannot be null unless defaults have been set.");
            } else {
                this.min = null;
            }
        } else if (this.min == null || !this.min.equals(min)) {
            this.min = FastNumber.orNull(min);
        }
    }

    public boolean isMaxSet() {
        return max != null;
    }

    public Number getMax() {
        return isMaxSet() ? max : defaults.max;
    }

    public void setMax(Number max) {
        cachedLength = null;
        if(max == null) {
            if(defaults == null) {
                throw new NullPointerException(
                        "Region values can never be null unless defaults have been set.");
            } else {
                this.max = null;
            }
        } else if (this.max == null || !this.max.equals(max)) {
            this.max = FastNumber.orNull(max);
        }
    }

    /**
     *
     * @return True if both min and max values are non-null, false otherwise.  Does *not* consider defaults.
     */
    public boolean isDefined() {
        return min != null && max != null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Region{");
        sb.append("min=").append(min);
        sb.append(", max=").append(max);
        sb.append(", cachedLength=").append(cachedLength);
        sb.append(", defaults=");
        if (defaults != this) {
            sb.append(defaults);
        } else {
            sb.append("this");
        }
        sb.append('}');
        return sb.toString();
    }
}
