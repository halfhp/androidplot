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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.Region;
import com.androidplot.ui.RenderStack;
import com.androidplot.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class LineAndPointRenderer<FormatterType extends LineAndPointFormatter> extends XYSeriesRenderer<XYSeries, FormatterType> {

    protected static final int ZERO = 0;
    protected static final int ONE = 1;

    private final Path path = new Path();

    protected final ConcurrentHashMap<XYSeries, ArrayList<PointF>> pointsCaches
            = new ConcurrentHashMap<>(2, 0.75f, 2);

    public LineAndPointRenderer(XYPlot plot) {
        super(plot);
        plot.addListener(new PlotListener() {
            @Override
            public void onBeforeDraw(Plot source, Canvas canvas) {
                cullPointsCache();
            }

            @Override
            public void onAfterDraw(Plot source, Canvas canvas) {

            }
        });
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea, XYSeries series, FormatterType formatter, RenderStack stack) {
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
        if(formatter.hasLinePaint()) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, formatter.getLinePaint());
        }

        if(formatter.hasVertexPaint()) {
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

    /**
     * Retrieves or initializes a list for storing calculated screen-coords to render as points.
     * Also handles automatic resizing and culling of unused caches.
     * Should only be called once per render cycle.
     * @param series
     * @return
     */
    protected ArrayList<PointF> getPointsCache(XYSeries series) {
        ArrayList<PointF> pointsCache = pointsCaches.get(series);
        final int seriesSize = series.size();
        if(pointsCache == null) {
            pointsCache = new ArrayList<>(seriesSize);
            pointsCaches.put(series, pointsCache);
        }

        if(pointsCache.size() < seriesSize) {
            while(pointsCache.size() < seriesSize) {
                pointsCache.add(null);
            }
        } else if(pointsCache.size() > seriesSize) {
            while(pointsCache.size() > seriesSize) {
                pointsCache.remove(0);
            }
        }
        return pointsCache;
    }

    protected void cullPointsCache() {
        for(XYSeries series : pointsCaches.keySet()) {
            if(!getPlot().getRegistry().contains(series, LineAndPointFormatter.class)) {
                pointsCaches.remove(series);
            }
        }
    }

    protected void drawSeries(Canvas canvas, RectF plotArea, XYSeries series, LineAndPointFormatter formatter) {
        PointF thisPoint;
        PointF lastPoint = null;
        PointF firstPoint = null;
        path.reset();
        final List<PointF> points = getPointsCache(series);

        int iStart = 0;
        int iEnd = series.size();
        if(SeriesUtils.getXYOrder(series) == OrderedXYSeries.XOrder.ASCENDING) {
            final Region iBounds = SeriesUtils.iBounds(series, getPlot().getBounds());
            iStart = iBounds.getMin().intValue();
            if(iStart > 0) {
                iStart--;
            }
            iEnd = iBounds.getMax().intValue() + 1;
            if(iEnd < series.size() - 1) {
                iEnd++;
            }
        }
        for (int i = iStart; i < iEnd; i++) {
            final Number y = series.getY(i);
            final Number x = series.getX(i);
            PointF iPoint = points.get(i);

            if (y != null && x != null) {
                if(iPoint == null) {
                    iPoint = new PointF();
                    points.set(i, iPoint);
                }
                thisPoint = iPoint;
                getPlot().getBounds().transformScreen(thisPoint, x, y, plotArea);
            } else {
                thisPoint = null;
                iPoint = null;
                points.set(i, iPoint);
            }

            // don't need to do any of this if the line isnt going to be drawn:
            if(formatter.hasLinePaint() && formatter.getInterpolationParams() == null) {
                if (thisPoint != null) {

                    // record the first point of the new Path
                    if (firstPoint == null) {
                        path.reset();
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

        if(formatter.hasLinePaint()) {
            if(formatter.getInterpolationParams() != null) {
                List<XYCoords> interpolatedPoints = getInterpolator(
                        formatter.getInterpolationParams()).interpolate(series,
                        formatter.getInterpolationParams());
                firstPoint = convertPoint(interpolatedPoints.get(ZERO), plotArea);
                lastPoint = convertPoint(interpolatedPoints.get(interpolatedPoints.size()-ONE), plotArea);
                path.reset();
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
        renderPoints(canvas, plotArea, series, iStart, iEnd, points, formatter);
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
        return getPlot().getBounds().transformScreen(coord, plotArea);
    }

    protected void renderPoints(Canvas canvas, RectF plotArea, XYSeries series, int iStart, int iEnd, List<PointF> points,
                                LineAndPointFormatter formatter) {
        if (formatter.hasVertexPaint() || formatter.hasPointLabelFormatter()) {
            final Paint vertexPaint = formatter.hasVertexPaint() ? formatter.getVertexPaint() : null;
            final boolean hasPointLabelFormatter = formatter.hasPointLabelFormatter();
            final PointLabelFormatter plf = hasPointLabelFormatter ? formatter.getPointLabelFormatter() : null;
            final PointLabeler pointLabeler = hasPointLabelFormatter ? formatter.getPointLabeler() : null;
            for(int i = iStart; i < iEnd; i++) {
                PointF p = points.get(i);
                if(p != null) {

                    // if vertexPaint is available, draw vertex:
                    if (vertexPaint != null) {
                        canvas.drawPoint(p.x, p.y, vertexPaint);
                    }

                    // if textPaint and pointLabeler are available, draw point's text label:
                    if (pointLabeler != null) {
                        canvas.drawText(pointLabeler.getLabel(series, i),
                                p.x + plf.hOffset, p.y + plf.vOffset, plf.getTextPaint());
                    }
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
                float originPix = (float) getPlot().getBounds().getxRegion()
                        .transform(getPlot().getRangeOrigin()
                                .doubleValue(), plotArea.top, plotArea.bottom, true);
                path.lineTo(lastPoint.x, originPix);
                path.lineTo(firstPoint.x, originPix);
                path.close();
                break;
            default:
                throw new UnsupportedOperationException(
                        "Fill direction not yet implemented: " + formatter.getFillDirection());
        }

        if (formatter.getFillPaint() != null) {
            canvas.drawPath(path, formatter.getFillPaint());
        }

        final RectRegion bounds = getPlot().getBounds();
        final RectRegion plotRegion = new RectRegion(plotArea);

        // draw each region:
        for (RectRegion thisRegion : bounds.intersects(formatter.getRegions().elements())) {
            XYRegionFormatter regionFormatter = formatter.getRegionFormatter(thisRegion);
            RectRegion thisRegionTransformed = bounds
                    .transform(thisRegion, plotRegion, false, true);
            thisRegionTransformed.intersect(plotRegion);
            if(thisRegion.isFullyDefined()) {
                RectF thisRegionRectF = thisRegionTransformed.asRectF();
                if (thisRegionRectF != null) {
                    try {
                        canvas.save();
                        canvas.clipPath(path);
                        canvas.drawRect(thisRegionRectF, regionFormatter.getPaint());
                    } finally {
                        canvas.restore();
                    }
                }
            }
        }

        // finally we draw the outline path on top of everything else:
        if(formatter.hasLinePaint()) {
            canvas.drawPath(outlinePath, formatter.getLinePaint());
        }

        path.rewind();
    }
}
