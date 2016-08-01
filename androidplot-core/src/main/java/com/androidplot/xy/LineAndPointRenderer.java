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

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.RenderStack;
import com.androidplot.util.ValPixConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class LineAndPointRenderer<FormatterType extends LineAndPointFormatter> extends XYSeriesRenderer<XYSeries, FormatterType> {

    protected static final int ZERO = 0;
    protected static final int ONE = 1;

    public LineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea, XYSeries series, FormatterType formatter, RenderStack stack) throws PlotRenderException {
        drawSeries(canvas, plotArea, series, formatter);
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
        Path path = null;
        ArrayList<PointF> points = new ArrayList<>(series.size());
        for (int i = 0; i < series.size(); i++) {
            Number y = series.getY(i);
            Number x = series.getX(i);

            if (y != null && x != null) {
                thisPoint = ValPixConverter.valToPix(
                        x, y,
                        plotArea,
                        getPlot().getCalculatedMinX(),
                        getPlot().getCalculatedMaxX(),
                        getPlot().getCalculatedMinY(),
                        getPlot().getCalculatedMaxY());
                points.add(thisPoint);
            } else {
                thisPoint = null;
            }

            // don't need to do any of this if the line isnt going to be drawn:
            if(linePaint != null && formatter.getInterpolationParams() == null) {
                if (thisPoint != null) {

                    // record the first point of the new Path
                    if (firstPoint == null) {
                        path = new Path();
                        firstPoint = thisPoint;

                        // create our first point at the bottom/x position so filling will look good:
                        path.moveTo(firstPoint.x, firstPoint.y);
                    }

                    if (lastPoint != null) {
                        appendToPath(path, thisPoint, lastPoint);
                    }

                    lastPoint = thisPoint;
                } else {
                    if (lastPoint != null) {
                        renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
                    }
                    firstPoint = null;
                    lastPoint = null;
                }
            }
        }
        if(linePaint != null) {
            if(formatter.getInterpolationParams() != null) {
                List<XYCoords> interpolatedPoints = getInterpolator(
                        formatter.getInterpolationParams()).interpolate(series,
                        formatter.getInterpolationParams());
                firstPoint = convertPoint(interpolatedPoints.get(ZERO), plotArea);
                lastPoint = convertPoint(interpolatedPoints.get(interpolatedPoints.size()-ONE), plotArea);
                path = new Path();
                path.moveTo(firstPoint.x, firstPoint.y);
                for(int i = 1; i < interpolatedPoints.size(); i++) {
                    thisPoint = convertPoint(interpolatedPoints.get(i), plotArea);
                    path.lineTo(thisPoint.x, thisPoint.y);
                }
            }

            if(firstPoint != null) {
                renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
            }
        }
        renderPoints(canvas, plotArea, series, points, formatter);
    }

    /**
     * TODO: retrieve from a persistent registry
     * @param params
     * @return An interpol
     */
    protected Interpolator getInterpolator(InterpolationParams params) {
        try {
            return (Interpolator) params.getInterpolatorClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected PointF convertPoint(XYCoords coord, RectF plotArea) {
        return ValPixConverter.valToPix(
                coord.x.doubleValue(),
                coord.y.doubleValue(),
                plotArea,
                getPlot().getCalculatedMinX(),
                getPlot().getCalculatedMaxX(),
                getPlot().getCalculatedMinY(),
                getPlot().getCalculatedMaxY());
    }

    protected void renderPoints(Canvas canvas, RectF plotArea, XYSeries series, List<PointF> points,
                                LineAndPointFormatter formatter) {
        Paint vertexPaint = formatter.getVertexPaint();
        PointLabelFormatter plf = formatter.getPointLabelFormatter();
        if (vertexPaint != null || plf != null) {
            int i = 0;
            for (PointF p : points) {
                PointLabeler pointLabeler = formatter.getPointLabeler();

                // if vertexPaint is available, draw vertex:
                if (vertexPaint != null) {
                    canvas.drawPoint(p.x, p.y, formatter.getVertexPaint());
                }

                // if textPaint and pointLabeler are available, draw point's text label:
                if (plf != null && pointLabeler != null) {
                    canvas.drawText(pointLabeler.getLabel(series, i),
                            p.x + plf.hOffset, p.y + plf.vOffset, plf.getTextPaint());
                }
                i++;
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
                float originPix = (float) ValPixConverter.valToPix(
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
