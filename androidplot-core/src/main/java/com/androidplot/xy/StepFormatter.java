// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.ui.SeriesRenderer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StepFormatter extends LineAndPointFormatter {

    /**
     * Should only be used in conjunction with calls to configure()...
     */
    public StepFormatter() {}

    public StepFormatter(@Nullable Integer lineColor, @Nullable Integer fillColor) {
        initLinePaint(lineColor);
        initFillPaint(fillColor);
    }

    @Override
    @NonNull
    public Class<? extends SeriesRenderer> getRendererClass() {
        return StepRenderer.class;
    }

    @Override
    @NonNull
    public SeriesRenderer doGetRendererInstance(@NonNull XYPlot plot) {
        return new StepRenderer(plot);
    }

}