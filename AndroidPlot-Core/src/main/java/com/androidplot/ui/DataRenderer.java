/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.Plot;
//import com.androidplot.xy.ui.widget.renderer.XYRenderBundle;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.XYRegionFormatter;

public abstract class DataRenderer<PlotType extends Plot, SeriesFormatterType extends Formatter> {
    private PlotType plot;
    //private Paint regionPaint;

    {
    }

    public DataRenderer(PlotType plot) {
        this.plot = plot;
    }

    public PlotType getPlot() {
        return plot;
    }

    public void setPlot(PlotType plot) {
        this.plot = plot;
    }

    public SeriesAndFormatterList<XYSeries,SeriesFormatterType> getSeriesAndFormatterList() {
        return plot.getSeriesAndFormatterListForRenderer(getClass());
    }

    public SeriesFormatterType getFormatter(Series series) {
        return (SeriesFormatterType) plot.getFormatter(series, getClass());
    }

    public void render(Canvas canvas, RectF plotArea) throws PlotRenderException {
        //recalculateMinMaxVals();
        onRender(canvas, plotArea);
    }
    public abstract void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException;

    /**
     * Draw the legend icon in the rect passed in.
     * @param canvas
     * @param rect
     */
    protected abstract void doDrawLegendIcon(Canvas canvas, RectF rect, SeriesFormatterType formatter);

    public void drawSeriesLegendIcon(Canvas canvas, RectF rect, SeriesFormatterType formatter) {
        //int state = canvas.save(Canvas.CLIP_SAVE_FLAG);
        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(rect, Region.Op.INTERSECT);
            doDrawLegendIcon(canvas, rect, formatter);
            //canvas.restoreToCount(state);
        } finally {
            canvas.restore();
        }
    }

    public void drawRegionLegendIcon(Canvas canvas, RectF rect, XYRegionFormatter formatter) {
        canvas.drawRect(rect, formatter.getPaint());
    }

}
