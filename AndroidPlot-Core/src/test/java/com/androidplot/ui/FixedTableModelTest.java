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

package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.mock.MockRectF;
import com.androidplot.ui.FixedTableModel;
import com.androidplot.ui.TableOrder;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static junit.framework.Assert.*;

public class FixedTableModelTest {
    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockRectF.class);
    }

    @Test
    public void testConstructor() throws Exception {
        FixedTableModel model = new FixedTableModel(100, 100, null);
        // TODO
    }

    @Test
    public void testIterator() throws Exception {
        FixedTableModel model = new FixedTableModel(100, 100, TableOrder.COLUMN_MAJOR);

        RectF tableRect = new RectF(0, 0, 1000, 2000);

        Iterator<RectF> it = model.getIterator(tableRect, 5);

        assertTrue(it.hasNext());
        RectF cellRect = it.next();

        assertTrue(it.hasNext());
        cellRect = it.next();

        assertTrue(it.hasNext());
        cellRect = it.next();

        assertTrue(it.hasNext());
        cellRect = it.next();

        assertTrue(it.hasNext());
        cellRect = it.next();

        assertFalse(it.hasNext());
        try {
            cellRect = it.next();
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException ex) {
            // this was expected
        }
    }

    @Test
    public void testColumnMajor() throws Exception {
        FixedTableModel model = new FixedTableModel(300, 500, TableOrder.COLUMN_MAJOR);

        RectF tableRect = new RectF(0, 0, 1000, 2000);

        // test the numElement limit:
        Iterator<RectF> it = model.getIterator(tableRect, 5);
        assertTrue(it.hasNext());
        RectF cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(600f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(900f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertFalse(it.hasNext());

        // test border limit:
        it = model.getIterator(tableRect, 25);
        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(600f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(900f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(600f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(900f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(1500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(1500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(600f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(900f, cellRect.right);
        assertEquals(1500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(1500f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(1500f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(600f, cellRect.left);
        assertEquals(1500f, cellRect.top);
        assertEquals(900f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        //we've reached the limit
        assertFalse(it.hasNext());
    }

    @Test
    public void testRowMajor() throws Exception {
        FixedTableModel model = new FixedTableModel(300, 500, TableOrder.ROW_MAJOR);

        RectF tableRect = new RectF(0, 0, 1000, 2000);

        // test the numElement limit:
        Iterator<RectF> it = model.getIterator(tableRect, 5);
        assertTrue(it.hasNext());
        RectF cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(500f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(1500f, cellRect.bottom);

        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(0f, cellRect.left);
        assertEquals(1500f, cellRect.top);
        assertEquals(300f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        // next column over
        assertTrue(it.hasNext());
        cellRect = it.next();
        assertEquals(300f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(600f, cellRect.right);
        assertEquals(500f, cellRect.bottom);
    }
}