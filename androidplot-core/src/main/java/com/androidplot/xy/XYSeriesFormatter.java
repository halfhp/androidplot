// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.Context;

import com.androidplot.ui.Formatter;
import com.androidplot.util.LayerHash;
import com.androidplot.util.Layerable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public XYSeriesFormatter(@NonNull Context context, int xmlCfgId) {
        super(context, xmlCfgId);
    }

    public void addRegion(@NonNull RectRegion region, @NonNull XYRegionFormatterType regionFormatter) {
        regions.addToBottom(region, regionFormatter);
    }

    public void removeRegion(@NonNull RectRegion region) {
        regions.remove(region);
    }

    /**
     * Can be used to access z-index manipulation methods of ZIndexable.
     * @return
     */
    @NonNull
    public Layerable<RectRegion> getRegions() {
        return regions;
    }

    /**
     * @param region
     * @return
     */
    @Nullable
    public XYRegionFormatterType getRegionFormatter(@NonNull RectRegion region) {
        return regions.get(region);
    }


    @Nullable
    public PointLabeler getPointLabeler() {
        return pointLabeler;
    }

    public void setPointLabeler(@Nullable PointLabeler pointLabeler) {
        this.pointLabeler = pointLabeler;
    }

    public boolean hasPointLabelFormatter() {
        return pointLabelFormatter != null;
    }

    @NonNull
    public PointLabelFormatter getPointLabelFormatter() {
        if(pointLabelFormatter == null) {
            pointLabelFormatter = new PointLabelFormatter();
        }
        return pointLabelFormatter;
    }

    public void setPointLabelFormatter(@Nullable PointLabelFormatter pointLabelFormatter) {
        this.pointLabelFormatter = pointLabelFormatter;
    }
}
