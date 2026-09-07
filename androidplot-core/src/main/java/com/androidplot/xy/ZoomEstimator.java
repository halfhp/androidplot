// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import androidx.annotation.NonNull;

/**
 * Estimates optimal zoom level to be applied to a {@link SampledXYSeries} based on the current
 * visible bounds of the owning {@link XYPlot}.
 */
public class ZoomEstimator extends Estimator {

    @Override
    public void run(@NonNull XYPlot plot, @NonNull XYSeriesBundle sf) {
        if(sf.getSeries() instanceof SampledXYSeries) {
            SampledXYSeries oxy = (SampledXYSeries) sf.getSeries();
            if (oxy.getBounds() == null) {
                // nothing to estimate against; leave the current zoom level as-is.
                return;
            }
            final double factor = calculateZoom(oxy, plot.getBounds());
            oxy.setZoomFactor(factor);
        }
    }

    /**
     * @param series
     * @param visibleBounds
     * @return The zoom factor to apply to series, or 1 (no zoom) if series has no bounds.
     */
    protected double calculateZoom(@NonNull SampledXYSeries series, @NonNull RectRegion visibleBounds) {
        RectRegion seriesBounds = series.getBounds();
        if (seriesBounds == null) {
            return 1;
        }
        final double ratio = seriesBounds.getxRegion().ratio(visibleBounds.getxRegion()).doubleValue();
        final double maxFactor = series.getMaxZoomFactor();
        final double factor = Math.abs(Math.round(maxFactor / ratio));
        return factor > 0 ? factor : 1;
    }

}
