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

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.util.DisplayDimensions;

/**
 * Used by classes that depend on dimensional values to lay themselves out and draw.
 * Consideration should be given to synchronizing with any draw routines that also
 * exist within the class.
 */
public interface Resizable {

    /**
     * Called when a change to the class' dimensions is made.  This method is responsible
     * for cascading calls to update for any logical children of this class, for example
     * the Plot class is responsible for updating the LayoutManager.  Note that while dims
     * is marked final in this interface, the compiler will not enforce it.  Implementors of
     * this method should take care not to make changes to dims as this will affect parent
     * Resizables in likely undesired ways.
     * @param dims
     */
    public void layout(final DisplayDimensions dims);
}
