// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import androidx.annotation.NonNull;

public class PositionMetrics implements Comparable<PositionMetrics> {

    private HorizontalPosition horizontalPosition;
    private VerticalPosition verticalPosition;
    private Anchor anchor;
    private float layerDepth;

    public PositionMetrics(float x, HorizontalPositioning horizontalPositioning, float y, VerticalPositioning verticalPositioning, Anchor anchor) {
        setXPositionMetric(new HorizontalPosition(x, horizontalPositioning));
        setYPositionMetric(new VerticalPosition(y, verticalPositioning));
        setAnchor(anchor);

    }

    public VerticalPosition getYPositionMetric() {
        return verticalPosition;
    }

    public void setYPositionMetric(VerticalPosition verticalPosition) {
        this.verticalPosition = verticalPosition;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    @Override
    public int compareTo(@NonNull PositionMetrics o) {
        if(this.layerDepth < o.layerDepth) {
            return -1;
        } else if(this.layerDepth == o.layerDepth) {
            return 0;
        } else {
            return 1;
        }
    }

    public HorizontalPosition getXPositionMetric() {
        return horizontalPosition;
    }

    public void setXPositionMetric(HorizontalPosition horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }
}
