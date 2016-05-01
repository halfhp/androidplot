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
public class LineRegion {
    private Number minVal;
    private Number maxVal;

    public LineRegion(Number v1, Number v2) {
        if (v1 != null && v2 != null && v1.doubleValue() < v2.doubleValue()) {
            this.setMinVal(v1);
            this.setMaxVal(v2);
        } else {
            this.setMinVal(v2);
            this.setMaxVal(v1);
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
        return new LineRegion(v1, v2).length();
    }

    public Number length() {
        return maxVal == null || minVal == null ? null : maxVal.doubleValue() - minVal.doubleValue();
    }

    /**
     * Tests whether a value is within the given range
     * @param value
     * @return
     */
    public boolean contains(Number value) {
        return value.doubleValue() >= minVal.doubleValue() && value.doubleValue() <= maxVal.doubleValue();
    }

    public boolean intersects(LineRegion lineRegion) {
        return intersects(lineRegion.getMinVal(), lineRegion.getMaxVal());
    }

     /**
     * Tests whether this segment intersects another
     * @param line2Min
     * @param line2Max
     * @return
     */
    public  boolean intersects(Number line2Min, Number line2Max) {

        // is this line completely within line2?
        if(line2Min.doubleValue() <= this.minVal.doubleValue() && line2Max.doubleValue() >= this.maxVal.doubleValue()) {
            return true;
        // is line1 partially within line2
        } else return contains(line2Min) || contains(line2Max);
    }

    public Number getMinVal() {
        return minVal;
    }

    public void setMinVal(Number minVal) {
        if(minVal == null) {
            throw new NullPointerException("Region values can never be null.");
        }
        this.minVal = minVal;
    }

    public Number getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Number maxVal) {
        if(maxVal == null) {
            throw new NullPointerException("Region values can never be null.");
        }
        this.maxVal = maxVal;
    }
}
