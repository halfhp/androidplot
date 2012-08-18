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

package com.androidplot.util;

import java.util.List;

/**
 * Encapsulates the concept of z-indexable objects;  Each object is stored above or below each other object and may
 * be moved up and down in the queue relative to other elements in the hash or absolutely to the front or back of the queue.
 *
 * Note that the method names correspond to the order of items drawn directly on top of one another using an iterator;
 * the first element drawn (lowest z-index) is effectively the "bottom" element.
 * @param <ElementType>
 */
public interface ZIndexable<ElementType> {

    /**
     * Move above all other elements
     * @param element
     * @return
     */
    public boolean moveToTop(ElementType element);


    /**
     * Move above the specified element
     * @param objectToMove
     * @param reference
     * @return
     */
    public boolean moveAbove(ElementType objectToMove, ElementType reference);


    /**
     * Move beneath the specified element
     *
     * @param objectToMove
     * @param reference
     * @return
     */
    public boolean moveBeneath(ElementType objectToMove, ElementType reference);

    /**
     * Move beneath all other elements
     * @param key
     * @return
     */
    public boolean moveToBottom(ElementType key);


    /**
     * Move up by one element
     * @param key
     * @return
     */
    public boolean moveUp(ElementType key);

    /**
     * Move down by one element
     * @param key
     * @return
     */
    public boolean moveDown(ElementType key);

    public List<ElementType> elements();


    /**
     * Add beneath all other elements
     * @param element
     */
    //public void addToBottom(ElementType element);

    /**
     * Add above all other elements
     * @param element
     */
    //public void addToTop(ElementType element);
}