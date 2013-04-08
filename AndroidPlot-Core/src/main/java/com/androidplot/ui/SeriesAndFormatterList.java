/*
 * Copyright 2012 AndroidPlot.com
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

import java.util.LinkedList;
import java.util.List;

/**
 * Associates a Series with a Formatter.
 * @param <SeriesType>
 * @param <FormatterType>
 */
public class SeriesAndFormatterList<SeriesType extends Series, FormatterType> {
    private LinkedList<SeriesType> seriesList;
    private LinkedList<FormatterType> formatterList;
    {
        seriesList = new LinkedList<SeriesType>();
        formatterList = new LinkedList<FormatterType>();
    }

    public boolean contains(SeriesType series) {
        return seriesList.contains(series);
    }

    public int size() {
        return seriesList.size();
    }

    public List<SeriesType> getSeriesList() {
        return seriesList;
    }

    public List<FormatterType> getFormatterList() {
        return formatterList;
    }

    public boolean add(SeriesType series, FormatterType formatter) {
        if(series == null || formatter == null) {
            throw new IllegalArgumentException("series and formatter must not be null.");
        }
        if(seriesList.contains(series)) {
            return false;
        }
        seriesList.add(series);
        formatterList.add(formatter);
        return true;
    }

    public boolean remove(SeriesType series) {
        int index = seriesList.indexOf(series);
        if(index < 0) {
            return false;
        }
        seriesList.remove(index);
        formatterList.remove(index);
        return true;
    }


    public FormatterType getFormatter(SeriesType series) {
        return formatterList.get(seriesList.indexOf(series));
    }

    public FormatterType getFormatter(int index) {
        return formatterList.get(index);
    }

    public SeriesType getSeries(int index) {
        return seriesList.get(index);
    }

    public FormatterType setFormatter(SeriesType series, FormatterType formatter) {
        return formatterList.set(seriesList.indexOf(series), formatter);
    }
}
