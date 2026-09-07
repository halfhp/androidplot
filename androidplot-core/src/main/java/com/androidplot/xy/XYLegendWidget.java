// SPDX-License-Identifier: Apache-2.0

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Displays a legend for each series added to the owning {@link XYPlot}.
 */
public class XYLegendWidget extends LegendWidget<XYLegendItem> {

    private XYPlot plot;

    public XYLegendWidget(@NonNull LayoutManager layoutManager, @NonNull XYPlot plot,
                          @NonNull Size widgetSize,
                          @NonNull TableModel tableModel,
                          @NonNull Size iconSize) {
        super(tableModel, layoutManager, widgetSize, iconSize);
        this.plot = plot;

        // Set a default comparator that sorts by type and then alphabetically
        setLegendItemComparator(new Comparator<XYLegendItem>() {
            @Override
            public int compare(XYLegendItem o1, XYLegendItem o2) {
                if(o1.type == o2.type) {
                    // series and regions may have no title; sort those as an empty string:
                    final String t1 = o1.getTitle() != null ? o1.getTitle() : "";
                    final String t2 = o2.getTitle() != null ? o2.getTitle() : "";
                    return t1.compareTo(t2);
                } else {
                    return(o1.type.compareTo(o2.type));
                }
            }
        });
    }

    protected void drawRegionLegendIcon(@NonNull Canvas canvas, @NonNull RectF rect, @NonNull XYRegionFormatter formatter) {
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
    @NonNull
    protected List<XYLegendItem> getLegendItems() {
        final ArrayList<XYLegendItem> items = new ArrayList<>();
        for (SeriesBundle<XYSeries, XYSeriesFormatter> sfPair : plot.getRegistry().getLegendEnabledItems()) {
            items.add(new XYLegendItem(XYLegendItem.Type.SERIES, sfPair.getFormatter(), sfPair.getSeries().getTitle()));
        }

        for (XYSeriesRenderer renderer : plot.getRendererList()) {
            Map<XYRegionFormatter, String> urf = renderer.getUniqueRegionFormatters();
            for (Entry<XYRegionFormatter, String> entry : urf.entrySet()) {
                items.add(new XYLegendItem(XYLegendItem.Type.REGION, entry.getKey(), entry.getValue()));
            }
        }

        return items;
    }
}
