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

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

/**
 * Helper utility to simplify the creation of of candlestick charts
 */
public abstract class CandlestickMaker {

    /**
     * Adds a candlestick chart to the specified plot using the specified
     * high, low, open and close values.
     * @param plot
     * @param formatter
     * @param openVals
     * @param closeVals
     * @param highVals
     * @param lowVals
     */
    public static void make(XYPlot plot, CandlestickFormatter formatter,
                            XYSeries openVals, XYSeries closeVals, XYSeries highVals, XYSeries lowVals) {
        plot.addSeries(formatter, highVals, lowVals, openVals, closeVals);
    }
}
