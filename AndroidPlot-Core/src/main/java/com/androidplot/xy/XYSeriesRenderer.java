package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.DataRenderer;
import com.androidplot.util.ZIndexable;

import java.util.Hashtable;

public abstract class XYSeriesRenderer<XYFormatterType extends XYSeriesFormatter> extends DataRenderer<XYPlot, XYFormatterType> {

    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }

    public Hashtable<XYRegionFormatter, String> getUniqueRegionFormatters() {

        Hashtable<XYRegionFormatter, String> found = new Hashtable<XYRegionFormatter, String>();
        SeriesAndFormatterList<XYSeries, XYFormatterType> sfl = getSeriesAndFormatterList();

        for (XYFormatterType xyf : sfl.getFormatterList()) {
            ZIndexable<RectRegion> regionIndexer = xyf.getRegions();
            for (RectRegion region : regionIndexer.elements()) {
                XYRegionFormatter f = xyf.getRegionFormatter(region);
                found.put(f, region.getLabel());
            }
        }

        return found;
    }

    @Override
    public void render(Canvas canvas, RectF plotArea) throws PlotRenderException {
        super.render(canvas, plotArea);
        //onRender(canvas, plotArea);
        // TODO: draw point labels here

    }
}
