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

package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region;
import com.androidplot.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.Plot;

import java.util.ArrayList;
import java.util.List;

public abstract class SeriesRenderer
        <PlotType extends Plot, SeriesType extends Series, SeriesFormatterType extends Formatter> {

    private PlotType plot;

    public SeriesRenderer(PlotType plot) {
        this.plot = plot;
    }

    public PlotType getPlot() {
        return plot;
    }

    public void setPlot(PlotType plot) {
        this.plot = plot;
    }

    public SeriesFormatterType getFormatter(SeriesType series) {
        return (SeriesFormatterType) plot.getFormatter(series, getClass());
    }

    /**
     *
     * @param canvas
     * @param plotArea
     * @param sfPair The series / formatter pair to be rendered
     * @throws PlotRenderException
     */
    public void render(Canvas canvas, RectF plotArea, SeriesBundle<SeriesType,
                SeriesFormatterType> sfPair, RenderStack stack) throws PlotRenderException {
        onRender(canvas, plotArea, sfPair.getSeries(), sfPair.getFormatter(), stack);
    }

    /**
     *
     * @param canvas
     * @param plotArea
     * @param series The series to be rendered
     * @param formatter The getFormatter that should be used to render the series
     * @param stack Ordered list of all series being renderered.  May be manipulated by the Renderer
     *              to gain effect.
     * @throws PlotRenderException
     */
    protected abstract void onRender(Canvas canvas, RectF plotArea, SeriesType series,
                                  SeriesFormatterType formatter, RenderStack stack) throws PlotRenderException;

    /**
     * Draw the legend icon in the rect passed in.
     * @param canvas
     * @param rect
     */
    protected abstract void doDrawLegendIcon(Canvas canvas, RectF rect, SeriesFormatterType formatter);

    public void drawSeriesLegendIcon(Canvas canvas, RectF rect, SeriesFormatterType formatter) {
        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(rect, Region.Op.INTERSECT);
            doDrawLegendIcon(canvas, rect, formatter);
        } finally {
            canvas.restore();
        }
    }

    /**
     *
     * @return A List of all {@link SeriesBundle} instances currently associated
     * with this Renderer.
     */
    public List<SeriesBundle<SeriesType, ? extends SeriesFormatterType>> getSeriesAndFormatterList() {
        List<SeriesBundle<SeriesType, ? extends SeriesFormatterType>> results = new ArrayList<>();
        List<SeriesBundle> sfList = getPlot().getRegistry().getSeriesAndFormatterList();
        for(SeriesBundle<SeriesType, ? extends SeriesFormatterType> thisPair : sfList) {
            if(thisPair.rendersWith(this)) {
                results.add(thisPair);
            }
        }
        return results;
    }

    /**
     *
     * @return
     * @since 0.9.7
     */
    public List<SeriesType> getSeriesList() {
        List<SeriesType> results = new ArrayList<>();
        List<SeriesBundle> sfList = getPlot().getRegistry().getSeriesAndFormatterList();

        for(SeriesBundle<SeriesType, ? extends SeriesFormatterType> thisPair : sfList) {
            if(thisPair.rendersWith(this)) {
                results.add(thisPair.getSeries());
            }
        }
        return results;
    }
}
