// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region;
import com.androidplot.Series;
import com.androidplot.Plot;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

public abstract class SeriesRenderer
        <PlotType extends Plot, SeriesType extends Series, SeriesFormatterType extends Formatter> {

    private PlotType plot;

    public SeriesRenderer(@NonNull PlotType plot) {
        this.plot = plot;
    }

    @NonNull
    public PlotType getPlot() {
        return plot;
    }

    public void setPlot(@NonNull PlotType plot) {
        this.plot = plot;
    }

    @NonNull
    public SeriesFormatterType getFormatter(@NonNull SeriesType series) {
        return (SeriesFormatterType) plot.getFormatter(series, getClass());
    }

    /**
     *
     * @param canvas
     * @param plotArea
     * @param sfPair The series / formatter pair to be rendered
     */
    public void render(@NonNull Canvas canvas, @NonNull RectF plotArea, @NonNull SeriesBundle<SeriesType,
                SeriesFormatterType> sfPair, @NonNull RenderStack stack) {
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
     */
    protected abstract void onRender(@NonNull Canvas canvas, @NonNull RectF plotArea, @NonNull SeriesType series,
                                  @NonNull SeriesFormatterType formatter, @NonNull RenderStack stack);

    /**
     * Draw the legend icon in the rect passed in.
     * @param canvas
     * @param rect
     */
    protected abstract void doDrawLegendIcon(@NonNull Canvas canvas, @NonNull RectF rect, @NonNull SeriesFormatterType formatter);

    public void drawSeriesLegendIcon(@NonNull Canvas canvas, @NonNull RectF rect, @NonNull SeriesFormatterType formatter) {
        try {
            canvas.save();
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
    @NonNull
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
    @NonNull
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
