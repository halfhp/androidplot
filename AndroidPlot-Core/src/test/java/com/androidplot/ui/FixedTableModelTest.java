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