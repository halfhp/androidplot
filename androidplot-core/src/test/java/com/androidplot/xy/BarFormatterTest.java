// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BarFormatterTest extends AndroidplotTest {

    private static final float DELTA = 0.0001f;

    @Mock
    Canvas canvas;

    @Test
    public void defaultConstructor_buildsTranslucentFillAndStrokePaints() {
        BarFormatter f = new BarFormatter();
        assertEquals(Paint.Style.FILL, f.getFillPaint().getStyle());
        assertEquals(100, f.getFillPaint().getAlpha());
        assertEquals(Paint.Style.STROKE, f.getBorderPaint().getStyle());
        assertEquals(100, f.getBorderPaint().getAlpha());
        assertEquals(0f, f.getMarginTop(), DELTA);
        assertEquals(0f, f.getMarginBottom(), DELTA);
        assertEquals(0f, f.getMarginLeft(), DELTA);
        assertEquals(0f, f.getMarginRight(), DELTA);
    }

    @Test
    public void colorConstructor_setsFillAndBorderColors() {
        BarFormatter f = new BarFormatter(Color.RED, Color.BLUE);
        assertEquals(Color.RED, f.getFillPaint().getColor());
        assertEquals(Color.BLUE, f.getBorderPaint().getColor());
    }

    @Test
    public void xmlConfigConstructor_appliesResourceConfig() {
        // the test xml resource is packaged but absent from the compile time R stub
        final int cfgId = getContext().getResources().getIdentifier(
                "bar_formatter_cfg", "xml", getContext().getPackageName());
        assertNotEquals("test xml resource not found", 0, cfgId);

        BarFormatter f = new BarFormatter(getContext(), cfgId);

        assertEquals(0xFF00AA00, f.getFillPaint().getColor());
        assertEquals(0xFF112233, f.getBorderPaint().getColor());
        assertEquals(1f, f.getMarginTop(), DELTA);
        assertEquals(2f, f.getMarginBottom(), DELTA);
        assertEquals(3f, f.getMarginLeft(), DELTA);
        assertEquals(4f, f.getMarginRight(), DELTA);
    }

    @Test
    public void marginSetters_roundTrip() {
        BarFormatter f = new BarFormatter();
        f.setMarginTop(1.5f);
        f.setMarginBottom(2.5f);
        f.setMarginLeft(3.5f);
        f.setMarginRight(4.5f);
        assertEquals(1.5f, f.getMarginTop(), DELTA);
        assertEquals(2.5f, f.getMarginBottom(), DELTA);
        assertEquals(3.5f, f.getMarginLeft(), DELTA);
        assertEquals(4.5f, f.getMarginRight(), DELTA);
    }

    @Test
    public void setBorderPaint_isTheLinePaint() {
        BarFormatter f = new BarFormatter();
        Paint border = new Paint();
        f.setBorderPaint(border);
        assertSame(border, f.getBorderPaint());
        assertSame(border, f.getLinePaint());
        assertTrue(f.hasLinePaint());

        f.setBorderPaint(null);
        assertNull(f.getBorderPaint());
        assertFalse(f.hasLinePaint());
    }

    @Test
    public void rendererClassAndInstance_areBarRenderer() {
        BarFormatter f = new BarFormatter();
        assertEquals(BarRenderer.class, f.getRendererClass());
        assertTrue(f.doGetRendererInstance(new XYPlot(getContext(), "plot")) instanceof BarRenderer);
    }

    @Test
    public void legendIcon_drawsFillThenBorder() {
        BarFormatter f = new BarFormatter(Color.RED, Color.BLUE);
        BarRenderer renderer = new BarRenderer(new XYPlot(getContext(), "plot"));
        RectF rect = new RectF(0, 0, 10, 10);

        renderer.doDrawLegendIcon(canvas, rect, f);

        verify(canvas).drawRect(rect, f.getFillPaint());
        verify(canvas).drawRect(rect, f.getBorderPaint());
    }

    @Test
    public void legendIcon_withoutBorder_drawsFillOnly() {
        BarFormatter f = new BarFormatter(Color.RED, Color.BLUE);
        f.setBorderPaint(null);
        BarRenderer renderer = new BarRenderer(new XYPlot(getContext(), "plot"));
        RectF rect = new RectF(0, 0, 10, 10);

        renderer.doDrawLegendIcon(canvas, rect, f);

        verify(canvas).drawRect(rect, f.getFillPaint());
        verify(canvas, never()).drawRect(any(RectF.class), eq((Paint) null));
    }
}
