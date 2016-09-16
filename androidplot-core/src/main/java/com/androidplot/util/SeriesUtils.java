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

import com.androidplot.*;
import com.androidplot.xy.*;

import java.util.*;

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

    /**
     * @param constraints may be null.
     * @param seriesList
     * @return
     * @since 0.9.7
     */
    public static RectRegion minMax(XYConstraints constraints, List<XYSeries> seriesList) {
        // TODO: this is inefficient...clean it up!
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
                if (series.size() > 0) {
                    for (int i = 0; i < series.size(); i++) {
                        final Number xi = series.getX(i);
                        final Number yi = series.getY(i);

                        // if constraints have been set, make sure this xy coordinate exists within them:
                        if (constraints == null || constraints.contains(xi, yi)) {
                            minMax(bounds.getxRegion(), xi);
                            minMax(bounds.getyRegion(), yi);
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
                minMax(bounds, i);
            }
        }
        return bounds;
    }

    /**
     * Compares a number against the current min/max values in a region, updating the region
     * with the new value if appropriate.
     * @param bounds
     * @param number
     * @return
     */
    public static Region minMax(Region bounds, Number number) {
        if (number != null) {
            final double di = number.doubleValue();
            if (bounds.getMin() == null || di < bounds.getMin().doubleValue()) {
                bounds.setMin(number);
            }
            if (bounds.getMax() == null || di > bounds.getMax().doubleValue()) {
                bounds.setMax(number);
            }
        }
        return bounds;
    }

    /**
     * @param lists
     * @return
     * @since 0.9.7
     */
    public static Region minMax(List<Number>... lists) {
        return minMax(new Region(), lists);
    }

    public static void main(String[] args) {

        // seed the list:
        ArrayList<Number> values = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            values.add(Math.random() * 100);
        }
        final int numIterations = 20;
        long sumTime = 0;
        for(int j = 0; j < numIterations; j++) {
            final long startTime = System.currentTimeMillis();
            Region bounds = minMax(values);
            final long thisIteration = System.currentTimeMillis() - startTime;
            System.out.println("thisIteration took: " + thisIteration + "ms");
            sumTime += thisIteration;
        }

        System.out.println("Benchmark avg:" + (sumTime / numIterations) + "ms.");
    }
}
