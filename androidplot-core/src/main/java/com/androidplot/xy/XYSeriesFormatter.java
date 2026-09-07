// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.Context;

import com.androidplot.ui.Formatter;
import com.androidplot.util.LayerHash;
import com.androidplot.util.Layerable;

public abstract class XYSeriesFormatter<XYRegionFormatterType extends XYRegionFormatter> extends Formatter<XYPlot> {

    // instantiate a default implementation prints point's yVal:
    private PointLabeler pointLabeler = new PointLabeler() {
        @Override
        public String getLabel(XYSeries series, int index) {
            return String.valueOf(series.getY(index));
        }
    };

    private PointLabelFormatter pointLabelFormatter;

    LayerHash<RectRegion, XYRegionFormatterType> regions;

    {
        regions = new LayerHash<>();
    }

    public XYSeriesFormatter() {}

    public XYSeriesFormatter(Context context, int xmlCfgId) {
        super(context, xmlCfgId);
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
    public Layerable<RectRegion> getRegions() {
        return regions;
    }

    /**
     * @param region
     * @return
     */
    public XYRegionFormatterType getRegionFormatter(RectRegion region) {
        return regions.get(region);
    }


    public PointLabeler getPointLabeler() {
        return pointLabeler;
    }

    public void setPointLabeler(PointLabeler pointLabeler) {
        this.pointLabeler = pointLabeler;
    }

    public boolean hasPointLabelFormatter() {
        return pointLabelFormatter != null;
    }

    public PointLabelFormatter getPointLabelFormatter() {
        if(pointLabelFormatter == null) {
            pointLabelFormatter = new PointLabelFormatter();
        }
        return pointLabelFormatter;
    }

    public void setPointLabelFormatter(PointLabelFormatter pointLabelFormatter) {
        this.pointLabelFormatter = pointLabelFormatter;
    }
}
