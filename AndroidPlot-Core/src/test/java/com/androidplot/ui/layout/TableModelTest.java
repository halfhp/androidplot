package com.androidplot.ui.layout;

import android.graphics.RectF;
import com.androidplot.mock.MockRectF;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TableModelTest {
    @Before
    public void setUp() throws Exception {
        Mockit.setUpMocks(MockRectF.class);
    }

    @Test
    public void testConstructor() throws Exception {
        TableModel model = new TableModel(5, 5, TableOrientation.COLUMN_MAJOR);
        // TODO
    }

    @Test
    public void testGetCellRect() throws Exception {

        // square table, both rows and columns defined:
        TableModel model = new TableModel(5, 5);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        RectF cellRect = model.getCellRect(tableRect, 10);
        assertEquals(200f, cellRect.width());

        // only rows defined:
        model = new TableModel(5, 0);
        cellRect = model.getCellRect(tableRect, 10);
        assertEquals(200f, cellRect.width());

        // only columns defined:
        model = new TableModel(0, 5);
        cellRect = model.getCellRect(tableRect, 10);
        assertEquals(400f, cellRect.height());
    }

    @Test public void testIterator() throws Exception {
        TableModel model = new TableModel(2, 2);

        RectF tableRect = new RectF(0, 0, 1000, 2000);

        // should stop at 4 iterations since the table can only hold that many:
        TableModel.TableModelIterator it = model.getIterator(tableRect, 10);
        int iterations = 0;
        while(it.hasNext()) {
            it.next();
            iterations++;
        }
        assertEquals(4, iterations);

        // now set a dynamic number of columns.  iterations should equal however
        // many elements we throw at it:
        model = new TableModel(2, 0);
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
        TableModel model = new TableModel(2, 2);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        int createdCells = 4;
        TableModel.TableModelIterator it = model.getIterator(tableRect, createdCells);
        

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
        /*model = new TableModel(2, 0);
        tableRect = new RectF(0, 0, 1000, 2000);
        createdCells = 4;
        it = model.getIterator(tableRect, createdCells);*/



    }


    @Test
    public void testColumnMajorIteration() throws Exception {

        // square table, both rows and columns defined:
        TableModel model = new TableModel(2, 2, TableOrientation.COLUMN_MAJOR);
        RectF tableRect = new RectF(0, 0, 1000, 2000);
        int createdCells = 4;
        TableModel.TableModelIterator it = model.getIterator(tableRect, createdCells);


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
        TableModel model = new TableModel(0, 1);
        RectF tableRect = new RectF(0, 0, 1000, 1000);
        int createdCells = 4;
        TableModel.TableModelIterator it = model.getIterator(tableRect, createdCells);



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
