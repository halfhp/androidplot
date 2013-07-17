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

import android.content.Context;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Formatter;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;

public abstract class XYSeriesFormatter<XYRegionFormatterType extends XYRegionFormatter> extends Formatter<XYPlot> {
    ZHash<RectRegion, XYRegionFormatterType>  regions;

    {
        regions = new ZHash<RectRegion, XYRegionFormatterType>();
    }

    public void addRegion(RectRegion region, XYRegionFormatterType regionFormatter) {
        regions.addToBottom(region, regionFormatter);
    }

    public void removeRegion(RectRegion region) {
        regions.remove(region);
    }

    /**
     * Can be used to access z-index manipulation methods of ZIndexable.
     * @return
     */
    public ZIndexable<RectRegion> getRegions() {
        return regions;
    }

    /**
     * @param region
     * @return
     */
    public XYRegionFormatterType getRegionFormatter(RectRegion region) {
        return regions.get(region);
    }

    /**
     * Not completely sure why this is necessary, but if it's not here then
     * subclasses are forced to take a Plot instead of an XYPlot as a parameter,
     * which in turn breaks the pattern.
     * @param plot
     * @return
     */
    @Override
    public abstract SeriesRenderer getRendererInstance(XYPlot plot);
}
