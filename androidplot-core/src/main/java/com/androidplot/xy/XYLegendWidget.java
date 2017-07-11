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
import android.support.annotation.NonNull;

import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.Size;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.widget.LegendWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Displays a legend for each series added to the owning {@link XYPlot}.
 */
public class XYLegendWidget extends LegendWidget<XYLegendItem> {

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

    private XYPlot plot;

    private static final RegionEntryComparator regionEntryComparator = new RegionEntryComparator();

    public XYLegendWidget(LayoutManager layoutManager, XYPlot plot,
                          Size widgetSize,
                          TableModel tableModel,
                          Size iconSize) {
        super(tableModel, layoutManager, widgetSize, iconSize);
        this.plot = plot;
    }

    protected void drawRegionLegendIcon(Canvas canvas, RectF rect, XYRegionFormatter formatter) {
            canvas.drawRect(rect, formatter.getPaint());
        }

    @Override
    protected void drawIcon(@NonNull Canvas canvas, @NonNull RectF iconRect, @NonNull XYLegendItem XYLegendItem) {
        switch (XYLegendItem.type) {
            case REGION:
                drawRegionLegendIcon(canvas, iconRect, (XYRegionFormatter) XYLegendItem.item);
                break;
            case SERIES:
                final XYSeriesFormatter formatter = (XYSeriesFormatter) XYLegendItem.item;
                plot.getRenderer(formatter.getRendererClass()).drawSeriesLegendIcon(canvas, iconRect, formatter);
                break;
            default:
                throw new UnsupportedOperationException("Unexpected item type: " + XYLegendItem.type);

        }
    }

    @Override
    protected List<XYLegendItem> getLegendItems() {
        final ArrayList<XYLegendItem> items = new ArrayList<>();
        for(SeriesBundle<XYSeries, XYSeriesFormatter> sfPair : plot.getRegistry().getLegendEnabledItems()) {
            items.add(new XYLegendItem(XYLegendItem.Type.SERIES, sfPair.getFormatter(), sfPair.getSeries().getTitle()));
        }

        // Keep an alphabetically sorted list of regions:
        TreeSet<Map.Entry<XYRegionFormatter, String>> sortedRegions = new TreeSet<Map.Entry<XYRegionFormatter, String>>(regionEntryComparator);

        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            sortedRegions.addAll(urf.entrySet());
        }

        for(Map.Entry<XYRegionFormatter, String> item : sortedRegions) {
            items.add(new XYLegendItem(XYLegendItem.Type.REGION, item.getKey(), item.getValue()));
        }

        return items;
    }
}
