// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.test.AndroidplotTest;
import org.junit.Test;
import java.util.Iterator;
import static junit.framework.Assert.*;

public class FixedTableModelTest extends AndroidplotTest {

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

    /**
     * Same layout as {@link #testColumnMajor()} but with the table rect offset from the origin;
     * cells should wrap at the table's right edge, not at its width.
     */
    @Test
    public void testColumnMajor_offsetTableRect() throws Exception {
        FixedTableModel model = new FixedTableModel(300, 500, TableOrder.COLUMN_MAJOR);

        final float offsetX = 300;
        final float offsetY = 400;
        RectF tableRect = new RectF(offsetX, offsetY, offsetX + 1000, offsetY + 2000);

        Iterator<RectF> it = model.getIterator(tableRect, 25);

        // 3 columns x 4 rows fit in a 1000x2000 table with 300x500 cells:
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                assertTrue("row " + row + " col " + col, it.hasNext());
                RectF cellRect = it.next();
                assertEquals(offsetX + (col * 300f), cellRect.left);
                assertEquals(offsetY + (row * 500f), cellRect.top);
                assertEquals(offsetX + ((col + 1) * 300f), cellRect.right);
                assertEquals(offsetY + ((row + 1) * 500f), cellRect.bottom);
            }
        }

        // we've reached the limit
        assertFalse(it.hasNext());
    }

    /**
     * Same layout as {@link #testRowMajor()} but with the table rect offset from the origin;
     * cells should wrap at the table's bottom edge, not at its height.
     */
    @Test
    public void testRowMajor_offsetTableRect() throws Exception {
        FixedTableModel model = new FixedTableModel(300, 500, TableOrder.ROW_MAJOR);

        final float offsetX = 300;
        final float offsetY = 400;
        RectF tableRect = new RectF(offsetX, offsetY, offsetX + 1000, offsetY + 2000);

        Iterator<RectF> it = model.getIterator(tableRect, 25);

        // 4 rows x 3 columns fit in a 1000x2000 table with 300x500 cells:
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 4; row++) {
                assertTrue("row " + row + " col " + col, it.hasNext());
                RectF cellRect = it.next();
                assertEquals(offsetX + (col * 300f), cellRect.left);
                assertEquals(offsetY + (row * 500f), cellRect.top);
                assertEquals(offsetX + ((col + 1) * 300f), cellRect.right);
                assertEquals(offsetY + ((row + 1) * 500f), cellRect.bottom);
            }
        }

        // we've reached the limit
        assertFalse(it.hasNext());
    }
}