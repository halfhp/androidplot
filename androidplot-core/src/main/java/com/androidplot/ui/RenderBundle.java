// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import com.androidplot.Series;
import com.androidplot.xy.XYSeriesFormatter;

public abstract class RenderBundle<RenderBundleType extends RenderBundle, SeriesType extends Series, SeriesFormatterType extends XYSeriesFormatter> {
    //private XYDataset series;
    private Series series;
    private SeriesFormatterType formatter;

    public RenderBundle(SeriesType series, SeriesFormatterType formatter) {
        this.formatter = formatter;
        this.series = series;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public SeriesFormatterType getFormatter() {
        return formatter;
    }

    public void setFormatter(SeriesFormatterType formatter) {
        this.formatter = formatter;
    }
}
