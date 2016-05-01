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

/**
 * Calculates the min/max constraints for an xy plane.
 * @since 0.9.7
 */
public class XYConstraints {

    // used for  calculating the domain/range extents that will be displayed on the plot.
    // using boundaries and origins are mutually exclusive.  because of this,
    // setting one will disable the other.  when only setting the FramingModel,
    // the origin or boundary is set to the current value of the plot.
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

    public XYConstraints(Number minX, Number maxX, Number minY, Number maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean contains(Number x, Number y) {
        if(x == null || y == null) {
            // this is essentially an invisible point:
            return false;
        } else {
            final double dx = x.doubleValue();

            if(minX != null && dx < minX.doubleValue()) {
                return false;
            } else if(maxX != null && dx > maxX.doubleValue()) {
               return false;
            } else {
                final double dy = y.doubleValue();
                if(minY != null && dy < minY.doubleValue()) {
                    return false;
                } else if(maxY != null && dy > maxY.doubleValue()) {
                    return false;
                }
            }
            return true;
        }
    }

    public Number getMinX() {
        return minX;
    }

    public Number getMaxX() {
        return maxX;
    }

    public Number getMinY() {
        return minY;
    }

    public Number getMaxY() {
        return maxY;
    }

    public XYFramingModel getDomainFramingModel() {
        return domainFramingModel;
    }

    public void setDomainFramingModel(XYFramingModel domainFramingModel) {
        this.domainFramingModel = domainFramingModel;
    }

    public XYFramingModel getRangeFramingModel() {
        return rangeFramingModel;
    }

    public void setRangeFramingModel(XYFramingModel rangeFramingModel) {
        this.rangeFramingModel = rangeFramingModel;
    }

    public BoundaryMode getDomainUpperBoundaryMode() {
        return domainUpperBoundaryMode;
    }

    public void setDomainUpperBoundaryMode(BoundaryMode domainUpperBoundaryMode) {
        this.domainUpperBoundaryMode = domainUpperBoundaryMode;
    }

    public BoundaryMode getDomainLowerBoundaryMode() {
        return domainLowerBoundaryMode;
    }

    public void setDomainLowerBoundaryMode(BoundaryMode domainLowerBoundaryMode) {
        this.domainLowerBoundaryMode = domainLowerBoundaryMode;
    }

    public BoundaryMode getRangeUpperBoundaryMode() {
        return rangeUpperBoundaryMode;
    }

    public void setRangeUpperBoundaryMode(BoundaryMode rangeUpperBoundaryMode) {
        this.rangeUpperBoundaryMode = rangeUpperBoundaryMode;
    }

    public BoundaryMode getRangeLowerBoundaryMode() {
        return rangeLowerBoundaryMode;
    }

    public void setRangeLowerBoundaryMode(BoundaryMode rangeLowerBoundaryMode) {
        this.rangeLowerBoundaryMode = rangeLowerBoundaryMode;
    }

    public void setMinX(Number minX) {
        this.minX = minX;
    }

    public void setMaxX(Number maxX) {
        this.maxX = maxX;
    }

    public void setMinY(Number minY) {
        this.minY = minY;
    }

    public void setMaxY(Number maxY) {
        this.maxY = maxY;
    }
}
