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

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
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

        // Keep an alphabetically sorted list of regions:
        TreeSet<Map.Entry<XYRegionFormatter, String>> sortedRegions = new TreeSet<Map.Entry<XYRegionFormatter, String>>(new RegionEntryComparator());

        // Calculate the number of cells needed to draw the Legend:
        int seriesCount = 0;
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            seriesCount += plot.getSeriesAndFormatterListForRenderer(renderer.getClass()).size();

            // Figure out how many regions need to be added to the legend:
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            for(XYRegionFormatter xyf : urf.keySet()) {
                regionRendererLookup.put(xyf, renderer);
            }
            sortedRegions.addAll(renderer.getUniqueRegionFormatters().entrySet());
        }
        seriesCount += sortedRegions.size();

        // Create an iterator specially created to draw the number of cells we calculated:
        Iterator<RectF> it = tableModel.getIterator(widgetRect, seriesCount);

        RectF cellRect = null;

        // draw each series legend item:
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            SeriesAndFormatterList<XYSeries,XYSeriesFormatter> sfList = plot.getSeriesAndFormatterListForRenderer(renderer.getClass());

            // maxIndex is only used if it has been determined.
            // if it is 0 then it could not be determined.
            for(int i = 0; i < sfList.size() && it.hasNext(); i++) {
                cellRect = it.next();
                XYSeriesFormatter formatter = sfList.getFormatter(i);
                drawSeriesLegendCell(canvas, renderer, formatter, cellRect, sfList.getSeries(i).getTitle());
            }
        }

        // draw each region legend item:
        for(Map.Entry<XYRegionFormatter, String> entry : sortedRegions) {
            if(!it.hasNext()) {
                break;
            }
            cellRect = it.next();
            XYRegionFormatter formatter = entry.getKey();
            drawRegionLegendCell(canvas, regionRendererLookup.get(formatter), formatter, cellRect, entry.getValue());
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
