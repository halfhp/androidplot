// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Essentially just a version of the Map interface used to associate
 * a key of a given type with a value of a given type, does impose a 1:1
 * relationship between keys and values and defines no method for insertion or deletion.
 */
public interface Mapping<Key, Value> {

    /**
     * @param value
     * @return The Key associated with the specified value.
     */
    @Nullable
    Key get(@NonNull Value value);
}
