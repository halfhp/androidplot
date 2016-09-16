/*
 * Copyright 2015 AndroidPlot.com
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
 * Defines physical dimensions & scaling characteristics
 */
public class Size {

    // convenience value; sets size to 100% width and height of the widget container.
    public static Size FILL = new Size(0, SizeMode.FILL, 0, SizeMode.FILL);

    private SizeMetric height;
    private SizeMetric width;

    /**
     * Convenience constructor.  Wraps {@link #Size(SizeMetric, SizeMetric)}.
     * @param height Height value used algorithm to calculate the height of the associated widget(s).
     * @param heightLayoutType Algorithm used to calculate the height of the associated widget(s).
     * @param width Width value used algorithm to calculate the width of the associated widget(s).
     * @param widthLayoutType Algorithm used to calculate the width of the associated widget(s).
     */
    public Size(float height, SizeMode heightLayoutType, float width, SizeMode widthLayoutType) {
        this.height = new SizeMetric(height, heightLayoutType);
        this.width = new SizeMetric(width, widthLayoutType);
    }

    /**
     * Creates a new SizeMetrics instance using the specified size layout algorithm and value.
     * See {@link SizeMetric} for details on what can be passed in.
     * @param height
     * @param width
     */
    public Size(SizeMetric height, SizeMetric width) {
        this.height = height;
        this.width = width;
    }

    public SizeMetric getHeight() {
        return height;
    }

    public void setHeight(SizeMetric height) {
        this.height = height;
    }

    public SizeMetric getWidth() {
        return width;
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
                width.getPixelValue(canvasRect.width()),
                height.getPixelValue(canvasRect.height()));
    }

    public void setWidth(SizeMetric width) {
        this.width = width;
    }
}
