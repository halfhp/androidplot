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

package com.androidplot.pie;

import android.content.*;
import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Formatter;

public class SegmentFormatter extends Formatter<PieChart> {

    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_EDGE_COLOR = Color.BLACK;
    private static final int DEFAULT_LABEL_COLOR = Color.WHITE;
    private static final float DEFAULT_EDGE_THICKNESS = 3;
    private static final float DEFAULT_LABEL_MARKER_THICKNESS = 3;
    private static final float DEFAULT_LABEL_FONT_SIZE = 18;

    private Paint innerEdgePaint;
    private Paint outerEdgePaint;
    private Paint radialEdgePaint;
    private Paint fillPaint;

    private Paint labelPaint;
    private Paint labelMarkerPaint;

    private float offset;
    private float radialInset;
    private float innerInset;
    private float outerInset;

    {
        setFillPaint(new Paint());
        // outer edge:
        setOuterEdgePaint(new Paint());
        getOuterEdgePaint().setStyle(Paint.Style.STROKE);
        getOuterEdgePaint().setStrokeWidth(DEFAULT_EDGE_THICKNESS);
        getOuterEdgePaint().setAntiAlias(true);

        // inner edge:
        setInnerEdgePaint(new Paint());
        getInnerEdgePaint().setStyle(Paint.Style.STROKE);
        getInnerEdgePaint().setStrokeWidth(DEFAULT_EDGE_THICKNESS);
        getInnerEdgePaint().setAntiAlias(true);

        // radial edge:
        setRadialEdgePaint(new Paint());
        getRadialEdgePaint().setStyle(Paint.Style.STROKE);
        getRadialEdgePaint().setStrokeWidth(DEFAULT_EDGE_THICKNESS);
        getRadialEdgePaint().setAntiAlias(true);

        // label paint:
        setLabelPaint(new Paint());
        getLabelPaint().setColor(DEFAULT_LABEL_COLOR);
        getLabelPaint().setTextSize(DEFAULT_LABEL_FONT_SIZE);
        getLabelPaint().setAntiAlias(true);
        getLabelPaint().setTextAlign(Paint.Align.CENTER);

        // label marker paint:
        setLabelMarkerPaint(new Paint());
        getLabelMarkerPaint().setColor(DEFAULT_LABEL_COLOR);
        getLabelMarkerPaint().setStrokeWidth(DEFAULT_LABEL_MARKER_THICKNESS);
    }

    public SegmentFormatter(Integer fillColor) {
        if(fillColor != null) {
            getFillPaint().setColor(fillColor);
        } else {
            getFillPaint().setColor(DEFAULT_FILL_COLOR);
        }
    }

    public SegmentFormatter(Context context, int xmlCfgId) {
        configure(context, xmlCfgId);
    }

    public SegmentFormatter(Integer fillColor, Integer borderColor) {
        this(fillColor);
        getInnerEdgePaint().setColor(borderColor);
        getOuterEdgePaint().setColor(borderColor);
        getRadialEdgePaint().setColor(borderColor);
    }

    public SegmentFormatter(Integer fillColor, Integer outerEdgeColor,
                            Integer innerEdgeColor, Integer radialEdgeColor) {
        this(fillColor);
        if(getOuterEdgePaint() != null) {
            getOuterEdgePaint().setColor(outerEdgeColor);
        } else {
            outerEdgePaint = new Paint();
            getOuterEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }

        if (getInnerEdgePaint() != null) {
            getInnerEdgePaint().setColor(innerEdgeColor);
        } else {
            outerEdgePaint = new Paint();
            getInnerEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }

        if (getRadialEdgePaint() != null) {
            getRadialEdgePaint().setColor(radialEdgeColor);
        } else {
            radialEdgePaint = new Paint();
            getRadialEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return PieRenderer.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(PieChart plot) {
        return new PieRenderer(plot);
    }

    public Paint getInnerEdgePaint() {
        return innerEdgePaint;
    }

    public void setInnerEdgePaint(Paint innerEdgePaint) {
        this.innerEdgePaint = innerEdgePaint;
    }

    public Paint getOuterEdgePaint() {
        return outerEdgePaint;
    }

    public void setOuterEdgePaint(Paint outerEdgePaint) {
        this.outerEdgePaint = outerEdgePaint;
    }

    public Paint getRadialEdgePaint() {
        return radialEdgePaint;
    }

    public void setRadialEdgePaint(Paint radialEdgePaint) {
        this.radialEdgePaint = radialEdgePaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public Paint getLabelPaint() {
        return labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    public Paint getLabelMarkerPaint() {
        return labelMarkerPaint;
    }

    public void setLabelMarkerPaint(Paint labelMarkerPaint) {
        this.labelMarkerPaint = labelMarkerPaint;
    }

    public float getOffset() {
        return offset;
    }

    /**
     * Set an offset relative to the center of the pie chart at which this segment should be drawn;
     * generally used to highlight specific segments.
     * @param offset
     */
    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getRadialInset() {
        return radialInset;
    }

    /**
     * Set an inset in degrees for the radial edges of this segment.
     * generally used to highlight specific segments.
     * @param radialInset
     */
    public void setRadialInset(float radialInset) {
        this.radialInset = radialInset;
    }

    public float getInnerInset() {
        return innerInset;
    }

    public void setInnerInset(float innerInset) {
        this.innerInset = innerInset;
    }

    public float getOuterInset() {
        return outerInset;
    }

    public void setOuterInset(float outerInset) {
        this.outerInset = outerInset;
    }
}
