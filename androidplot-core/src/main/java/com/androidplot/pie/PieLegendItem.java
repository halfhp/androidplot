package com.androidplot.pie;


import android.support.annotation.NonNull;

import com.androidplot.ui.widget.LegendWidget;

public class PieLegendItem implements LegendWidget.Item {

    public SegmentFormatter formatter;

    public Segment segment;

    public PieLegendItem(@NonNull Segment segment, @NonNull SegmentFormatter formatter) {
        this.segment = segment;
        this.formatter = formatter;
    }

    @Override
    public String getTitle() {
        return segment.getTitle();
    }
}
