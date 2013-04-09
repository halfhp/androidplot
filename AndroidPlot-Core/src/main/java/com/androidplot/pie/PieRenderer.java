/*
 * Copyright 2013 AndroidPlot.com
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

package com.androidplot.pie;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.SeriesRenderer;

import java.util.Set;

public class PieRenderer extends SeriesRenderer<PieChart, SegmentFormatter> {

    // starting angle to use when drawing the first radial line of the first segment.
    private float startDeg = 15;

    // TODO: express donut in units other than px.
    private float donutPix = 160;

    public PieRenderer(PieChart plot) {
        super(plot);
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {

        float radius = plotArea.width() < plotArea.height() ? plotArea.width() / 2 : plotArea.height() / 2;
        PointF origin = new PointF(plotArea.centerX(), plotArea.centerY());

        double scale = calculateScale();
        float offset = startDeg;
        Set<Segment> segments = getPlot().getSeriesSet();

        PointF lastRadial = calculateLineEnd(origin, radius, offset);

        RectF rec = new RectF(origin.x - radius, origin.y - radius, origin.x + radius, origin.y + radius);

        for (Segment segment : segments) {
            float lastOffset = offset;
            float sweep = (float) scale * (segment.getValue().floatValue()) * 360;
            offset += sweep;
            PointF radial = calculateLineEnd(origin, radius, offset);
            drawSegment(canvas, rec, getPlot().getFormatter(segment, PieRenderer.class),
                    lastRadial, radial, radius, lastOffset, sweep);
            lastRadial = radial;
        }
    }

    protected void drawSegment(Canvas canvas, RectF bounds, SegmentFormatter f,
                               PointF r1, PointF r2, float rad, float startAngle, float sweep) {
        canvas.save();

        float cx = bounds.centerX();
        float cy = bounds.centerY();

        Path clip = new Path();

        //float outerStroke = f.getOuterEdgePaint().getStrokeWidth();
        //float halfOuterStroke = outerStroke / 2;

        // leave plenty of room on the outside for stroked borders;
        // necessary because the clipping border is ugly
        // and cannot be easily anti aliased.  Really we only care about masking off the
        // radial edges.
        clip.arcTo(new RectF(bounds.left - rad,
                bounds.top - rad,
                bounds.right + rad,
                bounds.bottom + rad),
                startAngle, sweep);
        clip.lineTo(cx, cy);
        clip.close();
        canvas.clipPath(clip);

        canvas.save();

        Path innerClip = new Path();
        // reduce clip enough to provide room for stroked borders; same reason as above.
        innerClip.addCircle(cx, cy, donutPix, Path.Direction.CW);
        // TODO: figure out which op mode to use to merge
        canvas.clipPath(innerClip, Region.Op.DIFFERENCE);

        Path p = new Path();

        p.arcTo(bounds, startAngle, sweep);
        p.lineTo(cx, cy);
        p.close();

        // fill segment:
        canvas.drawPath(p, f.getFillPaint());

        // no more use for inner clipping so discard it:
        canvas.restore();

        // draw radial lines
        canvas.drawLine(cx, cy, r1.x, r1.y, f.getRadialEdgePaint());
        canvas.drawLine(cx, cy, r2.x, r2.y, f.getRadialEdgePaint());

        // draw inner line:
        canvas.drawCircle(cx, cy, donutPix, f.getInnerEdgePaint());

        // draw outer line:
        canvas.drawCircle(cx, cy, rad, f.getOuterEdgePaint());

        //canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, f.getFillPaint());
        canvas.restore();
        //canvas.drawCircle(bounds.centerX(), bounds.centerY(), donutPix, f.getInnerEdgePaint());
    }

    @Override
    protected void doDrawLegendIcon(Canvas canvas, RectF rect, SegmentFormatter formatter) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Determines how many counts there are per cent of whatever the
     * pie chart is displaying as a fraction, 1 being 100%.
     */
    private double calculateScale() {
        double total = 0;
        for (Segment seg : getPlot().getSeriesSet()) {
            total += seg.getValue().doubleValue();
        }

        return (1d / total);
    }

    private PointF calculateLineEnd(PointF origin, float rad, float deg) {

        double radians = deg * Math.PI / 180F;
        double x = rad * Math.cos(radians);
        double y = rad * Math.sin(radians);

        // convert to screen space:
        return new PointF(origin.x + (float) x, origin.y + (float) y);
    }
}
