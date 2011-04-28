package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.layout.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.util.FontUtils;
import com.androidplot.util.ZIndexable;

import java.util.*;

public class XYLegendWidget extends Widget {
    ////public static final int ICON_WIDTH_DEFAULT = 10;
    //public static final int ICON_HEIGHT_DEFAULT = 10;

    private enum CellType {
        SERIES,
        REGION
    }

    private XYPlot plot;
    //private float iconWidth = 12;
    private Paint textPaint;
    private Paint iconBorderPaint;
    private TableModel tableModel;
    private boolean drawIconBackgroundEnabled = true;
    private boolean drawIconBorderEnabled = true;

    private SizeMetrics iconSizeMetrics;
    //private RectF iconRect = new RectF(0, 0, ICON_WIDTH_DEFAULT, ICON_HEIGHT_DEFAULT);

    {
        textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setAntiAlias(true);

        iconBorderPaint = new Paint();
        iconBorderPaint.setStyle(Paint.Style.STROKE);
    }

    public XYLegendWidget(XYPlot plot,
                          SizeMetrics widgetSizeMetrics,
                          TableModel tableModel,
                          SizeMetrics iconSizeMetrics) {
        super(widgetSizeMetrics);
        this.plot = plot;
        setTableModel(tableModel);
        this.iconSizeMetrics = iconSizeMetrics;
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    private RectF getIconRect(RectF cellRect) {
        float cellRectCenterY = cellRect.top + (cellRect.height()/2);
        RectF iconRect = iconSizeMetrics.getRectF(cellRect);

        // center the icon rect vertically
        float centeredIconOriginY = cellRectCenterY - (iconRect.height()/2);
        iconRect.offsetTo(cellRect.left + 1, centeredIconOriginY);
        return iconRect;
    }

    private static float getRectCenterY(RectF cellRect) {
        return cellRect.top + (cellRect.height()/2);
    }

    private void beginDrawingCell(Canvas canvas, RectF iconRect) {

        Paint bgPaint = plot.getGraphWidget().getGridBackgroundPaint();
        if(drawIconBackgroundEnabled && bgPaint != null) {
            canvas.drawRect(iconRect, bgPaint);
        }
    }

    private void finishDrawingCell(Canvas canvas, RectF cellRect, RectF iconRect, String text) {

        Paint bgPaint = plot.getGraphWidget().getGridBackgroundPaint();
        if(drawIconBorderEnabled && bgPaint != null) {
            iconBorderPaint.setColor(bgPaint.getColor());
            canvas.drawRect(iconRect, iconBorderPaint);
        }

        float centeredTextOriginY = getRectCenterY(cellRect) + (FontUtils.getFontHeight(textPaint)/2);
                canvas.drawText(text, iconRect.right + 2, centeredTextOriginY, textPaint);
    }

    private void drawRegionLegendCell(Canvas canvas, XYSeriesRenderer renderer, XYRegionFormatter formatter, RectF cellRect, String text) {
        RectF iconRect = getIconRect(cellRect);
        beginDrawingCell(canvas, iconRect);

                renderer.drawRegionLegendIcon(
                        canvas,
                        iconRect,
                        formatter
                        );
        finishDrawingCell(canvas, cellRect, iconRect, text);
    }

    private void drawSeriesLegendCell(Canvas canvas, XYSeriesRenderer renderer, XYSeriesFormatter formatter, RectF cellRect, String seriesTitle) {
        RectF iconRect = getIconRect(cellRect);
        beginDrawingCell(canvas, iconRect);

                renderer.drawSeriesLegendIcon(
                        canvas,
                        iconRect,
                        formatter);
        finishDrawingCell(canvas, cellRect, iconRect, seriesTitle);
    }

    @Override
    protected synchronized void doOnDraw(Canvas canvas, RectF widgetRect) {
        // TODO: a good amount of iterating could be avoided if
        // we create a temporary list of all the legend items up here.
        if(plot.isEmpty()) {
            return;
        }

        Hashtable<XYRegionFormatter, XYSeriesRenderer> regionRendererLookup = new Hashtable<XYRegionFormatter, XYSeriesRenderer>();
        Hashtable<XYRegionFormatter, String> regionFormatters = new Hashtable<XYRegionFormatter, String>();





        // Calculate the number of cells needed to draw the Legend:
        int seriesCount = 0;
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            seriesCount += plot.getSeriesAndFormatterListForRenderer(renderer.getClass()).size();



            // Figure out how many regions need to be added to the legend:
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            for(XYRegionFormatter xyf : urf.keySet()) {
                regionRendererLookup.put(xyf, renderer);
            }
            regionFormatters.putAll(renderer.getUniqueRegionFormatters());

           /* XYSeriesFormatter formatter = renderer.getF


            // TODO: make regions reusable
                ZIndexable<XYRegion> regionIndexer = formatter.getRegions();

            // Keep a record of the formatters already drawn:
                Hashtable<XYRegionFormatter, XYRegionFormatter> renderedFormatters = new Hashtable<XYRegionFormatter, XYRegionFormatter>();
                for(XYRegion region : regionIndexer.elements()) {
                    //drawCell()
                    XYRegionFormatter f = formatter.getRegionFormatter(region);
                    if(! renderedFormatters.contains(f)) {} {
                        drawRegionLegendCell(canvas, renderer, f, cellRect, region.getLabel());
                        renderedFormatters.put(f, f);
                    }
                }*/
        }

        seriesCount += regionFormatters.size();

        // Create an iterator specially created to draw the number of cells we calculated:
        Iterator<RectF> it = tableModel.getIterator(widgetRect, seriesCount);

        // Draw each cell:
        RectF cellRect = null;
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            SeriesAndFormatterList<XYSeries,XYSeriesFormatter> sfList = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());

            // maxIndex is only used if it has been determined.
            // if it is 0 then it could not be determined.

            for(int i = 0; i < sfList.size() && it.hasNext(); i++) {
                cellRect = it.next();
                XYSeriesFormatter formatter = sfList.getFormatter(i);
                drawSeriesLegendCell(canvas, renderer, formatter, cellRect, sfList.getSeries(i).getTitle());

                /*// TODO: make regions reusable
                ZIndexable<XYRegion> regionIndexer = formatter.getRegions();*/

                /*// Keep a record of the formatters already drawn:
                Hashtable<XYRegionFormatter, XYRegionFormatter> renderedFormatters = new Hashtable<XYRegionFormatter, XYRegionFormatter>();
                for(XYRegion region : regionIndexer.elements()) {
                    //drawCell()
                    XYRegionFormatter f = formatter.getRegionFormatter(region);
                    if(! renderedFormatters.contains(f)) {} {
                        drawRegionLegendCell(canvas, renderer, f, cellRect, region.getLabel());
                        renderedFormatters.put(f, f);
                    }
                }*/
            }
        }
        for(Map.Entry<XYRegionFormatter, String> s : regionFormatters.entrySet()) {
            if(!it.hasNext()) {
                break;
            }
            cellRect = it.next();
            drawRegionLegendCell(canvas, regionRendererLookup.get(s.getKey()), s.getKey(), cellRect, s.getValue());
        }
    }


    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public boolean isDrawIconBackgroundEnabled() {
        return drawIconBackgroundEnabled;
    }

    public void setDrawIconBackgroundEnabled(boolean drawIconBackgroundEnabled) {
        this.drawIconBackgroundEnabled = drawIconBackgroundEnabled;
    }

    public boolean isDrawIconBorderEnabled() {
        return drawIconBorderEnabled;
    }

    public void setDrawIconBorderEnabled(boolean drawIconBorderEnabled) {
        this.drawIconBorderEnabled = drawIconBorderEnabled;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public SizeMetrics getIconSizeMetrics() {
        return iconSizeMetrics;
    }

    /**
     * Set the size of each legend's icon.  Note that when using relative sizing,
     * the size is calculated against the countaining cell's size, not the plot's size.
     * @param iconSizeMetrics
     */
    public void setIconSizeMetrics(SizeMetrics iconSizeMetrics) {
        this.iconSizeMetrics = iconSizeMetrics;
    }
}
