package com.androidplot.ui.widget;

import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.xy.XYPlot;

public class RangeLabelWidget extends TextLabelWidget {

    private XYPlot plot;

    public RangeLabelWidget(XYPlot plot, SizeMetrics sizeMetrics, TextOrientationType orientationType) {
        super(sizeMetrics, orientationType);
        this.plot = plot;
    }
    @Override
    protected String getText() {
        return plot.getRangeLabel();
    }
}
