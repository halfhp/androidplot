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

import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.VerticalPosition;

public class XValueMarker extends ValueMarker<VerticalPosition> {

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     */
    public XValueMarker(Number value, String text) {
        super(value, text, new VerticalPosition(3, VerticalPositioning.ABSOLUTE_FROM_TOP));
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public XValueMarker(Number value, String text, VerticalPosition textPosition, Paint linePaint, Paint textPaint) {
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
    public XValueMarker(Number value, String text, VerticalPosition textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    @Override
    public void draw(Canvas canvas, XYPlot plot, RectF gridRect) {
        if (getValue() != null) {
            float xPix = (float) plot.getBounds().xRegion
                    .transform(getValue().doubleValue(), gridRect.left, gridRect.right, false);
            canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom, getLinePaint()
            );
            float yPix = getTextPosition().getPixelValue(gridRect.height());
            yPix += gridRect.top;
            if (getText() != null) {
                drawMarkerText(canvas, getText(), gridRect, xPix, yPix);
            }
        }
    }
}
