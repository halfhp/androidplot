package com.androidplot.xy;

import android.support.annotation.NonNull;

import com.androidplot.ui.widget.LegendItem;

public class XYLegendItem implements LegendItem {

    public enum Type {
        SERIES,
        REGION
    }

    public final Type type;
    public final Object item;
    private final String text;

    public XYLegendItem(@NonNull Type cellType, @NonNull Object item, @NonNull String text) {
        this.type = cellType;
        this.item = item;
        this.text = text;
    }

    @Override
    public String getTitle() {
        return this.text;
    }
}
