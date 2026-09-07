// SPDX-License-Identifier: Apache-2.0
package com.androidplot.pie;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.fig.Fig;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SegmentFormatterTest extends AndroidplotTest {

    @Test
    public void defaults() {
        final SegmentFormatter formatter = new SegmentFormatter(Color.BLUE);
        assertEquals(Color.BLUE, formatter.getFillPaint().getColor());
        assertTrue(formatter.isLegendIconEnabled());
        assertEquals(0f, formatter.getOffset(), 0);
        assertEquals(0f, formatter.getRadialInset(), 0);
        assertEquals(0f, formatter.getInnerInset(), 0);
        assertEquals(0f, formatter.getOuterInset(), 0);

        for (Paint edge : new Paint[]{formatter.getOuterEdgePaint(),
                formatter.getInnerEdgePaint(), formatter.getRadialEdgePaint()}) {
            assertNotNull(edge);
            assertEquals(Paint.Style.STROKE, edge.getStyle());
            assertEquals(3f, edge.getStrokeWidth(), 0);
            assertTrue(edge.isAntiAlias());
        }
        assertNotSame(formatter.getOuterEdgePaint(), formatter.getInnerEdgePaint());
        assertNotSame(formatter.getInnerEdgePaint(), formatter.getRadialEdgePaint());

        assertEquals(Color.WHITE, formatter.getLabelPaint().getColor());
        assertEquals(18f, formatter.getLabelPaint().getTextSize(), 0);
        assertEquals(Paint.Align.CENTER, formatter.getLabelPaint().getTextAlign());
        assertEquals(Color.WHITE, formatter.getLabelMarkerPaint().getColor());
        assertEquals(3f, formatter.getLabelMarkerPaint().getStrokeWidth(), 0);
    }

    @Test
    public void nullFillColor_isTransparent() {
        final SegmentFormatter formatter = new SegmentFormatter((Integer) null);
        assertEquals(Color.TRANSPARENT, formatter.getFillPaint().getColor());
    }

    @Test
    public void fillAndBorderConstructor_colorsAllEdges() {
        final SegmentFormatter formatter = new SegmentFormatter(Color.RED, Color.GREEN);
        assertEquals(Color.RED, formatter.getFillPaint().getColor());
        assertEquals(Color.GREEN, formatter.getOuterEdgePaint().getColor());
        assertEquals(Color.GREEN, formatter.getInnerEdgePaint().getColor());
        assertEquals(Color.GREEN, formatter.getRadialEdgePaint().getColor());
    }

    @Test
    public void perEdgeConstructor_colorsEachEdge() {
        final SegmentFormatter formatter = new SegmentFormatter(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        assertEquals(Color.RED, formatter.getFillPaint().getColor());
        assertEquals(Color.GREEN, formatter.getOuterEdgePaint().getColor());
        assertEquals(Color.BLUE, formatter.getInnerEdgePaint().getColor());
        assertEquals(Color.YELLOW, formatter.getRadialEdgePaint().getColor());
    }

    @Test
    public void renderer() {
        final SegmentFormatter formatter = new SegmentFormatter(Color.RED);
        assertEquals(PieRenderer.class, formatter.getRendererClass());
        final PieChart chart = new PieChart(getContext(), "pie");
        final SeriesRenderer renderer = formatter.doGetRendererInstance(chart);
        assertTrue(renderer instanceof PieRenderer);
        final PieRenderer viaGeneric = formatter.getRendererInstance(chart);
        assertNotNull(viaGeneric);
        assertNotSame(renderer, viaGeneric);
    }

    @Test
    public void setters() {
        final SegmentFormatter formatter = new SegmentFormatter(Color.RED);
        final Paint p1 = new Paint();
        final Paint p2 = new Paint();
        final Paint p3 = new Paint();
        final Paint p4 = new Paint();
        final Paint p5 = new Paint();
        final Paint p6 = new Paint();
        formatter.setFillPaint(p1);
        formatter.setInnerEdgePaint(p2);
        formatter.setOuterEdgePaint(p3);
        formatter.setRadialEdgePaint(p4);
        formatter.setLabelPaint(p5);
        formatter.setLabelMarkerPaint(p6);
        formatter.setOffset(1.5f);
        formatter.setRadialInset(2.5f);
        formatter.setInnerInset(3.5f);
        formatter.setOuterInset(4.5f);
        formatter.setLegendIconEnabled(false);

        assertSame(p1, formatter.getFillPaint());
        assertSame(p2, formatter.getInnerEdgePaint());
        assertSame(p3, formatter.getOuterEdgePaint());
        assertSame(p4, formatter.getRadialEdgePaint());
        assertSame(p5, formatter.getLabelPaint());
        assertSame(p6, formatter.getLabelMarkerPaint());
        assertEquals(1.5f, formatter.getOffset(), 0);
        assertEquals(2.5f, formatter.getRadialInset(), 0);
        assertEquals(3.5f, formatter.getInnerInset(), 0);
        assertEquals(4.5f, formatter.getOuterInset(), 0);
        assertFalse(formatter.isLegendIconEnabled());
    }

    @Test
    public void configure_fromParams_appliesNestedPaintProperties() throws Exception {
        final SegmentFormatter formatter = new SegmentFormatter(Color.RED);
        final HashMap<String, String> params = new HashMap<>();
        params.put("fillPaint.color", "#FF00AA00");
        params.put("innerEdgePaint.strokeWidth", "7px");
        params.put("labelPaint.textSize", "21px");
        params.put("labelPaint.textAlign", "right");
        params.put("offset", "8dp");
        params.put("outerInset", "2.5");
        params.put("legendIconEnabled", "false");
        Fig.configure(getContext(), formatter, params);

        assertEquals(0xFF00AA00, formatter.getFillPaint().getColor());
        assertEquals(7f, formatter.getInnerEdgePaint().getStrokeWidth(), 0);
        assertEquals(21f, formatter.getLabelPaint().getTextSize(), 0);
        assertEquals(Paint.Align.RIGHT, formatter.getLabelPaint().getTextAlign());
        assertEquals(8f, formatter.getOffset(), 0);
        assertEquals(2.5f, formatter.getOuterInset(), 0);
        assertFalse(formatter.isLegendIconEnabled());
    }

    @Test
    public void xmlConfigConstructor_appliesResourceConfig() {
        // the test xml resource is packaged but absent from the compile time R stub
        final int cfgId = getContext().getResources().getIdentifier(
                "segment_formatter_cfg", "xml", getContext().getPackageName());
        assertNotEquals("test xml resource not found", 0, cfgId);

        final SegmentFormatter formatter = new SegmentFormatter(getContext(), cfgId);

        assertEquals(0xFF00AA00, formatter.getFillPaint().getColor());
        assertEquals(0xFF112233, formatter.getOuterEdgePaint().getColor());
        assertEquals(4f, formatter.getOuterEdgePaint().getStrokeWidth(), 0);
        assertEquals(15f, formatter.getLabelPaint().getTextSize(), 0);
        assertEquals(Paint.Align.LEFT, formatter.getLabelPaint().getTextAlign());
        assertEquals(6f, formatter.getOffset(), 0);
        assertEquals(1f, formatter.getRadialInset(), 0);
        assertEquals(2f, formatter.getInnerInset(), 0);
        assertEquals(3f, formatter.getOuterInset(), 0);
        assertFalse(formatter.isLegendIconEnabled());

        // untouched defaults survive:
        assertEquals(3f, formatter.getInnerEdgePaint().getStrokeWidth(), 0);
        assertEquals(Color.WHITE, formatter.getLabelPaint().getColor());
    }
}
