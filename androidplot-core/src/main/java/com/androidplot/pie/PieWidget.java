// SPDX-License-Identifier: Apache-2.0

package com.androidplot.pie;

import android.graphics.*;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.RenderStack;

/**
 * Visualizes data as a pie chart.
 */
public class PieWidget extends Widget {

    private PieChart pieChart;
    private RenderStack<? extends Segment, ? extends SegmentFormatter> renderStack;

    public PieWidget(LayoutManager layoutManager, PieChart pieChart, Size metrics) {
        super(layoutManager, metrics);
        this.pieChart = pieChart;
        renderStack = new RenderStack(pieChart);
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) {
        renderStack.sync();
        for(RenderStack.StackElement thisElement : renderStack.getElements()) {
            if(thisElement.isEnabled()) {
                pieChart.getRenderer(thisElement.get().getFormatter().getRendererClass()).
                        render(canvas, widgetRect, thisElement.get(), renderStack);
            }
        }
    }
}
