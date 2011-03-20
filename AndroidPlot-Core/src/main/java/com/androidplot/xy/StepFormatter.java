package com.androidplot.xy;

import android.graphics.Paint;

public class StepFormatter extends LineAndPointFormatter {

    public StepFormatter(Paint linePaint) {
        super(linePaint, null, null);
    }

    public StepFormatter(Paint linePaint, Paint fillPaint) {
        super(linePaint, null, fillPaint);
    }

    public StepFormatter(int lineColor, int fillColor) {
        //super(lineColor, fillColor);
        initLinePaint(lineColor);
        initFillPaint(fillColor);
    }

}

/*public class StepFormatter extends XYSeriesFormatter {

    private Paint linePaint;
    private Paint vertexPaint;

    {

    }

    public StepFormatter(Paint linePaint, Paint vertexPaint) {
        this.linePaint = linePaint;
        this.vertexPaint = vertexPaint;
    }

    *//**
     * Creates a new StepFormatter using default Paint configurations
     * for line and vertex, but using the specified colors.
     * @param lineColor
     * @param vertexColor
     *//*
    public StepFormatter(int lineColor, int vertexColor) {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(1.5f);
        linePaint.setColor(lineColor);

        vertexPaint = new Paint();
        vertexPaint.setAntiAlias(true);
        vertexPaint.setStrokeWidth(2.5f);
        vertexPaint.setColor(vertexColor);
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
}*/
