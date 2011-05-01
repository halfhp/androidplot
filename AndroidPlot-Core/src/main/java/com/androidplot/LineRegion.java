package com.androidplot;

/**
 * A one dimensional region represented by a starting and ending value.
 */
public class LineRegion {
    private double minVal;
    private double maxVal;

    public LineRegion(double minVal, double maxVal) {
        this.setMinVal(minVal);
        this.setMaxVal(maxVal);
    }

    /**
     * Tests whether a value is within the given range
     * @param value
     * @return
     */
    public boolean contains(double value) {
        return value >= minVal && value <= maxVal;
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
    public  boolean intersects(double line2Min, double line2Max) {

        //double l1min = getMinVal() == null ? Double.NEGATIVE_INFINITY : getMinVal().doubleValue();
        //double l1max = getMaxVal() == null ? Double.POSITIVE_INFINITY : getMaxVal().doubleValue();

        //double l2min = line2Min == null ? Double.NEGATIVE_INFINITY : line2Min.doubleValue();
        //double l2max = line2Max == null ? Double.POSITIVE_INFINITY : line2Max.doubleValue();

        // is this line completely within line2?
        if(line2Min <= this.minVal && line2Max >= this.maxVal) {
            return true;
        // is line1 partially within line2
        } else return contains(line2Min) || contains(line2Max);
    }

    public double getMinVal() {
        return minVal;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }
}
