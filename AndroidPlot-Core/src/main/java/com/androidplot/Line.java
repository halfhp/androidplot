package com.androidplot;

/**
 * A one dimensional element
 */
public class Line {
    private Number minVal;
    private Number maxVal;

    public Line(Number minVal, Number maxVal) {
        this.setMinVal(minVal);
        this.setMaxVal(maxVal);
    }

    /**
     * Tests whether a value is within the given range
     * @param value
     * @return
     */
    public boolean contains(double value) {
        if((getMinVal() == null || value >= getMinVal().doubleValue()) &&
                (getMaxVal() == null || value <= getMaxVal().doubleValue())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean intersects(Line line) {
        return intersects(line.getMinVal(), line.getMaxVal());
    }

     /**
     * Tests whether this segment intersects another
     * @param line2Min
     * @param line2Max
     * @return
     */
    public  boolean intersects(Number line2Min, Number line2Max) {

        double l1min = getMinVal() == null ? Double.NEGATIVE_INFINITY : getMinVal().doubleValue();
        double l1max = getMaxVal() == null ? Double.POSITIVE_INFINITY : getMaxVal().doubleValue();

        double l2min = line2Min == null ? Double.NEGATIVE_INFINITY : line2Min.doubleValue();
        double l2max = line2Max == null ? Double.POSITIVE_INFINITY : line2Max.doubleValue();

        // is this line completely within line2?
        if(l2min <= l1min && l2max >= l1max) {
            return true;
        // is line1 partially within line2
        } else if(contains(l2min) || contains(l2max)) {
            return true;
        // lines do not intersect at all
        } else {
            return false;
        }
    }

    public Number getMinVal() {
        return minVal;
    }

    public void setMinVal(Number minVal) {
        this.minVal = minVal;
    }

    public Number getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Number maxVal) {
        this.maxVal = maxVal;
    }
}
