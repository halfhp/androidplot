/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.ui;

import com.androidplot.Series;

/**
 * Defines an association between a Series and a Formatter.
 */
public class SeriesAndFormatter<SeriesType extends Series, FormatterType extends Formatter> {

    private final SeriesType series;
    private final FormatterType formatter;

    public SeriesAndFormatter(SeriesType series, FormatterType formatter) {
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
