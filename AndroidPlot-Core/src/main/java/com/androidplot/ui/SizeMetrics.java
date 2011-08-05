/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
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
