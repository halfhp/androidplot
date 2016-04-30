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

import android.util.Pair;
import com.androidplot.Bounds;
import com.androidplot.candlestick.CandlestickSeries;
import com.androidplot.xy.XYBounds;
import com.androidplot.xy.XYConstraints;
import com.androidplot.xy.XYSeries;

import java.util.List;

/**
 * Utilities for dealing with Series data.
 */
public class SeriesUtils {

    /**
     * @param constraints may be null.
     * @param seriesList
     * @return
     * @since 0.9.7
     */
    public static XYBounds minMax(XYConstraints constraints, List<XYSeries> seriesList) {
        // TODO: this is inefficient...clean it up!
        return minMax(constraints, seriesList.toArray(new XYSeries[seriesList.size()]));
    }

    /**
     * @param constraints May be null.
     * @param seriesArray
     * @return
     * @since 0.9.7
     */
    public static XYBounds minMax(XYConstraints constraints, XYSeries... seriesArray) {

        final XYBounds bounds = new XYBounds();

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
                            if (xi != null) {
                                if (bounds.getMinX() == null ||
                                        xi.doubleValue() < bounds.getMinX().doubleValue()) {
                                    bounds.setMinX(xi);
                                }
                                if (bounds.getMaxX() == null ||
                                        xi.doubleValue() > bounds.getMaxX().doubleValue()) {
                                    bounds.setMaxX(xi);
                                }
                            }
                            if (yi != null) {
                                if (series instanceof CandlestickSeries) {
                                    // TODO
                                    throw new UnsupportedOperationException("Not yet implemented.");
                                } else {
                                    if (bounds.getMinY() == null ||
                                            yi.doubleValue() < bounds.getMinY().doubleValue()) {
                                        bounds.setMinY(yi);
                                    }
                                }
                                if (series instanceof CandlestickSeries) {
                                    // TODO
                                    throw new UnsupportedOperationException("Not yet implemented.");
                                } else {
                                    if (bounds.getMaxY() == null ||
                                            yi.doubleValue() > bounds.getMaxY().doubleValue()) {
                                        bounds.setMaxY(yi);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return bounds;
    }

    /**
     * @param lists
     * @return
     * @since 0.9.7
     */
    public static Bounds minMax(List<Number>... lists) {
        Number min = null;
        Number max = null;
        for (List<Number> list : lists) {
            for (Number i : list) {
                if (i != null) {
                    double di = i.doubleValue();
                    if (min == null || di < min.doubleValue()) {
                        min = i;
                    }
                    if (max == null || di > max.doubleValue()) {
                        max = i;
                    }
                }
            }
        }
        return new Bounds(min, max);
    }


    Number min(Number... numbers) {
        Number min = null;
        for(Number number : numbers) {
            if(min == null || number != null && number.doubleValue() < min.doubleValue()) {
                min = number;
            }
        }
        return min;
    }

    Number max(Number... numbers) {
        Number max = null;
        for(Number number : numbers) {
            if(max == null || number != null && number.doubleValue() > max.doubleValue()) {
                max = number;
            }
        }
        return max;
    }

//    Pair<Number, Number> minMaxY(CandlestickSeries series, int index) {
//        Number min = series.getY(index);
//        Number max = series.getY(index);
//
//        final Number a = series.getA(index);
//
//        if(a != null) {
//            double ad = a.doubleValue();
//            if(min == null || ad < min.doubleValue()) {
//                min = ad;
//            } else if(max == null || ad > max.doubleValue()) {
//                max = ad;
//            }
//        }
//
//        final Number z = series.getZ(index);
//
//        if(z != null) {
//            double zd = z.doubleValue();
//            if(min == null || zd < min.doubleValue()) {
//                min = zd;
//            } else if(max == null || zd > max.doubleValue()) {
//                max = zd;
//            }
//        }
//
//        return new Pair<>(min, max);
//    }
}
