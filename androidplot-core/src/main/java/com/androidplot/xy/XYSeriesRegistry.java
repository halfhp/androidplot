// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import com.androidplot.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Maintains the "registry" of mappings of XYSeries instances and their associated formatters.
 */
public class XYSeriesRegistry extends SeriesRegistry<XYSeriesBundle, XYSeries, XYSeriesFormatter> {

    private Estimator estimator;

    public void estimate(@NonNull XYPlot plot) {
        if(estimator != null) {
            for (XYSeriesBundle sf : getSeriesAndFormatterList()) {
                getEstimator().run(plot, sf);
            }
        }
    }

    @Override
    @NonNull
    protected XYSeriesBundle newSeriesBundle(@NonNull XYSeries series, @NonNull XYSeriesFormatter formatter) {
        return new XYSeriesBundle(series, formatter);
    }

    /**
     *
     * @return The currently active Estimator, or null if none is set.
     */
    @Nullable
    public Estimator getEstimator() {
        return estimator;
    }

    public void setEstimator(@Nullable Estimator estimator) {
        this.estimator = estimator;
    }
}
