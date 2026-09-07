// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import java.util.LinkedList;
import java.util.List;

/**
 * A implementation of {@link Layerable} backed by a {@link LinkedList}.
 * @param <Type>
 */
public class LinkedLayerList<Type> extends LinkedList<Type> implements Layerable<Type> {

    private LayerListOrganizer<Type> organizer = new LayerListOrganizer<>(this);

    @Override
    public boolean moveToTop(Type element) {
        return organizer.moveToTop(element);
    }

    @Override
    public boolean moveAbove(Type objectToMove, Type reference) {
        return organizer.moveAbove(objectToMove, reference);
    }

    @Override
    public boolean moveBeneath(Type objectToMove, Type reference) {
        return organizer.moveBeneath(objectToMove, reference);
    }

    @Override
    public boolean moveToBottom(Type key) {
        return organizer.moveToBottom(key);
    }

    @Override
    public boolean moveUp(Type key) {
        return organizer.moveUp(key);
    }

    @Override
    public boolean moveDown(Type key) {
        return organizer.moveDown(key);
    }

    @Override
    public List<Type> elements() {
        return organizer.elements();
    }

    public void addToBottom(Type element) {
        organizer.addToBottom(element);
    }

    public void addToTop(Type element) {
        organizer.addToTop(element);
    }



}
