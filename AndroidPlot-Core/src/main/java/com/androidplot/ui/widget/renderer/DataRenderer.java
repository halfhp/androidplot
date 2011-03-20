package com.androidplot.ui.widget.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.Plot;
//import com.androidplot.xy.ui.widget.renderer.XYRenderBundle;
import com.androidplot.ui.widget.formatter.Formatter;
import com.androidplot.util.Point;

public abstract class DataRenderer<PlotType extends Plot, SeriesFormatterType extends Formatter> {
    private PlotType plot;

    public DataRenderer(PlotType plot) {
        this.plot = plot;
    }

    public PlotType getPlot() {
        return plot;
    }

    public void setPlot(PlotType plot) {
        this.plot = plot;
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
     * @param text
     */
    protected abstract void doDrawLegendIcon(Canvas canvas, RectF rect, String text, SeriesFormatterType formatter);

    public void drawLegendIcon(Canvas canvas, RectF rect, String text, SeriesFormatterType formatter) {
        int state = canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(rect, Region.Op.INTERSECT);
        doDrawLegendIcon(canvas, rect, text, formatter);
        canvas.restoreToCount(state);
    }

}
