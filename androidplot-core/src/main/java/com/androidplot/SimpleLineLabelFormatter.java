// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.util.PixelUtils;

/**
 * A basic implementation of a {@link LineLabelFormatter}.
 */
public class SimpleLineLabelFormatter implements LineLabelFormatter {

    private static final int DEFAULT_TEXT_SIZE_SP = 12;
    private static final int DEFAULT_STROKE_SIZE_DP = 2;
    private Paint paint;

    public SimpleLineLabelFormatter() {
        this(new Paint());
        getPaint().setColor(Color.WHITE);
        getPaint().setTextSize(PixelUtils.spToPix(DEFAULT_TEXT_SIZE_SP));
        getPaint().setStrokeWidth(PixelUtils.dpToPix(DEFAULT_STROKE_SIZE_DP));
    }

    public SimpleLineLabelFormatter(int color) {
        this();
        getPaint().setColor(color);
    }

    public SimpleLineLabelFormatter(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public Paint getPaint(Number value) {
        return getPaint();
    }
}
