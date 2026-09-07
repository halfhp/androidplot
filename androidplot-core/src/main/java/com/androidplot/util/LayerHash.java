// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import java.util.HashMap;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An implementation of {@link Layerable}.  Provides fast element retrieval via hash key in addition to
 * mutable ordering (z indexing) of elements.
 */
public class LayerHash<KeyType, ValueType> implements Layerable<KeyType> {

    private HashMap<KeyType, ValueType> hash;
    private LinkedLayerList<KeyType> zlist;

    {
        hash = new HashMap<>();
        zlist = new LinkedLayerList<>();
    }

    public int size() {
        return zlist.size();
    }


    @Nullable
    public ValueType get(@NonNull KeyType key) {
        return hash.get(key);
    }

    @NonNull
    public List<KeyType> getKeysAsList() {
        return zlist;
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addToTop(@NonNull KeyType key, @NonNull ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
        } else {
            hash.put(key, value);
            zlist.addToTop(key);
        }
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addToBottom(@NonNull KeyType key, @NonNull ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
        } else {
            hash.put(key, value);
            zlist.addToBottom(key);
        }
    }

    public synchronized boolean moveToTop(@NonNull KeyType element) {
        if(!hash.containsKey(element)) {
            return false;
        } else {
            return zlist.moveToTop(element);
        }
    }

    public synchronized boolean moveAbove(@NonNull KeyType objectToMove, @NonNull KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAbove(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return zlist.moveAbove(objectToMove, reference);
        }
    }

    public synchronized boolean moveBeneath(@NonNull KeyType objectToMove, @NonNull KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return zlist.moveBeneath(objectToMove, reference);
        }
    }

    public synchronized boolean moveToBottom(@NonNull KeyType key) {
        if(!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveToBottom(key);
        }
    }

    public synchronized boolean moveUp(@NonNull KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveUp(key);
        }
    }

    public synchronized boolean moveDown(@NonNull KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveDown(key);
        }
    }

    @Override
    @NonNull
    public List<KeyType> elements() {
        return zlist;
    }

    /**
     *
     * @return Ordered list of keys.
     */
    @NonNull
    public List<KeyType> keys() {
        return elements();
    }


    public synchronized boolean remove(@NonNull KeyType key) {
        if(hash.containsKey(key)) {
            hash.remove(key);
            zlist.remove(key);
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    public ValueType getTop() {
        return hash.get(zlist.getLast());
    }

    @NonNull
    public ValueType getBottom() {
        return hash.get(zlist.getFirst());
    }

    @Nullable
    public ValueType getAbove(@NonNull KeyType key) {
        final int index = zlist.indexOf(key);
        if(index >= 0 && index < size() - 1) {
            return hash.get(zlist.get(index + 1));
        }
        return null;
    }

    @Nullable
    public ValueType getBeneath(@NonNull KeyType key) {
        final int index = zlist.indexOf(key);
        if(index > 0) {
            return hash.get(zlist.get(index - 1));
        }
        return null;
    }
}
