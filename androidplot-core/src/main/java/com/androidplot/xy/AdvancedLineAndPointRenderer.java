/*
 * Copyright 2016 AndroidPlot.com
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
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.ValPixConverter;
import java.util.ArrayList;

/**
 * This is an experimental (but stable) implementation of an {@link XYSeriesRenderer} that provides instrumentation
 * allowing advanced behaviors like dynamically coloring / styling individual segments of a series, etc.  This class
 * may be removed or renamed in future releases.
 * Currently has the following constraints:
 * - Interpolation is not supported
 * - Only draws lines; no points or fill
 * - Draws series lines using simple Canvas.drawLine(...) invocations.
 * @since 0.9.9
 */
public class AdvancedLineAndPointRenderer extends XYSeriesRenderer<XYSeries, AdvancedLineAndPointRenderer.Formatter> {

    private int latestIndex;

    public AdvancedLineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void onRender(Canvas canvas, RectF plotArea, XYSeries series, Formatter formatter, RenderStack stack) throws PlotRenderException {
        PointF thisPoint;
        PointF lastPoint = null;
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
            if(formatter.getLinePaint() != null) {
                if (thisPoint != null && lastPoint != null) {
                    canvas.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y, formatter.getLinePaint(i, latestIndex, series.size()));
                }
            }
            lastPoint = thisPoint;
        }
    }

    @Override
    protected void doDrawLegendIcon(Canvas canvas, RectF rect, Formatter formatter) {
        if(formatter.getLinePaint() != null) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, formatter.getLinePaint());
        }
    }

    public void setLatestIndex(int latestIndex) {
        this.latestIndex = latestIndex;
    }


    /**
     * Formatter designed to work in tandem with {@link AdvancedLineAndPointRenderer}.
     * @since 0.9.9
     */
    public static class Formatter extends XYSeriesFormatter<XYRegionFormatter> {

        private Paint linePaint;

        {
            linePaint = new Paint();
            linePaint.setStrokeWidth(3);
            linePaint.setColor(Color.RED);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return AdvancedLineAndPointRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new AdvancedLineAndPointRenderer(plot);
        }

        public Paint getLinePaint() {
            return linePaint;
        }

        /**
         * By default, simply returns the line paint as-is.  May be overridden to provide custom behavior based
         * on input params.
         * @param thisIndex
         * @param latestIndex
         * @param seriesSize
         * @return
         */
        public Paint getLinePaint(int thisIndex, int latestIndex, int seriesSize) {
            return getLinePaint();
        }


        public void setLinePaint(Paint linePaint) {
            this.linePaint = linePaint;
        }
    }
}
