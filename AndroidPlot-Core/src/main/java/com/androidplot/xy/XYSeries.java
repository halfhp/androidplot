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

package com.androidplot.xy;

import android.util.Pair;
import com.androidplot.Series;

/**
 * Represents a two dimensional series of data represented as xy values.
 */
public interface XYSeries extends Series<Pair<Number, Number>> {

    /**
     * @return Number of elements in this Series.
     */
    public int size();

    /**
     * Returns the x-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The x-value.
     */
    public Number getX(int index);

    /**
     * Returns the y-value for an index within a series.
     *
     * @param index  the index index (in the range <code>0</code> to
     *     <code>size()-1</code>).
     *
     * @return The y-value.
     */
    public Number getY(int index);
}
