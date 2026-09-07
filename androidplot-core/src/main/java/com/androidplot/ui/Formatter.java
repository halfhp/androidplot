// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.content.Context;
import com.androidplot.Plot;
import com.androidplot.util.fig.*;

/**
 * Base class of all Formatters.  Encapsulates visual elements of a series; line style, color etc.
 * Implementors of this class should include both a default constructor and a one argument
 * constructor in the following form:
 *
 * <pre>
 * {@code
 * // provided as a convenience to users; allows instantiation and
 * // xml configuration in a single line.
 * public MyFormatter(Context ctx, int xmlCfgId) {
 *     // prevent configuration of classes derived from this one:
 *     if (getClass().equals(MyFormatter.class)) {
 *         Configurator.configure(ctx, this, xmlCfgId);
 *     }
 * }
 * </pre>
 */
public abstract class Formatter<PlotType extends Plot> {

    private boolean isLegendIconEnabled = true;

    public Formatter() {}

    public Formatter(Context ctx, int xmlCfgId) {
        configure(ctx, xmlCfgId);
    }

    public void configure(Context ctx, int xmlCfgId) {
        try {
            Fig.configure(ctx, this, xmlCfgId);
        } catch (FigException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param plot
     * @param <T>
     * @return @return An instance of SeriesRenderer constructed with the specified plot.
     */
    public <T extends SeriesRenderer> T getRendererInstance(PlotType plot) {
        return (T) doGetRendererInstance(plot);
    }

    /**
     *
     * @return The Class of SeriesRenderer that should be used when rendering series associated
     * with instances of this formatter.
     */
    public abstract Class<? extends SeriesRenderer> getRendererClass();

    /**
     *
     * @return An instance of SeriesRenderer constructed with the specified plot.
     */
    protected abstract SeriesRenderer doGetRendererInstance(PlotType plot);

    public boolean isLegendIconEnabled() {
        return isLegendIconEnabled;
    }

    /**
     * Sets whether or not a legend icon should be drawn for the series associated with this formatter.
     * @param legendIconEnabled
     */
    public void setLegendIconEnabled(boolean legendIconEnabled) {
        this.isLegendIconEnabled = legendIconEnabled;
    }
}
