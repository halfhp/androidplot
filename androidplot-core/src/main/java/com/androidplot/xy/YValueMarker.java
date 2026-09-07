// SPDX-License-Identifier: Apache-2.0

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
