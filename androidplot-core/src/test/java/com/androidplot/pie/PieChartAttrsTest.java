// SPDX-License-Identifier: Apache-2.0
package com.androidplot.pie;

import com.androidplot.Plot;
import com.androidplot.R;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.AttributeSetBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * XML attribute processing of {@link PieChart} driven through a real AttributeSet.
 */
public class PieChartAttrsTest extends AndroidplotTest {

    private static final int COLOR_1 = 0xFF112233;
    private static final int COLOR_2 = 0xFF445566;

    PieChart defaults;

    @Before
    public void setUp() {
        defaults = new PieChart(getContext(), attrs().build());
    }

    private static AttributeSetBuilder attrs() {
        return Robolectric.buildAttributeSet();
    }

    private PieChart chart(AttributeSetBuilder builder) {
        return new PieChart(getContext(), builder.build());
    }

    @Test
    public void pieBorderAttrs_configureBorderPaint() {
        final PieChart chart = chart(attrs()
                .addAttribute(R.attr.pieBorderColor, "#FF112233")
                .addAttribute(R.attr.pieBorderThickness, "9px"));

        assertEquals(COLOR_1, chart.getBorderPaint().getColor());
        assertEquals(9f, chart.getBorderPaint().getStrokeWidth(), 0);
    }

    @Test
    public void pieBorderAttrs_absent_keepDefaults() {
        final PieChart chart = chart(attrs().addAttribute(R.attr.title, "t"));
        assertEquals(defaults.getBorderPaint().getColor(), chart.getBorderPaint().getColor());
        assertEquals(defaults.getBorderPaint().getStrokeWidth(), chart.getBorderPaint().getStrokeWidth(), 0);
    }

    @Test
    public void plotBorderAttrs_applyAfterPieBorderAttrs() {
        // both target the plot's border paint; base Plot attrs are applied last and win
        final PieChart chart = chart(attrs()
                .addAttribute(R.attr.pieBorderColor, "#FF112233")
                .addAttribute(R.attr.pieBorderThickness, "9px")
                .addAttribute(R.attr.borderColor, "#FF445566")
                .addAttribute(R.attr.borderThickness, "4px"));

        assertEquals(COLOR_2, chart.getBorderPaint().getColor());
        assertEquals(4f, chart.getBorderPaint().getStrokeWidth(), 0);
    }

    @Test
    public void plotBaseAttrs_applyToPieChart() {
        final PieChart chart = chart(attrs()
                .addAttribute(R.attr.title, "Pie")
                .addAttribute(R.attr.titleTextSize, "23px")
                .addAttribute(R.attr.titleTextColor, "#FF112233")
                .addAttribute(R.attr.backgroundColor, "#FF445566")
                .addAttribute(R.attr.renderMode, "use_background_thread")
                .addAttribute(R.attr.markupEnabled, "true")
                .addAttribute(R.attr.marginTop, "1px")
                .addAttribute(R.attr.marginBottom, "2px")
                .addAttribute(R.attr.marginLeft, "3px")
                .addAttribute(R.attr.marginRight, "4px")
                .addAttribute(R.attr.paddingTop, "5px")
                .addAttribute(R.attr.paddingBottom, "6px")
                .addAttribute(R.attr.paddingLeft, "7px")
                .addAttribute(R.attr.paddingRight, "8px"));

        assertEquals("Pie", chart.getTitle().getText());
        assertEquals(23f, chart.getTitle().getLabelPaint().getTextSize(), 0);
        assertEquals(COLOR_1, chart.getTitle().getLabelPaint().getColor());
        assertEquals(COLOR_2, chart.getBackgroundPaint().getColor());
        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, chart.getRenderMode());
        assertTrue(chart.getLayoutManager().isDrawOutlinesEnabled());
        assertEquals(1f, chart.getPlotMarginTop(), 0);
        assertEquals(2f, chart.getPlotMarginBottom(), 0);
        assertEquals(3f, chart.getPlotMarginLeft(), 0);
        assertEquals(4f, chart.getPlotMarginRight(), 0);
        assertEquals(5f, chart.getPlotPaddingTop(), 0);
        assertEquals(6f, chart.getPlotPaddingBottom(), 0);
        assertEquals(7f, chart.getPlotPaddingLeft(), 0);
        assertEquals(8f, chart.getPlotPaddingRight(), 0);

        // pie widget untouched by plot-level attrs:
        assertEquals(defaults.getPie().getPaddingLeft(), chart.getPie().getPaddingLeft(), 0);
        assertEquals(defaults.getPie().getSize().getHeight().getValue(),
                chart.getPie().getSize().getHeight().getValue(), 0);
    }

    @Test
    public void onPreInit_defaultsSurviveAttrProcessing() {
        final PieChart chart = chart(attrs()
                .addAttribute(R.attr.title, "Pie")
                .addAttribute(R.attr.marginTop, "1px"));
        assertEquals("Pie", chart.getTitle().getText());

        assertFalse(chart.getLegend().isVisible());
        assertEquals(SizeMode.FILL, chart.getPie().getSize().getHeight().getLayoutType());
        assertEquals(Anchor.CENTER, chart.getPie().getAnchor());
        assertEquals(HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                chart.getPie().getPositionMetrics().getXPositionMetric().getLayoutType());
        assertEquals(VerticalPositioning.ABSOLUTE_FROM_CENTER,
                chart.getPie().getPositionMetrics().getYPositionMetric().getLayoutType());
        assertEquals(Anchor.RIGHT_BOTTOM, chart.getLegend().getAnchor());
        assertEquals(5f, chart.getPie().getPaddingLeft(), 0);
    }

    @Test
    public void noAttrs_titleIsNull() {
        assertNull(defaults.getTitle().getText());
    }

    @Test
    public void setPie_replacesPieWidget() {
        final PieChart chart = new PieChart(getContext(), "pie");
        final PieWidget original = chart.getPie();
        final PieWidget replacement = new PieWidget(chart.getLayoutManager(), chart,
                original.getSize());
        chart.setPie(replacement);
        assertSame(replacement, chart.getPie());
    }

    @Test
    public void addAndRemoveSegment_updateRegistry() {
        final PieChart chart = new PieChart(getContext(), "pie");
        final Segment segment = new Segment("a", 1);
        chart.addSegment(segment, new SegmentFormatter(0xFF00FF00));
        assertEquals(1, chart.getRegistry().size());
        assertTrue(chart.getRegistry().contains(segment, SegmentFormatter.class));
        chart.removeSegment(segment);
        assertEquals(0, chart.getRegistry().size());
    }
}
