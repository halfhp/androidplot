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

import com.androidplot.Plot;

/**
 * Base class of all Formatters.  Encapsulates visual elements of a series; line style, color etc.
 */
public abstract class Formatter<PlotType extends Plot> {

    /**
     *
     * @return The Class of DataRenderer that should be used.
     */
    public abstract Class<? extends DataRenderer> getRendererClass();

    /**
     *
     * @return An instance of DataRenderer that took plot as an argument to its constructor.
     */
    public abstract DataRenderer getRendererInstance(PlotType plot);
}