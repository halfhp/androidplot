/*
 * Copyright 2016 AndroidPlot.com
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

package com.androidplot.candlestick;

import com.androidplot.Bounds;
import com.androidplot.util.SeriesUtils;
import com.androidplot.xy.XYBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * An immutable bare-bones implementation of {@link CandlestickSeries}.
 */
public class SimpleCandlestickSeries implements CandlestickSeries {

    private List<Number> xVals;
    private List<Number> yVals;
    private List<Number> zVals;
    private List<Number> aVals;
    private List<Number> bVals;

    //private XYBounds bounds;

    private String title;

    /**
     * @param xVals xValue of the element.  May be null; if null then i will be implicitly used.
     * @param yVals
     * @param zVals
     * @param aVals
     * @param bVals
     */
    public SimpleCandlestickSeries(List<Number> xVals, List<Number> yVals,
                                   List<Number> zVals, List<Number> aVals, List<Number> bVals, String title) {
        final int size = yVals.size();
        if (zVals.size() != size || aVals.size() != size ||
                bVals.size() != size || (xVals != null && xVals.size() != size)) {
            throw new RuntimeException("All list params must be of the same length.");
        }
        this.title = title;
        this.xVals = xVals;
        this.yVals = yVals;
        this.zVals = zVals;
        this.aVals = aVals;
        this.bVals = bVals;
        //bounds = new XYBounds();
        if (this.xVals == null) {
            this.xVals = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                this.xVals.add(i);
            }
            // we generated the xVals so we already know the min/max:
            //bounds.setMinX(0);
            //bounds.setMaxX(size - 1);
        }
//        } else {
//            bounds.setXBounds(SeriesUtils.minMax(xVals));
//        }
//
//        bounds.setYBounds(SeriesUtils.minMax(yVals, zVals));
    }

    @Override
    public Number getA(int index) {
        return aVals.get(index);
    }

    @Override
    public Number getB(int index) {
        return bVals.get(index);
    }

    @Override
    public Number getZ(int index) {
        return zVals.get(index);
    }

    @Override
    public int size() {
        return yVals.size();
    }

    @Override
    public Number getX(int index) {
        return xVals.get(index);
    }

    @Override
    public Number getY(int index) {
        return yVals.get(index);
    }

//    @Override
//    public XYBounds getBounds() {
//        return bounds;
//    }

    @Override
    public String getTitle() {
        return title;
    }
}
