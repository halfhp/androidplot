package com.androidplot.pie;

import com.androidplot.*;

/**
 * SeriesRegistry implementation to be used in a {@link PieChart}.
 */
public class SegmentRegistry extends SeriesRegistry<SegmentBundle, Segment, SegmentFormatter> {

    @Override
    protected SegmentBundle newSeriesBundle(Segment series, SegmentFormatter formatter) {
        return new SegmentBundle(series, formatter);
    }
}
