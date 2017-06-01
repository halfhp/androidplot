/*
 * Copyright 2015 AndroidPlot.com
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

package com.androidplot.util;

import com.androidplot.Region;
import com.androidplot.xy.FastXYSeries;
import com.androidplot.xy.OrderedXYSeries;
import com.androidplot.xy.RectRegion;
import com.androidplot.xy.XYConstraints;
import com.androidplot.xy.XYSeries;

import java.util.List;

/**
 * Utilities for dealing with Series data.
 */
public class SeriesUtils {

    public static RectRegion minMax(List<XYSeries> seriesList) {
        return minMax(null, seriesList);
    }

    public static RectRegion minMax(XYSeries... seriesList) {
        return minMax(null, seriesList);
    }

    public static Region minMaxX(XYSeries... seriesList) {
        final Region bounds = new Region();
        for (XYSeries series : seriesList) {
            for (int i = 0; i < series.size(); i++) {
                bounds.union(series.getX(i));
            }
        }
        return bounds;
    }

    public static Region minMaxY(XYSeries... seriesList) {
        final Region bounds = new Region();
        for (XYSeries series : seriesList) {
            for (int i = 0; i < series.size(); i++) {
                bounds.union(series.getY(i));
            }
        }
        return bounds;
    }

    /**
     * @param constraints may be null.
     * @param seriesList
     * @return
     * @since 0.9.7
     */
    public static RectRegion minMax(XYConstraints constraints, List<XYSeries> seriesList) {
        return minMax(constraints, seriesList.toArray(new XYSeries[seriesList.size()]));
    }

    /**
     * @param constraints May be null.
     * @param seriesArray
     * @return
     * @since 0.9.7
     */
    public static RectRegion minMax(XYConstraints constraints, XYSeries... seriesArray) {

        final RectRegion bounds = new RectRegion();

        // make sure there is series data to iterate over:
        if (seriesArray != null && seriesArray.length > 0) {

            // iterate over each series
            for (XYSeries series : seriesArray) {

                // if this is an advanced xy series then minMax have already been calculated for us:
                boolean isPreCalculated = false;
                if (series instanceof FastXYSeries) {
                    final RectRegion b = ((FastXYSeries) series).minMax();
                    if(b == null) {
                        continue;
                    }
                    if(constraints == null || constraints.contains(b)) {
                        bounds.union(b);
                    }
                }
                if (!isPreCalculated && series.size() > 0) {
                    for (int i = 0; i < series.size(); i++) {
                        final Number xi = series.getX(i);
                        final Number yi = series.getY(i);

                        // if constraints have been set, make sure this xy coordinate exists within them:
                        if (constraints == null || constraints.contains(xi, yi)) {
                            bounds.union(xi, yi);
                        }
                    }
                }
            }
        }
        return bounds;
    }

    /**
     *
     * @param bounds Starting minMax values to work from; only lists values that are greater than or less
     * than those in bounds will be be used.
     * @param lists lists to be evaluated for min/max values.
     * @return the original bounds instance passed in
     */
    public static Region minMax(Region bounds, List<Number>... lists) {
        for (final List<Number> list : lists) {
            for (final Number i : list) {
                bounds.union(i);
            }
        }
        return bounds;
    }

    /**
     * Compute the range of visible i-vals in the specified series.  Assumes that x-vals are
     * in strict ascending order; behavior is undefined otherwise.
     * @param series
     * @param visibleBounds The visible constraints of the plot
     * @return
     */
    public static Region iBounds(XYSeries series, RectRegion visibleBounds) {
        final float step = series.size() >= 200 ? 50 : 1;
        final int iBoundsMin = iBoundsMin(series, visibleBounds.getMinX().doubleValue(), step);
        final int iBoundsMax = iBoundsMax(series, visibleBounds.getMaxX().doubleValue(), step);
        return new Region(iBoundsMin, iBoundsMax);
    }

    /**
     * TODO: This is a poor alternative to a true binary search implementation.  Unfortunately writing
     * TODO a binary search algorithm that also supports nulls is not trivial and would not likely
     * TODO result in any noticeable performance increase here.  It's a task for another day!
     * @param series
     * @param visibleMax
     * @param step
     * @return The index of the smallest non-null value that is greater than visibleMax, or the index
     * of the last element if no such value exists.
     */
    protected static int iBoundsMax(XYSeries series, double visibleMax, float step) {
        int max = series.size() - 1;
        final int seriesSize = series.size();
        final int steps = (int) Math.ceil(seriesSize / step);
        for (int stepIndex = steps; stepIndex >= 0; stepIndex--) {
            final int i = stepIndex * (int) step;
            for (int ii = 0; ii < step; ii++) {
                final int iii = i + ii;
                if(iii < seriesSize) {
                    final Number thisX = series.getX(iii);
                    if (thisX != null) {
                        final double thisDouble = thisX.doubleValue();
                        if (thisDouble > visibleMax) {
                            // this is the smallest non-null value in this block, so skip
                            // to the next block:
                            max = iii;
                            break;
                        } else if (thisDouble == visibleMax) {
                            return iii;
                        } else {
                            return max;
                        }
                    }
                }
            }
        }
        return max;
    }

    /**
     * TODO: This is a poor alternative to a true binary search implementation.  Unfortunately writing
     * TODO a binary search algorithm that also supports nulls is not trivial and would not likely
     * TODO result in any noticeable performance increase here.  It's a task for another day!
     * @param series
     * @param visibleMin
     * @param step
     * @return The index of the largest non-null value that is less than visible, or 0
     * (the first element index) if no such value exists.
     */
    protected static int iBoundsMin(XYSeries series, double visibleMin, float step) {
        int min = 0;
        final int steps = (int) Math.ceil(series.size() / step);
        for (int stepIndex = 1; stepIndex <= steps; stepIndex++) {
            final int i = stepIndex * (int) step;
            for (int ii = 1; ii <= step; ii++) {
                final int iii = i - ii;
                if(iii < 0) {
                    break;
                }
                if(iii < series.size()) {
                    final Number thisX = series.getX(iii);
                    if (thisX != null) {
                        if (thisX.doubleValue() < visibleMin) {
                            // this is the largest non-null value in this block, so skip
                            // to the next block:
                            min = iii;
                            break;
                        } else if (thisX.doubleValue() == visibleMin) {
                            return iii;
                        } else {
                            return min;
                        }
                    }
                }
            }
        }
        return min;
    }

    /**
     * Determine the minMax iVals of the xVals surrounding a range of one or more null values.
     * @param series
     * @param index index of the null value in question
     * @return The iVals of the non-null values surrounding the null range. If the null range is unbounded on
     * either side then either or both min and max values will also be null.
     */
    protected static Region getNullRegion(XYSeries series, int index) {
        Region region = new Region();
        if(series.getX(index) != null) {
            throw new IllegalArgumentException("Attempt to find null region for non null index: " + index);
        }
        for(int i = index - 1; i >= 0; i--) {
            Number val = series.getX(i);
            if(val != null) {
                region.setMin(i);
                break;
            }
        }

        for(int i = index + 1; i < series.size(); i++) {
            Number val = series.getX(i);
            if(val != null) {
                region.setMax(i);
                break;
            }
        }
        return region;
    }

    /**
     * @param lists
     * @return
     * @since 0.9.7
     */
    public static Region minMax(List<Number>... lists) {
        return minMax(new Region(), lists);
    }

    /**
     * Determine the XVal order of an XYSeries.  If series does not implement {@link OrderedXYSeries}
     * then {@link com.androidplot.xy.OrderedXYSeries.XOrder#NONE} is assumed.
     * @param series
     * @return The {@link com.androidplot.xy.OrderedXYSeries.XOrder} of the series.
     */
    public static OrderedXYSeries.XOrder getXYOrder(XYSeries series) {
        return series instanceof OrderedXYSeries ?
                ((OrderedXYSeries) series).getXOrder() : OrderedXYSeries.XOrder.NONE;
    }
}
