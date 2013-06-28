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

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.ZIndexable;

import java.util.Hashtable;

public abstract class XYSeriesRenderer<XYFormatterType extends XYSeriesFormatter>
        extends SeriesRenderer<XYPlot, XYSeries, XYFormatterType> {

    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }

    public Hashtable<XYRegionFormatter, String> getUniqueRegionFormatters() {

        Hashtable<XYRegionFormatter, String> found = new Hashtable<XYRegionFormatter, String>();
        SeriesAndFormatterList<XYSeries, XYFormatterType> sfl = getSeriesAndFormatterList();

        if (sfl != null) {
            for (XYFormatterType xyf : sfl.getFormatterList()) {
                ZIndexable<RectRegion> regionIndexer = xyf.getRegions();
                for (RectRegion region : regionIndexer.elements()) {
                    XYRegionFormatter f = xyf.getRegionFormatter(region);
                    found.put(f, region.getLabel());
                }
            }
        }

        return found;
    }
}
