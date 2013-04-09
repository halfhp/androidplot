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

        //PointF lastRadial = calculateLineEnd(origin, radius, offset);

        RectF rec = new RectF(origin.x - radius, origin.y - radius, origin.x + radius, origin.y + radius);

        for (Segment segment : segments) {
            float lastOffset = offset;
            float sweep = (float) scale * (segment.getValue().floatValue()) * 360;
            offset += sweep;
            //PointF radial = calculateLineEnd(origin, radius, offset);
            drawSegment(canvas, rec, getPlot().getFormatter(segment, PieRenderer.class),
                    radius, lastOffset, sweep);
            //lastRadial = radial;
        }
    }

    protected void drawSegment(Canvas canvas, RectF bounds, SegmentFormatter f,
                               float rad, float startAngle, float sweep) {
        canvas.save();

        float cx = bounds.centerX();
        float cy = bounds.centerY();

        // vertices of the first radial:
        PointF r1Outer = calculateLineEnd(cx, cy, rad, startAngle);
        PointF r1Inner = calculateLineEnd(cx, cy, rad - donutPix, startAngle);

        // vertices of the second radial:
        PointF r2Outer = calculateLineEnd(cx, cy, rad, startAngle + sweep);
        PointF r2Inner = calculateLineEnd(cx, cy, rad - donutPix, startAngle + sweep);

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

        Path p = new Path();
        p.arcTo(bounds, startAngle, sweep);
        p.lineTo(r2Inner.x, r2Inner.y);

        // sweep back to original angle:
        p.arcTo(new RectF(
                cx - donutPix,
                cy - donutPix,
                cx + donutPix,
                cy + donutPix),
                startAngle + sweep, -sweep);

        p.close();

        // fill segment:
        canvas.drawPath(p, f.getFillPaint());

        // draw radial lines
        canvas.drawLine(r1Inner.x, r1Inner.y, r1Outer.x, r1Outer.y, f.getRadialEdgePaint());
        canvas.drawLine(r2Inner.x, r2Inner.y, r2Outer.x, r2Outer.y, f.getRadialEdgePaint());

        // draw inner line:
        canvas.drawCircle(cx, cy, donutPix, f.getInnerEdgePaint());

        // draw outer line:
        canvas.drawCircle(cx, cy, rad, f.getOuterEdgePaint());
        canvas.restore();
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

    private PointF calculateLineEnd(float x, float y, float rad, float deg) {
        return calculateLineEnd(new PointF(x, y), rad, deg);
    }

    private PointF calculateLineEnd(PointF origin, float rad, float deg) {

        double radians = deg * Math.PI / 180F;
        double x = rad * Math.cos(radians);
        double y = rad * Math.sin(radians);

        // convert to screen space:
        return new PointF(origin.x + (float) x, origin.y + (float) y);
    }
}
