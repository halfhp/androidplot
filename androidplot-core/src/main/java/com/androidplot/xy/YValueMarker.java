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

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.HorizontalPosition;

public class YValueMarker extends ValueMarker<HorizontalPosition> {


    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     */
    public YValueMarker(Number value, String text) {
        super(value, text, new HorizontalPosition(3, HorizontalPositioning.ABSOLUTE_FROM_LEFT));
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public YValueMarker(Number value, String text, HorizontalPosition textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public YValueMarker(Number value, String text, HorizontalPosition textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    @Override
    public void draw(Canvas canvas, XYPlot plot, RectF gridRect) {
        if (getValue() != null) {
            float yPix = (float) plot.getBounds().yRegion
                    .transform(getValue()
                            .doubleValue(), gridRect.top, gridRect.bottom, true);
            canvas.drawLine(gridRect.left, yPix,
                    gridRect.right, yPix, getLinePaint()
            );

            float xPix = getTextPosition().getPixelValue(
                    gridRect.width());
            xPix += gridRect.left;
            drawMarkerText(canvas, getText(), gridRect, xPix, yPix);
        }
    }
}
