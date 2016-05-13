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

package com.androidplot.util;

import java.util.List;

/**
 * Utility class providing additional element organization operations.
 * @param <ElementType>
 */
public class LayerListOrganizer<ElementType> implements Layerable<ElementType> {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    private List<ElementType> list;

    public LayerListOrganizer(List<ElementType> list) {
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
        list.add(refIndex + ONE, objectToMove);
        return true;
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
        list.remove(key);
        list.add(ZERO, key);
        return true;
    }

    public boolean moveUp(ElementType key) {
        int widgetIndex = list.indexOf(key);
        if(widgetIndex == - ONE) {
            // key not found:
            return false;
        }
        if(widgetIndex >= list.size() - ONE) {
            // already at the top:
            return true;
        }

        ElementType widgetAbove = list.get(widgetIndex + ONE);
        return moveAbove(key, widgetAbove);
    }

    public boolean moveDown(ElementType key) {
        int widgetIndex = list.indexOf(key);
        if(widgetIndex == - ONE) {
            // key not found:
            return false;
        }
        if(widgetIndex <= ZERO) {
            // already at the bottom:
            return true;
        }

        ElementType widgetBeneath = list.get(widgetIndex - ONE);
        return moveBeneath(key, widgetBeneath);
    }

    @Override
    public List<ElementType> elements() {
        return list;
    }

    public void addToBottom(ElementType element) {
        list.add(ZERO, element);
    }

    public void addToTop(ElementType element) {
        list.add(list.size(), element);
    }
}
