// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Iterator;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DynamicTableModelTest {

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

    @Test
    public void testGetCellRect_dynamicAxisSizedByComputedRowOrColumnCount() throws Exception {
        RectF tableRect = new RectF(0, 0, 1000, 900);

        // 2 fixed rows, 4 elements -> 2 columns of 500px:
        DynamicTableModel model = new DynamicTableModel(0, 2);
        RectF cellRect = model.getCellRect(tableRect, 4);
        assertEquals(500f, cellRect.width());
        assertEquals(450f, cellRect.height());

        // 3 fixed rows, 4 elements -> 2 columns (round up), not 1:
        model = new DynamicTableModel(0, 3);
        cellRect = model.getCellRect(tableRect, 4);
        assertEquals(500f, cellRect.width());
        assertEquals(300f, cellRect.height());

        // 3 fixed columns, 4 elements -> 2 rows of 450px:
        model = new DynamicTableModel(3, 0);
        cellRect = model.getCellRect(tableRect, 4);
        assertEquals(1000f / 3, cellRect.width(), 0.001f);
        assertEquals(450f, cellRect.height());
    }

    @Test
    public void testIterator_roundsDynamicAxisUp() throws Exception {
        // 3 fixed rows, 4 elements: needs 2 columns, so 6 cells of capacity:
        TableModel model = new DynamicTableModel(0, 3);
        RectF tableRect = new RectF(0, 0, 1000, 900);
        Iterator<RectF> it = model.getIterator(tableRect, 4);
        int iterations = 0;
        while (it.hasNext()) {
            it.next();
            iterations++;
        }
        assertEquals(6, iterations);
    }

    @Test
    public void testRowMajorIteration_dynamicColumns_wrapsAtComputedColumnCount() throws Exception {
        // 3 fixed rows, 4 elements -> 2 columns x 3 rows on a 1000x900 table:
        TableModel model = new DynamicTableModel(0, 3, TableOrder.ROW_MAJOR);
        RectF tableRect = new RectF(0, 0, 1000, 900);
        Iterator<RectF> it = model.getIterator(tableRect, 4);

        // cell 0 (top-left)
        RectF cellRect = it.next();
        assertEquals(new RectF(0, 0, 500, 300), cellRect);

        // cell 1 (top-right)
        cellRect = it.next();
        assertEquals(new RectF(500, 0, 1000, 300), cellRect);

        // cell 2 (middle-left; wrapped to the next row)
        cellRect = it.next();
        assertEquals(new RectF(0, 300, 500, 600), cellRect);

        // cell 3 (middle-right)
        cellRect = it.next();
        assertEquals(new RectF(500, 300, 1000, 600), cellRect);
    }

    @Test
    public void testColumnMajorIteration_dynamicColumns_fillsTableWidth() throws Exception {
        // 2 fixed rows, 4 elements -> 2 columns x 2 rows on a 1000x1000 table:
        TableModel model = new DynamicTableModel(0, 2, TableOrder.COLUMN_MAJOR);
        RectF tableRect = new RectF(0, 0, 1000, 1000);
        Iterator<RectF> it = model.getIterator(tableRect, 4);

        // cell 0 (top-left)
        RectF cellRect = it.next();
        assertEquals(new RectF(0, 0, 500, 500), cellRect);

        // cell 1 (bottom-left)
        cellRect = it.next();
        assertEquals(new RectF(0, 500, 500, 1000), cellRect);

        // cell 2 (top-right)
        cellRect = it.next();
        assertEquals(new RectF(500, 0, 1000, 500), cellRect);

        // cell 3 (bottom-right)
        cellRect = it.next();
        assertEquals(new RectF(500, 500, 1000, 1000), cellRect);
    }

    @Test
    public void testColumnMajorIteration_dynamicRows_wrapsAtComputedRowCount() throws Exception {
        // 2 fixed columns, 4 elements -> 2 rows x 2 columns on a 1000x1000 table:
        TableModel model = new DynamicTableModel(2, 0, TableOrder.COLUMN_MAJOR);
        RectF tableRect = new RectF(0, 0, 1000, 1000);
        Iterator<RectF> it = model.getIterator(tableRect, 4);

        assertEquals(new RectF(0, 0, 500, 500), it.next());
        assertEquals(new RectF(0, 500, 500, 1000), it.next());
        assertEquals(new RectF(500, 0, 1000, 500), it.next());
        assertEquals(new RectF(500, 500, 1000, 1000), it.next());
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
