package com.androidplot.ui.widget;

import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;

public class UserTextLabelWidget extends TextLabelWidget {

    private String label;
    public UserTextLabelWidget(String label, SizeMetrics sizeMetrics, TextOrientationType orientationType) {
        super(sizeMetrics, orientationType);
        this.label = label;
    }
    @Override
    protected String getText() {
        return label;
    }
}
