package com.androidplot.pie;

import com.androidplot.ui.*;

/**
 * Manages the association between a given {@link Segment} and the {@link SegmentFormatter} that
 * will be used to render it.
 */
public class SegmentBundle extends SeriesBundle<Segment, SegmentFormatter> {

    public SegmentBundle(Segment series, SegmentFormatter formatter) {
        super(series, formatter);
    }
}
