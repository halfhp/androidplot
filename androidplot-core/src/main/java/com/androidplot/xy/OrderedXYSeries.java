package com.androidplot.xy;

/**
 * An implementation of {@link XYSeries} that gives hints to it's renderer about the order
 * of the data being rendered.
 */
public interface OrderedXYSeries extends XYSeries {

    enum XOrder {
        /**
         * XVals are in strict ascending order such that:
         * x(i) < x(i+1) == true
         */
        ASCENDING,

        /**
         * XVals are in strict descending order such that:
         * x(i) > x(i+1) == true
         */
        DESCENDING,

        /**
         * XVals appear in no particular order.
         */
        NONE
    }

    /**
     * The order of XVals as they appear in this series.
     * @return
     */
    XOrder getXOrder();
}
