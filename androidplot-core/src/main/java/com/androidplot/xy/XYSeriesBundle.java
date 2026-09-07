// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import com.androidplot.ui.*;
import androidx.annotation.NonNull;

/**
 * Created by halfhp on 10/6/16.
 */
public class XYSeriesBundle extends SeriesBundle<XYSeries, XYSeriesFormatter> {

    public XYSeriesBundle(@NonNull XYSeries series, @NonNull XYSeriesFormatter formatter) {
        super(series, formatter);
    }
}
