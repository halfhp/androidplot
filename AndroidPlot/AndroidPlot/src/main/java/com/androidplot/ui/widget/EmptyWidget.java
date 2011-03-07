package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.layout.SizeMetrics;

public class EmptyWidget extends Widget {

    public EmptyWidget(SizeMetrics sizeMetrics) {
        super(sizeMetrics);
    }
    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        // nothing to do
    }
}
