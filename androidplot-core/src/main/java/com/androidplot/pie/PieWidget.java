/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.pie;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
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
    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        renderStack.sync();
        for(RenderStack.StackElement thisElement : renderStack.getElements()) {
            if(thisElement.isEnabled()) {
                pieChart.getRenderer(thisElement.get().getFormatter().getRendererClass()).
                        render(canvas, widgetRect, thisElement.get(), renderStack);
            }
        }
    }
}
