package com.androidplot.xy;

import com.androidplot.*;

/**
 * Maintains the "registry" of mappings of XYSeries instances and their associated formatters.
 */
public class XYSeriesRegistry extends SeriesRegistry<XYSeriesBundle, XYSeries, XYSeriesFormatter> {

    private Estimator estimator;

    public void estimate(XYPlot plot) {
        if(estimator != null) {
            for (XYSeriesBundle sf : getSeriesAndFormatterList()) {
                getEstimator().run(plot, sf);
            }
        }
    }

    @Override
    protected XYSeriesBundle newSeriesBundle(XYSeries series, XYSeriesFormatter formatter) {
        return new XYSeriesBundle(series, formatter);
    }

    /**
     *
     * @return The currently active Estimator, or null if none is set.
     */
    public Estimator getEstimator() {
        return estimator;
    }

    public void setEstimator(Estimator estimator) {
        this.estimator = estimator;
    }
}
