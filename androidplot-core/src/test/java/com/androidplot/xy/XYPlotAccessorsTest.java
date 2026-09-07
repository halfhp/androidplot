// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;

import com.androidplot.R;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.widget.TextLabelWidget;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Accessors, convenience wrappers and the less travelled branches of {@link XYPlot}.
 */
public class XYPlotAccessorsTest extends AndroidplotTest {

    private static final float DELTA = 0.0001f;

    XYPlot plot;
    List<Integer> numList1;
    List<Integer> numList2;
    SimpleXYSeries series0To100;

    @Before
    public void setUp() {
        plot = new XYPlot(getContext(), "test");
        numList1 = Arrays.asList(0, 1, 3, 5, 10, 15, 25, 50, 75, 100); // 10 elements
        numList2 = Arrays.asList(-100, 0, 1, 3, 5, 10, 15, 25, 50, 75, 100, 200); // 12 elements
        series0To100 = new SimpleXYSeries(numList1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
    }

    /**
     * Populates the plot so that its bounds are fully defined (x: 0..9, y: 0..100) and gives the
     * graph a 100x100 grid rect so screen / series conversions are deterministic.
     */
    private void populate() {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.calculateMinMaxVals();
        plot.getGraph().setGridRect(new RectF(0, 0, 100, 100));
    }

    // ---- constructors ----

    @Test
    public void constructor_withAttrsAndDefStyle_buildsPlot() {
        XYPlot p = new XYPlot(getContext(), Robolectric.buildAttributeSet().build(), 0);
        assertNotNull(p.getGraph());
        assertNotNull(p.getLegend());
        assertNotNull(p.getDomainTitle());
        assertNotNull(p.getRangeTitle());
    }

    // ---- widget accessors ----

    @Test
    public void setLegend_roundTrips() {
        XYLegendWidget legend = new XYLegendWidget(plot.getLayoutManager(), plot,
                plot.getLegend().getSize(), new DynamicTableModel(0, 1),
                plot.getLegend().getIconSize());
        plot.setLegend(legend);
        assertSame(legend, plot.getLegend());
    }

    @Test
    public void setGraph_roundTrips() {
        XYGraphWidget graph = new XYGraphWidget(plot.getLayoutManager(), plot,
                plot.getGraph().getSize());
        plot.setGraph(graph);
        assertSame(graph, plot.getGraph());
    }

    @Test
    public void setDomainTitle_roundTrips() {
        TextLabelWidget title = new TextLabelWidget(plot.getLayoutManager(),
                plot.getDomainTitle().getSize());
        plot.setDomainTitle(title);
        assertSame(title, plot.getDomainTitle());
    }

    @Test
    public void setRangeTitle_roundTrips() {
        TextLabelWidget title = new TextLabelWidget(plot.getLayoutManager(),
                plot.getRangeTitle().getSize());
        plot.setRangeTitle(title);
        assertSame(title, plot.getRangeTitle());
    }

    @Test
    public void setDomainLabel_setsDomainTitleText() {
        plot.setDomainLabel("the domain");
        assertEquals("the domain", plot.getDomainTitle().getText());
    }

    @Test
    public void setRangeLabel_setsRangeTitleText() {
        plot.setRangeLabel("the range");
        assertEquals("the range", plot.getRangeTitle().getText());
    }

    @Test
    public void linesPerLabel_delegateToGraph() {
        plot.setLinesPerRangeLabel(3);
        assertEquals(3, plot.getLinesPerRangeLabel());
        assertEquals(3, plot.getGraph().getLinesPerRangeLabel());

        plot.setLinesPerDomainLabel(4);
        assertEquals(4, plot.getLinesPerDomainLabel());
        assertEquals(4, plot.getGraph().getLinesPerDomainLabel());
    }

    // ---- origin ----

    @Test
    public void getOrigin_reflectsDomainAndRangeOrigin() {
        populate();
        XYCoords origin = plot.getOrigin();
        assertNotNull(origin);
        assertEquals(plot.getDomainOrigin(), origin.x);
        assertEquals(plot.getRangeOrigin(), origin.y);
    }

    @Test(expected = NullPointerException.class)
    public void setUserDomainOrigin_null_throws() {
        plot.setUserDomainOrigin(null);
    }

    @Test(expected = NullPointerException.class)
    public void setUserRangeOrigin_null_throws() {
        plot.setUserRangeOrigin(null);
    }

    // ---- markers ----

    @Test
    public void addMarker_yValueMarker_rejectsDuplicates() {
        YValueMarker marker = new YValueMarker(5, "five");
        assertTrue(plot.addMarker(marker));
        assertFalse(plot.addMarker(marker));
        assertEquals(1, plot.getYValueMarkers().size());
    }

    @Test
    public void removeMarker_yValueMarker_returnsNullWhenAbsent() {
        YValueMarker marker = new YValueMarker(5, "five");
        assertNull(plot.removeMarker(marker));

        plot.addMarker(marker);
        assertSame(marker, plot.removeMarker(marker));
        assertTrue(plot.getYValueMarkers().isEmpty());
    }

    @Test
    public void addMarker_xValueMarker_rejectsDuplicates() {
        XValueMarker marker = new XValueMarker(5, "five");
        assertTrue(plot.addMarker(marker));
        assertFalse(plot.addMarker(marker));
        assertEquals(1, plot.getXValueMarkers().size());
    }

    @Test
    public void removeMarker_xValueMarker_returnsNullWhenAbsent() {
        XValueMarker marker = new XValueMarker(5, "five");
        assertNull(plot.removeMarker(marker));

        plot.addMarker(marker);
        assertSame(marker, plot.removeMarker(marker));
        assertTrue(plot.getXValueMarkers().isEmpty());
    }

    // ---- screen <-> series conversions (delegated to the graph widget) ----

    @Test
    public void containsPoint_checksGridRect() {
        populate();
        assertTrue(plot.containsPoint(50, 50));
        assertTrue(plot.containsPoint(new PointF(1, 1)));
        assertFalse(plot.containsPoint(150, 50));
        assertFalse(plot.containsPoint(new PointF(-1, 1)));
    }

    @Test
    public void containsPoint_noGridRect_isFalse() {
        plot.getGraph().setGridRect(null);
        assertFalse(plot.containsPoint(50, 50));
    }

    @Test
    public void setCursorPosition_setsGraphCursorPosition() {
        plot.setCursorPosition(11f, 22f);
        assertEquals(11f, plot.getGraph().getDomainCursorPosition(), DELTA);
        assertEquals(22f, plot.getGraph().getRangeCursorPosition(), DELTA);

        plot.setCursorPosition(new PointF(33f, 44f));
        assertEquals(33f, plot.getGraph().getDomainCursorPosition(), DELTA);
        assertEquals(44f, plot.getGraph().getRangeCursorPosition(), DELTA);
    }

    @Test
    public void screenToSeries_convertsPixelsToValues() {
        populate();
        // x runs 0..9 across 100px; y runs 0 (bottom) .. 100 (top)
        assertEquals(4.5, plot.screenToSeriesX(50).doubleValue(), DELTA);
        assertEquals(50.0, plot.screenToSeriesY(50).doubleValue(), DELTA);
        assertEquals(100.0, plot.screenToSeriesY(0).doubleValue(), DELTA);
        assertEquals(0.0, plot.screenToSeriesY(100).doubleValue(), DELTA);

        XYCoords coords = plot.screentoSeries(new PointF(100, 100));
        assertEquals(9.0, coords.x.doubleValue(), DELTA);
        assertEquals(0.0, coords.y.doubleValue(), DELTA);
    }

    @Test
    public void seriesToScreen_convertsValuesToPixels() {
        populate();
        assertEquals(50f, plot.seriesToScreenX(4.5), DELTA);
        assertEquals(0f, plot.seriesToScreenY(100), DELTA);
        assertEquals(100f, plot.seriesToScreenY(0), DELTA);

        PointF point = plot.seriesToScreen(new XYCoords(9, 100));
        assertEquals(100f, point.x, DELTA);
        assertEquals(0f, point.y, DELTA);
    }

    @Test
    public void deprecatedGetXValGetYVal_matchScreenToSeries() {
        populate();
        assertEquals(plot.screenToSeriesX(50), plot.getXVal(50));
        assertEquals(plot.screenToSeriesY(50), plot.getYVal(50));
        assertEquals(plot.screenToSeriesX(50), plot.getXVal(new PointF(50, 0)));
        assertEquals(plot.screenToSeriesY(50), plot.getYVal(new PointF(0, 50)));
    }

    @Test
    public void conversions_undefinedBounds_returnNull() {
        // no series and no calculateMinMaxVals: bounds are not defined yet
        plot.getGraph().setGridRect(new RectF(0, 0, 100, 100));
        assertFalse(plot.getBounds().isFullyDefined());
        assertNull(plot.screenToSeriesX(50));
        assertNull(plot.screenToSeriesY(50));
        assertNull(plot.screentoSeries(new PointF(50, 50)));
        assertNull(plot.seriesToScreen(new XYCoords(1, 1)));
    }

    // ---- origin model, SHRINK: previous bounds win over a wider data set ----

    @Test
    public void domainOriginShrinkMode_keepsPreviousBoundsWhenDataGrows() {
        plot.addSeries(series0To100, new LineAndPointFormatter());
        plot.centerOnDomainOrigin(5, null, BoundaryMode.SHRINK);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);

        // widen the data set and recalculate: in SHRINK mode the boundaries may only contract
        series0To100.setModel(numList2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        plot.calculateMinMaxVals();

        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);
    }

    // ---- editor preview (onAfterConfig while isInEditMode) ----

    private XYPlot editModePlot(String previewMode) {
        XYPlot p = spy(new XYPlot(getContext(), previewMode == null
                ? Robolectric.buildAttributeSet().build()
                : Robolectric.buildAttributeSet()
                        .addAttribute(R.attr.previewMode, previewMode).build()));
        doReturn(true).when(p).isInEditMode();
        return p;
    }

    @Test
    public void onAfterConfig_editMode_defaultPreview_addsLineAndPointSeries() {
        XYPlot p = editModePlot(null);
        assertTrue(p.getRegistry().isEmpty());
        p.onAfterConfig();
        assertEquals(3, p.getRegistry().size());
        assertEquals(1, p.getRendererList().size());
        assertTrue(p.getRendererList().get(0) instanceof LineAndPointRenderer);
    }

    @Test
    public void onAfterConfig_editMode_candlestickPreview_addsCandlestickSeries() {
        XYPlot p = editModePlot("candlestick");
        p.onAfterConfig();
        assertFalse(p.getRegistry().isEmpty());
        assertEquals(1, p.getRendererList().size());
        assertTrue(p.getRendererList().get(0) instanceof CandlestickRenderer);
    }

    @Test
    public void onAfterConfig_editMode_barPreview_isNotSupported() {
        XYPlot p = editModePlot("bar");
        try {
            p.onAfterConfig();
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        assertTrue(p.getRegistry().isEmpty());
    }

    @Test
    public void onAfterConfig_notInEditMode_addsNothing() {
        XYPlot p = new XYPlot(getContext(), Robolectric.buildAttributeSet().build());
        p.onAfterConfig();
        assertTrue(p.getRegistry().isEmpty());
    }
}
