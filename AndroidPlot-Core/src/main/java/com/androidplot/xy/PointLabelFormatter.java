/*
 * Copyright 2012 AndroidPlot.com
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.androidplot.util.PixelUtils;

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

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    protected void initTextPaint(Integer textColor) {
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
