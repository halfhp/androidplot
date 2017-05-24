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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * A faster implementation of of {@link LineAndPointRenderer}.  For performance reasons, has these constraints:
 * - Interpolation is not supported
 * - Does not draw fill
 * - Does not support null values
 * @since 1.2.0
 */
public class FastLineAndPointRenderer extends XYSeriesRenderer<XYSeries, FastLineAndPointRenderer.Formatter> {

    /**
     * A line drawn by {@link Canvas#drawLines(float[], int, int, Paint)} must be defined by at
     * least four points {@code x0, y0, x1, y1}
     */
    private static final int MINIMUM_NUMBER_OF_POINTS_TO_DEFINE_A_LINE = 4;
    private float[] points;
    List<Integer> segmentOffsets = new ArrayList<>();
    List<Integer> segmentLengths = new ArrayList<>();
    public FastLineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void onRender(Canvas canvas, RectF plotArea, XYSeries series, Formatter formatter, RenderStack stack) throws PlotRenderException {

        segmentOffsets.clear();
        segmentLengths.clear();

        final int numPoints = series.size() * 2;
        if(points == null || points.length != numPoints) {
            // only allocate when necessary:
            points = new  float[series.size()*2];
        }

        int segmentLen = 0;
        boolean isLastPointNull = true;
        PointF resultPoint = new PointF();
        for (int i = 0, j = 0;  i < series.size(); i++, j+=2) {
            Number y = series.getY(i);
            Number x = series.getX(i);

            if (y != null && x != null) {
                if(isLastPointNull) {
                    segmentOffsets.add(j);
                    segmentLen = 0;
                    isLastPointNull = false;
                }

                getPlot().getBounds().transformScreen(resultPoint, x, y, plotArea);
                points[j] = resultPoint.x;
                points[j + 1] = resultPoint.y;
                segmentLen+=2;

                // if this is the last point, account for it in segment lengths:
                if(i == series.size()-1) {
                    segmentLengths.add(segmentLen);
                }
            } else if(!isLastPointNull) {
                segmentLengths.add(segmentLen);
                isLastPointNull = true;
            }
        }

        // draw segments
        if(formatter.linePaint != null || formatter.vertexPaint != null) {
            for (int i = 0; i < segmentOffsets.size(); i++) {
                final int len = segmentLengths.get(i);
                final int offset = segmentOffsets.get(i);
                drawSegment(canvas, points, offset, len, formatter);
            }
        }
    }

    protected void drawSegment(@NonNull Canvas canvas,
                               @NonNull float[] points,
                               int offset,
                               int len,
                               Formatter formatter) {
        if(formatter.linePaint != null) {
            // draw lines:
            if (len >= MINIMUM_NUMBER_OF_POINTS_TO_DEFINE_A_LINE) {
                // optimization to avoid using 2x storage space to represent the full path:
                if ((len & 2) != 0) {
                    canvas.drawLines(points, offset, len - 2, formatter.linePaint);
                    canvas.drawLines(points, offset + 2, len - 2, formatter.linePaint);
                } else {
                    canvas.drawLines(points, offset, len, formatter.linePaint);
                    canvas.drawLines(points, offset + 2, len - 4, formatter.linePaint);
                }
            }
        }

        if(formatter.vertexPaint != null) {
            // draw vertices:
            canvas.drawPoints(points, offset, len, formatter.vertexPaint);
        }
    }

    @Override
    protected void doDrawLegendIcon(@NonNull Canvas canvas,
                                    @NonNull RectF rect,
                                    @NonNull Formatter formatter) {
        if(formatter.hasLinePaint()) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, formatter.getLinePaint());
        }

        if(formatter.hasVertexPaint()) {
            canvas.drawPoint(rect.centerX(), rect.centerY(), formatter.getVertexPaint());
        }
    }

    /**
     * Formatter designed to work in tandem with {@link AdvancedLineAndPointRenderer}.
     * @since 0.9.9
     */
    public static class Formatter extends LineAndPointFormatter {

        public Formatter(Integer lineColor, Integer vertexColor, PointLabelFormatter plf) {
            super(lineColor, vertexColor, null, plf);
        }

        @Override
        protected void initLinePaint(Integer lineColor) {
            super.initLinePaint(lineColor);

            // disable anti-aliasing by default:
            getLinePaint().setAntiAlias(false);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return FastLineAndPointRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new FastLineAndPointRenderer(plot);
        }
    }
}
