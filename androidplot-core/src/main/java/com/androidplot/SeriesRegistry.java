/*
 * Copyright 2016 AndroidPlot.com
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

package com.androidplot;

import com.androidplot.ui.Formatter;
import com.androidplot.ui.SeriesAndFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of {@link Series} and their associated {@link Formatter} in the context of a {@link Plot}.
 * @since 0.9.7
 */
public class SeriesRegistry<SeriesType extends Series, FormatterType extends Formatter>
        extends ArrayList<SeriesAndFormatter<SeriesType, FormatterType>> {

    public List<SeriesType> getSeriesList() {
        List<SeriesType> result = new ArrayList<>();
        for(SeriesAndFormatter<SeriesType, FormatterType> sfPair : this) {
            result.add(sfPair.getSeries());
        }
        return result;
    }
}
