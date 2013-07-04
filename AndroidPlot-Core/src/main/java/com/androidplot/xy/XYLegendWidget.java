/*
 * Copyright 2012 AndroidPlot.com
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

import android.graphics.*;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;

import java.util.*;

public class XYLegendWidget extends Widget {

    /**
     * This class is of no use outside of XYLegendWidget.  It's just used to alphabetically sort
     * Region legend entries.
     */
    private static class RegionEntryComparator implements Comparator<Map.Entry<XYRegionFormatter, String>> {
        @Override
        public int compare(Map.Entry<XYRegionFormatter, String> o1, Map.Entry<XYRegionFormatter, String> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

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
    private static final RegionEntryComparator regionEntryComparator = new RegionEntryComparator();
    //private RectF iconRect = new RectF(0, 0, ICON_WIDTH_DEFAULT, ICON_HEIGHT_DEFAULT);

    {
        textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setAntiAlias(true);

        iconBorderPaint = new Paint();
        iconBorderPaint.setStyle(Paint.Style.STROKE);
        //regionEntryComparator = new RegionEntryComparator();
    }

    public XYLegendWidget(LayoutManager layoutManager, XYPlot plot,
                          SizeMetrics widgetSizeMetrics,
                          TableModel tableModel,
                          SizeMetrics iconSizeMetrics) {
        super(layoutManager, widgetSizeMetrics);
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

    protected void drawRegionLegendIcon(Canvas canvas, RectF rect, XYRegionFormatter formatter) {
            canvas.drawRect(rect, formatter.getPaint());
        }

    private void drawRegionLegendCell(Canvas canvas, XYRegionFormatter formatter, RectF cellRect, String text) {
        RectF iconRect = getIconRect(cellRect);
        beginDrawingCell(canvas, iconRect);

                drawRegionLegendIcon(
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

        //Hashtable<XYRegionFormatter, XYSeriesRenderer> regionRendererLookup = new Hashtable<XYRegionFormatter, XYSeriesRenderer>();

        // Keep an alphabetically sorted list of regions:
        TreeSet<Map.Entry<XYRegionFormatter, String>> sortedRegions = new TreeSet<Map.Entry<XYRegionFormatter, String>>(new RegionEntryComparator());

        // Calculate the number of cells needed to draw the Legend:
        int seriesCount = 0;
        for(XYSeriesRenderer renderer : plot.getRendererList()) {

            SeriesAndFormatterList sfl = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());
            if(sfl != null) {
                seriesCount += sfl.size();
            }

            // Figure out how many regions need to be added to the legend:
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            /*for(XYRegionFormatter xyf : urf.keySet()) {
                regionRendererLookup.put(xyf, renderer);
            }*/
            sortedRegions.addAll(urf.entrySet());
            //sortedRegions.addAll(renderer.getUniqueRegionFormatters().entrySet());
        }
        seriesCount += sortedRegions.size();

        // Create an iterator specially created to draw the number of cells we calculated:
        Iterator<RectF> it = tableModel.getIterator(widgetRect, seriesCount);

        RectF cellRect;

        // draw each series legend item:
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            SeriesAndFormatterList<XYSeries,XYSeriesFormatter> sfl = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());

            if (sfl != null) {
                // maxIndex is only used if it has been determined.
                // if it is 0 then it could not be determined.
                for (int i = 0; i < sfl.size() && it.hasNext(); i++) {
                    cellRect = it.next();
                    XYSeriesFormatter formatter = sfl.getFormatter(i);
                    drawSeriesLegendCell(canvas, renderer, formatter, cellRect, sfl.getSeries(i).getTitle());
                }
            }
        }

        // draw each region legend item:
        for(Map.Entry<XYRegionFormatter, String> entry : sortedRegions) {
            if(!it.hasNext()) {
                break;
            }
            cellRect = it.next();
            XYRegionFormatter formatter = entry.getKey();
            drawRegionLegendCell(canvas, formatter, cellRect, entry.getValue());
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
