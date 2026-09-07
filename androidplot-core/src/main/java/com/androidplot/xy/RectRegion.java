// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;

import com.androidplot.Region;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * RectRegion is just a rectangle with additional methods for determining
 * intersections with other RectRegion instances.  RectRegion operates on
 * "native units" which are simply whatever unit type is passed into it by the user.
 */
public class RectRegion {

    Region xRegion;
    Region yRegion;
    private String label;

    public RectRegion() {
        xRegion = new Region();
        yRegion = new Region();
    }

    @NonNull
    public static RectRegion withDefaults(@NonNull RectRegion defaults) {
        if(defaults == null || !defaults.isFullyDefined()) {
            throw new IllegalArgumentException("When specifying defaults, RectRegion param must contain no null values.");
        }

        RectRegion r = new RectRegion();
        r.xRegion = Region.withDefaults(defaults.getxRegion());
        r.yRegion = Region.withDefaults(defaults.getyRegion());
        return r;
    }

    public RectRegion(@NonNull XYCoords min, @NonNull XYCoords max) {
        this(min.x, max.x, min.y, max.y);
    }

    /**
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public RectRegion(@Nullable Number minX, @Nullable Number maxX, @Nullable Number minY, @Nullable Number maxY, @Nullable String label) {
        xRegion = new Region(minX, maxX);
        yRegion = new Region(minY, maxY);
        this.setLabel(label);
    }

    public RectRegion(@NonNull RectF rect) {
        this(rect.left < rect.right ? rect.left : rect.right,
                rect.right > rect.left ? rect.right : rect.left,
                rect.bottom < rect.top ? rect.bottom : rect.top,
                rect.top > rect.bottom ? rect.top : rect.bottom);
    }

    @SuppressWarnings("SameParameterValue")
    public RectRegion(@Nullable Number minX, @Nullable Number maxX, @Nullable Number minY, @Nullable Number maxY) {
        this(minX, maxX, minY, maxY, null);
    }

    @NonNull
    public XYCoords transform(@NonNull Number x, @NonNull Number y, @NonNull RectRegion region2, boolean flipX, boolean flipY) {
        Number xx = xRegion.transform(x.doubleValue(), region2.xRegion, flipX);
        Number yy = yRegion.transform(y.doubleValue(), region2.yRegion, flipY);
        return new XYCoords(xx, yy);
    }

    @NonNull
    public XYCoords transform(@NonNull Number x, @NonNull Number y, @NonNull RectRegion region2) {
        return transform(x, y, region2, false, false);
    }

    @NonNull
    public XYCoords transform(@NonNull XYCoords value, @NonNull RectRegion region2) {
        return transform(value.x, value.y, region2);
    }

    /**
     * Transform a region (r) from the current region space (this) into the specified one (r2)
     * @param r The region to which the transformation applies
     * @param r2 The region into which r is being transformed
     * @return
     */
    @NonNull
    public RectRegion transform(@NonNull RectRegion r, @NonNull RectRegion r2, boolean flipX, boolean flipY) {
        return new RectRegion(
                transform(r.getMinX(), r.getMinY(), r2, flipX, flipY),
                transform(r.getMaxX(), r.getMaxY(), r2, flipX, flipY)
        );
    }

    /**
     * Convenience method to transform into screen coordinate space.  Equivalent to invoking
     * {@link #transform(Number, Number, RectF, boolean, boolean)} with
     * flipX = false and flipY = true.
     * @param x
     * @param y
     * @param region2
     * @return
     */
    @NonNull
    public PointF transformScreen(@NonNull Number x, @NonNull Number y, @NonNull RectF region2) {
        return transform(x, y, region2, false, true);
    }

    public void transformScreen(@NonNull PointF result, @NonNull Number x, @NonNull Number y, @NonNull RectF region2) {
        transform(result, x, y, region2, false, true);
    }

    public void transform(@NonNull PointF result, @NonNull Number x, @NonNull Number y, @NonNull RectF region2, boolean flipX, boolean flipY) {
        result.x = (float) xRegion.transform(x.doubleValue(), region2.left, region2.right, flipX);
        result.y = (float) yRegion.transform(y.doubleValue(), region2.top, region2.bottom, flipY);
    }

    @NonNull
    public PointF transform(@NonNull Number x, @NonNull Number y, @NonNull RectF region2, boolean flipX, boolean flipY) {
        PointF result = new PointF();
        transform(result, x, y, region2, flipX, flipY);
        return result;
    }

    @NonNull
    public PointF transformScreen(@NonNull XYCoords value, @NonNull RectF region2) {
        return transform(value, region2, false, true);
    }

    /**
     * Convenience method to transform into screen coordinate space.  Equivalent to invoking
     * {@link #transform(XYCoords, RectF, boolean, boolean)} with flipX = false and flipY = true.
     * @param value
     * @param region2
     * @param flipX
     * @param flipY
     * @return
     */
    @NonNull
    public PointF transform(@NonNull XYCoords value, @NonNull RectF region2, boolean flipX, boolean flipY) {
        return transform(value.x, value.y, region2, flipX, flipY);
    }

    public void union(@Nullable Number x, @Nullable Number y) {
        xRegion.union(x);
        yRegion.union(y);
    }

    /**
     * Compares the input bounds xy min/max vals against this instance's current xy min/max vals.
     * If the input.min is less than this.min then this.min will be set to input.min.
     * If the input.max is greater than this.max then this.max will be set to input.max
     *
     * The result will always have equal or greater area than the inputs.
     * @param input
     */
    public void union(@NonNull RectRegion input) {
        xRegion.union(input.xRegion);
        yRegion.union(input.yRegion);
    }

    public boolean intersects(@NonNull RectRegion region) {
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
    public boolean intersects(@Nullable Number minX, @Nullable Number maxX, @Nullable Number minY, @Nullable Number maxY) {
        return xRegion.intersects(minX, maxX) && yRegion.intersects(minY, maxY);
    }

    @NonNull
    public RectF asRectF() {
        return new RectF(getMinX().floatValue(), getMinY().floatValue(),
                getMaxX().floatValue(), getMaxY().floatValue());
    }

    /**
     * The result of an intersect is always a RectRegion with an equal or smaller area.
     * @param clippingBounds
     */
    public void intersect(@NonNull RectRegion clippingBounds) {
        if(intersects(clippingBounds)) {
            xRegion.intersect(clippingBounds.xRegion);
            yRegion.intersect(clippingBounds.yRegion);
        } else {
            setMinY(null);
            setMaxY(null);
            setMinX(null);
            setMaxX(null);
        }
    }

    /**
     * Returns a list of XYRegions that either completely or partially intersect the area
     * defined by params. A null value for any parameter represents infinity / no boundary.
     * @param regions The list of regions to search through
     * @return
     */
    @NonNull
    public List<RectRegion> intersects(@NonNull List<RectRegion> regions) {
        ArrayList<RectRegion> intersectingRegions = new ArrayList<>();
        for (RectRegion r : regions) {
            if (r.intersects(getMinX(), getMaxX(), getMinY(), getMaxY())) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }

    /**
     * @return Width of this region, in native units
     */
    @NonNull
    public Number getWidth() {
        return distanceBetween(getMinX(), getMaxX());
    }

    /**
     * @return Height of this region, in native units
     */
    @NonNull
    public Number getHeight() {
        return distanceBetween(getMinY(), getMaxY());
    }

    /**
     * Calculate the distance between two points in a single dimension.
     * @param x
     * @param y
     * @return
     */
    private static Number distanceBetween(Number x, Number y) {
        return Math.abs(x.doubleValue() - y.doubleValue());
    }

    public void set(@Nullable Number minX, @Nullable Number maxX, @Nullable Number minY, @Nullable Number maxY) {
        setMinX(minX);
        setMaxX(maxX);
        setMinY(minY);
        setMaxY(maxY);
    }

    public boolean isMinXSet() {
        return  xRegion.isMinSet();
    }

    @Nullable
    public Number getMinX() {
        return xRegion.getMin();
    }

    public void setMinX(@Nullable Number minX) {
        xRegion.setMin(minX);
    }

    public boolean isMaxXSet() {
        return  xRegion.isMaxSet();
    }

    @Nullable
    public Number getMaxX() {
        return xRegion.getMax();
    }

    public void setMaxX(@Nullable Number maxX) {
        xRegion.setMax(maxX);
    }

    public boolean isMinYSet() {
        return  yRegion.isMinSet();
    }

    @Nullable
    public Number getMinY() {
        return yRegion.getMin();
    }

    public void setMinY(@Nullable Number minY) {
        yRegion.setMin(minY);
    }

    public boolean isMaxYSet() {
        return  yRegion.isMaxSet();
    }

    @Nullable
    public Number getMaxY() {
        return yRegion.getMax();
    }

    public void setMaxY(@Nullable Number maxY) {
        yRegion.setMax(maxY);
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    @NonNull
    public Region getxRegion() {
        return xRegion;
    }

    public void setxRegion(@NonNull Region xRegion) {
        this.xRegion = xRegion;
    }

    @NonNull
    public Region getyRegion() {
        return yRegion;
    }

    public void setyRegion(@NonNull Region yRegion) {
        this.yRegion = yRegion;
    }

    /**
     *
     * @return True if both xRegion and yRegion are defined, false otherwise
     */
    public boolean isFullyDefined() {
        return xRegion.isDefined() && yRegion.isDefined();
    }

    /**
     * True if this region contains the specified coordinates.
     * @param x
     * @param y
     * @return
     */
    public boolean contains(@NonNull Number x, @NonNull Number y) {
        return getxRegion().contains(x) && getyRegion().contains(y);
    }

    @Override
    public String toString() {
        return "RectRegion{" +
                "xRegion=" + xRegion +
                ", yRegion=" + yRegion +
                ", label='" + label + '\'' +
                '}';
    }
}
