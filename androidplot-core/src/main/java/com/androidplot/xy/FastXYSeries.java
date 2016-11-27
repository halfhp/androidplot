package com.androidplot.xy;

/**
 * An implementation of {@link XYSeries} that defines additional methods to speed up rendering by
 * giving a hint to the renderer about the min/max values contained in the series.
 */
public interface FastXYSeries extends XYSeries {

    /**
     * TIP: You can use {@link RectRegion#union(Number, Number)} during
     * to keep a running tally of min/max values when iterating.
     * @return A {@link RectRegion} representing the min/max values that currently exist this series.
     */
    RectRegion minMax();
}
