// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.test.AndroidplotTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XYRegionFormatterTest extends AndroidplotTest {

    @Test
    public void colorConstructor_setsFilledAntiAliasedPaint() {
        XYRegionFormatter f = new XYRegionFormatter(Color.MAGENTA);
        assertEquals(Color.MAGENTA, f.getColor());
        assertEquals(Color.MAGENTA, f.getPaint().getColor());
        assertEquals(Paint.Style.FILL, f.getPaint().getStyle());
        assertTrue(f.getPaint().isAntiAlias());
    }

    @Test
    public void setColor_updatesPaintColor() {
        XYRegionFormatter f = new XYRegionFormatter(Color.MAGENTA);
        Paint paint = f.getPaint();
        f.setColor(Color.CYAN);
        assertEquals(Color.CYAN, f.getColor());
        assertSame(paint, f.getPaint());
        assertEquals(Color.CYAN, paint.getColor());
    }

    @Test
    public void xmlConfigConstructor_appliesResourceConfig() {
        // the test xml resource is packaged but absent from the compile time R stub
        final int cfgId = getContext().getResources().getIdentifier(
                "region_formatter_cfg", "xml", getContext().getPackageName());
        assertNotEquals("test xml resource not found", 0, cfgId);

        XYRegionFormatter f = new XYRegionFormatter(getContext(), cfgId);
        assertEquals(0xFF445566, f.getColor());
    }

    @Test
    public void xmlConfigConstructor_invalidResource_throws() {
        try {
            new XYRegionFormatter(getContext(), 0);
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // expected: the FigException / missing resource is wrapped
        }
    }

    @Test
    public void xmlConfigConstructor_derivedClass_isNotConfigured() {
        // derived classes skip xml configuration entirely, so even a bogus id is harmless:
        DerivedRegionFormatter f = new DerivedRegionFormatter(getContext(), 0);
        // the paint keeps its default (unset) color:
        assertEquals(new Paint().getColor(), f.getColor());
    }

    static class DerivedRegionFormatter extends XYRegionFormatter {
        DerivedRegionFormatter(Context ctx, int xmlCfgId) {
            super(ctx, xmlCfgId);
        }
    }
}
