// SPDX-License-Identifier: Apache-2.0
package com.androidplot.pie;

import com.androidplot.ui.*;
import androidx.annotation.NonNull;

/**
 * Manages the association between a given {@link Segment} and the {@link SegmentFormatter} that
 * will be used to render it.
 */
public class SegmentBundle extends SeriesBundle<Segment, SegmentFormatter> {

    public SegmentBundle(@NonNull Segment series, @NonNull SegmentFormatter formatter) {
        super(series, formatter);
    }
}
