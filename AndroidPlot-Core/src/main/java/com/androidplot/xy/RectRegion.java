package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.LineRegion;
import com.androidplot.util.ValPixConverter;

import java.util.ArrayList;
import java.util.List;

public class RectRegion {

    LineRegion xLineRegion;
    LineRegion yLineRegion;
    private String label;

    /**
     * Null values are interpreted as infinity
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public RectRegion(double minX, double maxX, double minY, double maxY, String label) {
        xLineRegion = new LineRegion(minX, maxX);
        yLineRegion = new LineRegion(minY, maxY);
        this.setLabel(label);
    }

    public boolean containsPoint(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsValue(double x, double y) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsDomainValue(double value) {
        //return RectRegion.isBetween(value, minX, maxX);
        return xLineRegion.contains(value);
    }

    public boolean containsRangeValue(double value) {
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
    public boolean intersects(double minX, double maxX, double minY, double maxY) {
        return xLineRegion.intersects(minX, maxX) && yLineRegion.intersects(minY, maxY);
    }

    public boolean intersects(RectF region, double visMinX, double visMaxX, double visMinY, double visMaxY) {

        RectF thisRegion = getRectF(region, visMinX, visMaxX, visMinY, visMaxY);
        return RectF.intersects(thisRegion, region);
    }

    public RectF getRectF(RectF plotRect, double visMinX, double visMaxX, double visMinY, double visMaxY) {
        PointF topLeftPoint = ValPixConverter.valToPix(
                xLineRegion.getMinVal() != Double.NEGATIVE_INFINITY ? xLineRegion.getMinVal() : visMinX,
                //this.minX,
                yLineRegion.getMaxVal() != Double.NEGATIVE_INFINITY ? yLineRegion.getMaxVal() : visMaxY,
                //this.maxY,
                plotRect,
                visMinX,
                visMaxX,
                visMinY,
                visMaxY);
        PointF bottomRightPoint = ValPixConverter.valToPix(
                xLineRegion.getMaxVal() != Double.POSITIVE_INFINITY ? xLineRegion.getMaxVal() : visMaxX,
                //this.maxX,
                yLineRegion.getMinVal() != Double.POSITIVE_INFINITY ? yLineRegion.getMinVal() : visMinY,
                //this.minY,
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
    public static List<RectRegion> regionsWithin(List<RectRegion> regions, double minX, double maxX, double minY, double maxY) {
        ArrayList<RectRegion> intersectingRegions = new ArrayList<RectRegion>();
        for(RectRegion r : regions) {
            if(r.intersects(minX, maxX, minY, maxY)) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }


    public double getMinX() {
        return xLineRegion.getMinVal();
    }

    public void setMinX(double minX) {
        xLineRegion.setMinVal(minX);
    }

    public double getMaxX() {
        return xLineRegion.getMaxVal();
    }

    public void setMaxX(double maxX) {
        xLineRegion.setMaxVal(maxX);
    }

    public double getMinY() {
        return yLineRegion.getMinVal();
    }

    public void setMinY(double minY) {
        yLineRegion.setMinVal(minY);
    }

    public double getMaxY() {
        return yLineRegion.getMaxVal();
    }

    public void setMaxY(double maxY) {
        yLineRegion.setMaxVal(maxY);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
