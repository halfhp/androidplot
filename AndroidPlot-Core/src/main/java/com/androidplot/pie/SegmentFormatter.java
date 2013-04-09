/*
 * Copyright 2013 AndroidPlot.com
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

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Formatter;

public class SegmentFormatter extends Formatter<PieChart> {

    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_EDGE_COLOR = Color.BLACK;
    private static final float DEFAULT_EDGE_THICKNESS = 8;

    private Paint innerEdgePaint;
    private Paint outerEdgePaint;
    private Paint radialEdgePaint;
    private Paint fillPaint;

    public SegmentFormatter(Integer fillColor) {
        setFillPaint(new Paint());
        if(fillColor != null) {
            getFillPaint().setColor(fillColor);
        } else {
            getFillPaint().setColor(DEFAULT_FILL_COLOR);
        }

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
    }

    public SegmentFormatter(Integer fillColor, Integer outerEdgeColor,
                            Integer innerEdgeColor, Integer radialEdgeColor) {
        this(fillColor);


        if(getOuterEdgePaint() != null) {
            getOuterEdgePaint().setColor(outerEdgeColor);
        } else {
            getOuterEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }

        if (getInnerEdgePaint() != null) {
            getInnerEdgePaint().setColor(innerEdgeColor);
        } else {
            getInnerEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }

        if (getRadialEdgePaint() != null) {
            getRadialEdgePaint().setColor(radialEdgeColor);
        } else {
            getRadialEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return PieRenderer.class;
    }

    @Override
    public SeriesRenderer getRendererInstance(PieChart plot) {
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
}
