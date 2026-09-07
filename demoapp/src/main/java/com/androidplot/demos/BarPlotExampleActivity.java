// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.androidplot.Region;
import com.androidplot.ui.*;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.*;
import com.androidplot.xy.*;

/**
 * An example of a Bar Plot.
 */
public class BarPlotExampleActivity extends Activity {

    private static final String NO_SELECTION_TXT = "Touch bar to select.";

    // y-values to plot; sample() cycles these to produce 10, 20 or 60 elements:
    private static final Number[] SERIES1_PATTERN =
            {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    private static final Number[] SERIES2_PATTERN =
            {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};

    private enum SeriesSize {
        TEN(10, 2),
        TWENTY(20, 4),
        SIXTY(60, 6);

        final int count;
        final int domainStep;

        SeriesSize(int count, int domainStep) {
            this.count = count;
            this.domainStep = domainStep;
        }
    }

    private record Selection(int index, XYSeries series) {}

    private XYPlot plot;

    private CheckBox series1CheckBox;
    private CheckBox series2CheckBox;
    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
    private SeekBar sbFixedWidth, sbVariableWidth;

    private MyBarFormatter formatter1;
    private MyBarFormatter formatter2;
    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Selection selection;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_plot_example);
        plot = findViewById(R.id.plot);

        // MyBarFormatter/MyBarRenderer below swap in selectionFormatter for the tapped bar
        formatter1 = new MyBarFormatter(Color.rgb(100, 150, 100), Color.LTGRAY);
        formatter1.setMarginLeft(PixelUtils.dpToPix(1));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.LTGRAY);
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(1));
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        // TextLabelWidget: an extra widget positioned on the plot via LayoutManager
        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new Size(
                        PixelUtils.dpToPix(100), SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeMode.ABSOLUTE),
                TextOrientation.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, HorizontalPositioning.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.TOP_MIDDLE);
        selectionWidget.pack();

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        plot.setLinesPerDomainLabel(2);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new NumberFormat() {
                    @Override
                    public StringBuffer format(double value, StringBuffer buffer,
                            FieldPosition field) {
                        int year = (int) (value + 0.5d) / 12;
                        int month = (int) ((value + 0.5d) % 12);
                        return new StringBuffer(DateFormatSymbols.getInstance()
                                .getShortMonths()[month] + " '0" + year);
                    }

                    @Override
                    public StringBuffer format(long value, StringBuffer buffer,
                            FieldPosition field) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }

                    @Override
                    public Number parse(String string, ParsePosition position) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }
                });

        plot.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
            }
            return true;
        });

        bindControls();
        updatePlot();
    }

    private void updatePlot() {
        plot.clear();

        // Setup our Series with the selected number of elements
        SeriesSize seriesSize = (SeriesSize) spSeriesSize.getSelectedItem();
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(sample(SERIES1_PATTERN, seriesSize.count)),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Us");
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(sample(SERIES2_PATTERN, seriesSize.count)),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        plot.setDomainBoundaries(-1, series1.size(), BoundaryMode.FIXED);
        plot.setRangeUpperBoundary(
                SeriesUtils.minMax(series1, series2).
                        getMaxY().doubleValue() + 1, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, seriesSize.domainStep);

        if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
        if (series2CheckBox.isChecked()) plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        BarRenderer.BarGroupWidthMode barGroupWidthMode
                = (BarRenderer.BarGroupWidthMode) spWidthStyle.getSelectedItem();
        boolean fixedWidth = barGroupWidthMode == BarRenderer.BarGroupWidthMode.FIXED_WIDTH;
        sbFixedWidth.setVisibility(fixedWidth ? View.VISIBLE : View.INVISIBLE);
        sbVariableWidth.setVisibility(fixedWidth ? View.INVISIBLE : View.VISIBLE);

        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        renderer.setBarOrientation((BarRenderer.BarOrientation) spRenderStyle.getSelectedItem());
        renderer.setBarGroupWidth(barGroupWidthMode,
                fixedWidth ? sbFixedWidth.getProgress() : sbVariableWidth.getProgress());

        if (BarRenderer.BarOrientation.STACKED.equals(spRenderStyle.getSelectedItem())) {
            plot.getInnerLimits().setMaxY(15);
        } else {
            plot.getInnerLimits().setMaxY(0);
        }

        plot.redraw();
    }

    // the first n values of pattern, repeating it when n is larger than the pattern
    private static Number[] sample(Number[] pattern, int n) {
        Number[] result = new Number[n];
        for (int i = 0; i < n; i++) {
            result[i] = pattern[i % pattern.length];
        }
        return result;
    }

    private void onPlotClicked(PointF point) {
        // a press outside the graph area deselects.  containsPoint accounts
        // for margins and padding as well.
        selection = null;
        if (plot.containsPoint(point.x, point.y)) {
            Number x = plot.screenToSeriesX(point.x);
            Number y = plot.screenToSeriesY(point.y);
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (SeriesBundle<XYSeries, ? extends XYSeriesFormatter> sfPair : plot
                    .getRegistry().getSeriesAndFormatterList()) {
                XYSeries series = sfPair.getSeries();
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                Region.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                Region.measure(y, thisY).doubleValue();
                        if (selection == null || thisXDistance < xDistance ||
                                (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue())) {
                            selection = new Selection(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.series().getTitle() +
                    " Value: " + selection.series().getY(selection.index()));
        }
        plot.redraw();
    }

    private void bindControls() {
        series1CheckBox = findViewById(R.id.s1CheckBox);
        series1CheckBox.setOnCheckedChangeListener((cb, checked) -> updatePlot());
        series2CheckBox = findViewById(R.id.s2CheckBox);
        series2CheckBox.setOnCheckedChangeListener((cb, checked) -> updatePlot());

        spRenderStyle = findViewById(R.id.spRenderStyle);
        bindSpinner(spRenderStyle, BarRenderer.BarOrientation.values(),
                BarRenderer.BarOrientation.OVERLAID);
        spWidthStyle = findViewById(R.id.spWidthStyle);
        bindSpinner(spWidthStyle, BarRenderer.BarGroupWidthMode.values(),
                BarRenderer.BarGroupWidthMode.FIXED_WIDTH);
        spSeriesSize = findViewById(R.id.spSeriesSize);
        bindSpinner(spSeriesSize, SeriesSize.values(), SeriesSize.TEN);

        sbFixedWidth = findViewById(R.id.sbFixed);
        bindSeekBar(sbFixedWidth, 50);
        sbVariableWidth = findViewById(R.id.sbVariable);
        bindSeekBar(sbVariableWidth, 1);
    }

    private <T extends Enum<T>> void bindSpinner(Spinner spinner, T[] values, T initial) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(initial.ordinal());
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void bindSeekBar(SeekBar seekBar, int progress) {
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    private class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * getFormatter(int index, XYSeries series) is the per-point formatter hook;
         * overriding it lets us inject our special selection formatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return selectionFormatter for the selected bar, the series' own formatter otherwise.
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.series() == series &&
                    selection.index() == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}
