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
    public XYRegion(Number minX, Number maxX, Number minY, Number maxY, String label) {
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
    public boolean intersects(Number minX, Number maxX, Number minY, Number maxY) {
        return xLine.intersects(minX, maxX) && yLine.intersects(minY, maxY);
    }

    public boolean intersects(RectF region, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {

        RectF thisRegion = getRectF(region, visMinX, visMaxX, visMinY, visMaxY);
        return RectF.intersects(thisRegion, region);
    }

    public RectF getRectF(RectF plotRect, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {
        PointF topLeftPoint = ValPixConverter.valToPix(
                xLine.getMinVal() != null ? xLine.getMinVal() : visMinX,
                //this.minX,
                yLine.getMaxVal() != null ? yLine.getMaxVal() : visMaxY,
                //this.maxY,
                plotRect,
                visMinX,
                visMaxX,
                visMinY,
                visMaxY);
        PointF bottomRightPoint = ValPixConverter.valToPix(
                xLine.getMaxVal() != null ? xLine.getMaxVal() : visMaxX,
                //this.maxX,
                yLine.getMinVal() != null ? yLine.getMinVal() : visMinY,
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
    public static List<XYRegion> regionsWithin(List<XYRegion> regions, Number minX, Number maxX, Number minY, Number maxY) {
        ArrayList<XYRegion> intersectingRegions = new ArrayList<XYRegion>();
        for(XYRegion r : regions) {
            if(r.intersects(minX, maxX, minY, maxY)) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }


    public Number getMinX() {
        return xLine.getMinVal();
    }

    public void setMinX(Number minX) {
        xLine.setMinVal(minX);
    }

    public Number getMaxX() {
        return xLine.getMaxVal();
    }

    public void setMaxX(Number maxX) {
        xLine.setMaxVal(maxX);
    }

    public Number getMinY() {
        return yLine.getMinVal();
    }

    public void setMinY(Number minY) {
        yLine.setMinVal(minY);
    }

    public Number getMaxY() {
        return yLine.getMaxVal();
    }

    public void setMaxY(Number maxY) {
        yLine.setMaxVal(maxY);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
