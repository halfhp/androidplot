package com.androidplot.xy;

import com.androidplot.ui.widget.formatter.Formatter;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;

public abstract class XYSeriesFormatter<XYRegionFormatterType extends XYRegionFormatter> extends Formatter {
    ZHash<XYRegion, XYRegionFormatterType>  regions;

    {
        regions = new ZHash<XYRegion, XYRegionFormatterType>();
    }

    public void addRegion(XYRegion region, XYRegionFormatterType regionFormatter) {
        regions.addToBottom(region, regionFormatter);
    }

    public void removeRegion(XYRegion region) {
        regions.remove(region);
    }

    /**
     * Can be used to access z-index manipulation methods of ZIndexable.
     * @return
     */
    public ZIndexable<XYRegion> getRegions() {
        return regions;
    }

    /**
     * @param region
     * @return
     */
    public XYRegionFormatterType getRegionFormatter(XYRegion region) {
        return regions.get(region);
    }
}
