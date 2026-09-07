// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.Layerable;

import java.util.LinkedHashMap;
import java.util.Map;

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
     * @return Map of all unique XYRegionFormatters to region labels, in the order the regions
     * were encountered.  A region without a label maps to null.
     */
    public Map<XYRegionFormatter, String> getUniqueRegionFormatters() {

        // a LinkedHashMap rather than a Hashtable; regions are not required to have a label:
        Map<XYRegionFormatter, String> found = new LinkedHashMap<>();
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
