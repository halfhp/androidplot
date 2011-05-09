package com.androidplot.ui.widget;

import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.xy.XYPlot;

public class DomainLabelWidget extends TextLabelWidget {

    private XYPlot plot;
    
    public DomainLabelWidget(XYPlot plot, SizeMetrics sizeMetrics, TextOrientationType orientationType) {
        super(sizeMetrics, orientationType);
        this.plot = plot;
    }
    @Override
    protected String getText() {
        return plot.getDomainLabel();
    }
}
