// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

public interface PointLabeler<SeriesType extends XYSeries> {

    String getLabel(SeriesType series, int index);
}
