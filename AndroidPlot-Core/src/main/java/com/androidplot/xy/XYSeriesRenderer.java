package com.androidplot.xy;

import com.androidplot.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.widget.renderer.DataRenderer;
import com.androidplot.util.ZIndexable;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public abstract class XYSeriesRenderer<XYFormatterType extends XYSeriesFormatter> extends DataRenderer<XYPlot, XYFormatterType> {

    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }

    public Hashtable<XYRegionFormatter, String> getUniqueRegionFormatters() {

        Hashtable<XYRegionFormatter, String> found = new Hashtable<XYRegionFormatter, String>();
        SeriesAndFormatterList<XYSeries, XYFormatterType> sfl = getSeriesAndFormatterList();

        for (XYFormatterType xyf : sfl.getFormatterList()) {
            ZIndexable<XYRegion> regionIndexer = xyf.getRegions();
            for (XYRegion region : regionIndexer.elements()) {
                //drawCell()
                XYRegionFormatter f = xyf.getRegionFormatter(region);
                found.put(f, region.getLabel());
                /*if (!found.contains(f)) {
                }
                {
                    //drawRegionLegendCell(canvas, renderer, f, cellRect, region.getLabel());
                    //found.add(f);
                    found.put
                }*/
            }
        }

        return found;
    }
}
