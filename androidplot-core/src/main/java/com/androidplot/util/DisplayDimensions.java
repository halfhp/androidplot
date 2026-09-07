// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import android.graphics.RectF;

/**
 * Convenience class for managing {@link com.androidplot.ui.BoxModel} data
 */
public class DisplayDimensions {

    private static final int ONE = 1;

    public final RectF canvasRect;
    public final RectF marginatedRect;
    public final RectF paddedRect;

    // init to 1 to avoid potential divide by zero errors (yet to be observed in practice)
    private static final RectF initRect;

    static {
        initRect = new RectF(ONE, ONE, ONE, ONE);
    }

    public DisplayDimensions() {
        this(initRect, initRect, initRect);
    }
    public DisplayDimensions(RectF canvasRect, RectF marginatedRect, RectF paddedRect) {
        this.canvasRect = canvasRect;
        this.marginatedRect = marginatedRect;
        this.paddedRect = paddedRect;
    }
}
