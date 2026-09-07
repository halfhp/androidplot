// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.HorizontalPosition;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class YValueMarker extends ValueMarker<HorizontalPosition> {


    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     */
    public YValueMarker(@Nullable Number value, @Nullable String text) {
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
    public YValueMarker(@Nullable Number value, @Nullable String text, @NonNull HorizontalPosition textPosition, @NonNull Paint linePaint, @NonNull Paint textPaint) {
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
    public YValueMarker(@Nullable Number value, @Nullable String text, @NonNull HorizontalPosition textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull XYPlot plot, @NonNull RectF gridRect) {
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
