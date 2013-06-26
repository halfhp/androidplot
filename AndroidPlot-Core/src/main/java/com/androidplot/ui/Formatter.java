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

import android.content.Context;
import com.androidplot.Plot;
import com.androidplot.util.Configurator;

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

    public Formatter<PlotType> configure(Context ctx, int xmlCfgId) {
        Configurator.configure(ctx, this, xmlCfgId);
        return this;
    }



    /**
     *
     * @return The Class of SeriesRenderer that should be used.
     */
    public abstract Class<? extends SeriesRenderer> getRendererClass();

    /**
     *
     * @return An instance of SeriesRenderer that took plot as an argument to its constructor.
     */
    public abstract SeriesRenderer getRendererInstance(PlotType plot);

}
