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
