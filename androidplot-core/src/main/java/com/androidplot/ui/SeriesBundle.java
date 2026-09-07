// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import com.androidplot.Series;

/**
 * Defines a relationship between a Series instance and other elements needed to unique render that instance
 * such as a Formatter etc.
 */
public class SeriesBundle<SeriesType extends Series, FormatterType extends Formatter> {

    private final SeriesType series;
    private final FormatterType formatter;

    public SeriesBundle(SeriesType series, FormatterType formatter) {
        this.series = series;
        this.formatter = formatter;
    }

    public SeriesType getSeries() {
        return series;
    }

    public FormatterType getFormatter() {
        return formatter;
    }

    public boolean rendersWith(SeriesRenderer renderer) {
        return getFormatter().getRendererClass() == renderer.getClass();
    }
}
