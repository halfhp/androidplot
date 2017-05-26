/*
 * Copyright 2016 AndroidPlot.com
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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;

/**
 * Format for drawing a value using {@link BubbleRenderer}.
 * @since 1.2.2
 */
public class BubbleFormatter extends XYSeriesFormatter<XYRegionFormatter> {

    private static final float DEFAULT_STROKE_PIX = 1;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_FILL_COLOR = Color.YELLOW;


    private Paint strokePaint;
    private Paint fillPaint;

    {
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(PixelUtils.dpToPix(DEFAULT_STROKE_PIX));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(DEFAULT_STROKE_COLOR);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(DEFAULT_FILL_COLOR);

        // default point labeler should draw z for bubbles:
        setPointLabeler(new PointLabeler<BubbleSeries>() {
            @Override
            public String getLabel(BubbleSeries series, int index) {
                return String.valueOf(series.getZ(index));
            }
        });
    }

    public BubbleFormatter() {}

    public BubbleFormatter(Context context, int xmlCfgId) {
        this();
        configure(context, xmlCfgId);
    }

    public BubbleFormatter(int fillColor, int strokeColor) {
        fillPaint.setColor(fillColor);
        strokePaint.setColor(strokeColor);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return BubbleRenderer.class;
    }

    @Override
    public BubbleRenderer doGetRendererInstance(XYPlot plot) {
        return new BubbleRenderer(plot);
    }

    public Paint getStrokePaint() {
        return strokePaint;
    }

    public void setStrokePaint(Paint strokePaint) {
        this.strokePaint = strokePaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }
}
