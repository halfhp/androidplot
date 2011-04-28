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
