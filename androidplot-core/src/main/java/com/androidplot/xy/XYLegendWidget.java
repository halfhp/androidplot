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
import androidx.annotation.NonNull;

import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.Size;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.widget.LegendWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

/**
 * Displays a legend for each series added to the owning {@link XYPlot}.
 */
public class XYLegendWidget extends LegendWidget<XYLegendItem> {

    private XYPlot plot;

    public XYLegendWidget(LayoutManager layoutManager, XYPlot plot,
                          Size widgetSize,
                          TableModel tableModel,
                          Size iconSize) {
        super(tableModel, layoutManager, widgetSize, iconSize);
        this.plot = plot;

        // Set a default comparator that sorts by type and then alphabetically
        setLegendItemComparator(new Comparator<XYLegendItem>() {
            @Override
            public int compare(XYLegendItem o1, XYLegendItem o2) {
                if(o1.type == o2.type) {
                    return o1.getTitle().compareTo(o2.getTitle());
                } else {
                    return(o1.type.compareTo(o2.type));
                }
            }
        });
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
        for (SeriesBundle<XYSeries, XYSeriesFormatter> sfPair : plot.getRegistry().getLegendEnabledItems()) {
            items.add(new XYLegendItem(XYLegendItem.Type.SERIES, sfPair.getFormatter(), sfPair.getSeries().getTitle()));
        }

        for (XYSeriesRenderer renderer : plot.getRendererList()) {
            Hashtable<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            for (Entry<XYRegionFormatter, String> entry : urf.entrySet()) {
                items.add(new XYLegendItem(XYLegendItem.Type.REGION, entry.getKey(), entry.getValue()));
            }
        }

        return items;
    }
}
