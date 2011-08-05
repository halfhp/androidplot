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
