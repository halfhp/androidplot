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

import java.util.HashMap;
import java.util.List;

/**
 * Concrete implementation of ZIndexable.  Provides fast element retrieval via hash key.  Also provides
 * mutable ordering (z indexing) of elements.
 */
public class ZHash<KeyType, ValueType> implements ZIndexable<KeyType> {

    private HashMap<KeyType, ValueType> hash;
    //private LinkedList<KeyType> zlist;
    //private ListOrganizer<KeyType> listOrganizer;
    private ZLinkedList<KeyType> zlist;

    {
        hash = new HashMap<KeyType, ValueType>();
        zlist = new ZLinkedList<KeyType>();
        //listOrganizer = new ListOrganizer<KeyType>(zlist);
    }

    public int size() {
        return zlist.size();
    }


    public ValueType get(KeyType key) {
        return hash.get(key);
    }

    public List<KeyType> getKeysAsList() {
        return zlist;
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addToTop(KeyType key, ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
           //throw new IllegalArgumentException("Key already exists in series structure...duplicates not permitted.");
        } else {
            hash.put(key, value);
            zlist.addToTop(key);
            //zlist.addToTop(key);
        }
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addToBottom(KeyType key, ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
           //throw new IllegalArgumentException("Key already exists in series structure...duplicates not permitted.");
        } else {
            hash.put(key, value);
            zlist.addToBottom(key);
            //zlist.addToBottom(key);
        }
    }

    public synchronized boolean moveToTop(KeyType element) {
        if(!hash.containsKey(element)) {
            return false;
        } else {
            return zlist.moveToTop(element);
        }
    }

    public synchronized boolean moveAbove(KeyType objectToMove, KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAbove(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return zlist.moveAbove(objectToMove, reference);
        }
    }

    public synchronized boolean moveBeneath(KeyType objectToMove, KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return zlist.moveBeneath(objectToMove, reference);
        }
    }

    public synchronized boolean moveToBottom(KeyType key) {
        if(!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveToBottom(key);
        }
    }

    public synchronized boolean moveUp(KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveUp(key);
        }
    }

    public synchronized boolean moveDown(KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return zlist.moveDown(key);
        }
    }

    @Override
    public List<KeyType> elements() {
        return zlist;
    }

    /**
     *
     * @return Ordered list of keys.
     */
    public List<KeyType> keys() {
        return elements();
    }


    public synchronized boolean remove(KeyType key) {
        if(hash.containsKey(key)) {
            hash.remove(key);
            zlist.remove(key);
            return true;
        } else {
            return false;
        }
    }
}
