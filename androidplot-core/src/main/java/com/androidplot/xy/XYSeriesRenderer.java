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
