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

    public FillDirection getFillDirection() {
        return fillDirection;
    }

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
            //linePaint.setShadowLayer(1, 3, 3, Color.BLACK);
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

            vertexPaint.setShadowLayer(1, 3, 3, Color.BLACK);
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
}
