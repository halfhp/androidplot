package com.androidplot.xy;

import android.util.*;

/**
 * Adapted from:
 * https://github.com/drcrane/downsample
 *
 * Note that this implementation does not yet support null values.
 *
 * Basic usage example:
 * <pre>
 * {@code
 * // An instance of any implementation of XYSeries; SimpleXYSeries, etc:
 * XYSeries origalSeries = ...;
 *
 * // Sampled series with half the resolution of the
 * EditableXYSeries sampledSeries = new FixedSizeEditableXYSeries(
 * origalSeries.getTitle(), origalSeries.size() / 2);
 *
 * // does the actual sampling:
 * new LTTBSampler().run(origalSeries, sampledSeries);
 * }
 * </pre>
 */
public class LTTBSampler implements Sampler {

    public RectRegion run(XYSeries rawData, EditableXYSeries sampled) {
        RectRegion bounds = new RectRegion();
        final int threshold = sampled.size();
        final int dataLength = rawData.size();
        final int startIndex = 0;

        if (threshold >= dataLength || threshold == 0) {
            //return data; // Nothing to do
            // TODO: set flag to return raw data
            throw new RuntimeException("Shouldnt be here!");
        }

        int sampledIndex = 0;
        // Bucket size. Leave room for start and end data points
        final double bucketSize = (double) (dataLength - 2) / (threshold - 2);
        int a = 0; // Initially a is the first point in the triangle
        int nextA = 0;
        setSample(rawData, sampled, a + startIndex, sampledIndex, bounds);
        sampledIndex++;
        for (int i = 0; i < threshold - 2; i++) {
            // Calculate point average for next bucket (containing c)
            double pointCX = 0;
            double pointCY = 0;
            int pointCStart = (int) Math.floor((i + 1) * bucketSize) + 1;
            int pointCEnd = (int) Math.floor((i + 2) * bucketSize) + 1;
            pointCEnd = pointCEnd < dataLength ? pointCEnd : dataLength;
            final int pointCSize = pointCEnd - pointCStart;
            for (; pointCStart < pointCEnd; pointCStart++) {
                if(rawData.getX(pointCStart + startIndex) != null) {
                    pointCX += rawData.getX(pointCStart + startIndex).doubleValue();
                }

                if(rawData.getY(pointCStart + startIndex) != null) {
                    pointCY += rawData.getY(pointCStart + startIndex).doubleValue();
                }
            }
            pointCX /= pointCSize;
            pointCY /= pointCSize;
            double pointAX = rawData.getX(a + startIndex).doubleValue();
            double pointAY = rawData.getY(a + startIndex).doubleValue();
            // Get the range for bucket b
            int pointBStart = (int) Math.floor((i + 0) * bucketSize) + 1;
            final int pointBEnd = (int) Math.floor((i + 1) * bucketSize) + 1;
            double maxArea = -1;
            XYCoords maxAreaPoint = null;
            for (; pointBStart < pointBEnd; pointBStart++) {
                final double area = Math.abs((pointAX - pointCX) * (rawData.getY(pointBStart + startIndex)
                        .doubleValue() - pointAY) - (pointAX - rawData.getX(pointBStart + startIndex)
                        .doubleValue())
                        * (pointCY - pointAY)) * 0.5;
                if (area > maxArea) {
                    if(rawData.getY(pointBStart + startIndex) == null) {
                        Log.i("LTTB", "Null value encountered in raw data at index: " + pointBStart);
                    }
                    maxArea = area;
                    maxAreaPoint = new XYCoords(rawData.getX(pointBStart + startIndex),
                            rawData.getY(pointBStart + startIndex));
                    nextA = pointBStart; // Next a is this b
                }
            }
            setSample(sampled, maxAreaPoint.x, maxAreaPoint.y, sampledIndex, bounds);
            sampledIndex++;
            a = nextA; // This a is the next a (chosen b)
        }
        setSample(rawData, sampled, (dataLength + startIndex) - 1, sampledIndex, bounds);
        sampledIndex++;
        return bounds;
    }

    protected void setSample(XYSeries raw, EditableXYSeries sampled, int rawIndex, int sampleIndex, RectRegion bounds) {
        setSample(sampled, raw.getX(rawIndex), raw.getY(rawIndex), sampleIndex, bounds);
    }

    protected void setSample(EditableXYSeries sampled, Number x, Number y, int sampleIndex, RectRegion bounds) {
        bounds.union(x, y);
        sampled.setX(x, sampleIndex);
        sampled.setY(y, sampleIndex);
    }
}
