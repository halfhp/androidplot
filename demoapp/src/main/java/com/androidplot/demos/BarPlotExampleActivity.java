/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private XYPlot plot;

    private CheckBox series1CheckBox;
    private CheckBox series2CheckBox;
    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
    private SeekBar sbFixedWidth, sbVariableWidth;

    private XYSeries series1;
    private XYSeries series2;

    private enum SeriesSize {
        TEN,
        TWENTY,
        SIXTY
    }

    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers10 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5};
    Number[] series2Numbers10 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4};
    Number[] series1Numbers20 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    Number[] series2Numbers20 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
    Number[] series1Numbers60 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    Number[] series2Numbers60 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
    Number[] series1Numbers = series1Numbers10;
    Number[] series2Numbers = series2Numbers10;

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        formatter1 = new MyBarFormatter(Color.rgb(100, 150, 100), Color.LTGRAY);
        formatter1.setMarginLeft(PixelUtils.dpToPix(1));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.LTGRAY);
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(1));
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

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

        // setup checkbox listers:
        series1CheckBox = (CheckBox) findViewById(R.id.s1CheckBox);
        series1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked(b);
            }
        });

        series2CheckBox = (CheckBox) findViewById(R.id.s2CheckBox);
        series2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS2CheckBoxClicked(b);
            }
        });

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

        spRenderStyle = (Spinner) findViewById(R.id.spRenderStyle);
        ArrayAdapter<BarRenderer.BarOrientation> adapter = new ArrayAdapter<BarRenderer.BarOrientation>(this,
                android.R.layout.simple_spinner_item, BarRenderer.BarOrientation
                .values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRenderStyle.setAdapter(adapter);
        spRenderStyle.setSelection(BarRenderer.BarOrientation.OVERLAID.ordinal());
        spRenderStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spWidthStyle = (Spinner) findViewById(R.id.spWidthStyle);
        ArrayAdapter<BarRenderer.BarGroupWidthMode> adapter1 = new ArrayAdapter<BarRenderer.BarGroupWidthMode>(
                this, android.R.layout.simple_spinner_item, BarRenderer.BarGroupWidthMode
                .values());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWidthStyle.setAdapter(adapter1);
        spWidthStyle.setSelection(BarRenderer.BarGroupWidthMode.FIXED_WIDTH.ordinal());
        spWidthStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (BarRenderer.BarGroupWidthMode.FIXED_WIDTH.equals(spWidthStyle.getSelectedItem())) {
                    sbFixedWidth.setVisibility(View.VISIBLE);
                    sbVariableWidth.setVisibility(View.INVISIBLE);
                } else {
                    sbFixedWidth.setVisibility(View.INVISIBLE);
                    sbVariableWidth.setVisibility(View.VISIBLE);
                }
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spSeriesSize = (Spinner) findViewById(R.id.spSeriesSize);
        ArrayAdapter<SeriesSize> adapter11 = new ArrayAdapter<SeriesSize>(this,
                android.R.layout.simple_spinner_item, SeriesSize.values());
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeriesSize.setAdapter(adapter11);
        spSeriesSize.setSelection(SeriesSize.TEN.ordinal());
        spSeriesSize.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final SeriesSize selectedSize = (SeriesSize) arg0.getSelectedItem();
                switch (selectedSize) {
                    case TEN:
                        series1Numbers = series1Numbers10;
                        series2Numbers = series2Numbers10;
                        break;
                    case TWENTY:
                        series1Numbers = series1Numbers20;
                        series2Numbers = series2Numbers20;
                        break;
                    case SIXTY:
                        series1Numbers = series1Numbers60;
                        series2Numbers = series2Numbers60;
                        break;
                    default:
                        break;
                }
                updatePlot(selectedSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        sbFixedWidth = (SeekBar) findViewById(R.id.sbFixed);
        sbFixedWidth.setProgress(50);
        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbVariableWidth = (SeekBar) findViewById(R.id.sbVariable);
        sbVariableWidth.setProgress(1);
        sbVariableWidth.setVisibility(View.INVISIBLE);
        sbVariableWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
        updatePlot();
    }

    private void updatePlot() {
        updatePlot(null);
    }

    private void updatePlot(SeriesSize seriesSize) {

        // Remove all current series from each plot
        plot.clear();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Us");
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        plot.setDomainBoundaries(-1, series1.size(), BoundaryMode.FIXED);
        plot.setRangeUpperBoundary(
                SeriesUtils.minMax(series1, series2).
                        getMaxY().doubleValue() + 1, BoundaryMode.FIXED);

        if(seriesSize != null) {
            switch(seriesSize) {
                case TEN:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 2);
                    break;
                case TWENTY:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 4);
                    break;
                case SIXTY:
                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 6);
                    break;
            }
        }

        // add a new series' to the xyplot:
        if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
        if (series2CheckBox.isChecked()) plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        renderer.setBarOrientation((BarRenderer.BarOrientation) spRenderStyle.getSelectedItem());
        final BarRenderer.BarGroupWidthMode barGroupWidthMode
                = (BarRenderer.BarGroupWidthMode) spWidthStyle.getSelectedItem();
        renderer.setBarGroupWidth(barGroupWidthMode,
                barGroupWidthMode == BarRenderer.BarGroupWidthMode.FIXED_WIDTH
                ? sbFixedWidth.getProgress() : sbVariableWidth.getProgress());

        if (BarRenderer.BarOrientation.STACKED.equals(spRenderStyle.getSelectedItem())) {
            plot.getInnerLimits().setMaxY(15);
        } else {
            plot.getInnerLimits().setMaxY(0);
        }

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well. 
        if (plot.containsPoint(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);

            selection = null;
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
                        if (selection == null) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }

    private void onS1CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series1, formatter1);
        } else {
            plot.removeSeries(series1);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series2, formatter2);
        } else {
            plot.removeSeries(series2);
        }
        plot.redraw();
    }

    class MyBarFormatter extends BarFormatter {

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

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection getFormatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}
