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
