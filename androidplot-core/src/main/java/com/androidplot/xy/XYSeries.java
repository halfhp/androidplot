// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.Series;

/**
 * Represents a two dimensional series of data represented as xy values.
 */
public interface XYSeries extends Series {

    /**
     * @return Number of elements in this Series.
     */
    int size();

    /**
     * Returns the x-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The x-value.
     */
    Number getX(int index);

    /**
     * Returns the y-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The y-value.
     */
    Number getY(int index);
}
