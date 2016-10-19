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

import android.content.*;
import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;

/**
 * Defines the visual aesthetics of an XYSeries; outline color and width, fill style,
 * vertex size and color, shadowing, etc.
 */
public class LineAndPointFormatter extends XYSeriesFormatter<XYRegionFormatter> {

    private static final float DEFAULT_LINE_STROKE_WIDTH_DP   = 1.5f;
    private static final float DEFAULT_VERTEX_STROKE_WIDTH_DP = 4.5f;

    public FillDirection getFillDirection() {
        return fillDirection;
    }

    /**
     * Sets which edge to use to close the line's path for filling purposes.
     * See {@link FillDirection}.
     * @param fillDirection
     */
    public void setFillDirection(FillDirection fillDirection) {
        this.fillDirection = fillDirection;
    }

    protected FillDirection fillDirection = FillDirection.BOTTOM;
    protected Paint linePaint;
    protected Paint vertexPaint;
    protected Paint fillPaint;
    protected InterpolationParams interpolationParams;

    public LineAndPointFormatter(Context context, int xmlCfgId) {
        super(context, xmlCfgId);
    }

    /**
     * Should only be used in conjunction with calls to configure()...
     */
    public LineAndPointFormatter() {
        this(Color.RED, Color.GREEN, Color.BLUE, null);
    }

    public LineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor,
            PointLabelFormatter plf) {
        this(lineColor, vertexColor, fillColor, plf, FillDirection.BOTTOM);
    }

    public LineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor,
            PointLabelFormatter plf, FillDirection fillDir) {
        initLinePaint(lineColor);
        initVertexPaint(vertexColor);
        initFillPaint(fillColor);
        setFillDirection(fillDir);
        this.setPointLabelFormatter(plf);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return LineAndPointRenderer.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(XYPlot plot) {
        return new LineAndPointRenderer(plot);
    }

    protected void initLinePaint(Integer lineColor) {
        if (lineColor == null) {
            linePaint = null;
        } else {
            linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(PixelUtils.dpToPix(DEFAULT_LINE_STROKE_WIDTH_DP));
            linePaint.setColor(lineColor);
            linePaint.setStyle(Paint.Style.STROKE);
        }
    }

    protected void initVertexPaint(Integer vertexColor) {
        if (vertexColor == null) {
            vertexPaint = null;
        } else {
            vertexPaint = new Paint();
            vertexPaint.setAntiAlias(true);
            vertexPaint.setStrokeWidth(PixelUtils.dpToPix(DEFAULT_VERTEX_STROKE_WIDTH_DP));
            vertexPaint.setColor(vertexColor);
            vertexPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    protected void initFillPaint(Integer fillColor) {
        if (fillColor == null) {
            fillPaint = null;
        } else {
            fillPaint = new Paint();
            fillPaint.setAntiAlias(true);
            fillPaint.setColor(fillColor);
        }
    }

    /**
     *
     * @return True if linePaint has been set, false otherwise.
     */
    public boolean hasLinePaint() {
        return linePaint != null;
    }

    /**
     * Get the {@link Paint} used to draw lines.  Will instantiate and a new default instance
     * if it is currently null.  To run whether or not line paint has been set, use
     * {@link #hasLinePaint()}.
     * @return
     */
    public Paint getLinePaint() {
        if(linePaint == null) {
            initLinePaint(Color.TRANSPARENT);
        }
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    /**
     *
     * @return True if vertexPaint has been set, false otherwise.
     */
    public boolean hasVertexPaint() {
        return vertexPaint != null;
    }

    /**
     * Get the {@link Paint} used to draw vertices (points).  Will instantiate and a new default instance
     * if it is currently null.  To run whether or not vertex paint has been set, use
     * {@link #hasVertexPaint()}.
     * @return
     */
    public Paint getVertexPaint() {
        if(vertexPaint == null) {
            initVertexPaint(Color.TRANSPARENT);
        }
        return vertexPaint;
    }

    public void setVertexPaint(Paint vertexPaint) {
        this.vertexPaint = vertexPaint;
    }

    /**
     *
     * @return True if fillPaint has been set, false otherwise.
     */
    public boolean hasFillPaint() {
        return fillPaint != null;
    }
    /**
     * Get the {@link Paint} used to fill series areas.  Will instantiate and a new default instance
     * if it is currently null.  To run whether or not fill paint has been set, use
     * {@link #hasFillPaint()}.
     * @return
     */
    public Paint getFillPaint() {
        if(fillPaint == null) {
            initFillPaint(Color.TRANSPARENT);
        }
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public InterpolationParams getInterpolationParams() {
        return interpolationParams;
    }

    public void setInterpolationParams(InterpolationParams params) {
        this.interpolationParams = params;
    }
}
