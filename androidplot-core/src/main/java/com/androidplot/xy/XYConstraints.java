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

package com.androidplot.xy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Calculates the min/max constraints for an xy plane.
 *
 * @since 0.9.7
 */
public class XYConstraints {

    // used for  calculating the domain/range extents that will be displayed on the plot.
    // using boundaries and origins are mutually exclusive and enabling one will disable
    // the other.  when only setting the FramingModel, the origin or boundary is set to
    // the current value of the plot.
    private XYFramingModel domainFramingModel = XYFramingModel.EDGE;
    private XYFramingModel rangeFramingModel = XYFramingModel.EDGE;

    // determines how boundaries adjust as new min/max values are encountered:
    private BoundaryMode domainUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode domainLowerBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeLowerBoundaryMode = BoundaryMode.AUTO;

    private Number minX;
    private Number maxX;
    private Number minY;
    private Number maxY;

    public XYConstraints() {
        this(null, null, null, null);
    }

    public XYConstraints(@Nullable Number minX, @Nullable Number maxX, @Nullable Number minY, @Nullable Number maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean contains(@NonNull RectRegion rectRegion) {
        return contains(rectRegion.getMinY(), rectRegion.getMinY())
                && contains(rectRegion.getMaxX(), rectRegion.getMaxY());
    }

    public boolean contains(Number x, Number y) {
        if (x == null || y == null) {
            // this is essentially an invisible point:
            return false;
        }

        if (minX == null && maxX == null && minY == null && maxY == null) {
            //there are no constraints
            return true;
        }

        final double dx = x.doubleValue();
        if (minX != null && dx < minX.doubleValue()) {
            return false;
        } else if (maxX != null && dx > maxX.doubleValue()) {
            return false;
        }

        final double dy = y.doubleValue();
        if (minY != null && dy < minY.doubleValue()) {
            return false;
        } else if (maxY != null && dy > maxY.doubleValue()) {
            return false;
        }

        return true;
    }

    @Nullable
    public Number getMinX() {
        return minX;
    }

    @Nullable
    public Number getMaxX() {
        return maxX;
    }

    @Nullable
    public Number getMinY() {
        return minY;
    }

    @Nullable
    public Number getMaxY() {
        return maxY;
    }

    public void setMinX(@Nullable Number minX) {
        this.minX = minX;
    }

    public void setMaxX(@Nullable Number maxX) {
        this.maxX = maxX;
    }

    public void setMinY(@Nullable Number minY) {
        this.minY = minY;
    }

    public void setMaxY(@Nullable Number maxY) {
        this.maxY = maxY;
    }

    @NonNull
    public XYFramingModel getDomainFramingModel() {
        return domainFramingModel;
    }

    public void setDomainFramingModel(@NonNull XYFramingModel domainFramingModel) {
        this.domainFramingModel = domainFramingModel;
    }

    @NonNull
    public XYFramingModel getRangeFramingModel() {
        return rangeFramingModel;
    }

    public void setRangeFramingModel(@NonNull XYFramingModel rangeFramingModel) {
        this.rangeFramingModel = rangeFramingModel;
    }

    @NonNull
    public BoundaryMode getDomainUpperBoundaryMode() {
        return domainUpperBoundaryMode;
    }

    public void setDomainUpperBoundaryMode(@NonNull BoundaryMode domainUpperBoundaryMode) {
        this.domainUpperBoundaryMode = domainUpperBoundaryMode;
    }

    @NonNull
    public BoundaryMode getDomainLowerBoundaryMode() {
        return domainLowerBoundaryMode;
    }

    public void setDomainLowerBoundaryMode(@NonNull BoundaryMode domainLowerBoundaryMode) {
        this.domainLowerBoundaryMode = domainLowerBoundaryMode;
    }

    @NonNull
    public BoundaryMode getRangeUpperBoundaryMode() {
        return rangeUpperBoundaryMode;
    }

    public void setRangeUpperBoundaryMode(@NonNull BoundaryMode rangeUpperBoundaryMode) {
        this.rangeUpperBoundaryMode = rangeUpperBoundaryMode;
    }

    @NonNull
    public BoundaryMode getRangeLowerBoundaryMode() {
        return rangeLowerBoundaryMode;
    }

    public void setRangeLowerBoundaryMode(@NonNull BoundaryMode rangeLowerBoundaryMode) {
        this.rangeLowerBoundaryMode = rangeLowerBoundaryMode;
    }

    @Override
    public String toString() {
        return "XYConstraints{" + "domainFramingModel=" + domainFramingModel +
                ", rangeFramingModel=" + rangeFramingModel +
                ", domainUpperBoundaryMode=" + domainUpperBoundaryMode +
                ", domainLowerBoundaryMode=" + domainLowerBoundaryMode +
                ", rangeUpperBoundaryMode=" + rangeUpperBoundaryMode +
                ", rangeLowerBoundaryMode=" + rangeLowerBoundaryMode +
                ", minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                '}';
    }
}
