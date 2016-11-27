package com.androidplot.xy;

/**
 * Estimates optimal zoom level to be applied to a {@link SampledXYSeries} based on the current
 * visible bounds of the owning {@link XYPlot}.
 */
public class ZoomEstimator extends Estimator {

    @Override
    public void run(XYPlot plot, XYSeriesBundle sf) {
        if(sf.getSeries() instanceof SampledXYSeries) {
            SampledXYSeries oxy = (SampledXYSeries) sf.getSeries();
            final double factor = calculateZoom(oxy, plot.getBounds());
            oxy.setZoomFactor(factor);
        }
    }

    protected double calculateZoom(SampledXYSeries series, RectRegion visibleBounds) {
        RectRegion seriesBounds = series.getBounds();
        final double ratio = seriesBounds.getxRegion().ratio(visibleBounds.getxRegion()).doubleValue();
        final double maxFactor = series.getMaxZoomFactor();
        final double factor = Math.abs(Math.round(maxFactor / ratio));
        return factor > 0 ? factor : 1;
    }

}
