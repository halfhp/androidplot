// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.util.PixelUtils;
import androidx.annotation.NonNull;

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
    public Size(float height, @NonNull SizeMode heightLayoutType, float width, @NonNull SizeMode widthLayoutType) {
        this.height = new SizeMetric(height, heightLayoutType);
        this.width = new SizeMetric(width, widthLayoutType);
    }

    /**
     * Creates a new SizeMetrics instance using the specified size layout algorithm and value.
     * See {@link SizeMetric} for details on what can be passed in.
     * @param height
     * @param width
     */
    public Size(@NonNull SizeMetric height, @NonNull SizeMetric width) {
        this.height = height;
        this.width = width;
    }

    @NonNull
    public SizeMetric getHeight() {
        return height;
    }

    public void setHeight(@NonNull SizeMetric height) {
        this.height = height;
    }

    @NonNull
    public SizeMetric getWidth() {
        return width;
    }

    /**
     * Calculates a RectF with calculated width and height.  The top-left corner is set to 0,0.
     * @param canvasRect
     * @return
     */
    @NonNull
    public RectF getRectF(@NonNull RectF canvasRect) {
        return new RectF(
                0,
                0,
                width.getPixelValue(canvasRect.width()),
                height.getPixelValue(canvasRect.height()));
    }

    public void setWidth(@NonNull SizeMetric width) {
        this.width = width;
    }
}
