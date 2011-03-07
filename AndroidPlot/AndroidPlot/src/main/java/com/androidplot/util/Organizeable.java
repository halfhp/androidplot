package com.androidplot.util;

public interface Organizeable<ElementType> {
    public boolean moveToEnd(ElementType element);


    public boolean moveAfter(ElementType objectToMove, ElementType reference);

    public boolean moveBefore(ElementType objectToMove, ElementType reference);

    public boolean moveToFront(ElementType key);

    public boolean moveBack(ElementType key);

    public boolean moveForward(ElementType key);

    public void addToFront(ElementType element);

    public void addToBack(ElementType element);
}