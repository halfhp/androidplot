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

package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.util.PixelUtils;

/**
 * Encapsulates sizing preferences associated with a Widget; how/if it scales etc.
 */
public class SizeMetrics {
    private SizeMetric heightMetric;
    private SizeMetric widthMetric;

    /**
     * Convenience constructor.  Wraps {@link #SizeMetrics(SizeMetric, SizeMetric)}.
     * @param height Height value used algorithm to calculate the height of the associated widget(s).
     * @param heightLayoutType Algorithm used to calculate the height of the associated widget(s).
     * @param width Width value used algorithm to calculate the width of the associated widget(s).
     * @param widthLayoutType Algorithm used to calculate the width of the associated widget(s).
     */
    public SizeMetrics(float height, SizeLayoutType heightLayoutType, float width, SizeLayoutType widthLayoutType) {
        heightMetric = new SizeMetric(height, heightLayoutType);
        widthMetric = new SizeMetric(width, widthLayoutType);
    }

    /**
     * Creates a new SizeMetrics instance using the specified size layout algorithm and value.
     * See {@link SizeMetric} for details on what can be passed in.
     * @param heightMetric
     * @param widthMetric
     */
    public SizeMetrics(SizeMetric heightMetric, SizeMetric widthMetric) {
        this.heightMetric = heightMetric;
        this.widthMetric = widthMetric;
    }

    public SizeMetric getHeightMetric() {
        return heightMetric;
    }

    public void setHeightMetric(SizeMetric heightMetric) {
        this.heightMetric = heightMetric;
    }

    public SizeMetric getWidthMetric() {
        return widthMetric;
    }

    /**
     * Calculates a RectF with calculated width and height.  The top-left corner is set to 0,0.
     * @param canvasRect
     * @return
     */
    public RectF getRectF(RectF canvasRect) {
        return new RectF(
                0,
                0,
                widthMetric.getPixelValue(canvasRect.width()),
                heightMetric.getPixelValue(canvasRect.height()));
    }

    /**
     * Same as getRectF but with edges rounded to the nearest full pixel.
     * @param canvasRect
     * @return
     */
    public RectF getRoundedRect(RectF canvasRect) {
        return PixelUtils.nearestPixRect(0, 0, widthMetric.getPixelValue(canvasRect.width()),
                heightMetric.getPixelValue(canvasRect.height()));
    }

    public void setWidthMetric(SizeMetric widthMetric) {
        this.widthMetric = widthMetric;
    }
}
