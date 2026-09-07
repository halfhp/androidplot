// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.ui.SeriesRenderer;

public class StepFormatter extends LineAndPointFormatter {

    /**
     * Should only be used in conjunction with calls to configure()...
     */
    public StepFormatter() {}

    public StepFormatter(Integer lineColor, Integer fillColor) {
        initLinePaint(lineColor);
        initFillPaint(fillColor);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return StepRenderer.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(XYPlot plot) {
        return new StepRenderer(plot);
    }

}