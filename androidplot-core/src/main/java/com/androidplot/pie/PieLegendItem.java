package com.androidplot.pie;


import android.support.annotation.NonNull;

import com.androidplot.ui.widget.LegendItem;

/**
 * An item in a {@link PieLegendWidget} corresponding to a {@link Segment} in a {@link PieChart}.
 */
public class PieLegendItem implements LegendItem {

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
