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

import android.graphics.Path;
import android.graphics.PointF;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */

public class StepRenderer extends LineAndPointRenderer<StepFormatter> {

    public StepRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        //path.lineTo(thisPoint.x, thisPoint.y);

        path.lineTo(thisPoint.x, lastPoint.y);
        path.lineTo(thisPoint.x, thisPoint.y);
                        //canvas.drawPoint(point.x, lastPoint.y, format.getVertexPaint());
                        // next the vertical:
                        //canvas.drawLine(point.x, lastPoint.y, point.x, point.y, format.getLinePaint());
    }
}
/*
public class StepRenderer extends XYSeriesRenderer<StepFormatter> {

    private PointF lastPoint;

    private boolean drawLinesEnabled = true;
    private boolean drawPointsEnabled = true;

    private XYAxisType stepAxis = XYAxisType.DOMAIN;



    public StepRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {



        for(XYSeries series : getPlot().getSeriesListForRenderer(this.getClass())) {

            drawSeries(canvas, plotArea, series, getFormatter(series));
        }
        //foreach(this.)
        //foreach()
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, String text, StepFormatter formatter) {
        // horizontal icon:
        float centerY = rect.centerY();
        float centerX = rect.centerX();
        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, formatter.getLinePaint());
        canvas.drawPoint(centerX, centerY, formatter.getVertexPaint());
        //canvas.drawRect(rect, formatter.getLinePaint());

    }


    private void drawSeries(Canvas canvas, RectF plotArea, XYSeries series, StepFormatter formatter) throws PlotRenderException {
        beginSeries(canvas, plotArea, formatter);
        //XYDataset series = bundle.getDataset();
        //int seriesIndex = bundle.getSeriesIndex();
        PointF thisPoint;
        for (int i = 0; i < series.size(); i++) {
            Number y = series.getY(i);
            Number x = series.getD(i);

            if (y != null && x != null) {

                thisPoint = ValPixConverter.valToPix(
                        x,
                        y,
                        plotArea,
                        getPlot().getCalculatedMinX(),
                        getPlot().getCalculatedMaxX(),
                        getPlot().getCalculatedMinY(),
                        getPlot().getCalculatedMaxY());
                //float pixX = ValPixConverter.valToPix(x.doubleValue(), getPlot().getCalculatedMinX().doubleValue(), getPlot().getCalculatedMaxX().doubleValue(), plotArea.width(), false) + (plotArea.left);
                //float pixY = ValPixConverter.valToPix(y.doubleValue(), getPlot().getCalculatedMinY().doubleValue(), getPlot().getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;

                //thisPoint = new PointF(pixX, pixY);
            } else {
                thisPoint = null;
            }
            drawPoint(canvas, thisPoint, plotArea, formatter);
        }
        endSeries(canvas, plotArea, formatter);
    }

    private void beginSeries(Canvas canvas, RectF plotArea, StepFormatter format) throws PlotRenderException {
        lastPoint = null;
    }

    private void drawPoint(Canvas canvas, PointF point, RectF plotArea, StepFormatter format) throws PlotRenderException {
        if (lastPoint != null) {
            if (point != null) {

                switch(stepAxis) {
                    case DOMAIN:
                        // first draw the horizontal line:
                        canvas.drawLine(lastPoint.x, lastPoint.y, point.x, lastPoint.y, format.getLinePaint());
                        canvas.drawPoint(point.x, lastPoint.y, format.getVertexPaint());
                        // next the vertical:
                        canvas.drawLine(point.x, lastPoint.y, point.x, point.y, format.getLinePaint());
                        break;
                    case RANGE:
                        break;
                }
                //doDrawLine(canvas, lastPoint, point, plotArea, format);


            }
            drawLastPoint(canvas, plotArea, format);
        }

        lastPoint = point;
    }

    private void endSeries(Canvas canvas, RectF plotArea, StepFormatter format) throws PlotRenderException {
        if(lastPoint != null) {
            drawLastPoint(canvas, plotArea, format);
        }
    }

    protected void drawLastPoint(Canvas canvas, RectF plotArea, StepFormatter format) throws PlotRenderException {
        canvas.drawPoint(lastPoint.x, lastPoint.y, format.getVertexPaint());
    }


    public XYAxisType getStepAxis() {
        return stepAxis;
    }

    public void setStepAxis(XYAxisType stepAxis) {
        this.stepAxis = stepAxis;
    }
}
*/
