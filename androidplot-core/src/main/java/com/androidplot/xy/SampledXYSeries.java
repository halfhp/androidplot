package com.androidplot.xy;

import com.androidplot.*;
import com.androidplot.util.SeriesUtils;

import java.util.*;

/**
 * An implementation of {@link FastXYSeries} that samples its self into multiple levels to
 * achieve faster rendering / zoom behavior.  By default, uses {@link LTTBSampler} as
 * it's sampling algorithm.  Note that this algorithm does not yet support null values.
 *
 * Sampling behavior is controlled by two values:
 * Ratio: A value greater than 1; controls the sampling ratio of each successive series.  For example,
 * a step of 2 would mean that each successive series contains 2x fewer points than the previous.
 *
 * Threshold: A value < the original series size; controls the lower limit at which point sampling should stop.
 * For example, sampling a series with size 1000 given a ratio of 2 and a threshold of 100,
 * three sampled resolutions will be generated:
 *
 * 500 - 2x sampling
 * 250 - 4x sampling
 * 125 - 8x sampling
 *
 */
public class SampledXYSeries implements FastXYSeries, OrderedXYSeries {
    private int threshold;
    private Sampler algorithm = new LTTBSampler();

    private XYSeries rawData;

    private List<EditableXYSeries> zoomLevels;

    private XYSeries activeSeries;

    private RectRegion bounds;
    private Exception lastResamplingException;

    private final XOrder xOrder;
    private float ratio;

    /**
     *
     * @param rawData
     * @param xOrder If your data is in ascending or descending order, specifying it here speed up
     * optimize render times.
     * @param ratio The ratio used to determine the size of each new sampled series.  Must be > 1.
     * downsampled series until threshold is reached.
     * @param threshold The desired size of the smallest sample series.  Must be < rawData.size.
     */
    public SampledXYSeries(XYSeries rawData, XOrder xOrder, float ratio, int threshold) {
        this.rawData = rawData;
        this.xOrder = xOrder;
        this.setRatio(ratio);
        this.setThreshold(threshold);
        resample();
    }

    /**
     * Generate a SampledXYSeries from the input series.
     * @param rawData The original series to be downsampled
     * @param ratio The ratio used to determine the size of each new sampled series.  Must be > 1.
     * downsampled series until threshold is reached.
     * @param threshold The desired size of the smallest sample series.  Must be < rawData.size.
     */
    public SampledXYSeries(XYSeries rawData, float ratio, int threshold) {
        this(rawData, SeriesUtils.getXYOrder(rawData), ratio, threshold);
    }

    public void resample() {
        bounds = null;
        zoomLevels = new ArrayList<>();
        int t = (int) Math.ceil(rawData.size() / getRatio());
        List<Thread> threads = new ArrayList<>(zoomLevels.size());
        while (t > threshold) {
            final int thisThreshold = t;
            final EditableXYSeries thisSeries = new FixedSizeEditableXYSeries(getTitle(), thisThreshold);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO: make bounds a param to prevent calculating on each setZoomFactor level:
                        RectRegion b = getAlgorithm()
                                .run(rawData, thisSeries);
                        if (bounds == null) {
                            bounds = b;
                        }
                    } catch(Exception ex) {
                        lastResamplingException = ex;
                    }
                }
            }, "Androidplot XY Series Sampler");
            getZoomLevels().add(thisSeries);
            threads.add(thread);
            thread.start();

            t = (int) Math.ceil(t / getRatio());
        }
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(lastResamplingException != null) {
            throw new RuntimeException("Exception encountered during resampling", lastResamplingException);
        }

    }

    protected List<EditableXYSeries> getZoomLevels() {
        return this.zoomLevels;
    }

    /**
     * Set zoom factor; 2.5 = 2.5x zoom, 10.0 = 10x zoom etc. This method will set the zoom level
     * to the closest available factor to the specified factor; a specified factor of 4.5x may result
     * in an actual factor of 4x.
     * @param factor
     */
    public void setZoomFactor(double factor) {
        if(factor <= 1) {
            activeSeries = rawData;
        } else {
            //int i = (int) Math.round(Math.sqrt(factor) - 1);
            int i = getZoomIndex(factor, getRatio());
            if (i < zoomLevels.size()) {
                activeSeries = zoomLevels.get(i);
            } else {
                activeSeries = zoomLevels.get(zoomLevels.size() - 1);
            }
        }
    }

    protected static int getZoomIndex(double zoomFactor, double ratio) {
        final double lhs = Math.log(zoomFactor);
        final double rhs = Math.log(ratio);
        final double log = lhs / rhs;
        final int index = (int) Math.round(log);
        return index > 0 ? index - 1 : 0;
    }

    public double getMaxZoomFactor() {
        return Math.pow(getRatio(), zoomLevels.size());
    }

    public Sampler getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Sampler algorithm) {
        this.algorithm = algorithm;
        resample();
    }

    @Override
    public String getTitle() {
        return rawData.getTitle();
    }

    @Override
    public int size() {
        return activeSeries.size();
    }

    @Override
    public Number getX(int index) {
        return activeSeries.getX(index);
    }

    @Override
    public Number getY(int index) {
        return activeSeries.getY(index);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        if(threshold >= rawData.size()) {
            throw new IllegalArgumentException("Threshold must be < original series size.");
        }
        this.threshold = threshold;
    }

    public RectRegion getBounds() {
        return bounds;
    }

    public void setBounds(RectRegion bounds) {
        this.bounds = bounds;
    }

    @Override
    public RectRegion minMax() {
        return bounds;
    }

    @Override
    public XOrder getXOrder() {
        return xOrder;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        if(ratio <= 1) {
            throw new IllegalArgumentException("Ratio must be greater than 1");
        }
        this.ratio = ratio;
    }
}
