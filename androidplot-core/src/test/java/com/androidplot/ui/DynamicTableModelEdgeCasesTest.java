// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Edge cases of {@link DynamicTableModel} not covered by {@link DynamicTableModelTest}: an
 * unconstrained table, the row/column accessors and iterator misuse.
 */
@RunWith(RobolectricTestRunner.class)
public class DynamicTableModelEdgeCasesTest {

    private static final RectF TABLE_RECT = new RectF(0, 0, 1000, 500);

    @Test
    public void unconstrainedTable_laysOutASingleRow() {
        DynamicTableModel model = new DynamicTableModel(0, 0);
        assertEquals(1, model.calculateNumRows(7));
        assertEquals(7, model.calculateNumColumns(7));

        // 7 cells across a 1000px wide table, each the full height:
        RectF cellRect = model.getCellRect(TABLE_RECT, 7);
        assertEquals(1000f / 7, cellRect.width(), 0.001f);
        assertEquals(500f, cellRect.height(), 0.001f);

        Iterator<RectF> it = model.getIterator(TABLE_RECT, 7);
        int iterations = 0;
        RectF last = null;
        while (it.hasNext()) {
            RectF rect = it.next();
            if (last != null) {
                // every cell sits to the right of the previous one, on the same row:
                assertEquals(last.right, rect.left, 0.001f);
                assertEquals(last.top, rect.top, 0.001f);
            }
            last = rect;
            iterations++;
        }
        assertEquals(7, iterations);
    }

    @Test
    public void rowAndColumnSetters_roundTrip() {
        DynamicTableModel model = new DynamicTableModel(2, 3);
        assertEquals(2, model.getNumColumns());
        assertEquals(3, model.getNumRows());

        model.setNumColumns(5);
        model.setNumRows(0);
        assertEquals(5, model.getNumColumns());
        assertEquals(0, model.getNumRows());
        // and the new values drive the layout: 12 elements in 5 columns need 3 rows
        assertEquals(3, model.calculateNumRows(12));
    }

    @Test
    public void iterator_nextPastTheEnd_throwsAndStaysExhausted() {
        DynamicTableModel model = new DynamicTableModel(1, 1);
        Iterator<RectF> it = model.getIterator(TABLE_RECT, 1);
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
        try {
            it.next();
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        assertFalse(it.hasNext());
    }

    @Test
    public void iterator_remove_isUnsupported() {
        Iterator<RectF> it = new DynamicTableModel(1, 1).getIterator(TABLE_RECT, 1);
        try {
            it.remove();
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
}
