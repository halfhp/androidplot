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

import android.graphics.*;
import android.util.Pair;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.util.ValPixConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class LineAndPointRenderer<FormatterType extends LineAndPointFormatter> extends XYSeriesRenderer<FormatterType> {

    public LineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {


        List<XYSeries> seriesList = getPlot().getSeriesListForRenderer(this.getClass());
        if (seriesList != null) {
            for (XYSeries series : seriesList) {
                //synchronized(series) {
                    drawSeries(canvas, plotArea, series, getFormatter(series));
                //}
            }
        }
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, LineAndPointFormatter formatter) {
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


    protected void drawSeries(Canvas canvas, RectF plotArea, XYSeries series, LineAndPointFormatter formatter) {
        PointF thisPoint;
        PointF lastPoint = null;
        PointF firstPoint = null;
        Paint  linePaint = formatter.getLinePaint();

        //PointF lastDrawn = null;
        Path path = null;
        ArrayList<Pair<PointF, Integer>> points = new ArrayList<Pair<PointF, Integer>>(series.size());
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
                points.add(new Pair<PointF, Integer>(thisPoint, i));
                //appendToPath(path, thisPoint, lastPoint);
            } else {
                thisPoint = null;
            }

            if(linePaint != null && thisPoint != null) {

                // record the first point of the new Path
                if(firstPoint == null) {
                    path = new Path();
                    firstPoint = thisPoint;
                    // create our first point at the bottom/x position so filling
                    // will look good
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
        if(linePaint != null && firstPoint != null) {
            renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
        }

        // TODO: benchmark this against drawPoints(float[]);
        Paint vertexPaint = formatter.getVertexPaint();
        PointLabelFormatter plf = formatter.getPointLabelFormatter();
        if (vertexPaint != null || plf != null) {
            for (Pair<PointF, Integer> p : points) {
            	PointLabeler pointLabeler = formatter.getPointLabeler();
 
                // if vertexPaint is available, draw vertex:
                if(vertexPaint != null) {
                    canvas.drawPoint(p.first.x, p.first.y, formatter.getVertexPaint());
                }

                // if textPaint and pointLabeler are available, draw point's text label:
                if(plf != null && pointLabeler != null) {
                    canvas.drawText(pointLabeler.getLabel(series, p.second), p.first.x + plf.hOffset, p.first.y + plf.vOffset, plf.getTextPaint());
                }
            }
        }
    }

    protected void renderPath(Canvas canvas, RectF plotArea, Path path, PointF firstPoint, PointF lastPoint, LineAndPointFormatter formatter) {
        Path outlinePath = new Path(path);

        // determine how to close the path for filling purposes:
        // We always need to calculate this path because it is also used for
        // masking off for region highlighting.
        switch (formatter.getFillDirection()) {
            case BOTTOM:
                path.lineTo(lastPoint.x, plotArea.bottom);
                path.lineTo(firstPoint.x, plotArea.bottom);
                path.close();
                break;
            case TOP:
                path.lineTo(lastPoint.x, plotArea.top);
                path.lineTo(firstPoint.x, plotArea.top);
                path.close();
                break;
            case RANGE_ORIGIN:
                float originPix = ValPixConverter.valToPix(
                        getPlot().getRangeOrigin().doubleValue(),
                        getPlot().getCalculatedMinY().doubleValue(),
                        getPlot().getCalculatedMaxY().doubleValue(),
                        plotArea.height(),
                        true);
                originPix += plotArea.top;

                path.lineTo(lastPoint.x, originPix);
                path.lineTo(firstPoint.x, originPix);
                path.close();
                break;
            default:
                throw new UnsupportedOperationException("Fill direction not yet implemented: " + formatter.getFillDirection());
        }

        if (formatter.getFillPaint() != null) {
            canvas.drawPath(path, formatter.getFillPaint());
        }


        //}

        // draw any visible regions on top of the base region:
        double minX = getPlot().getCalculatedMinX().doubleValue();
        double maxX = getPlot().getCalculatedMaxX().doubleValue();
        double minY = getPlot().getCalculatedMinY().doubleValue();
        double maxY = getPlot().getCalculatedMaxY().doubleValue();

        // draw each region:
        for (RectRegion r : RectRegion.regionsWithin(formatter.getRegions().elements(), minX, maxX, minY, maxY)) {
            XYRegionFormatter f = formatter.getRegionFormatter(r);
            RectF regionRect = r.getRectF(plotArea, minX, maxX, minY, maxY);
            if (regionRect != null) {
                try {
                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.clipPath(path);
                canvas.drawRect(regionRect, f.getPaint());
                } finally {
                    canvas.restore();
                }
            }
        }

        // finally we draw the outline path on top of everything else:
        if(formatter.getLinePaint() != null) {
            canvas.drawPath(outlinePath, formatter.getLinePaint());
        }

        path.rewind();
    }
}
