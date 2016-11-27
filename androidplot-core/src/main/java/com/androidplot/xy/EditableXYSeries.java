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

package com.androidplot.xy;

/**
 * An {@link XYSeries} that exposes methods to set values and resize
 */
public interface EditableXYSeries extends XYSeries {

    void setX(Number x, int index);
    void setY(Number y, int index);

    /**
     * Resize to accommodate the specified number of x/y pairs.  If elements must be droped, those
     * at the highest iVal should be removed first.
     * @param size
     */
    void resize(int size);
}
