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
