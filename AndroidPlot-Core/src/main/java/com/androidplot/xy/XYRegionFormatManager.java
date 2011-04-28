package com.androidplot.xy;

import java.util.Hashtable;

/**
 * Ties XYRegions to XYRegionFormatters.
 * @param <XYRegionFormatterType>
 */
@Deprecated
public class XYRegionFormatManager<XYRegionFormatterType> {
    XYRegionGroup regions;
    Hashtable<XYRegion, XYRegionFormatterType> regionFormatterLookup;

    {
        regions = new XYRegionGroup();
        regionFormatterLookup = new Hashtable<XYRegion, XYRegionFormatterType>();
    }

    public void addRegion(XYRegion region, XYRegionFormatterType regionFormatter) {

        regions.addToTop(region);
        regionFormatterLookup.put(region, regionFormatter);
    }

    public void removeRegion(XYRegion region) {
        regions.remove(region);
        regionFormatterLookup.remove(region);
    }

    /**
     * Can be used to access z-index manipulation methods of XYRegionGroup.
     * @return
     */
    public XYRegionGroup getRegionGroup() {
        return regions;
    }

    /**
     * @param region
     * @return
     */
    public XYRegionFormatterType getRegionFormatter(XYRegion region) {
        return regionFormatterLookup.get(region);
    }
}


