// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import java.util.LinkedList;
import java.util.List;
import androidx.annotation.NonNull;

/**
 * A implementation of {@link Layerable} backed by a {@link LinkedList}.
 * @param <Type>
 */
public class LinkedLayerList<Type> extends LinkedList<Type> implements Layerable<Type> {

    private LayerListOrganizer<Type> organizer = new LayerListOrganizer<>(this);

    @Override
    public boolean moveToTop(@NonNull Type element) {
        return organizer.moveToTop(element);
    }

    @Override
    public boolean moveAbove(@NonNull Type objectToMove, @NonNull Type reference) {
        return organizer.moveAbove(objectToMove, reference);
    }

    @Override
    public boolean moveBeneath(@NonNull Type objectToMove, @NonNull Type reference) {
        return organizer.moveBeneath(objectToMove, reference);
    }

    @Override
    public boolean moveToBottom(@NonNull Type key) {
        return organizer.moveToBottom(key);
    }

    @Override
    public boolean moveUp(@NonNull Type key) {
        return organizer.moveUp(key);
    }

    @Override
    public boolean moveDown(@NonNull Type key) {
        return organizer.moveDown(key);
    }

    @Override
    @NonNull
    public List<Type> elements() {
        return organizer.elements();
    }

    public void addToBottom(@NonNull Type element) {
        organizer.addToBottom(element);
    }

    public void addToTop(@NonNull Type element) {
        organizer.addToTop(element);
    }



}
