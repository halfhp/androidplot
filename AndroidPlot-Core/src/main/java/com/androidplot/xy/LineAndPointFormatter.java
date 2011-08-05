/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Defines the visual aesthetics of an XYSeries; outline color and width, fill style,
 * vertex size and color, shadowing, etc.
 */
public class LineAndPointFormatter extends XYSeriesFormatter<XYRegionFormatter> {

    private static final float DEFAULT_LINE_STROKE_WIDTH = 1.5f;
    private static final float DEFAULT_VERTEX_STROKE_WIDTH = 4.5f;

    private int labelStep = 1;  // only every labelStep'th point label will be drawn.

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

    private FillDirection fillDirection;

    private Paint linePaint;
    private Paint vertexPaint;
    private Paint fillPaint;

    protected LineAndPointFormatter() {
        this(Color.RED, Color.GREEN, Color.BLUE);
    }

    /**
     * Set corresponding parameter to null to disable the drawing of lines, vertexes or fill.
     * Uses a default of FillDirection.BOTTOM.
     * @param lineColor
     * @param vertexColor
     * @param fillColor
     */
    public LineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor) {
         this(lineColor, vertexColor, fillColor, FillDirection.BOTTOM);
    }

    /**
     *
     * @param lineColor
     * @param vertexColor
     * @param fillColor
     * @param fillDir Determines which edge or origin of the plot is used to close the path for filling.
     */
    public LineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor, FillDirection fillDir) {
        initLinePaint(lineColor);
        initVertexPaint(vertexColor);
        //this(lineColor, vertexColor);
        initFillPaint(fillColor);
        setFillDirection(fillDir);
    }

    protected void initLinePaint(Integer lineColor) {
        if (lineColor == null) {
            linePaint = null;
        } else {
            linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(DEFAULT_LINE_STROKE_WIDTH);
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
            vertexPaint.setStrokeWidth(DEFAULT_VERTEX_STROKE_WIDTH);
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
     * Enables the shadow layer on linePaint and shadowPaint by calling
     * setShadowLayer() with preset values.
     */
    public void enableShadows() {
        linePaint.setShadowLayer(1, 3, 3, Color.BLACK);
        vertexPaint.setShadowLayer(1, 3, 3, Color.BLACK);
    }

    public void disableShadows() {
        linePaint.setShadowLayer(0, 0, 0, Color.BLACK);
        vertexPaint.setShadowLayer(0, 0, 0, Color.BLACK);
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public Paint getVertexPaint() {
        return vertexPaint;
    }

    public void setVertexPaint(Paint vertexPaint) {
        this.vertexPaint = vertexPaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public int getLabelStep() {
        return labelStep;
    }

    public void setLabelStep(int labelStep) {
        this.labelStep = labelStep;
    }
}
