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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;

public class ListOrganizerTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMoveToTop() throws Exception {

    }

    @Test
    public void testMoveAbove() throws Exception {

    }

    @Test
    public void testMoveBeneath() throws Exception {

    }

    @Test
    public void testMoveToBottom() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        LinkedList list = new LinkedList();

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);

        assertEquals(obj1, list.getFirst());
        assertEquals(obj3, list.getLast());

        ListOrganizer organizer = new ListOrganizer(list);

        organizer.moveToBottom(obj3);

        assertEquals(obj2, list.getLast());
        assertEquals(obj3, list.getFirst());

    }

    @Test
    public void testMoveUp() throws Exception {

    }

    @Test
    public void testMoveDown() throws Exception {

    }

    @Test
    public void testAddFirst() throws Exception {

    }

    @Test
    public void testAddLast() throws Exception {

    }
}
