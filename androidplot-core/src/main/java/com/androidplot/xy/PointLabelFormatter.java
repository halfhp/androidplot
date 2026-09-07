// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.util.PixelUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PointLabelFormatter {
    private static final float DEFAULT_H_OFFSET_DP = 0;
    private static final float DEFAULT_V_OFFSET_DP = -4;
    private static final float DEFAULT_TEXT_SIZE_SP = 12;
    private Paint textPaint;
    public float hOffset;
    public float vOffset;

    public PointLabelFormatter() {
        this(Color.WHITE);
    }

    public PointLabelFormatter(int textColor) {
        this(textColor, PixelUtils.dpToPix(DEFAULT_H_OFFSET_DP),
                PixelUtils.dpToPix(DEFAULT_V_OFFSET_DP));
    }

    /**
     *
     * @param textColor
     * @param hOffset Horizontal offset of text in pixels.
     * @param vOffset Vertical offset of text in pixels.  Offset is in screen coordinates;
     *                positive values shift the text further down the screen.
     */
    public PointLabelFormatter(int textColor, float hOffset, float vOffset) {
        initTextPaint(textColor);
        this.hOffset = hOffset;
        this.vOffset = vOffset;
    }

    public boolean hasTextPaint() {
        return textPaint != null;
    }

    @NonNull
    public Paint getTextPaint() {
        if(textPaint == null) {
            initTextPaint(Color.TRANSPARENT);
        }
        return textPaint;
    }

    public void setTextPaint(@Nullable Paint textPaint) {
        this.textPaint = textPaint;
    }

    protected void initTextPaint(@Nullable Integer textColor) {
        if (textColor == null) {
            setTextPaint(null);
        } else {
            setTextPaint(new Paint());
            getTextPaint().setAntiAlias(true);
            getTextPaint().setColor(textColor);
            getTextPaint().setTextAlign(Paint.Align.CENTER);
            getTextPaint().setTextSize(PixelUtils.spToPix(DEFAULT_TEXT_SIZE_SP));
            //textPaint.setStyle(Paint.Style.STROKE);
        }
    }
}
