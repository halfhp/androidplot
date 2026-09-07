// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

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

    @Test
    public void constructor_mismatchedXValsAndItems_throws() {
        List<Number> xVals = Arrays.<Number>asList(1, 2);
        List<CandlestickSeries.Item> items = Arrays.asList(new CandlestickSeries.Item(1, 2, 1, 2));
        try {
            new CandlestickSeries(xVals, items);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void constructor_withXVals_usesThemForEverySeries() {
        List<Number> xVals = Arrays.<Number>asList(5, 7);
        CandlestickSeries series = new CandlestickSeries(xVals, Arrays.asList(
                new CandlestickSeries.Item(1, 4, 2, 3),
                new CandlestickSeries.Item(2, 5, 3, 4)));

        assertEquals(2, series.getHighSeries().size());
        for (SimpleXYSeries s : new SimpleXYSeries[] {series.getHighSeries(),
                series.getLowSeries(), series.getOpenSeries(), series.getCloseSeries()}) {
            assertEquals(5, s.getX(0));
            assertEquals(7, s.getX(1));
        }
    }

    @Test
    public void seriesSetters_roundTrip() {
        CandlestickSeries series = new CandlestickSeries(new CandlestickSeries.Item(1, 4, 2, 3));

        SimpleXYSeries high = new SimpleXYSeries("high");
        SimpleXYSeries low = new SimpleXYSeries("low");
        SimpleXYSeries open = new SimpleXYSeries("open");
        SimpleXYSeries close = new SimpleXYSeries("close");
        series.setHighSeries(high);
        series.setLowSeries(low);
        series.setOpenSeries(open);
        series.setCloseSeries(close);

        assertSame(high, series.getHighSeries());
        assertSame(low, series.getLowSeries());
        assertSame(open, series.getOpenSeries());
        assertSame(close, series.getCloseSeries());
    }

    @Test
    public void item_settersRoundTrip() {
        CandlestickSeries.Item item = new CandlestickSeries.Item(1, 4, 2, 3);
        assertEquals(1d, item.getLow(), 0);
        assertEquals(4d, item.getHigh(), 0);
        assertEquals(2d, item.getOpen(), 0);
        assertEquals(3d, item.getClose(), 0);

        item.setLow(11);
        item.setHigh(44);
        item.setOpen(22);
        item.setClose(33);
        assertEquals(11d, item.getLow(), 0);
        assertEquals(44d, item.getHigh(), 0);
        assertEquals(22d, item.getOpen(), 0);
        assertEquals(33d, item.getClose(), 0);
    }
}
