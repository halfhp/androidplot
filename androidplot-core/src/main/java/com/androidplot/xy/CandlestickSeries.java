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

package com.androidplot.xy;

import com.androidplot.xy.SimpleXYSeries;

import java.util.*;

/**
 * Convenience class for representing a series of candlestick values;
 * is NOT a descendant of {@link com.androidplot.xy.XYSeries} and therefore
 * cannot be directly added to an {@link com.androidplot.xy.XYPlot}.
 *
 * This class is NOT threadsafe.
 *
 * @since 0.9.8
 */
public class CandlestickSeries {

    private SimpleXYSeries highSeries = new SimpleXYSeries(null);
    private SimpleXYSeries lowSeries = new SimpleXYSeries(null);
    private SimpleXYSeries openSeries = new SimpleXYSeries(null);
    private SimpleXYSeries closeSeries = new SimpleXYSeries(null);

    protected static List<Number> generateRange(int start, int end) {
        List<Number> range = new ArrayList<>(end - start);
        for(int i = start; i < end; i++) {
            range.add(i);
        }
        return range;
    }

    public CandlestickSeries(Item... items) {
        this(Arrays.asList(items));
    }

    /**
     * Creates a new CandlestickSeries.
     * Calls {@link #CandlestickSeries(List, List)} with a list of xVals
     * generated using the formula x=i.
     * @param items
     */
    public CandlestickSeries(List<Item> items) {
        this(generateRange(0, items.size()), items);
    }

    public CandlestickSeries(List<Number> xVals, List<Item> items) {
        if(xVals.size() != items.size()) {
            throw new IllegalArgumentException("xVals and yVals length must be identical.");
        }
        for(int i = 0; i < xVals.size(); i++) {
            Number x = xVals.get(i);
            highSeries.addLast(x, items.get(i).getHigh());
            lowSeries.addLast(x, items.get(i).getLow());
            openSeries.addLast(x, items.get(i).getOpen());
            closeSeries.addLast(x, items.get(i).getClose());
        }
    }

    public SimpleXYSeries getHighSeries() {
        return highSeries;
    }

    public void setHighSeries(SimpleXYSeries highSeries) {
        this.highSeries = highSeries;
    }

    public SimpleXYSeries getLowSeries() {
        return lowSeries;
    }

    public void setLowSeries(SimpleXYSeries lowSeries) {
        this.lowSeries = lowSeries;
    }

    public SimpleXYSeries getOpenSeries() {
        return openSeries;
    }

    public void setOpenSeries(SimpleXYSeries openSeries) {
        this.openSeries = openSeries;
    }

    public SimpleXYSeries getCloseSeries() {
        return closeSeries;
    }

    public void setCloseSeries(SimpleXYSeries closeSeries) {
        this.closeSeries = closeSeries;
    }

    public static class Item {
        private double low;
        private double high;
        private double open;
        private double close;

        /**
         * An individual candlestick value.  Since it is illegal to include
         * null values for any member of a candlestick, this class is modeled with
         * double values instead of {@link Number} instances.
         * @param low
         * @param high
         * @param open
         * @param close
         */
        public Item(double low, double high, double open, double close) {
            this.low = low;
            this.high = high;
            this.open = open;
            this.close = close;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }
    }
}
