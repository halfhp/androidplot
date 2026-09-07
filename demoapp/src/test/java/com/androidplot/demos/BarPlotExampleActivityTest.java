// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class BarPlotExampleActivityTest {

    private static final String NO_SELECTION_TXT = "Touch bar to select.";

    private ActivityController<BarPlotExampleActivity> controller;
    private BarPlotExampleActivity activity;
    private XYPlot plot;

    @Before
    public void setUp() {
        controller = DemoAppTest.launch(BarPlotExampleActivity.class);
        activity = controller.get();
        plot = activity.findViewById(R.id.plot);
    }

    @After
    public void tearDown() {
        DemoAppTest.finish(controller);
        DemoAppTest.assertNoLoggedErrors();
    }

    @Test
    public void initialState() {
        assertEquals(2, plot.getRegistry().getSeriesList().size());
        assertEquals(10, plot.getRegistry().getSeriesList().get(0).size());
        assertEquals(BarRenderer.BarOrientation.OVERLAID, barRenderer().getBarOrientation());
        assertEquals(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, barRenderer().getBarGroupWidthMode());
        assertEquals(50, barRenderer().getBarGroupWidth(), 0.001f);
        assertEquals(NO_SELECTION_TXT, selectionWidget().getText());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.sbFixed).getVisibility());
        assertEquals(View.INVISIBLE, activity.findViewById(R.id.sbVariable).getVisibility());
    }

    @Test
    public void seriesSizeSpinner_changesSeriesSize() {
        Spinner spinner = activity.findViewById(R.id.spSeriesSize);
        assertEquals("TEN", spinner.getSelectedItem().toString());

        select(spinner, 2);
        assertEquals("SIXTY", spinner.getSelectedItem().toString());
        for (XYSeries series : plot.getRegistry().getSeriesList()) {
            assertEquals(60, series.size());
        }
        DemoAppTest.draw(plot);

        select(spinner, 1);
        for (XYSeries series : plot.getRegistry().getSeriesList()) {
            assertEquals(20, series.size());
        }
        DemoAppTest.draw(plot);
    }

    @Test
    public void renderStyleSpinner_changesBarOrientation() {
        Spinner spinner = activity.findViewById(R.id.spRenderStyle);
        for (BarRenderer.BarOrientation orientation : BarRenderer.BarOrientation.values()) {
            select(spinner, orientation.ordinal());
            assertEquals(orientation, barRenderer().getBarOrientation());
            // stacked bars need more head room
            assertEquals(orientation == BarRenderer.BarOrientation.STACKED ? 15 : 0,
                    plot.getInnerLimits().getMaxY().intValue());
            DemoAppTest.draw(plot);
        }
    }

    @Test
    public void widthStyleSpinner_changesWidthModeAndSeekBars() {
        Spinner spinner = activity.findViewById(R.id.spWidthStyle);
        SeekBar fixed = activity.findViewById(R.id.sbFixed);
        SeekBar variable = activity.findViewById(R.id.sbVariable);

        select(spinner, BarRenderer.BarGroupWidthMode.FIXED_GAP.ordinal());
        assertEquals(BarRenderer.BarGroupWidthMode.FIXED_GAP, barRenderer().getBarGroupWidthMode());
        assertEquals(variable.getProgress(), barRenderer().getBarGroupWidth(), 0.001f);
        assertEquals(View.INVISIBLE, fixed.getVisibility());
        assertEquals(View.VISIBLE, variable.getVisibility());
        DemoAppTest.draw(plot);

        variable.setProgress(7);
        DemoAppTest.idle();
        assertEquals(7, barRenderer().getBarGroupWidth(), 0.001f);

        select(spinner, BarRenderer.BarGroupWidthMode.FIXED_WIDTH.ordinal());
        assertEquals(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, barRenderer().getBarGroupWidthMode());
        assertEquals(View.VISIBLE, fixed.getVisibility());
        assertEquals(View.INVISIBLE, variable.getVisibility());

        fixed.setProgress(20);
        DemoAppTest.idle();
        assertEquals(20, barRenderer().getBarGroupWidth(), 0.001f);
        DemoAppTest.draw(plot);
    }

    @Test
    public void seriesCheckBoxes_addAndRemoveSeries() {
        CheckBox us = activity.findViewById(R.id.s1CheckBox);
        CheckBox them = activity.findViewById(R.id.s2CheckBox);

        us.performClick();
        DemoAppTest.idle();
        assertEquals(1, plot.getRegistry().getSeriesList().size());
        assertEquals("Them", plot.getRegistry().getSeriesList().get(0).getTitle());
        DemoAppTest.draw(plot);

        them.performClick();
        DemoAppTest.idle();
        assertEquals(0, plot.getRegistry().getSeriesList().size());
        DemoAppTest.draw(plot);

        us.performClick();
        them.performClick();
        DemoAppTest.idle();
        assertEquals(2, plot.getRegistry().getSeriesList().size());
        DemoAppTest.draw(plot);
    }

    @Test
    public void tappingTheGraph_selectsABar() {
        // the graph's grid rect, which screen-to-series conversion needs, is computed on the
        // first draw; on a device that always happens before the user can tap
        DemoAppTest.draw(plot);
        RectF grid = plot.getGraph().getGridRect();
        assertNotNull("the plot must be drawn for touch handling", grid);
        assertTrue(grid.width() > 0 && grid.height() > 0);

        // the domain is fixed to -1..10 for 10 bars, so the middle of the grid is over bar 4 or 5
        PointF onBar = new PointF(grid.centerX(), grid.centerY());
        assertTrue(plot.containsPoint(onBar));
        DemoAppTest.tap(plot, onBar);
        String text = selectionWidget().getText();
        assertTrue(text, text.startsWith("Selected: "));
        assertTrue(text, text.contains(" Value: "));
        assertTrue(text, text.contains("Us") || text.contains("Them"));
        assertTrue(text, text.equals("Selected: Us Value: 7") || text.equals("Selected: Them Value: 2")
                || text.equals("Selected: Us Value: 4") || text.equals("Selected: Them Value: 0"));
        DemoAppTest.draw(plot);

        // a tap outside the graph area clears the selection
        PointF offGraph = new PointF(1, 1);
        assertFalse(plot.containsPoint(offGraph));
        DemoAppTest.tap(plot, offGraph);
        assertEquals(NO_SELECTION_TXT, selectionWidget().getText());
        DemoAppTest.draw(plot);
    }

    /**
     * Selects a spinner item the way the framework does: the selection is applied and the
     * listener notified during the next layout pass.
     */
    private void select(Spinner spinner, int position) {
        spinner.setSelection(position);
        DemoAppTest.layoutPass(activity);
        assertEquals(position, spinner.getSelectedItemPosition());
    }

    private BarRenderer<?> barRenderer() {
        for (SeriesRenderer<?, ?, ?> renderer : plot.getRendererList()) {
            if (renderer instanceof BarRenderer) {
                return (BarRenderer<?>) renderer;
            }
        }
        throw new AssertionError("no BarRenderer on the plot");
    }

    /** The extra label the example adds to the plot: the only TextLabelWidget that isn't a title. */
    private TextLabelWidget selectionWidget() {
        for (Widget widget : plot.getLayoutManager().elements()) {
            if (widget instanceof TextLabelWidget && widget != plot.getTitle()
                    && widget != plot.getDomainTitle() && widget != plot.getRangeTitle()) {
                return (TextLabelWidget) widget;
            }
        }
        throw new AssertionError("no selection widget on the plot");
    }
}
