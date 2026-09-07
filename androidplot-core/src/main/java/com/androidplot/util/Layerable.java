// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import java.util.List;
import androidx.annotation.NonNull;

/**
 * Encapsulates the concept of "layerable" objects;  Each object is stored above or below each other object and may
 * be moved up and down in the queue relative to other elements in the hash or absolutely to the front or back of the queue.
 *
 * Note that the method names correspond to the order of items drawn directly on top of one another using an iterator;
 * the first element drawn (lowest layer) is effectively the "bottom" element.
 * @param <ElementType>
 */
public interface Layerable<ElementType> {

    /**
     * Move above all other elements
     * @param element
     * @return
     */
    boolean moveToTop(@NonNull ElementType element);


    /**
     * Move above the specified element
     * @param objectToMove
     * @param reference
     * @return
     */
    boolean moveAbove(@NonNull ElementType objectToMove, @NonNull ElementType reference);


    /**
     * Move beneath the specified element
     *
     * @param objectToMove
     * @param reference
     * @return
     */
    boolean moveBeneath(@NonNull ElementType objectToMove, @NonNull ElementType reference);

    /**
     * Move beneath all other elements
     * @param key
     * @return
     */
    boolean moveToBottom(@NonNull ElementType key);


    /**
     * Move up by one element
     * @param key
     * @return
     */
    boolean moveUp(@NonNull ElementType key);

    /**
     * Move down by one element
     * @param key
     * @return
     */
    boolean moveDown(@NonNull ElementType key);

    @NonNull
    List<ElementType> elements();
}