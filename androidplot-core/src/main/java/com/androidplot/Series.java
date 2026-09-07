// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import androidx.annotation.Nullable;

/**
 * Base interface for all Series implementations
 */
public interface Series {

    /**
     *
     * @return The title of this Series.
     */
    @Nullable
    String getTitle();

}
