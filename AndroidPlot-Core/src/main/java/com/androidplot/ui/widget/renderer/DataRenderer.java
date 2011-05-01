package com.androidplot.ui.widget.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.Plot;
//import com.androidplot.xy.ui.widget.renderer.XYRenderBundle;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.widget.formatter.Formatter;
import com.androidplot.xy.XYRegionFormatter;

public abstract class DataRenderer<PlotType extends Plot, SeriesFormatterType extends Formatter> {
    private PlotType plot;
    //private Paint regionPaint;

    {
        //regionPaint = new Paint();
        //regionPaint.setAntiAlias(true);
        //regionPaint.setStyle(Paint.Style.FILL);
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
        int state = canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(rect, Region.Op.INTERSECT);
        doDrawLegendIcon(canvas, rect, formatter);
        canvas.restoreToCount(state);
    }

    public void drawRegionLegendIcon(Canvas canvas, RectF rect, XYRegionFormatter formatter) {
        //canvas.save(Canvas.CLIP_SAVE_FLAG);
        //canvas.clipRect(rect);
        //canvas.restore();
        //regionPaint.setColor(formatter.getColor());
        canvas.drawRect(rect, formatter.getPaint());
    }

}
