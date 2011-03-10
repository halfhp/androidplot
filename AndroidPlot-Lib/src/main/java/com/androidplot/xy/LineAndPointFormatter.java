package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;

public class LineAndPointFormatter extends XYSeriesFormatter {

    private static final float DEFAULT_LINE_STROKE_WIDTH = 1.5f;
    private static final float DEFAULT_VERTEX_STROKE_WIDTH = 4.5f;

    private Paint linePaint;
    private Paint vertexPaint;
    private Paint fillPaint;




    @Deprecated
    public LineAndPointFormatter(Paint linePaint, Paint vertexPaint) {
        this(linePaint, vertexPaint, null);
    }

    public LineAndPointFormatter(Paint linePaint, Paint vertexPaint, Paint fillPaint) {
        this.linePaint = linePaint;
        this.vertexPaint = vertexPaint;
        this.linePaint = linePaint;
        this.vertexPaint = vertexPaint;
        this.fillPaint = fillPaint;
    }

    protected LineAndPointFormatter() {        
    }

    /**
     * Creates a new LineAndPointFormatter using default Paint configurations
     * for line and vertex, but using the specified colors.
     * @param lineColor
     * @param vertexColor
     */
    @Deprecated
    public LineAndPointFormatter(Integer lineColor, Integer vertexColor) {

        initLinePaint(lineColor);


        initVertexPaint(vertexColor);

        fillPaint = null;
    }

    public LineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor) {
        this(lineColor, vertexColor);
        initFillPaint(fillColor);
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
            linePaint.setShadowLayer(1, 3, 3, Color.BLACK);
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
