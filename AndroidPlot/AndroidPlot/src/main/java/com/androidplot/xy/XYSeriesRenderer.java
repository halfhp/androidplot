package com.androidplot.xy;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.widget.renderer.DataRenderer;

import java.util.List;

public abstract class XYSeriesRenderer<XYFormatterType extends XYSeriesFormatter> extends DataRenderer<XYPlot, XYFormatterType> {
    //private Number minX;
    //private Number minY;

    //private Number maxX;
    //private Number maxY;
    //private XYPlot plot;
    //private final XYRendererType type;

    //private ArrangeableHash<Integer, RenderBundleType> seriesList;
    //LinkedList<RenderBundleType> seriesList;

    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }



    /*public List<RenderBundleType> getSeriesListForRenderer() {
        return seriesList;
    }*/



    /*public void recalculateMinMaxVals() {

        *//*recalculateMaxX();
        recalculateMaxY();
        recalculateMinX();
        recalculateMinY();*//*
        minX = null;
        maxX = null;
        minY = null;
        maxY = null;



        getPlot().getSeriesListForRenderer(this.getClass());
        // tmpMinX

        // TODO: get rid of the cast for the below foreach calls, AND consolidate
        // into a single foreach.
        for(XYSeries series : getPlot().getSeriesListForRenderer(this.getClass())) {
            Number thisMinX = series.getMinX();
            if(minX == null || thisMinX.doubleValue() < minX.doubleValue()) {
                minX = thisMinX;
            }
        }

        // tmpMaxX
        for(XYSeries series : (List<XYSeries>)getPlot().getSeriesListForRenderer(this.getClass())) {
            Number thisMax = series.getMaxX();
            if(maxX == null || thisMax.doubleValue() > maxX.doubleValue()) {
                maxX = thisMax;
            }
        }

        // tmpMinY
        for(XYSeries series : (List<XYSeries>)getPlot().getSeriesListForRenderer(this.getClass())) {
            Number thisMin = series.getMinY();
            if(minY == null || thisMin.doubleValue() < minY.doubleValue()) {
                minY = thisMin;
            }
        }


        // tmpMaxY
        for(XYSeries series : (List<XYSeries>)getPlot().getSeriesListForRenderer(this.getClass())) {
            Number thisMax = series.getMaxY();
            if(maxY == null || thisMax.doubleValue() > maxY.doubleValue()) {
                maxY = thisMax;
            }
        }

        //this.minX = tmpMinX;
        //this.maxX = tmpMaxX;
        //this.minY = tmpMinY;
        //this.maxY = tmpMaxY;

    }*/

    /*private void recalculateMinX() {
        Number min = null;
        for(XYRenderBundle thisBundle : getSeriesListForRenderer()) {
            Number thisMin = thisBundle.getDataset().getActualMinX(thisBundle.getSeriesIndex());
            if(min == null || thisMin.doubleValue() < min.doubleValue()) {
                min = thisMin;
            }
        }
        //return min;
        minX = min.doubleValue();
    }



    private void recalculateMaxX() {
        Number max = null;
        for(XYRenderBundle thisBundle : getSeriesListForRenderer()) {
            Number thisMax = thisBundle.getDataset().getActualMaxX(thisBundle.getSeriesIndex());
            if(max == null || thisMax.doubleValue() > max.doubleValue()) {
                max = thisMax;
            }
        }
        maxX = max.doubleValue();
        //return max;
    }

    private void recalculateMinY() {
        Number min = null;
        for(XYRenderBundle thisBundle : getSeriesListForRenderer()) {
            Number thisMin = thisBundle.getDataset().getActualMinY(thisBundle.getSeriesIndex());
            if(min == null || thisMin.doubleValue() < min.doubleValue()) {
                min = thisMin;
            }
        }
        minY = min.doubleValue();
    }

    private void recalculateMaxY() {
        Number max = null;
        for(XYRenderBundle thisBundle : getSeriesListForRenderer()) {
            Number thisMax = thisBundle.getDataset().getActualMaxY(thisBundle.getSeriesIndex());
            if(max == null || thisMax.doubleValue() > max.doubleValue()) {
                max = thisMax;
            }
        }
        maxY = max.doubleValue();
        //return max;
    }*/


    /**
     *
     * @param bundle
     * @return true if the renderer did not already contain the element.
     *//*
    public boolean addSeries(RenderBundleType bundle) {
        if(containsSeries(bundle)) {
            return false;
        }
        seriesList.addLast(bundle);
        return true;
    }*/

    /**
     *
     * @return true if the renderer contains the series
     *//*
    public boolean containsSeries(RenderBundleType bundle) {
        return containsSeries(bundle.getDataset(), bundle.getSeriesIndex());
    }

    protected boolean containsSeries(XYDataset series, int series) {
        for(RenderBundleType thisBundle : seriesList) {
            if(thisBundle.compare(series, series)) {
                return true;
            }
        }
        return false;
    }*/

    /*protected RenderBundleType getBundle(XYDataset series, int series) {
        for(RenderBundleType thisBundle : seriesList) {
            if(thisBundle.compare(series, series)) {
                return thisBundle;
            }
        }
        return null;
    }*/

    /*protected boolean removeSeries(RenderBundleType bundle) {
        return removeSeries(bundle.getDataset(), bundle.getSeriesIndex());
    }*/

    /*public boolean removeSeries(XYDataset series, int series) {
        RenderBundleType bundle = getBundle(series, series);

        if(bundle != null) {
            return seriesList.remove(bundle);
        } else {
            return false;
        }


    }*/







    /*
    public abstract void beginSeries(Canvas canvas, RectF plotArea, XYDatasetSeriesFormat format) throws PlotRenderException;
    public abstract void drawPoint(Canvas canvas, Point point, RectF plotArea, XYDatasetSeriesFormat format) throws PlotRenderException;
    public abstract void endSeries(Canvas canvas, RectF plotArea, XYDatasetSeriesFormat format) throws PlotRenderException;
    */

    /*public Number getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public Number getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public Number getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public Number getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }*/
}
