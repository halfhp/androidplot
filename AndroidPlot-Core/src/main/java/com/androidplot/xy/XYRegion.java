package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.Line;
import com.androidplot.util.ValPixConverter;

import java.util.ArrayList;
import java.util.List;

public class XYRegion {

    Line xLine;
    Line yLine;
    private String label;

    /**
     * Null values are interpreted as infinity
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public XYRegion(double minX, double maxX, double minY, double maxY, String label) {
        xLine = new Line(minX, maxX);
        yLine = new Line(minY, maxY);
        this.setLabel(label);
    }

    public boolean containsPoint(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsValue(double x, double y) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsDomainValue(double value) {
        //return XYRegion.isBetween(value, minX, maxX);
        return xLine.contains(value);
    }

    public boolean containsRangeValue(double value) {
        //return XYRegion.isBetween(value, minY, maxY);
        return yLine.contains(value);
    }

    public boolean intersects(XYRegion region) {
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
        return xLine.intersects(minX, maxX) && yLine.intersects(minY, maxY);
    }

    public boolean intersects(RectF region, double visMinX, double visMaxX, double visMinY, double visMaxY) {

        RectF thisRegion = getRectF(region, visMinX, visMaxX, visMinY, visMaxY);
        return RectF.intersects(thisRegion, region);
    }

    public RectF getRectF(RectF plotRect, double visMinX, double visMaxX, double visMinY, double visMaxY) {
        PointF topLeftPoint = ValPixConverter.valToPix(
                xLine.getMinVal() != Double.NEGATIVE_INFINITY ? xLine.getMinVal() : visMinX,
                //this.minX,
                yLine.getMaxVal() != Double.NEGATIVE_INFINITY ? yLine.getMaxVal() : visMaxY,
                //this.maxY,
                plotRect,
                visMinX,
                visMaxX,
                visMinY,
                visMaxY);
        PointF bottomRightPoint = ValPixConverter.valToPix(
                xLine.getMaxVal() != Double.POSITIVE_INFINITY ? xLine.getMaxVal() : visMaxX,
                //this.maxX,
                yLine.getMinVal() != Double.POSITIVE_INFINITY ? yLine.getMinVal() : visMinY,
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
    public static List<XYRegion> regionsWithin(List<XYRegion> regions, double minX, double maxX, double minY, double maxY) {
        ArrayList<XYRegion> intersectingRegions = new ArrayList<XYRegion>();
        for(XYRegion r : regions) {
            if(r.intersects(minX, maxX, minY, maxY)) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }


    public double getMinX() {
        return xLine.getMinVal();
    }

    public void setMinX(double minX) {
        xLine.setMinVal(minX);
    }

    public double getMaxX() {
        return xLine.getMaxVal();
    }

    public void setMaxX(double maxX) {
        xLine.setMaxVal(maxX);
    }

    public double getMinY() {
        return yLine.getMinVal();
    }

    public void setMinY(double minY) {
        yLine.setMinVal(minY);
    }

    public double getMaxY() {
        return yLine.getMaxVal();
    }

    public void setMaxY(double maxY) {
        yLine.setMaxVal(maxY);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
