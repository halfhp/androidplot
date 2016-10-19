/*
 * Copyright 2016 AndroidPlot.com
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

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesBundle;

import java.util.List;

/**
 * Renders data to an XYPlot that potentially contains more than a single yVal per index, or in other
 * words data with potentially more than two dimensions.
 * Examples of such data are bar plot groups and candlestick charts.
 * @since 0.9.7
 */
public abstract class GroupRenderer<FormatterType extends XYSeriesFormatter<XYRegionFormatter>>
        extends XYSeriesRenderer<XYSeries, FormatterType>  {

    private static final String TAG = GroupRenderer.class.getName();

    public GroupRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void onRender(Canvas canvas, RectF plotArea, XYSeries series,
                         FormatterType formatter, RenderStack stack) throws PlotRenderException {


        // get all the data  associated with this renderer:
        List<SeriesBundle<XYSeries, ? extends FormatterType>> sfList = getSeriesAndFormatterList();

        // no data to render so exit:
        if(sfList == null) {
            return;
        }

        int seriesLength = sfList.get(0).getSeries().size();

        // make sure all associated series are the same length:
        for(int i = 1; i < sfList.size(); i++) {
            if(sfList.get(i).getSeries().size() != seriesLength) {
                // series sizes are irregular so don't try to render:
                Log.w(TAG, getClass() + ": " + "not all associated series are of same size.");
                return;
            }
        }


        // this renderer uses special element-by-element z-indexing that results in
        // all series associated with the renderer being rendered in a single pass, so
        // we need to exclude the rest of the series on the render stack from being redrawn later:
        stack.disable(getClass());

        onRender(canvas, plotArea, sfList, seriesLength, stack);
    }

    /**
     *
     * @param canvas
     * @param plotArea
     * @param sfList
     * @param stack
     */
    public abstract void  onRender(Canvas canvas, RectF plotArea, List<SeriesBundle<XYSeries,
                ? extends FormatterType>> sfList, int size, RenderStack stack);
}
