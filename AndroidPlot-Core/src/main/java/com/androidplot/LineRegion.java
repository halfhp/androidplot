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


package com.androidplot;

/**
 * A one dimensional region represented by a starting and ending value.
 */
public class LineRegion {
    private Number minVal;
    private Number maxVal;

    public LineRegion(Number minVal, Number maxVal) {
        this.setMinVal(minVal);
        this.setMaxVal(maxVal);
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

        //double l1min = getMinVal() == null ? Double.NEGATIVE_INFINITY : getMinVal().doubleValue();
        //double l1max = getMaxVal() == null ? Double.POSITIVE_INFINITY : getMaxVal().doubleValue();

        //double l2min = line2Min == null ? Double.NEGATIVE_INFINITY : line2Min.doubleValue();
        //double l2max = line2Max == null ? Double.POSITIVE_INFINITY : line2Max.doubleValue();


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
