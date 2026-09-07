// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class CandlestickSeriesTest {

    @Test
    public void testConstructors() throws Exception {
        CandlestickSeries candlestickSeries = new CandlestickSeries(
                new CandlestickSeries.Item(1, 10, 2, 9),
                new CandlestickSeries.Item(4, 18, 6, 5),
                new CandlestickSeries.Item(3, 11, 5, 10),
                new CandlestickSeries.Item(2, 17, 2, 15),
                new CandlestickSeries.Item(6, 11, 11, 7),
                new CandlestickSeries.Item(8, 16, 10, 15));

        XYSeries lowSeries = candlestickSeries.getLowSeries();
        assertEquals(1d, lowSeries.getY(0));
        assertEquals(4d, lowSeries.getY(1));
        assertEquals(3d, lowSeries.getY(2));

        XYSeries highSeries = candlestickSeries.getHighSeries();
        assertEquals(10d, highSeries.getY(0));
        assertEquals(18d, highSeries.getY(1));
        assertEquals(11d, highSeries.getY(2));

        XYSeries openSeries = candlestickSeries.getOpenSeries();
        assertEquals(2d, openSeries.getY(0));
        assertEquals(6d, openSeries.getY(1));
        assertEquals(5d, openSeries.getY(2));

        XYSeries closeSeries = candlestickSeries.getCloseSeries();
        assertEquals(9d, closeSeries.getY(0));
        assertEquals(5d, closeSeries.getY(1));
        assertEquals(10d, closeSeries.getY(2));

        CandlestickMaker.check(candlestickSeries);

    }
}
