// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import com.androidplot.util.DisplayDimensions;
import androidx.annotation.NonNull;

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
    void layout(@NonNull final DisplayDimensions dims);
}
