package com.androidplot.util;

import java.util.LinkedList;
import java.util.List;

public class ZLinkedList<Type> extends LinkedList<Type> implements ZIndexable<Type> {

    //private LinkedList<Type> list;
    private ListOrganizer<Type> organizer;

    {
        //list = new LinkedList<Type>();
        organizer = new ListOrganizer<Type>(this);
    }


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
