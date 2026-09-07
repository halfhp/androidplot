// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import androidx.annotation.NonNull;

public interface PointLabeler<SeriesType extends XYSeries> {

    @NonNull
    String getLabel(@NonNull SeriesType series, int index);
}
