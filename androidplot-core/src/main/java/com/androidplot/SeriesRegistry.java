// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import com.androidplot.ui.Formatter;
import com.androidplot.ui.SeriesBundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import androidx.annotation.NonNull;

/**
 * Manages a list of {@link Series} and their associated {@link Formatter} in the context of a {@link Plot}.
 * @since 0.9.7
 */
public abstract class SeriesRegistry
        <BundleType extends SeriesBundle<SeriesType, FormatterType>,
                SeriesType extends Series, FormatterType extends Formatter> implements Serializable {

    private ArrayList<BundleType> registry = new ArrayList<>();

    @NonNull
    public List<BundleType> getSeriesAndFormatterList() {
        return registry;
    }
    @NonNull
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

    public synchronized boolean add(@NonNull SeriesType series, @NonNull FormatterType formatter) {
        if(series == null || formatter == null) {
            throw new IllegalArgumentException("Neither series nor formatter param may be null.");
        }
        return registry.add(newSeriesBundle(series, formatter));
    }

    @NonNull
    protected abstract BundleType newSeriesBundle(@NonNull SeriesType series, @NonNull FormatterType formatter);

    /**
     *
     * @param series
     * @return A List of {@link SeriesBundle} instances that reference series.
     */
    @NonNull
    protected List<SeriesBundle<SeriesType, FormatterType>> get(@NonNull SeriesType series) {
        List<SeriesBundle<SeriesType, FormatterType>> results =
                new ArrayList<>();
        for(SeriesBundle<SeriesType, FormatterType> thisPair : registry) {
            if(thisPair.getSeries() == series) {
                results.add(thisPair);
            }
        }
        return results;
    }

    @NonNull
    public synchronized List<BundleType> remove(@NonNull SeriesType series, @NonNull Class rendererClass) {
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
    public synchronized boolean remove(@NonNull SeriesType series) {
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
    public synchronized void clear() {
        registry.clear();
    }

    @NonNull
    public List<SeriesBundle<SeriesType, FormatterType>> getLegendEnabledItems() {
        List<SeriesBundle<SeriesType, FormatterType>> sfList = new ArrayList<>();
        for(SeriesBundle<SeriesType, FormatterType> sf : registry) {
            if(sf.getFormatter().isLegendIconEnabled()) {
                sfList.add(sf);
            }
        }
        return sfList;
    }

    public boolean contains(@NonNull SeriesType series, @NonNull Class<? extends FormatterType> formatterClass) {
        for(BundleType b : registry) {
            if(b.getFormatter().getClass() == formatterClass && b.getSeries() == series) {
                return true;
            }
        }
        return false;
    }
}
