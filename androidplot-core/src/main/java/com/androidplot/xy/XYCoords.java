// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import androidx.annotation.Nullable;

/**
 * A pair of x/y coordinates
 */
public class XYCoords {
    public Number x;
    public Number y;

    public XYCoords() {}

    public XYCoords(@Nullable Number x, @Nullable Number y) {
        this.x = x;
        this.y = y;
    }
}
