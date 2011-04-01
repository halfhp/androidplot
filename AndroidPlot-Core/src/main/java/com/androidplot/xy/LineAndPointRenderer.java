package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.series.XYSeries;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.util.ValPixConverter;

import java.util.ArrayList;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class LineAndPointRenderer<FormatterType extends LineAndPointFormatter> extends XYSeriesRenderer<FormatterType> {

    private PointF lastPoint;

    private boolean drawLinesEnabled = true;
    private boolean drawPointsEnabled = true;



    public LineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {



        for(XYSeries series : getPlot().getSeriesListForRenderer(this.getClass())) {

            drawSeries(canvas, plotArea, series, getFormatter(series));
        }
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, String text, LineAndPointFormatter formatter) {
        // horizontal icon:
        float centerY = rect.centerY();
        float centerX = rect.centerX();

        if(formatter.getFillPaint() != null) {
            canvas.drawRect(rect, formatter.getFillPaint());
        }
        if(formatter.getLinePaint() != null) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, formatter.getLinePaint());
        }

        if(formatter.getVertexPaint() != null) {
            canvas.drawPoint(centerX, centerY, formatter.getVertexPaint());
        }
    }

    /**
     * This method exists for StepRenderer to override without having to duplicate any
     * additional code.
     */
    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        path.lineTo(thisPoint.x, thisPoint.y);
    }


    private void drawSeries(Canvas canvas, RectF plotArea, XYSeries series, LineAndPointFormatter formatter) throws PlotRenderException {
        //beginSeries(canvas, plotArea, formatter);
        //XYDataset series = bundle.getDataset();
        //int seriesIndex = bundle.getSeriesIndex();
        PointF thisPoint = null;
        PointF lastPoint = null;
        PointF firstPoint = null;
        //PointF lastDrawn = null;
        Path path = null;
        ArrayList<PointF> points = new ArrayList<PointF>(series.size());
        for (int i = 0; i < series.size(); i++) {
            Number y = series.getY(i);
            Number x = series.getX(i);

            if (y != null && x != null) {
                thisPoint = ValPixConverter.valToPix(
                        x,
                        y,
                        plotArea,
                        getPlot().getCalculatedMinX(),
                        getPlot().getCalculatedMaxX(),
                        getPlot().getCalculatedMinY(),
                        getPlot().getCalculatedMaxY());
                points.add(thisPoint);
                //appendToPath(path, thisPoint, lastPoint);
            } else {
                thisPoint = null;
            }

            if(thisPoint != null) {

                // record the first point of the new Path
                if(firstPoint == null) {
                    path = new Path();
                    firstPoint = thisPoint;
                    // create our first point at the bottom/x position so filling
                    // will look good
                    //path.moveTo(firstPoint.x, plotArea.bottom);
                    //path.lineTo(firstPoint.x, firstPoint.y);
                    path.moveTo(firstPoint.x, firstPoint.y);
                }

                if(lastPoint != null) {
                    appendToPath(path, thisPoint, lastPoint);
                }

                lastPoint = thisPoint;
            } else {
                if(lastPoint != null) {
                    renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
                }
                firstPoint = null;
                lastPoint = null;
            }
        }
        if(firstPoint != null) {
            renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
        }

        // TODO: benchmark this against drawPoints(float[]);
        if (formatter.getVertexPaint() != null) {
            for (PointF p : points) {
                canvas.drawPoint(p.x, p.y, formatter.getVertexPaint());
            }
        }
    }

    protected void renderPath(Canvas canvas, RectF plotArea, Path path, PointF firstPoint, PointF lastPoint, LineAndPointFormatter formatter) {
        Path outlinePath = null;
        if (formatter.getLinePaint() != null) {
            if (formatter.getFillPaint() == null) {
                outlinePath = path;

            // if there is no fillpaint, then we dont need to
            // clone the original path to avoid fill points being added.
            } else {
                outlinePath = new Path(path);
            }
        }

        // draw the fill "layer":
        if (formatter.getFillPaint() != null) {
            // create our last point at the bottom/x position so filling
            // will look good
            path.lineTo(lastPoint.x, plotArea.bottom);
            path.lineTo(firstPoint.x, plotArea.bottom);
            path.close();
            canvas.drawPath(path, formatter.getFillPaint());
        }

        // finally we draw the outline path on top of everything else:
        if(outlinePath != null) {
            //formatter.getLinePaint().setStrokeWidth(5);
            canvas.drawPath(outlinePath, formatter.getLinePaint());
        }

        if(path != null) {
            path.rewind();
        }
    }

    /*protected void renderLines() {

    }

    protected void renderPoints() {

    }

    protected void renderFill() {

    }*/

    /*private void beginSeries(Canvas canvas, RectF plotArea, LineAndPointFormatter format) throws PlotRenderException {
        lastPoint = null;
    }

    private void drawPoint(Canvas canvas, PointF point, RectF plotArea, LineAndPointFormatter format) throws PlotRenderException {
        if (lastPoint != null) {
            if (point != null) {
                //doDrawLine(canvas, lastPoint, point, plotArea, format);
                canvas.drawLine(lastPoint.x, lastPoint.y, point.x, point.y, format.getLinePaint());

            }
            drawLastPoint(canvas, plotArea, format);
        }

        lastPoint = point;
    }

    private void endSeries(Canvas canvas, RectF plotArea, LineAndPointFormatter format) throws PlotRenderException {
        if(lastPoint != null) {
            drawLastPoint(canvas, plotArea, format);
        }
    }*/

    protected void drawLastPoint(Canvas canvas, RectF plotArea, LineAndPointFormatter format) throws PlotRenderException {
        canvas.drawPoint(lastPoint.x, lastPoint.y, format.getVertexPaint());
       
    }


}
