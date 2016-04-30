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
 * Created by halfhp on 1/16/16.
 */
public class SeriesRegistry<SeriesType extends Series, FormatterType extends Formatter> {

    private ArrayList<SeriesAndFormatter<SeriesType, FormatterType>> sfPairs;

    {
        sfPairs = new ArrayList<>();
    }

    public void add(SeriesAndFormatter<SeriesType, FormatterType> sfPair) {
        sfPairs.add(sfPair);
    }

    public void remove(SeriesAndFormatter<SeriesType, FormatterType> sfPair) {
        sfPairs.remove(sfPair);
    }

    public ArrayList<SeriesAndFormatter<SeriesType, FormatterType>> asList() {
        return sfPairs;
    }

    public Iterator<SeriesAndFormatter<SeriesType, FormatterType>> iterator() {
        return sfPairs.iterator();
    }

    public List<SeriesType> getSeriesList() {
        List<SeriesType> result = new ArrayList<>();
        for(SeriesAndFormatter<SeriesType, FormatterType> sfPair : sfPairs) {
            result.add(sfPair.getSeries());
        }
        return result;
    }

    public boolean isEmpty() {
        return sfPairs.isEmpty();
    }

    public int size() {
        return sfPairs.size();
    }
}
