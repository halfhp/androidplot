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

import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.LineRegion;
import com.androidplot.util.ValPixConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * RectRegion is just a rectangle with additional methods for determining
 * intersections with other RectRegion instances.
 */
public class RectRegion {

    LineRegion xLineRegion;
    LineRegion yLineRegion;
    private String label;

    /**
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public RectRegion(Number minX, Number maxX, Number minY, Number maxY, String label) {
        xLineRegion = new LineRegion(minX, maxX);
        yLineRegion = new LineRegion(minY, maxY);
        this.setLabel(label);
    }

    @SuppressWarnings("SameParameterValue")
    public RectRegion(Number minX, Number maxX, Number minY, Number maxY) {
        this(minX, maxX, minY, maxY, null);
    }

    public boolean containsPoint(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsValue(Number x, Number y) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsDomainValue(Number value) {
        //return RectRegion.isBetween(value, minX, maxX);
        return xLineRegion.contains(value);
    }

    public boolean containsRangeValue(Number value) {
        //return RectRegion.isBetween(value, minY, maxY);
        return yLineRegion.contains(value);
    }

    public boolean intersects(RectRegion region) {
        return intersects(region.getMinX(), region.getMaxX(), region.getMinY(), region.getMaxY());
    }


    /**
     * Tests whether this region intersects the region defined by params.  Use
     * null to represent infinity.  Negative and positive infinity is implied by
     * the boundary edge, ie. a maxX of null equals positive infinity while a
     * minX of null equals negative infinity.
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @return
     */
    public boolean intersects(Number minX, Number maxX, Number minY, Number maxY) {
        return xLineRegion.intersects(minX, maxX) && yLineRegion.intersects(minY, maxY);
    }

    public boolean intersects(RectF region, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {

        RectF thisRegion = getRectF(region, visMinX.doubleValue(), visMaxX.doubleValue(),
                visMinY.doubleValue(), visMaxY.doubleValue());
        return RectF.intersects(thisRegion, region);
    }

    public RectF getRectF(RectF plotRect, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {
        PointF topLeftPoint = ValPixConverter.valToPix(
                xLineRegion.getMinVal().doubleValue() != Double.NEGATIVE_INFINITY ? xLineRegion.getMinVal() : visMinX,
                yLineRegion.getMaxVal().doubleValue() != Double.POSITIVE_INFINITY ? yLineRegion.getMaxVal() : visMaxY,
                plotRect,
                visMinX,
                visMaxX,
                visMinY,
                visMaxY);
        PointF bottomRightPoint = ValPixConverter.valToPix(
                xLineRegion.getMaxVal().doubleValue() != Double.POSITIVE_INFINITY ? xLineRegion.getMaxVal() : visMaxX,
                yLineRegion.getMinVal().doubleValue() != Double.NEGATIVE_INFINITY ? yLineRegion.getMinVal() : visMinY,
                plotRect,
                visMinX,
                visMaxX,
                visMinY,
                visMaxY);
        // TODO: figure out why the y-values are inverted
        return new RectF(topLeftPoint.x, topLeftPoint.y, bottomRightPoint.x, bottomRightPoint.y);
    }

    /**
     * Returns a list of XYRegions that either completely or partially intersect the area
     * defined by params. A null value for any parameter represents infinity / no boundary.
     * @param regions The list of regions to search through
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @return
     */
    public static List<RectRegion> regionsWithin(List<RectRegion> regions, Number minX, Number maxX, Number minY, Number maxY) {
        ArrayList<RectRegion> intersectingRegions = new ArrayList<RectRegion>();
        for(RectRegion r : regions) {
            if(r.intersects(minX, maxX, minY, maxY)) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }


    public Number getMinX() {
        return xLineRegion.getMinVal();
    }

    public void setMinX(double minX) {
        xLineRegion.setMinVal(minX);
    }

    public Number getMaxX() {
        return xLineRegion.getMaxVal();
    }

    public void setMaxX(Number maxX) {
        xLineRegion.setMaxVal(maxX);
    }

    public Number getMinY() {
        return yLineRegion.getMinVal();
    }

    public void setMinY(Number minY) {
        yLineRegion.setMinVal(minY);
    }

    public Number getMaxY() {
        return yLineRegion.getMaxVal();
    }

    public void setMaxY(Number maxY) {
        yLineRegion.setMaxVal(maxY);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
