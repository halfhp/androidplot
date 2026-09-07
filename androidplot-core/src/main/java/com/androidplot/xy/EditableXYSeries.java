// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

/**
 * An {@link XYSeries} that exposes methods to set values and resize
 */
public interface EditableXYSeries extends XYSeries {

    void setX(Number x, int index);
    void setY(Number y, int index);

    /**
     * Resize to accommodate the specified number of x/y pairs.  If elements must be droped, those
     * at the highest iVal should be removed first.
     * @param size
     */
    void resize(int size);
}
