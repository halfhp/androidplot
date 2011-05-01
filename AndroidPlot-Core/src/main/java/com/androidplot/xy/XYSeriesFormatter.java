package com.androidplot.xy;

import com.androidplot.ui.widget.Formatter;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;

public abstract class XYSeriesFormatter<XYRegionFormatterType extends XYRegionFormatter> extends Formatter {
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
}
