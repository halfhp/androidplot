// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import com.androidplot.Series;
import androidx.annotation.NonNull;

/**
 * Defines a relationship between a Series instance and other elements needed to unique render that instance
 * such as a Formatter etc.
 */
public class SeriesBundle<SeriesType extends Series, FormatterType extends Formatter> {

    private final SeriesType series;
    private final FormatterType formatter;

    public SeriesBundle(@NonNull SeriesType series, @NonNull FormatterType formatter) {
        this.series = series;
        this.formatter = formatter;
    }

    @NonNull
    public SeriesType getSeries() {
        return series;
    }

    @NonNull
    public FormatterType getFormatter() {
        return formatter;
    }

    public boolean rendersWith(@NonNull SeriesRenderer renderer) {
        return getFormatter().getRendererClass() == renderer.getClass();
    }
}
