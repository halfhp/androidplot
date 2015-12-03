/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.util;

import android.graphics.RectF;

/**
 * Convenience class for managing BoxModel data
 */
public class DisplayDimensions {

    public final RectF canvasRect;
    public final RectF marginatedRect;
    public final RectF paddedRect;

    // init to 1 to avoid potential divide by zero errors (yet to be observed in practice)
    private static final RectF initRect;

    static {
        initRect = new RectF(1, 1, 1, 1);
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
