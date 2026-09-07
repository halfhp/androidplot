// SPDX-License-Identifier: Apache-2.0
package com.androidplot.ui.widget;

import androidx.annotation.Nullable;

/**
 * An item to be displayed by {@link LegendWidget}.
 */
public interface LegendItem {

    /**
     *
     * @return The user facing label for this item, or null if it has none.
     */
    @Nullable
    String getTitle();
}
