// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import java.util.List;
import androidx.annotation.NonNull;

/**
 * Utility class providing additional element organization operations.
 * @param <ElementType>
 */
public class LayerListOrganizer<ElementType> implements Layerable<ElementType> {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    private List<ElementType> list;

    public LayerListOrganizer(@NonNull List<ElementType> list) {
        this.list = list;
    }


    public boolean moveToTop(@NonNull ElementType element) {
            if(list.remove(element)) {
                list.add(list.size(), element);
                return true;
            } else {
                return false;
            }
    }

    /**
     * @param objectToMove
     * @param reference
     * @return
     * @throws IllegalArgumentException if reference is not an element of this list, or is the
     * same as objectToMove.  The list is left unchanged.
     */
    public boolean moveAbove(@NonNull ElementType objectToMove, @NonNull ElementType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAbove(A, B); A cannot be equal to B.");
        }
        checkReference(reference, "moveAbove");

        list.remove(objectToMove);
        int refIndex = list.indexOf(reference);
        list.add(refIndex + ONE, objectToMove);
        return true;
    }

    /**
     * @param objectToMove
     * @param reference
     * @return
     * @throws IllegalArgumentException if reference is not an element of this list, or is the
     * same as objectToMove.  The list is left unchanged.
     */
    public boolean moveBeneath(@NonNull ElementType objectToMove, @NonNull ElementType reference) {
        if (objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }
        checkReference(reference, "moveBeneath");

        list.remove(objectToMove);
        int refIndex = list.indexOf(reference);
        list.add(refIndex, objectToMove);
        return true;

    }

    private void checkReference(ElementType reference, String operation) {
        if (!list.contains(reference)) {
            throw new IllegalArgumentException(
                    "Illegal argument to " + operation + "(A, B); B must be an element of the list.");
        }
    }

    public boolean moveToBottom(@NonNull ElementType key) {
        list.remove(key);
        list.add(ZERO, key);
        return true;
    }

    public boolean moveUp(@NonNull ElementType key) {
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

    public boolean moveDown(@NonNull ElementType key) {
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
    @NonNull
    public List<ElementType> elements() {
        return list;
    }

    public void addToBottom(@NonNull ElementType element) {
        list.add(ZERO, element);
    }

    public void addToTop(@NonNull ElementType element) {
        list.add(list.size(), element);
    }
}
