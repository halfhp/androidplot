// SPDX-License-Identifier: Apache-2.0


package com.androidplot;

import com.androidplot.util.FastNumber;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A one dimensional region represented by a starting and ending value.
 */
public class Region {
    private FastNumber min;
    private FastNumber max;
    private FastNumber cachedLength;

    private Region defaults = this;

    public Region() {}

    @NonNull
    public static Region withDefaults(@NonNull Region defaults) {
        if(defaults == null || !defaults.isDefined()) {
            throw new IllegalArgumentException("When specifying default min and max must both be non-null values");
        }
        Region r = new Region();
        r.defaults = defaults;
        return r;
    }

    /**
     * @param v1
     * @param v2
     * Values are ordered so that min is the smaller of the two.  A null value represents infinity
     * relative to its position (null v1 = negative infinity, null v2 = positive infinity) and
     * is never reordered.
     */
    public Region(@Nullable Number v1, @Nullable Number v2) {
        if (v1 != null && v2 != null && v1.doubleValue() > v2.doubleValue()) {
            this.setMin(v2);
            this.setMax(v1);
        } else {
            this.setMin(v1);
            this.setMax(v2);
        }
    }

    public void setMinMax(@NonNull Region region) {
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
    @Nullable
    public static Number measure(@Nullable Number v1, @Nullable Number v2) {
        return new Region(v1, v2).length();
    }

    @Nullable
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
    public boolean contains(@NonNull Number value) {
        return value.doubleValue() >= getMin().doubleValue() && value.doubleValue() <= getMax().doubleValue();
    }

    public boolean intersects(@NonNull Region region) {
        return intersects(region.getMin(), region.getMax());
    }

    /**
     *
     * @return Middle value within this region
     */
    @NonNull
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
    @NonNull
    public Number transform(double value, @NonNull Region region2) {
        return transform(value, region2, false);
    }

    @NonNull
    public Number transform(double value, @NonNull Region region2, boolean flip) {
        return transform(value, region2.getMin().doubleValue(), region2.getMax().doubleValue(), flip);
    }

    public double transform(double value, double min, double max, boolean flip) {
        double range = length().doubleValue();
        final double r2 = max - min;

        // a zero-length region cannot be scaled (the result would be NaN or infinite);
        // map everything onto the center of the target range instead:
        if (range == 0) {
            return min + (r2 / 2);
        }

        // TODO: refactor to use ratio here
        final double scale = r2 / range;
        if(!flip) {
            return min + (scale * (value - this.getMin().doubleValue()));
        } else {
            return  max - (scale * (value - this.getMin().doubleValue()));
        }
    }

    @NonNull
    public Number ratio(@NonNull Region r2) {
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


    public void union(@Nullable Number value) {
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
    public void union(@NonNull Region input) {
        union(input.getMin());
        union(input.getMax());
    }

    /**
     * The result of an intersect will always be an equal or smaller size region.
     * @param input
     */
    public void intersect(@NonNull Region input) {
        if(getMin().doubleValue() < input.getMin().doubleValue()) {
            setMin(input.getMin());
        }

        if(getMax().doubleValue() > input.getMax().doubleValue()) {
            setMax(input.getMax());
        }
    }

     /**
     * Tests whether this segment intersects another.  A null min represents negative infinity
     * and a null max represents positive infinity, both for the params and for this region's
     * own min / max.
     * @param line2Min
     * @param line2Max
     * @return
     */
    public  boolean intersects(@Nullable Number line2Min, @Nullable Number line2Max) {
        final double min1 = getMin() == null ? Double.NEGATIVE_INFINITY : getMin().doubleValue();
        final double max1 = getMax() == null ? Double.POSITIVE_INFINITY : getMax().doubleValue();
        final double min2 = line2Min == null ? Double.NEGATIVE_INFINITY : line2Min.doubleValue();
        final double max2 = line2Max == null ? Double.POSITIVE_INFINITY : line2Max.doubleValue();

        // is this line completely within line2?
        if(min2 <= min1 && max2 >= max1) {
            return true;
        // is line1 partially within line2
        } else return (min2 >= min1 && min2 <= max1) || (max2 >= min1 && max2 <= max1);
    }

    public boolean isMinSet() {
        return min != null;
    }

    @Nullable
    public Number getMin() {
        return isMinSet() ? min : defaults.min;
    }

    public void setMin(@Nullable Number min) {
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

    @Nullable
    public Number getMax() {
        return isMaxSet() ? max : defaults.max;
    }

    public void setMax(@Nullable Number max) {
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
