package com.androidplot.series;

/**
 * Represents a two dimensional series of data represented as xy values.
 */
public interface XYSeries extends Series {
    /**
     * Returns the x-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The x-value.
     */
    public Number getX(int index);

    /**
     * Returns the y-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The y-value.
     */
    public Number getY(int index);
}
