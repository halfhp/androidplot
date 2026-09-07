// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.Layerable;

import java.util.Hashtable;

/**
 * Base class for all Renderers that render XYSeries data.
 * @param <XYFormatterType>
 */
public abstract class XYSeriesRenderer<SeriesType extends XYSeries, XYFormatterType extends XYSeriesFormatter>
        extends SeriesRenderer<XYPlot, SeriesType, XYFormatterType> {

    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }

    /**
     * TODO: get rid of this method!
     * @return Map of all unique XYRegionFormatters to region labels.
     */
    public Hashtable<XYRegionFormatter, String> getUniqueRegionFormatters() {

        Hashtable<XYRegionFormatter, String> found = new Hashtable<>();
        for(SeriesBundle<SeriesType, ? extends XYFormatterType> sfPair : getSeriesAndFormatterList()) {
            Layerable<RectRegion> regionIndexer = sfPair.getFormatter().getRegions();
            for (RectRegion region : regionIndexer.elements()) {
                XYRegionFormatter f = sfPair.getFormatter().getRegionFormatter(region);
                found.put(f, region.getLabel());
            }
        }
        return found;
    }
}
