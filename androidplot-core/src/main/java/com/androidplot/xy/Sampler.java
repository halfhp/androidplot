package com.androidplot.xy;

/**
 * An algorithm used to to resample a larger set of data into a smaller set.
 */
public interface Sampler {

    /**
     *
     * @param input The original unsampled series
     * @param output The destination series to contain sampled result.
     * This series size should be set to the desired sampled size.
     * @return min/max values encountered while processing input.
     */
    RectRegion run(XYSeries input, EditableXYSeries output);
}
