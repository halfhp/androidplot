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

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesAndFormatter;
import com.androidplot.ui.Size;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;

import java.util.*;

/**
 * Displays a legend for each series added to the owning {@link XYPlot}.
 */
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

    private Size iconSize;
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
                          Size widgetSize,
                          TableModel tableModel,
                          Size iconSize) {
        super(layoutManager, widgetSize);
        this.plot = plot;
        setTableModel(tableModel);
        this.iconSize = iconSize;
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    private RectF getIconRect(RectF cellRect) {
        float cellRectCenterY = cellRect.top + (cellRect.height()/2);
        RectF iconRect = iconSize.getRectF(cellRect);

        // center the icon rect vertically
        float centeredIconOriginY = cellRectCenterY - (iconRect.height()/2);
        iconRect.offsetTo(cellRect.left + 1, centeredIconOriginY);
        return iconRect;
    }

    private static float getRectCenterY(RectF cellRect) {
        return cellRect.top + (cellRect.height()/2);
    }

    private void beginDrawingCell(Canvas canvas, RectF iconRect) {

        Paint bgPaint = plot.getGraph().getGridBackgroundPaint();
        if(drawIconBackgroundEnabled && bgPaint != null) {
            canvas.drawRect(iconRect, bgPaint);
        }
    }

    private void finishDrawingCell(Canvas canvas, RectF cellRect, RectF iconRect, String text) {

        Paint bgPaint = plot.getGraph().getGridBackgroundPaint();
        if(drawIconBorderEnabled && bgPaint != null) {
            iconBorderPaint.setColor(bgPaint.getColor());
            canvas.drawRect(iconRect, iconBorderPaint);
        }

    	float centeredTextOriginY = getRectCenterY(cellRect) + (FontUtils.getFontHeight(textPaint)/2);

    	if (textPaint.getTextAlign().equals(Paint.Align.RIGHT)) {
	        canvas.drawText(text, iconRect.left - 2, centeredTextOriginY, textPaint);
        } else {
	        canvas.drawText(text, iconRect.right + 2, centeredTextOriginY, textPaint);
        }
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
        if(plot.isEmpty()) {
            return;
        }

        // Keep an alphabetically sorted list of regions:
        TreeSet<Map.Entry<XYRegionFormatter, String>> sortedRegions = new TreeSet<Map.Entry<XYRegionFormatter, String>>(new RegionEntryComparator());

        // Calculate the number of cells needed to draw the Legend:
        int seriesCount = plot.getSeriesRegistry().size();

        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            sortedRegions.addAll(urf.entrySet());
        }

        seriesCount += sortedRegions.size();

        // Create an iterator specially created to draw the number of cells we calculated:
        Iterator<RectF> it = tableModel.getIterator(widgetRect, seriesCount);

        RectF cellRect;

        // draw each series legend item:
        for(SeriesAndFormatter<XYSeries, XYSeriesFormatter> sfPair : plot.getSeriesRegistry()) {
            cellRect = it.next();
            XYSeriesFormatter format = sfPair.getFormatter();
            drawSeriesLegendCell(canvas, plot.getRenderer(sfPair.getFormatter().getRendererClass()),
                    format, cellRect, sfPair.getSeries().getTitle());
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

    public Size getIconSize() {
        return iconSize;
    }

    /**
     * Set the size of each legend's icon.  Note that when using relative sizing,
     * the size is calculated against the countaining cell's size, not the plot's size.
     * @param iconSize
     */
    public void setIconSize(Size iconSize) {
        this.iconSize = iconSize;
    }
}
