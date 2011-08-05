/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
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