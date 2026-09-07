// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;

import java.util.Iterator;
import androidx.annotation.NonNull;

public abstract class TableModel {
    private TableOrder order;

    protected TableModel(@NonNull TableOrder order) {
        setOrder(order);
    }

    @NonNull
    public abstract Iterator<RectF> getIterator(@NonNull RectF tableRect, int totalElements);

    //public abstract RectF getCellRect(RectF tableRect, int numElements);

    @NonNull
    public TableOrder getOrder() {
        return order;
    }

    public void setOrder(@NonNull TableOrder order) {
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
