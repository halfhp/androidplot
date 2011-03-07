package com.androidplot.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a mechanism to quickly retrieve elements via hash key.  Also provides
 * mutable ordering of elements.
 */
public class ArrangeableHash<KeyType, ValueType> {

    private HashMap<KeyType, ValueType> hash;
    private LinkedList<KeyType> list;
    private ListOrganizer<KeyType> listOrganizer;

    {
        hash = new HashMap<KeyType, ValueType>();
        list = new LinkedList<KeyType>();
        listOrganizer = new ListOrganizer<KeyType>(list);
    }

    public int size() {
        return list.size();
    }


    public ValueType get(KeyType key) {
        return hash.get(key);
    }

    public List<KeyType> getKeysAsList() {
        return list;
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addLast(KeyType key, ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
           //throw new IllegalArgumentException("Key already exists in series structure...duplicates not permitted.");
        } else {
            hash.put(key, value);
            listOrganizer.addToBack(key);
            //list.addToBack(key);
        }
    }

    /**
     * If key already exists within the structure, it's value is replaced with the new value and
     * it's existing order is maintained.
     * @param key
     * @param value
     */
    public synchronized void addFirst(KeyType key, ValueType value) {
        if(hash.containsKey(key)) {
            hash.put(key, value);
           //throw new IllegalArgumentException("Key already exists in series structure...duplicates not permitted.");
        } else {
            hash.put(key, value);
            listOrganizer.addToFront(key);
            //list.addToFront(key);
        }
    }


    public synchronized boolean moveToTop(KeyType element) {
        if(!hash.containsKey(element)) {
            return false;
        } else {
            return listOrganizer.moveToEnd(element);
            //int widgetIndex = widgetOrder.indexOf(element);
            //list.remove(element);
            //list.addToBack(element);
            //return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveAbove(KeyType objectToMove, KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAfter(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return listOrganizer.moveAfter(objectToMove, reference);

            //list.remove(objectToMove);
            //int refIndex = list.indexOf(reference);
            //list.add(refIndex+1, objectToMove);
            //return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveBeneath(KeyType objectToMove, KeyType reference) {
        if(objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }
        if(!hash.containsKey(reference) || !hash.containsKey(objectToMove)) {
            return false;
        } else {
            return listOrganizer.moveBefore(objectToMove, reference);
//
//            list.remove(objectToMove);
//            int refIndex = list.indexOf(reference);
//            list.add(refIndex, objectToMove);
//            return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveToBottom(KeyType key) {
        if(!hash.containsKey(key)) {
            return false;
        } else {
            return listOrganizer.moveToFront(key);
//            //int widgetIndex = widgetOrder.indexOf(key);
//            list.remove(key);
//            list.addToFront(key);
//            return true;
//            //widgetOrder.remove(key);
        }
    }

    public synchronized boolean moveUp(KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return listOrganizer.moveBack(key);
        }
//        int widgetIndex = list.indexOf(key);
//        if(widgetIndex == -1) {
//            // key not found:
//            return false;
//        }
//        if(widgetIndex >= list.size()-1) {
//            // already at the top:
//            return true;
//        }
//
//        KeyType widgetAbove = list.get(widgetIndex+1);
//        return moveAfter(key, widgetAbove);
    }

    public synchronized boolean moveDown(KeyType key) {
        if (!hash.containsKey(key)) {
            return false;
        } else {
            return listOrganizer.moveForward(key);
        }
//        int widgetIndex = list.indexOf(key);
//        if(widgetIndex == -1) {
//            // key not found:
//            return false;
//        }
//        if(widgetIndex <= 0) {
//            // already at the bottom:
//            return true;
//        }
//
//        KeyType widgetBeneath = list.get(widgetIndex-1);
//        return moveBefore(key, widgetBeneath);
    }


    public synchronized boolean remove(KeyType key) {
        if(hash.containsKey(key)) {
            hash.remove(key);
            list.remove(key);
            return true;
        } else {
            return false;
        }
    }
}
