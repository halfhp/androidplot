// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.VerticalPosition;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class XValueMarker extends ValueMarker<VerticalPosition> {

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default getFormatter.
     */
    public XValueMarker(@Nullable Number value, @Nullable String text) {
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
    public XValueMarker(@Nullable Number value, @Nullable String text, @NonNull VerticalPosition textPosition, @NonNull Paint linePaint, @NonNull Paint textPaint) {
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
    public XValueMarker(@Nullable Number value, @Nullable String text, @NonNull VerticalPosition textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull XYPlot plot, @NonNull RectF gridRect) {
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
