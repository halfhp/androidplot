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

package com.androidplot.pie;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.androidplot.*;
import com.androidplot.ui.*;
import com.androidplot.util.AttrUtils;
import com.androidplot.util.PixelUtils;

/**
 * Basic representation of a Pie Chart that displays a title and pie widget.
 */
public class PieChart extends Plot<Segment, SegmentFormatter, PieRenderer,
        SegmentBundle, SegmentRegistry> {

    private static final int DEFAULT_PIE_WIDGET_H_DP = 18;
    private static final int DEFAULT_PIE_WIDGET_W_DP = 10;

    private static final int DEFAULT_PIE_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_PIE_WIDGET_X_OFFSET_DP = 0;

    private static final int DEFAULT_LEGEND_WIDGET_H_DP = 30;
    private static final int DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP = 18;
    private static final int DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_LEGEND_WIDGET_X_OFFSET_DP = 40;

    private static final int DEFAULT_PADDING_DP = 5;

    @SuppressWarnings("FieldCanBeLocal")
    private PieWidget pie;

    private PieLegendWidget legend;

    @Override
    protected SegmentRegistry getRegistryInstance() {
        return new SegmentRegistry();
    }

    public PieChart(Context context, String title) {
        super(context, title);
    }

    public PieChart(Context context, String title, RenderMode mode) {
        super(context, title, mode);
    }

    public PieChart(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected void onPreInit() {
        pie = new PieWidget(
                getLayoutManager(),
                this,
                new Size(
                        PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_H_DP),
                        SizeMode.FILL,
                        PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_W_DP),
                        SizeMode.FILL));

        pie.position(
                PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_CENTER,
                Anchor.CENTER);

        legend = new PieLegendWidget(
                getLayoutManager(),
                this,
                new Size(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_H_DP),
                        SizeMode.ABSOLUTE, 0.5f, SizeMode.RELATIVE),
                new DynamicTableModel(0, 1),
                new Size(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeMode.ABSOLUTE));

        legend.position(
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.RIGHT_BOTTOM);

        legend.setVisible(false);

        final float padding = PixelUtils.dpToPix(DEFAULT_PADDING_DP);
        pie.setPadding(padding, padding, padding, padding);
    }

    @Override
    protected void processAttrs(TypedArray attrs) {

        // borderPaint
        AttrUtils.configureLinePaint(attrs, getBorderPaint(),
                R.styleable.pie_PieChart_pieBorderColor, R.styleable.pie_PieChart_pieBorderThickness);
    }

    public void setPie(PieWidget pie) {
        this.pie = pie;
    }

    public PieWidget getPie() {
        return pie;
    }

    public void addSegment(Segment segment, SegmentFormatter formatter) {
        addSeries(segment, formatter);
    }

    public void removeSegment(Segment segment) {
        removeSeries(segment);
    }

    public PieLegendWidget getLegend() {
        return legend;
    }

    public void setLegend(PieLegendWidget legend) {
        this.legend = legend;
    }
}
