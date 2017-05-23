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
import com.androidplot.ui.SeriesBundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of {@link Series} and their associated {@link Formatter} in the context of a {@link Plot}.
 * @since 0.9.7
 */
public abstract class SeriesRegistry
        <BundleType extends SeriesBundle<SeriesType, FormatterType>,
                SeriesType extends Series, FormatterType extends Formatter> implements Serializable {

    private ArrayList<BundleType> registry = new ArrayList<>();

    public List<BundleType> getSeriesAndFormatterList() {
        return registry;
    }
    public List<SeriesType> getSeriesList() {
        List<SeriesType> result = new ArrayList<>(registry.size());
        for(SeriesBundle<SeriesType, FormatterType> sfPair : registry) {
            result.add(sfPair.getSeries());
        }
        return result;
    }

    public int size() {
        return registry.size();
    }

    public boolean isEmpty() {
        return registry.isEmpty();
    }

    public boolean add(SeriesType series, FormatterType formatter) {
        if(series == null || formatter == null) {
            throw new IllegalArgumentException("Neither series nor formatter param may be null.");
        }
        return registry.add(newSeriesBundle(series, formatter));
    }

    protected abstract BundleType newSeriesBundle(SeriesType series, FormatterType formatter);

    /**
     *
     * @param series
     * @return A List of {@link SeriesBundle} instances that reference series.
     */
    protected List<SeriesBundle<SeriesType, FormatterType>> get(SeriesType series) {
        List<SeriesBundle<SeriesType, FormatterType>> results =
                new ArrayList<>();
        for(SeriesBundle<SeriesType, FormatterType> thisPair : registry) {
            if(thisPair.getSeries() == series) {
                results.add(thisPair);
            }
        }
        return results;
    }

    public synchronized List<BundleType> remove(SeriesType series, Class rendererClass) {
        ArrayList<BundleType> removedItems = new ArrayList<>();
        for(Iterator<BundleType> it = registry.iterator(); it.hasNext();) {
            BundleType b = it.next();
            if(b.getSeries() == series && b.getFormatter().getRendererClass() == rendererClass) {
                it.remove();
                removedItems.add(b);
            }
        }
        return removedItems;
    }

    /**
     * Remove all occurrences of series regardless of the associated Renderer.
     * @param series
     */
    public synchronized boolean remove(SeriesType series) {
        boolean result = false;
        for(Iterator<BundleType> it = registry.iterator(); it.hasNext();) {
            if(it.next().getSeries() == series) {
                it.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Remove all series from the plot.
     */
    public void clear() {
        for(Iterator<BundleType> it
            = registry.iterator(); it.hasNext();) {
            it.next();
            it.remove();
        }
    }

    public List<SeriesBundle<SeriesType, FormatterType>> getLegendEnabledItems() {
        List<SeriesBundle<SeriesType, FormatterType>> sfList = new ArrayList<>();
        for(SeriesBundle<SeriesType, FormatterType> sf : registry) {
            if(sf.getFormatter().isLegendIconEnabled()) {
                sfList.add(sf);
            }
        }
        return sfList;
    }

    public boolean contains(SeriesType series, Class<? extends FormatterType> formatterClass) {
        for(BundleType b : registry) {
            if(b.getFormatter().getClass() == formatterClass && b.getSeries() == series) {
                return true;
            }
        }
        return false;
    }
}
