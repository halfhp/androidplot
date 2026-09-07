// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import android.content.*;
import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Formatter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public SegmentFormatter(@Nullable Integer fillColor) {
        if(fillColor != null) {
            getFillPaint().setColor(fillColor);
        } else {
            getFillPaint().setColor(DEFAULT_FILL_COLOR);
        }
    }

    public SegmentFormatter(@NonNull Context context, int xmlCfgId) {
        configure(context, xmlCfgId);
    }

    public SegmentFormatter(@Nullable Integer fillColor, @NonNull Integer borderColor) {
        this(fillColor);
        getInnerEdgePaint().setColor(borderColor);
        getOuterEdgePaint().setColor(borderColor);
        getRadialEdgePaint().setColor(borderColor);
    }

    public SegmentFormatter(@Nullable Integer fillColor, @NonNull Integer outerEdgeColor,
                            @NonNull Integer innerEdgeColor, @NonNull Integer radialEdgeColor) {
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
    @NonNull
    public Class<? extends SeriesRenderer> getRendererClass() {
        return PieRenderer.class;
    }

    @Override
    @NonNull
    public SeriesRenderer doGetRendererInstance(@NonNull PieChart plot) {
        return new PieRenderer(plot);
    }

    @NonNull
    public Paint getInnerEdgePaint() {
        return innerEdgePaint;
    }

    public void setInnerEdgePaint(@NonNull Paint innerEdgePaint) {
        this.innerEdgePaint = innerEdgePaint;
    }

    @NonNull
    public Paint getOuterEdgePaint() {
        return outerEdgePaint;
    }

    public void setOuterEdgePaint(@NonNull Paint outerEdgePaint) {
        this.outerEdgePaint = outerEdgePaint;
    }

    @NonNull
    public Paint getRadialEdgePaint() {
        return radialEdgePaint;
    }

    public void setRadialEdgePaint(@NonNull Paint radialEdgePaint) {
        this.radialEdgePaint = radialEdgePaint;
    }

    @NonNull
    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(@NonNull Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    @Nullable
    public Paint getLabelPaint() {
        return labelPaint;
    }

    public void setLabelPaint(@Nullable Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    @NonNull
    public Paint getLabelMarkerPaint() {
        return labelMarkerPaint;
    }

    public void setLabelMarkerPaint(@NonNull Paint labelMarkerPaint) {
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
