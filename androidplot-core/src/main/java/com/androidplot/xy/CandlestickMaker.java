// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import androidx.annotation.NonNull;

/**
 * Helper utility to simplify the creation of of candlestick charts
 * @since 0.9.7
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
    public static void make(@NonNull XYPlot plot, @NonNull CandlestickFormatter formatter,
                            @NonNull XYSeries openVals, @NonNull XYSeries closeVals, @NonNull XYSeries highVals, @NonNull XYSeries lowVals) {
        plot.addSeries(formatter, highVals, lowVals, openVals, closeVals);
    }

    /**
     * Add a candlestick chart to the specified plot using the specified {@link CandlestickSeries}.
     * @param plot
     * @param formatter
     * @param series
     * @since 0.9.8
     */
    public static void make(@NonNull XYPlot plot, @NonNull CandlestickFormatter formatter, @NonNull CandlestickSeries series) {
        make(plot, formatter, series.getOpenSeries(), series.getCloseSeries(),
                series.getHighSeries(), series.getLowSeries());
    }

    /**
     * Check the validity of series data comprising a {@link CandlestickSeries}.
     * This is a development aid; be sure to remove any usage of this method in production code.
     * @param series
     * @since 0.9.8
     */
    public static void check(@NonNull CandlestickSeries series) {
        check(series.getOpenSeries(), series.getCloseSeries(), series.getHighSeries(), series.getLowSeries());
    }

    /**
     * Check the validity of series data comprising a candlestick chart.
     * This is a development aid; be sure to remove any usage of this method in production code.
     * @param openVals
     * @param closeVals
     * @param highVals
     * @param lowVals
     * @since 0.9.8
     */
    public static void check(@NonNull XYSeries openVals, @NonNull XYSeries closeVals, @NonNull XYSeries highVals, @NonNull XYSeries lowVals) {
        final int size = openVals.size();
        assert closeVals.size() == size : "closeVals has irregular size.";
        assert highVals.size() == size : "highVals has irregular size.";
        assert lowVals.size() == size : "lowVals has irregular size.";

        for(int i = 0; i < size; i++) {

            final double highVal = highVals.getY(i).doubleValue();
            final double lowVal = lowVals.getY(i).doubleValue();
            final double openVal = openVals.getY(i).doubleValue();
            final double closeVal = closeVals.getY(i).doubleValue();

            assert openVal <= highVal : "Detected openVal > highVal at index " + i;
            assert openVal >= lowVal : "Detected openVal < lowVal at index " + i;
            assert closeVal <= highVal : "Detected closeVal > highVal at index " + i;
            assert closeVal >= lowVal : "Detected closeVal < lowVal at index " + i;
            assert lowVal <= highVal : "Detected lowVal > highVal at index " + i;
        }
    }
}
