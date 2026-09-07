// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LinkedLayerListOrganizerTest {
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
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        LinkedList<Object> list = new LinkedList<>(Arrays.asList(obj1, obj2, obj3));
        LayerListOrganizer<Object> organizer = new LayerListOrganizer<>(list);

        organizer.moveAbove(obj1, obj2);
        assertEquals(Arrays.asList(obj2, obj1, obj3), list);

        organizer.moveAbove(obj2, obj3);
        assertEquals(Arrays.asList(obj1, obj3, obj2), list);
    }

    @Test
    public void testMoveAbove_unknownReference_throwsAndLeavesListUnchanged() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        LinkedList<Object> list = new LinkedList<>(Arrays.asList(obj1, obj2, obj3));
        LayerListOrganizer<Object> organizer = new LayerListOrganizer<>(list);

        try {
            organizer.moveAbove(obj1, new Object());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        assertEquals(Arrays.asList(obj1, obj2, obj3), list);
    }

    @Test
    public void testMoveBeneath() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        LinkedList<Object> list = new LinkedList<>(Arrays.asList(obj1, obj2, obj3));
        LayerListOrganizer<Object> organizer = new LayerListOrganizer<>(list);

        organizer.moveBeneath(obj3, obj2);
        assertEquals(Arrays.asList(obj1, obj3, obj2), list);

        organizer.moveBeneath(obj2, obj1);
        assertEquals(Arrays.asList(obj2, obj1, obj3), list);
    }

    @Test
    public void testMoveBeneath_unknownReference_throwsAndLeavesListUnchanged() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        LinkedList<Object> list = new LinkedList<>(Arrays.asList(obj1, obj2, obj3));
        LayerListOrganizer<Object> organizer = new LayerListOrganizer<>(list);

        try {
            organizer.moveBeneath(obj1, new Object());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        assertEquals(Arrays.asList(obj1, obj2, obj3), list);
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

        LayerListOrganizer organizer = new LayerListOrganizer(list);

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
