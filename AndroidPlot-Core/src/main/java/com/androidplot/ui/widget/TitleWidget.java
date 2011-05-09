package com.androidplot.ui.widget;

import com.androidplot.Plot;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;

public class TitleWidget extends TextLabelWidget {

    private Plot plot;

    public TitleWidget(Plot plot, SizeMetrics sizeMetrics, TextOrientationType orientationType) {
        super(sizeMetrics, orientationType);
        this.plot = plot;
        getLabelPaint().setTextSize(14);

    }
    @Override
    protected String getText() {
        return plot.getTitle();
    }
}
