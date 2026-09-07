// SPDX-License-Identifier: Apache-2.0
package com.androidplot.pie;

import com.androidplot.*;
import androidx.annotation.NonNull;

/**
 * SeriesRegistry implementation to be used in a {@link PieChart}.
 */
public class SegmentRegistry extends SeriesRegistry<SegmentBundle, Segment, SegmentFormatter> {

    @Override
    @NonNull
    protected SegmentBundle newSeriesBundle(@NonNull Segment series, @NonNull SegmentFormatter formatter) {
        return new SegmentBundle(series, formatter);
    }
}
