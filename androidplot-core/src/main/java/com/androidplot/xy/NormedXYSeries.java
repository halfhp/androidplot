// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import android.graphics.Canvas;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.Region;
import com.androidplot.util.SeriesUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wrapper implementation of {@link XYSeries} that wraps another XYSeries, normalizing values in the range of 0 to 1.
 * Note that it's possible to push normed values outside of the standard 0, 1 range by applying
 * a sufficiently large offset.
 *
 * Auto-calculated min/max bounds are refreshed before each draw of the plot this series is
 * attached to, so the wrapped series may continue to change after being wrapped.  Call
 * {@link #normalize()} to refresh them manually.
 */
public class NormedXYSeries implements XYSeries, PlotListener {

    private XYSeries rawData;

    private Norm normX;
    private Norm normY;

    private Region minMaxX;
    private Region minMaxY;

    private Region transformX;
    private Region transformY;

    public static class Norm {

        final Region minMax;
        final double offset;
        final boolean useOffsetCompression;

        public Norm(@Nullable Region minMax) {
            this(minMax, 0, false);
        }

        /**
         *
         * @param minMax Boundary to use when calculating the norm coefficient.  Set to null to let
         * Androidplot auto calculate the bounds. (Very inefficient)
         * @param offset An extra offset to apply, generally within the range of -1 and 1.
         * This value is useful for adjusting the positioning of a series relative to another normalized series.
         * @param useOffsetCompression If true, the offset value will result in further scaling down
         * of the series data in order to ensure that all points within the specified bounds remain
         * visible on the screen. If set to true, the specified offset MUST be > -1 and < 1.  Will be
         * ignored if bounds != null.
         */
        public Norm(@Nullable Region minMax, double offset, boolean useOffsetCompression) {
            this.minMax = minMax;
            this.offset = offset;
            this.useOffsetCompression = useOffsetCompression;

            if (useOffsetCompression && (offset <= -1 || offset >= 1)) {
                throw new IllegalArgumentException(
                        "When useOffsetCompression is true, offset must be > -1 and < 1.");
            }
        }
    }

    /**
     * Normalizes yVals only, auto calculating min/max.
     * @param rawData
     */
    public NormedXYSeries(@NonNull XYSeries rawData) {
        this(rawData, null, new Norm(null, 0, false));
    }

    /**
     *
     * @param rawData The XYSeries to be normalized.
     * @param x Normalization to apply to xVals.  Set to null to disable normalization on the x axis.
     * @param y Normalization to apply to yVals.  Set to null to disable normalization on the y axis.
     */
    public NormedXYSeries(@NonNull XYSeries rawData, @Nullable Norm x, @Nullable Norm y) {
        this.rawData = rawData;
        this.normX = x;
        this.normY = y;
        normalize();
    }

    /**
     * Recalculates the normalization bounds from the current contents of the wrapped series.
     * Only auto-calculated bounds (a {@link Norm} created with a null minMax) are affected.
     * Invoked automatically before each draw when attached to a {@link Plot}.
     */
    public void normalize() {
        normalize(normX, normY);
    }

    protected void normalize(@Nullable Norm x, @Nullable Norm y) {
        if( x != null) {
            this.minMaxX = x.minMax != null ? x.minMax : SeriesUtils.minMaxX(rawData);
            this.transformX = calculateTransform(x);
        }

        if( y != null) {
            this.minMaxY = y.minMax != null ? y.minMax : SeriesUtils.minMaxY(rawData);
            this.transformY = calculateTransform(y);
        }
    }

    @Override
    public void onBeforeDraw(@NonNull Plot source, @NonNull Canvas canvas) {
        if (rawData instanceof PlotListener) {
            ((PlotListener) rawData).onBeforeDraw(source, canvas);
        }
        normalize();
    }

    @Override
    public void onAfterDraw(@NonNull Plot source, @NonNull Canvas canvas) {
        if (rawData instanceof PlotListener) {
            ((PlotListener) rawData).onAfterDraw(source, canvas);
        }
    }

    @NonNull
    protected Region calculateTransform(@NonNull Norm norm) {
            if(norm.useOffsetCompression) {
                return new Region(
                        norm.offset > 0 ? norm.offset : 0,
                        norm.offset < 0 ? 1 + norm.offset : 1);
            } else {
                return new Region(0 + norm.offset, 1 + norm.offset);
            }
    }

    @Override
    @Nullable
    public String getTitle() {
        return rawData.getTitle();
    }

    @Override
    public int size() {
        return rawData.size();
    }

    @Nullable
    public Number denormalizeXVal(@Nullable Number xVal) {
        if(xVal != null) {
            return transformX.transform(xVal.doubleValue(), minMaxX);
        }
        return null;
    }

    @Nullable
    public Number denormalizeYVal(@Nullable Number yVal) {
        if(yVal != null) {
            return transformY.transform(yVal.doubleValue(), minMaxY);
        }
        return null;
    }

    @Override
    @Nullable
    public Number getX(int index) {
        final Number xVal = rawData.getX(index);
        if(xVal != null && transformX != null) {
            return minMaxX.transform(xVal.doubleValue(), transformX);
        }
        return xVal;
    }

    @Override
    @Nullable
    public Number getY(int index) {
        final Number yVal = rawData.getY(index);
        if(yVal != null && transformY != null) {
            return minMaxY.transform(yVal.doubleValue(), transformY);
        }
        return yVal;
    }
}
