// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import android.graphics.Paint;
import android.util.AttributeSet;

import com.androidplot.Plot;
import com.androidplot.R;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.PositionMetrics;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.XYGraphWidget.Edge;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.AttributeSetBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * XML attribute processing of {@link XYPlot} (and the {@link Plot} base attrs) driven through a
 * real {@link AttributeSet}, so the styleable ids, attr formats and enum ordinals declared in
 * attrs.xml are all exercised.  Each group of attrs is checked to land on its own widget and to
 * leave the sibling widgets untouched.
 *
 * Note: tests run at density 1.0 so "Npx" == "Ndp" == N.
 */
public class XYPlotAttrsTest extends AndroidplotTest {

    private static final int COLOR_1 = 0xFF112233;
    private static final int COLOR_2 = 0xFF445566;
    private static final String COLOR_1_HEX = "#FF112233";
    private static final String COLOR_2_HEX = "#FF445566";

    /** an XYPlot inflated from an empty attribute set, for comparing untouched state against. */
    XYPlot defaults;

    @Before
    public void setUp() {
        defaults = new XYPlot(getContext(), attrs().build());
    }

    private static AttributeSetBuilder attrs() {
        return Robolectric.buildAttributeSet();
    }

    private XYPlot plot(AttributeSetBuilder builder) {
        return new XYPlot(getContext(), builder.build());
    }

    // ---- Plot base attrs ----

    @Test
    public void plotAttrs_title() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.title, "The Title")
                .addAttribute(R.attr.titleTextSize, "21px")
                .addAttribute(R.attr.titleTextColor, COLOR_1_HEX));

        assertEquals("The Title", plot.getTitle().getText());
        assertEquals(21f, plot.getTitle().getLabelPaint().getTextSize(), 0);
        assertEquals(COLOR_1, plot.getTitle().getLabelPaint().getColor());

        // and nothing leaked into the sibling widgets:
        assertTextLabelUnchanged(defaults.getDomainTitle(), plot.getDomainTitle());
        assertTextLabelUnchanged(defaults.getRangeTitle(), plot.getRangeTitle());
        assertEquals(defaults.getLegend().getTextPaint().getTextSize(),
                plot.getLegend().getTextPaint().getTextSize(), 0);
    }

    @Test
    public void plotAttrs_noTitleAttr_clearsTitleText() {
        // base attrs are applied unconditionally: an absent title attr yields a null title
        final XYPlot plot = plot(attrs().addAttribute(R.attr.domainTitle, "x"));
        assertNull(plot.getTitle().getText());
        assertEquals(defaults.getTitle().getLabelPaint().getTextSize(),
                plot.getTitle().getLabelPaint().getTextSize(), 0);
    }

    @Test
    public void plotAttrs_backgroundAndBorder() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.backgroundColor, COLOR_1_HEX)
                .addAttribute(R.attr.borderColor, COLOR_2_HEX)
                .addAttribute(R.attr.borderThickness, "7px"));

        assertEquals(COLOR_1, plot.getBackgroundPaint().getColor());
        assertEquals(COLOR_2, plot.getBorderPaint().getColor());
        assertEquals(7f, plot.getBorderPaint().getStrokeWidth(), 0);

        // graph background is a separate attr:
        assertEquals(defaults.getGraph().getBackgroundPaint().getColor(),
                plot.getGraph().getBackgroundPaint().getColor());
    }

    @Test
    public void plotAttrs_marginsAndPadding() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.marginTop, "1px")
                .addAttribute(R.attr.marginBottom, "2px")
                .addAttribute(R.attr.marginLeft, "3px")
                .addAttribute(R.attr.marginRight, "4px")
                .addAttribute(R.attr.paddingTop, "5px")
                .addAttribute(R.attr.paddingBottom, "6px")
                .addAttribute(R.attr.paddingLeft, "7px")
                .addAttribute(R.attr.paddingRight, "8px"));

        assertEquals(1f, plot.getPlotMarginTop(), 0);
        assertEquals(2f, plot.getPlotMarginBottom(), 0);
        assertEquals(3f, plot.getPlotMarginLeft(), 0);
        assertEquals(4f, plot.getPlotMarginRight(), 0);
        assertEquals(5f, plot.getPlotPaddingTop(), 0);
        assertEquals(6f, plot.getPlotPaddingBottom(), 0);
        assertEquals(7f, plot.getPlotPaddingLeft(), 0);
        assertEquals(8f, plot.getPlotPaddingRight(), 0);

        // graph margins are separate attrs:
        assertEquals(defaults.getGraph().getMarginTop(), plot.getGraph().getMarginTop(), 0);
        assertEquals(defaults.getGraph().getPaddingLeft(), plot.getGraph().getPaddingLeft(), 0);
    }

    @Test
    public void plotAttrs_partialMargins_keepDefaultsForTheRest() {
        final XYPlot plot = plot(attrs().addAttribute(R.attr.marginTop, "99px"));
        assertEquals(99f, plot.getPlotMarginTop(), 0);
        assertEquals(defaults.getPlotMarginBottom(), plot.getPlotMarginBottom(), 0);
        assertEquals(defaults.getPlotMarginLeft(), plot.getPlotMarginLeft(), 0);
        assertEquals(defaults.getPlotMarginRight(), plot.getPlotMarginRight(), 0);
    }

    @Test
    public void plotAttrs_renderModeAndMarkup() {
        assertEquals(Plot.RenderMode.USE_MAIN_THREAD, defaults.getRenderMode());
        assertFalse(defaults.getLayoutManager().isDrawOutlinesEnabled());

        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.renderMode, "use_background_thread")
                .addAttribute(R.attr.markupEnabled, "true"));

        assertEquals(Plot.RenderMode.USE_BACKGROUND_THREAD, plot.getRenderMode());
        assertTrue(plot.getLayoutManager().isDrawOutlinesEnabled());
        assertTrue(plot.getLayoutManager().isDrawAnchorsEnabled());
        assertTrue(plot.getLayoutManager().isDrawMarginsEnabled());
        assertTrue(plot.getLayoutManager().isDrawPaddingEnabled());

        assertEquals(Plot.RenderMode.USE_MAIN_THREAD,
                plot(attrs().addAttribute(R.attr.renderMode, "use_main_thread")).getRenderMode());
    }

    // ---- domain / range title widgets ----

    /**
     * Note: the size attrs are only effective when no title text is set, since a
     * {@link com.androidplot.ui.widget.TextLabelWidget} with text auto-packs its size to the
     * text (again in onPostInit, after attrs are applied).  Text is covered separately below.
     */
    @Test
    public void domainTitleAttrs_landOnDomainTitleWidgetOnly() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.domainTitleTextColor, COLOR_1_HEX)
                .addAttribute(R.attr.domainTitleTextSize, "17px")
                .addAttribute(R.attr.domainTitleHeightMode, "absolute")
                .addAttribute(R.attr.domainTitleHeight, "31px")
                .addAttribute(R.attr.domainTitleWidthMode, "relative")
                .addAttribute(R.attr.domainTitleWidth, "0.25")
                .addAttribute(R.attr.domainTitleHorizontalPositioning, "absolute_from_right")
                .addAttribute(R.attr.domainTitleHorizontalPosition, "12px")
                .addAttribute(R.attr.domainTitleVerticalPositioning, "relative_from_center")
                .addAttribute(R.attr.domainTitleVerticalPosition, "0.5")
                .addAttribute(R.attr.domainTitleAnchor, "right_top")
                .addAttribute(R.attr.domainTitleVisible, "false"));

        assertEquals(COLOR_1, plot.getDomainTitle().getLabelPaint().getColor());
        assertEquals(17f, plot.getDomainTitle().getLabelPaint().getTextSize(), 0);
        assertSize(plot.getDomainTitle().getSize(), 31, SizeMode.ABSOLUTE, 0.25f, SizeMode.RELATIVE);
        assertPosition(plot.getDomainTitle().getPositionMetrics(),
                12, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                0.5f, VerticalPositioning.RELATIVE_TO_CENTER, Anchor.RIGHT_TOP);
        assertFalse(plot.getDomainTitle().isVisible());

        assertTextLabelUnchanged(defaults.getRangeTitle(), plot.getRangeTitle());
        assertWidgetUnchanged(defaults.getGraph(), plot.getGraph());
        assertWidgetUnchanged(defaults.getLegend(), plot.getLegend());
        assertTextLabelUnchanged(defaults.getTitle(), plot.getTitle());
    }

    @Test
    public void rangeTitleAttrs_landOnRangeTitleWidgetOnly() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.rangeTitleTextColor, COLOR_2_HEX)
                .addAttribute(R.attr.rangeTitleTextSize, "19px")
                .addAttribute(R.attr.rangeTitleHeightMode, "fill")
                .addAttribute(R.attr.rangeTitleHeight, "3px")
                .addAttribute(R.attr.rangeTitleWidthMode, "absolute")
                .addAttribute(R.attr.rangeTitleWidth, "45px")
                .addAttribute(R.attr.rangeTitleHorizontalPositioning, "relative_from_left")
                .addAttribute(R.attr.rangeTitleHorizontalPosition, "0.125")
                .addAttribute(R.attr.rangeTitleVerticalPositioning, "absolute_from_bottom")
                .addAttribute(R.attr.rangeTitleVerticalPosition, "9px")
                .addAttribute(R.attr.rangeTitleAnchor, "bottom_middle")
                .addAttribute(R.attr.rangeTitleVisible, "false"));

        assertEquals(COLOR_2, plot.getRangeTitle().getLabelPaint().getColor());
        assertEquals(19f, plot.getRangeTitle().getLabelPaint().getTextSize(), 0);
        assertSize(plot.getRangeTitle().getSize(), 3, SizeMode.FILL, 45, SizeMode.ABSOLUTE);
        assertPosition(plot.getRangeTitle().getPositionMetrics(),
                0.125f, HorizontalPositioning.RELATIVE_TO_LEFT,
                9, VerticalPositioning.ABSOLUTE_FROM_BOTTOM, Anchor.BOTTOM_MIDDLE);
        assertFalse(plot.getRangeTitle().isVisible());

        assertTextLabelUnchanged(defaults.getDomainTitle(), plot.getDomainTitle());
        assertWidgetUnchanged(defaults.getGraph(), plot.getGraph());
        assertWidgetUnchanged(defaults.getLegend(), plot.getLegend());
        assertTextLabelUnchanged(defaults.getTitle(), plot.getTitle());
    }

    @Test
    public void titleTextAttrs_landOnMatchingTitleWidget() {
        final XYPlot domainOnly = plot(attrs().addAttribute(R.attr.domainTitle, "D"));
        assertEquals("D", domainOnly.getDomainTitle().getText());
        assertNull(domainOnly.getRangeTitle().getText());
        assertNull(domainOnly.getTitle().getText());

        final XYPlot rangeOnly = plot(attrs().addAttribute(R.attr.rangeTitle, "R"));
        assertEquals("R", rangeOnly.getRangeTitle().getText());
        assertNull(rangeOnly.getDomainTitle().getText());
        assertNull(rangeOnly.getTitle().getText());

        final XYPlot both = plot(attrs()
                .addAttribute(R.attr.domainTitle, "D")
                .addAttribute(R.attr.rangeTitle, "R")
                .addAttribute(R.attr.title, "T"));
        assertEquals("D", both.getDomainTitle().getText());
        assertEquals("R", both.getRangeTitle().getText());
        assertEquals("T", both.getTitle().getText());

        // text does not disturb position, anchor or visibility (size auto-packs to the text):
        for (XYPlot plot : new XYPlot[]{domainOnly, rangeOnly, both}) {
            assertPositionUnchanged(defaults.getDomainTitle(), plot.getDomainTitle());
            assertPositionUnchanged(defaults.getRangeTitle(), plot.getRangeTitle());
            assertTrue(plot.getDomainTitle().isVisible());
            assertTrue(plot.getRangeTitle().isVisible());
        }
    }

    @Test
    public void titleSizeAttrs_withText_areOverriddenByAutoPack() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.domainTitle, "Domain")
                .addAttribute(R.attr.domainTitleHeightMode, "absolute")
                .addAttribute(R.attr.domainTitleHeight, "31px")
                .addAttribute(R.attr.domainTitleWidthMode, "absolute")
                .addAttribute(R.attr.domainTitleWidth, "41px"));
        assertTrue(plot.getDomainTitle().isAutoPackEnabled());
        assertEquals(SizeMode.ABSOLUTE, plot.getDomainTitle().getSize().getHeight().getLayoutType());
        assertEquals(SizeMode.ABSOLUTE, plot.getDomainTitle().getSize().getWidth().getLayoutType());
        // packed to the (Robolectric: empty) text bounds rather than the attr values:
        assertFalse(31f == plot.getDomainTitle().getSize().getHeight().getValue());
        assertFalse(41f == plot.getDomainTitle().getSize().getWidth().getValue());
    }

    // ---- legend ----

    @Test
    public void legendAttrs_landOnLegendWidgetOnly() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.legendTextColor, COLOR_1_HEX)
                .addAttribute(R.attr.legendTextSize, "13px")
                .addAttribute(R.attr.legendHeightMode, "absolute")
                .addAttribute(R.attr.legendHeight, "22px")
                .addAttribute(R.attr.legendWidthMode, "fill")
                .addAttribute(R.attr.legendWidth, "2px")
                .addAttribute(R.attr.legendHorizontalPositioning, "absolute_from_center")
                .addAttribute(R.attr.legendHorizontalPosition, "-4px")
                .addAttribute(R.attr.legendVerticalPositioning, "relative_from_top")
                .addAttribute(R.attr.legendVerticalPosition, "0.75")
                .addAttribute(R.attr.legendAnchor, "top_middle")
                .addAttribute(R.attr.legendIconHeightMode, "relative")
                .addAttribute(R.attr.legendIconHeight, "0.5")
                .addAttribute(R.attr.legendIconWidthMode, "absolute")
                .addAttribute(R.attr.legendIconWidth, "8px")
                .addAttribute(R.attr.legendVisible, "false"));

        assertEquals(COLOR_1, plot.getLegend().getTextPaint().getColor());
        assertEquals(13f, plot.getLegend().getTextPaint().getTextSize(), 0);
        assertSize(plot.getLegend().getSize(), 22, SizeMode.ABSOLUTE, 2, SizeMode.FILL);
        assertPosition(plot.getLegend().getPositionMetrics(),
                -4, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0.75f, VerticalPositioning.RELATIVE_TO_TOP, Anchor.TOP_MIDDLE);
        assertSize(plot.getLegend().getIconSize(), 0.5f, SizeMode.RELATIVE, 8, SizeMode.ABSOLUTE);
        assertFalse(plot.getLegend().isVisible());

        assertTextLabelUnchanged(defaults.getDomainTitle(), plot.getDomainTitle());
        assertTextLabelUnchanged(defaults.getRangeTitle(), plot.getRangeTitle());
        assertWidgetUnchanged(defaults.getGraph(), plot.getGraph());
        assertTextLabelUnchanged(defaults.getTitle(), plot.getTitle());
    }

    // ---- graph widget ----

    @Test
    public void graphAttrs_landOnGraphWidgetOnly() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.graphHeightMode, "relative")
                .addAttribute(R.attr.graphHeight, "0.75")
                .addAttribute(R.attr.graphWidthMode, "absolute")
                .addAttribute(R.attr.graphWidth, "150px")
                .addAttribute(R.attr.graphHorizontalPositioning, "absolute_from_left")
                .addAttribute(R.attr.graphHorizontalPosition, "11px")
                .addAttribute(R.attr.graphVerticalPositioning, "absolute_from_top")
                .addAttribute(R.attr.graphVerticalPosition, "13px")
                .addAttribute(R.attr.graphAnchor, "left_top")
                .addAttribute(R.attr.graphRotation, "ninety_degrees")
                .addAttribute(R.attr.graphVisible, "false")
                .addAttribute(R.attr.graphMarginTop, "1px")
                .addAttribute(R.attr.graphMarginBottom, "2px")
                .addAttribute(R.attr.graphMarginLeft, "3px")
                .addAttribute(R.attr.graphMarginRight, "4px")
                .addAttribute(R.attr.graphPaddingTop, "5px")
                .addAttribute(R.attr.graphPaddingBottom, "6px")
                .addAttribute(R.attr.graphPaddingLeft, "7px")
                .addAttribute(R.attr.graphPaddingRight, "8px")
                .addAttribute(R.attr.graphBackgroundColor, COLOR_1_HEX)
                .addAttribute(R.attr.gridBackgroundColor, COLOR_2_HEX));

        final XYGraphWidget graph = plot.getGraph();
        assertSize(graph.getSize(), 0.75f, SizeMode.RELATIVE, 150, SizeMode.ABSOLUTE);
        assertPosition(graph.getPositionMetrics(),
                11, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                13, VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);
        assertEquals(Widget.Rotation.NINETY_DEGREES, graph.getRotation());
        assertFalse(graph.isVisible());
        assertEquals(1f, graph.getMarginTop(), 0);
        assertEquals(2f, graph.getMarginBottom(), 0);
        assertEquals(3f, graph.getMarginLeft(), 0);
        assertEquals(4f, graph.getMarginRight(), 0);
        assertEquals(5f, graph.getPaddingTop(), 0);
        assertEquals(6f, graph.getPaddingBottom(), 0);
        assertEquals(7f, graph.getPaddingLeft(), 0);
        assertEquals(8f, graph.getPaddingRight(), 0);
        assertEquals(COLOR_1, graph.getBackgroundPaint().getColor());
        assertEquals(COLOR_2, graph.getGridBackgroundPaint().getColor());

        // plot-level background & box model untouched:
        assertEquals(defaults.getBackgroundPaint().getColor(), plot.getBackgroundPaint().getColor());
        assertEquals(defaults.getPlotMarginTop(), plot.getPlotMarginTop(), 0);
        assertEquals(defaults.getPlotPaddingLeft(), plot.getPlotPaddingLeft(), 0);

        assertTextLabelUnchanged(defaults.getDomainTitle(), plot.getDomainTitle());
        assertTextLabelUnchanged(defaults.getRangeTitle(), plot.getRangeTitle());
        assertWidgetUnchanged(defaults.getLegend(), plot.getLegend());
        assertTextLabelUnchanged(defaults.getTitle(), plot.getTitle());
    }

    @Test
    public void graphRotation_everyValue() {
        assertEquals(Widget.Rotation.NONE, defaults.getGraph().getRotation());
        assertEquals(Widget.Rotation.NONE,
                plot(attrs().addAttribute(R.attr.graphRotation, "none")).getGraph().getRotation());
        assertEquals(Widget.Rotation.NINETY_DEGREES,
                plot(attrs().addAttribute(R.attr.graphRotation, "ninety_degrees")).getGraph().getRotation());
        assertEquals(Widget.Rotation.NEGATIVE_NINETY_DEGREES,
                plot(attrs().addAttribute(R.attr.graphRotation, "negative_ninety_degrees"))
                        .getGraph().getRotation());
        assertEquals(Widget.Rotation.ONE_HUNDRED_EIGHTY_DEGREES,
                plot(attrs().addAttribute(R.attr.graphRotation, "one_hundred_eighty_degrees"))
                        .getGraph().getRotation());
    }

    @Test
    public void gridAttrs() {
        assertFalse(defaults.getGraph().isDrawGridOnTop());
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.drawGridOnTop, "true")
                .addAttribute(R.attr.gridClippingEnabled, "false")
                .addAttribute(R.attr.gridInsetTop, "1px")
                .addAttribute(R.attr.gridInsetBottom, "2px")
                .addAttribute(R.attr.gridInsetLeft, "3px")
                .addAttribute(R.attr.gridInsetRight, "4px")
                .addAttribute(R.attr.domainLineColor, COLOR_1_HEX)
                .addAttribute(R.attr.domainLineThickness, "5px")
                .addAttribute(R.attr.rangeLineColor, COLOR_2_HEX)
                .addAttribute(R.attr.rangeLineThickness, "6px")
                .addAttribute(R.attr.domainOriginLineColor, "#FF0000FF")
                .addAttribute(R.attr.domainOriginLineThickness, "7px")
                .addAttribute(R.attr.rangeOriginLineColor, "#FF00FF00")
                .addAttribute(R.attr.rangeOriginLineThickness, "8px"));

        final XYGraphWidget graph = plot.getGraph();
        assertTrue(graph.isDrawGridOnTop());
        assertFalse(graph.isGridClippingEnabled());
        assertEquals(1f, graph.getGridInsets().getTop(), 0);
        assertEquals(2f, graph.getGridInsets().getBottom(), 0);
        assertEquals(3f, graph.getGridInsets().getLeft(), 0);
        assertEquals(4f, graph.getGridInsets().getRight(), 0);
        assertEquals(COLOR_1, graph.getDomainGridLinePaint().getColor());
        assertEquals(5f, graph.getDomainGridLinePaint().getStrokeWidth(), 0);
        assertEquals(COLOR_2, graph.getRangeGridLinePaint().getColor());
        assertEquals(6f, graph.getRangeGridLinePaint().getStrokeWidth(), 0);
        assertEquals(0xFF0000FF, graph.getDomainOriginLinePaint().getColor());
        assertEquals(7f, graph.getDomainOriginLinePaint().getStrokeWidth(), 0);
        assertEquals(0xFF00FF00, graph.getRangeOriginLinePaint().getColor());
        assertEquals(8f, graph.getRangeOriginLinePaint().getStrokeWidth(), 0);

        // line label insets are separate attrs:
        assertEquals(defaults.getGraph().getLineLabelInsets().getTop(),
                graph.getLineLabelInsets().getTop(), 0);
    }

    @Test
    public void lineLabelAttrs() {
        for (Edge edge : Edge.values()) {
            assertFalse(edge.toString(), defaults.getGraph().isLineLabelEnabled(edge));
        }

        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.lineLabels, "left|bottom")
                .addAttribute(R.attr.lineLabelAlignTop, "center")
                .addAttribute(R.attr.lineLabelAlignBottom, "right")
                .addAttribute(R.attr.lineLabelAlignLeft, "left")
                .addAttribute(R.attr.lineLabelAlignRight, "center")
                .addAttribute(R.attr.lineLabelRotationTop, "10")
                .addAttribute(R.attr.lineLabelRotationBottom, "-45.5")
                .addAttribute(R.attr.lineLabelRotationLeft, "90")
                .addAttribute(R.attr.lineLabelRotationRight, "1.5")
                .addAttribute(R.attr.lineLabelTextSizeTop, "11px")
                .addAttribute(R.attr.lineLabelTextSizeBottom, "12px")
                .addAttribute(R.attr.lineLabelTextSizeLeft, "13px")
                .addAttribute(R.attr.lineLabelTextSizeRight, "14px")
                .addAttribute(R.attr.lineLabelTextColorTop, "#FF000001")
                .addAttribute(R.attr.lineLabelTextColorBottom, "#FF000002")
                .addAttribute(R.attr.lineLabelTextColorLeft, "#FF000003")
                .addAttribute(R.attr.lineLabelTextColorRight, "#FF000004")
                .addAttribute(R.attr.lineLabelInsetTop, "1px")
                .addAttribute(R.attr.lineLabelInsetBottom, "2px")
                .addAttribute(R.attr.lineLabelInsetLeft, "3px")
                .addAttribute(R.attr.lineLabelInsetRight, "4px")
                .addAttribute(R.attr.lineExtensionTop, "5px")
                .addAttribute(R.attr.lineExtensionBottom, "6px")
                .addAttribute(R.attr.lineExtensionLeft, "7px")
                .addAttribute(R.attr.lineExtensionRight, "8px"));

        final XYGraphWidget graph = plot.getGraph();
        assertTrue(graph.isLineLabelEnabled(Edge.LEFT));
        assertTrue(graph.isLineLabelEnabled(Edge.BOTTOM));
        assertFalse(graph.isLineLabelEnabled(Edge.TOP));
        assertFalse(graph.isLineLabelEnabled(Edge.RIGHT));

        assertLineLabelStyle(graph.getLineLabelStyle(Edge.TOP), Paint.Align.CENTER, 10, 11, 0xFF000001);
        assertLineLabelStyle(graph.getLineLabelStyle(Edge.BOTTOM), Paint.Align.RIGHT, -45.5f, 12, 0xFF000002);
        assertLineLabelStyle(graph.getLineLabelStyle(Edge.LEFT), Paint.Align.LEFT, 90, 13, 0xFF000003);
        assertLineLabelStyle(graph.getLineLabelStyle(Edge.RIGHT), Paint.Align.CENTER, 1.5f, 14, 0xFF000004);

        assertEquals(1f, graph.getLineLabelInsets().getTop(), 0);
        assertEquals(2f, graph.getLineLabelInsets().getBottom(), 0);
        assertEquals(3f, graph.getLineLabelInsets().getLeft(), 0);
        assertEquals(4f, graph.getLineLabelInsets().getRight(), 0);
        assertEquals(5f, graph.getLineExtensionTop(), 0);
        assertEquals(6f, graph.getLineExtensionBottom(), 0);
        assertEquals(7f, graph.getLineExtensionLeft(), 0);
        assertEquals(8f, graph.getLineExtensionRight(), 0);

        // grid insets are separate attrs:
        assertEquals(defaults.getGraph().getGridInsets().getTop(), graph.getGridInsets().getTop(), 0);
    }

    @Test
    public void lineLabels_singleEdgeAndNone() {
        final XYGraphWidget top = plot(attrs().addAttribute(R.attr.lineLabels, "top")).getGraph();
        assertTrue(top.isLineLabelEnabled(Edge.TOP));
        assertFalse(top.isLineLabelEnabled(Edge.LEFT));
        assertFalse(top.isLineLabelEnabled(Edge.BOTTOM));
        assertFalse(top.isLineLabelEnabled(Edge.RIGHT));

        final XYGraphWidget all = plot(attrs()
                .addAttribute(R.attr.lineLabels, "top|bottom|left|right")).getGraph();
        assertTrue(all.isLineLabelEnabled(Edge.TOP));
        assertTrue(all.isLineLabelEnabled(Edge.LEFT));
        assertTrue(all.isLineLabelEnabled(Edge.BOTTOM));
        assertTrue(all.isLineLabelEnabled(Edge.RIGHT));

        final XYGraphWidget none = plot(attrs().addAttribute(R.attr.lineLabels, "none")).getGraph();
        assertFalse(none.isLineLabelEnabled(Edge.TOP));
        assertFalse(none.isLineLabelEnabled(Edge.LEFT));
        assertFalse(none.isLineLabelEnabled(Edge.BOTTOM));
        assertFalse(none.isLineLabelEnabled(Edge.RIGHT));
    }

    @Test
    public void lineLabelAlign_absent_keepsDefaultAlignment() {
        final XYPlot plot = plot(attrs().addAttribute(R.attr.lineLabelTextSizeTop, "11px"));
        for (Edge edge : new Edge[]{Edge.TOP, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT}) {
            assertEquals(edge.toString(),
                    defaults.getGraph().getLineLabelStyle(edge).getPaint().getTextAlign(),
                    plot.getGraph().getLineLabelStyle(edge).getPaint().getTextAlign());
        }
    }

    // ---- step models ----

    @Test
    public void stepAttrs_landOnMatchingStepModel() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.domainStepMode, "increment_by_val")
                .addAttribute(R.attr.domainStep, "7")
                .addAttribute(R.attr.rangeStepMode, "increment_by_pixels")
                .addAttribute(R.attr.rangeStep, "2.5"));

        assertEquals(StepMode.INCREMENT_BY_VAL, plot.getDomainStepModel().getMode());
        assertEquals(7.0, plot.getDomainStepModel().getValue(), 0);
        assertEquals(StepMode.INCREMENT_BY_PIXELS, plot.getRangeStepModel().getMode());
        assertEquals(2.5, plot.getRangeStepModel().getValue(), 0);
    }

    @Test
    public void stepAttrs_valueOnly_keepsMode() {
        final XYPlot plot = plot(attrs().addAttribute(R.attr.domainStep, "5"));
        assertEquals(StepMode.SUBDIVIDE, plot.getDomainStepModel().getMode());
        assertEquals(5.0, plot.getDomainStepModel().getValue(), 0);
        assertEquals(defaults.getRangeStepModel().getValue(), plot.getRangeStepModel().getValue(), 0);
    }

    @Test
    public void stepAttrs_dimensionValue() {
        final XYPlot plot = plot(attrs()
                .addAttribute(R.attr.rangeStepMode, "increment_by_pixels")
                .addAttribute(R.attr.rangeStep, "40dp"));
        assertEquals(StepMode.INCREMENT_BY_PIXELS, plot.getRangeStepModel().getMode());
        assertEquals(40.0, plot.getRangeStepModel().getValue(), 0);
    }

    // ---- int|float|dimension value handling ----

    @Test
    public void sizeValue_acceptsIntFloatAndDimension() {
        assertEquals(50f, plot(attrs().addAttribute(R.attr.domainTitleHeight, "50"))
                .getDomainTitle().getSize().getHeight().getValue(), 0);
        assertEquals(12.5f, plot(attrs().addAttribute(R.attr.domainTitleHeight, "12.5"))
                .getDomainTitle().getSize().getHeight().getValue(), 0);
        assertEquals(33f, plot(attrs().addAttribute(R.attr.domainTitleHeight, "33px"))
                .getDomainTitle().getSize().getHeight().getValue(), 0);
        assertEquals(34f, plot(attrs().addAttribute(R.attr.domainTitleHeight, "34dp"))
                .getDomainTitle().getSize().getHeight().getValue(), 0);
    }

    @Test
    public void relativeModeWithOutOfRangeValue_throws() {
        try {
            plot(attrs()
                    .addAttribute(R.attr.graphWidthMode, "relative")
                    .addAttribute(R.attr.graphWidth, "150px"));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    // ---- helpers ----

    private static void assertLineLabelStyle(XYGraphWidget.LineLabelStyle style, Paint.Align align,
            float rotation, float textSize, int color) {
        assertEquals(align, style.getPaint().getTextAlign());
        assertEquals(rotation, style.getRotation(), 0);
        assertEquals(textSize, style.getPaint().getTextSize(), 0);
        assertEquals(color, style.getPaint().getColor());
    }

    private static void assertSize(Size size, float height, SizeMode heightMode, float width, SizeMode widthMode) {
        assertEquals(height, size.getHeight().getValue(), 0);
        assertEquals(heightMode, size.getHeight().getLayoutType());
        assertEquals(width, size.getWidth().getValue(), 0);
        assertEquals(widthMode, size.getWidth().getLayoutType());
    }

    private static void assertPosition(PositionMetrics metrics, float x, HorizontalPositioning hp,
            float y, VerticalPositioning vp, Anchor anchor) {
        assertEquals(x, metrics.getXPositionMetric().getValue(), 0);
        assertEquals(hp, metrics.getXPositionMetric().getLayoutType());
        assertEquals(y, metrics.getYPositionMetric().getValue(), 0);
        assertEquals(vp, metrics.getYPositionMetric().getLayoutType());
        assertEquals(anchor, metrics.getAnchor());
    }

    private static void assertPositionUnchanged(Widget expected, Widget actual) {
        assertPosition(actual.getPositionMetrics(),
                expected.getPositionMetrics().getXPositionMetric().getValue(),
                expected.getPositionMetrics().getXPositionMetric().getLayoutType(),
                expected.getPositionMetrics().getYPositionMetric().getValue(),
                expected.getPositionMetrics().getYPositionMetric().getLayoutType(),
                expected.getAnchor());
    }

    /** Size, position, anchor, visibility and rotation of actual match those of expected. */
    private static void assertWidgetUnchanged(Widget expected, Widget actual) {
        assertSize(actual.getSize(),
                expected.getSize().getHeight().getValue(), expected.getSize().getHeight().getLayoutType(),
                expected.getSize().getWidth().getValue(), expected.getSize().getWidth().getLayoutType());
        assertPositionUnchanged(expected, actual);
        assertEquals(expected.isVisible(), actual.isVisible());
        assertEquals(expected.getRotation(), actual.getRotation());
    }

    private static void assertTextLabelUnchanged(com.androidplot.ui.widget.TextLabelWidget expected,
            com.androidplot.ui.widget.TextLabelWidget actual) {
        assertWidgetUnchanged(expected, actual);
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getLabelPaint().getTextSize(), actual.getLabelPaint().getTextSize(), 0);
        assertEquals(expected.getLabelPaint().getColor(), actual.getLabelPaint().getColor());
    }
}
