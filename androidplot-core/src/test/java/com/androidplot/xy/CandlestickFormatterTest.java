// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.CandlestickFormatter.BodyStyle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CandlestickFormatterTest extends AndroidplotTest {

    private static final float DELTA = 0.0001f;

    @Test
    public void defaultConstructor_usesDefaultPaintsAndSquareBody() {
        CandlestickFormatter f = new CandlestickFormatter();

        assertEquals(BodyStyle.SQUARE, f.getBodyStyle());
        assertEquals(Paint.Style.STROKE, f.getWickPaint().getStyle());
        assertEquals(Color.YELLOW, f.getWickPaint().getColor());
        assertEquals(Paint.Style.FILL, f.getRisingBodyFillPaint().getStyle());
        assertEquals(Color.GREEN, f.getRisingBodyFillPaint().getColor());
        assertEquals(Paint.Style.FILL, f.getFallingBodyFillPaint().getStyle());
        assertEquals(Color.RED, f.getFallingBodyFillPaint().getColor());
        assertEquals(Paint.Style.STROKE, f.getRisingBodyStrokePaint().getStyle());
        assertEquals(Color.GREEN, f.getRisingBodyStrokePaint().getColor());
        assertEquals(Paint.Style.STROKE, f.getFallingBodyStrokePaint().getStyle());
        assertEquals(Color.RED, f.getFallingBodyStrokePaint().getColor());
        assertEquals(Color.YELLOW, f.getUpperCapPaint().getColor());
        assertEquals(Color.YELLOW, f.getLowerCapPaint().getColor());
        assertTrue(f.getBodyWidth() > 0);
        assertEquals(f.getBodyWidth(), f.getUpperCapWidth(), DELTA);
        assertEquals(f.getBodyWidth(), f.getLowerCapWidth(), DELTA);
    }

    @Test
    public void paintConstructor_assignsEveryPaint() {
        Paint wick = new Paint();
        Paint risingFill = new Paint();
        Paint fallingFill = new Paint();
        Paint risingStroke = new Paint();
        Paint fallingStroke = new Paint();
        Paint upperCap = new Paint();
        Paint lowerCap = new Paint();

        CandlestickFormatter f = new CandlestickFormatter(wick, risingFill, fallingFill,
                risingStroke, fallingStroke, upperCap, lowerCap, BodyStyle.TRIANGULAR);

        assertSame(wick, f.getWickPaint());
        assertSame(risingFill, f.getRisingBodyFillPaint());
        assertSame(fallingFill, f.getFallingBodyFillPaint());
        assertSame(risingStroke, f.getRisingBodyStrokePaint());
        assertSame(fallingStroke, f.getFallingBodyStrokePaint());
        assertSame(upperCap, f.getUpperCapPaint());
        assertSame(lowerCap, f.getLowerCapPaint());
        assertEquals(BodyStyle.TRIANGULAR, f.getBodyStyle());
    }

    @Test
    public void xmlConfigConstructor_appliesResourceConfig() {
        // the test xml resource is packaged but absent from the compile time R stub
        final int cfgId = getContext().getResources().getIdentifier(
                "candlestick_formatter_cfg", "xml", getContext().getPackageName());
        assertNotEquals("test xml resource not found", 0, cfgId);

        CandlestickFormatter f = new CandlestickFormatter(getContext(), cfgId);

        assertEquals(0xFF112233, f.getWickPaint().getColor());
        assertEquals(0xFF00AA00, f.getRisingBodyFillPaint().getColor());
        assertEquals(0xFFAA0000, f.getFallingBodyFillPaint().getColor());
        assertEquals(9f, f.getBodyWidth(), DELTA);
        assertEquals(7f, f.getUpperCapWidth(), DELTA);
        assertEquals(5f, f.getLowerCapWidth(), DELTA);
        assertEquals(BodyStyle.TRIANGULAR, f.getBodyStyle());
        // untouched by the config:
        assertEquals(Color.GREEN, f.getRisingBodyStrokePaint().getColor());
    }

    @Test
    public void paintSetters_roundTrip() {
        CandlestickFormatter f = new CandlestickFormatter();

        Paint p1 = new Paint();
        f.setWickPaint(p1);
        assertSame(p1, f.getWickPaint());

        Paint p2 = new Paint();
        f.setRisingBodyFillPaint(p2);
        assertSame(p2, f.getRisingBodyFillPaint());

        Paint p3 = new Paint();
        f.setFallingBodyFillPaint(p3);
        assertSame(p3, f.getFallingBodyFillPaint());

        Paint p4 = new Paint();
        f.setRisingBodyStrokePaint(p4);
        assertSame(p4, f.getRisingBodyStrokePaint());

        Paint p5 = new Paint();
        f.setFallingBodyStrokePaint(p5);
        assertSame(p5, f.getFallingBodyStrokePaint());

        Paint p6 = new Paint();
        f.setUpperCapPaint(p6);
        assertSame(p6, f.getUpperCapPaint());

        Paint p7 = new Paint();
        f.setLowerCapPaint(p7);
        assertSame(p7, f.getLowerCapPaint());
    }

    @Test
    public void widthSetters_roundTrip() {
        CandlestickFormatter f = new CandlestickFormatter();
        f.setBodyWidth(11f);
        f.setUpperCapWidth(12f);
        f.setLowerCapWidth(13f);
        assertEquals(11f, f.getBodyWidth(), DELTA);
        assertEquals(12f, f.getUpperCapWidth(), DELTA);
        assertEquals(13f, f.getLowerCapWidth(), DELTA);
    }

    @Test
    public void setBodyStyle_roundTrip() {
        CandlestickFormatter f = new CandlestickFormatter();
        f.setBodyStyle(BodyStyle.TRIANGULAR);
        assertEquals(BodyStyle.TRIANGULAR, f.getBodyStyle());
        f.setBodyStyle(BodyStyle.SQUARE);
        assertEquals(BodyStyle.SQUARE, f.getBodyStyle());
    }

    @Test
    public void setCapAndWickPaint_setsAllThree() {
        CandlestickFormatter f = new CandlestickFormatter();
        Paint paint = new Paint();
        f.setCapAndWickPaint(paint);
        assertSame(paint, f.getUpperCapPaint());
        assertSame(paint, f.getLowerCapPaint());
        assertSame(paint, f.getWickPaint());
    }

    @Test
    public void rendererClassAndInstance_areCandlestickRenderer() {
        CandlestickFormatter f = new CandlestickFormatter();
        assertEquals(CandlestickRenderer.class, f.getRendererClass());
        SeriesRenderer renderer = f.doGetRendererInstance(new XYPlot(getContext(), "plot"));
        assertTrue(renderer instanceof CandlestickRenderer);
    }
}
