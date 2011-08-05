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
 * Utility class providing additional element organization operations.
 * @param <ElementType>
 */
public class ListOrganizer<ElementType> implements ZIndexable<ElementType> {
    private List<ElementType> list;

    public ListOrganizer(List<ElementType> list) {
        this.list = list;
    }


    public boolean moveToTop(ElementType element) {
            if(list.remove(element)) {
                list.add(list.size(), element);
                return true;
            } else {
                return false;
            }
    }

    public boolean moveAbove(ElementType objectToMove, ElementType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAbove(A, B); A cannot be equal to B.");
        }


        list.remove(objectToMove);
        int refIndex = list.indexOf(reference);
        list.add(refIndex + 1, objectToMove);
        return true;
        //widgetOrder.remove(element);

    }

    public boolean moveBeneath(ElementType objectToMove, ElementType reference) {
        if (objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }

        list.remove(objectToMove);
        int refIndex = list.indexOf(reference);
        list.add(refIndex, objectToMove);
        return true;

    }

    public boolean moveToBottom(ElementType key) {

        //int widgetIndex = widgetOrder.indexOf(key);
        list.remove(key);
        //list.add(list.size(), key);
        list.add(0, key);
        return true;
        //widgetOrder.remove(key);
    }

    public boolean moveUp(ElementType key) {
        int widgetIndex = list.indexOf(key);
        if(widgetIndex == -1) {
            // key not found:
            return false;
        }
        if(widgetIndex >= list.size()-1) {
            // already at the top:
            return true;
        }

        ElementType widgetAbove = list.get(widgetIndex+1);
        return moveAbove(key, widgetAbove);
    }

    public boolean moveDown(ElementType key) {
        int widgetIndex = list.indexOf(key);
        if(widgetIndex == -1) {
            // key not found:
            return false;
        }
        if(widgetIndex <= 0) {
            // already at the bottom:
            return true;
        }

        ElementType widgetBeneath = list.get(widgetIndex-1);
        return moveBeneath(key, widgetBeneath);
    }

    @Override
    public List<ElementType> elements() {
        return list;
    }

    public void addToBottom(ElementType element) {
        list.add(0, element);
    }

    public void addToTop(ElementType element) {
        list.add(list.size(), element);
    }
}
