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
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.TableOrder;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static junit.framework.Assert.assertEquals;

public class DynamicTableModelTest {
    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockRectF.class);
    }

    @Test
    public void testConstructor() throws Exception {
        TableModel model = new DynamicTableModel(5, 5, TableOrder.COLUMN_MAJOR);
        // TODO
    }

    @Test
    public void testGetCellRect() throws Exception {

        // square table, both rows and columns defined:
        DynamicTableModel model = new DynamicTableModel(5, 5);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        RectF cellRect = model.getCellRect(tableRect, 10);
        assertEquals(200f, cellRect.width());

        // only rows defined:
        model = new DynamicTableModel(5, 0);
        cellRect = model.getCellRect(tableRect, 10);
        assertEquals(200f, cellRect.width());

        // only columns defined:
        model = new DynamicTableModel(0, 5);
        cellRect = model.getCellRect(tableRect, 10);
        assertEquals(400f, cellRect.height());
    }

    @Test public void testIterator() throws Exception {
        TableModel model = new DynamicTableModel(2, 2);

        RectF tableRect = new RectF(0, 0, 1000, 2000);

        // should stop at 4 iterations since the table can only hold that many:
        Iterator<RectF> it = model.getIterator(tableRect, 10);
        int iterations = 0;
        while(it.hasNext()) {
            it.next();
            iterations++;
        }
        assertEquals(4, iterations);

        // now set a dynamic number of columns.  iterations should equal however
        // many elements we throw at it:
        model = new DynamicTableModel(2, 0);
        it = model.getIterator(tableRect, 10);
        iterations = 0;
        while(it.hasNext()) {
            it.next();
            iterations++;
        }
        assertEquals(10, iterations);


    }

    @Test
    public void testRowMajorIteration() throws Exception {

        // square table, both rows and columns defined:
        TableModel model = new DynamicTableModel(2, 2);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        int createdCells = 4;
        Iterator<RectF> it = model.getIterator(tableRect, createdCells);
        

        // 2x2:
        // cell 0 (top-left
        RectF cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(500f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 1 (top-right)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(500f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(1000f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 2 (bottom-left)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(0f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(500f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        // cell 3 (bottom-right)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(500f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(1000f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        // 2xN:
        /*model = new DynamicTableModel(2, 0);
        tableRect = new RectF(0, 0, 1000, 2000);
        createdCells = 4;
        it = model.getIterator(tableRect, createdCells);*/



    }


    @Test
    public void testColumnMajorIteration() throws Exception {

        // square table, both rows and columns defined:
        TableModel model = new DynamicTableModel(2, 2, TableOrder.COLUMN_MAJOR);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        int createdCells = 4;
        Iterator<RectF> it = model.getIterator(tableRect, createdCells);


        // 2x2 fixed:
        // cell 0 (top-left
        RectF cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(500f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 1 (bottom-left)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(0f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(500f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);

        // cell 2 (bottom-left)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(500f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(1000f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 3 (bottom-right)
        cellRect = it.next();
        assertEquals(500f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(500f, cellRect.left);
        assertEquals(1000f, cellRect.top);
        assertEquals(1000f, cellRect.right);
        assertEquals(2000f, cellRect.bottom);
    }

    @Test
    public void testSingleRowIteration() throws Exception {
        // square table, both rows and columns defined:
        TableModel model = new DynamicTableModel(0, 1);
        RectF tableRect = new RectF(0, 0, 1000, 1000);
        int createdCells = 4;
        Iterator<RectF> it = model.getIterator(tableRect, createdCells);



        // 2x2 fixed:
        // cell 0 (top-left
        RectF cellRect = it.next();
        assertEquals(250f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(0f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(250f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 1
        cellRect = it.next();
        assertEquals(250f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(250f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(500f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 2
        cellRect = it.next();
        assertEquals(250f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(500f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(750f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);

        // cell 3
        cellRect = it.next();
        assertEquals(250f, cellRect.width());
        assertEquals(1000f, cellRect.height());
        assertEquals(750f, cellRect.left);
        assertEquals(0f, cellRect.top);
        assertEquals(1000f, cellRect.right);
        assertEquals(1000f, cellRect.bottom);
    }
}
