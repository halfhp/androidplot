package com.androidplot.ui.layout;

import android.graphics.RectF;

import java.util.Iterator;

public abstract class TableModel {
    private TableOrder order;

    protected TableModel(TableOrder order) {
        setOrder(order);
    }

    public abstract Iterator<RectF> getIterator(RectF tableRect, int totalElements);

    //public abstract RectF getCellRect(RectF tableRect, int numElements);

    public TableOrder getOrder() {
        return order;
    }

    public void setOrder(TableOrder order) {
        this.order = order;
    }

    public enum Axis {
        ROW,
        COLUMN
    }

    public enum CellSizingMethod {
        FIXED,
        FILL
    }
}
