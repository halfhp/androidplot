// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;

import java.util.Iterator;

/**
 * Encapsulates the visual aspects of a table; number of rows and columns
 * and the height and width in pixels of each element within the table.
 * There is no support (yet) for variable size cells within a table;  all
 * cells within a table share the same dimensions.
 *
 * The DynamicTableModel provides an Iterator implementation which returns a RectF
 * of each subsequent cell, based on the order of the plot.  Tables with
 * an order of COLUMN_MAJOR are traversed left to right column by column until
 * the end of the row is reached, then proceeding to the next row.
 * Tables with an order of ROW_MAJOR are traversed top to bottom row by row
 * until the end of the row is reached, then proceeding to the next column.
 */
public class DynamicTableModel extends TableModel {

    private int numRows;
    private int numColumns;

    /**
     * Convenience method.  Sets order to ROW_MAJOR.
     * @param numColumns
     * @param numRows
     */
    public DynamicTableModel(int numColumns, int numRows) {
        this(numColumns, numRows, TableOrder.ROW_MAJOR);

    }

    public DynamicTableModel(int numColumns, int numRows, TableOrder order) {
        super(order);
        this.numColumns = numColumns;
        this.numRows = numRows;
    }

    @Override
    public TableModelIterator getIterator(RectF tableRect, int totalElements) {
        return new TableModelIterator(this, tableRect, totalElements);
    }

    /**
     * Calculates the dimensions of a single element of this table with
     * tableRect representing the overall dimensions of the table.
     * @param tableRect Dimensions/position of the table
     * @return a RectF representing the first (top-left) element in
     * the tableRect passed in.
     */
    public RectF getCellRect(RectF tableRect, int numElements) {
        RectF cellRect = new RectF();
        cellRect.left = tableRect.left;
        cellRect.top = tableRect.top;
        cellRect.bottom = tableRect.top + calculateCellSize(tableRect, TableModel.Axis.ROW, numElements);
        cellRect.right = tableRect.left + calculateCellSize(tableRect, TableModel.Axis.COLUMN, numElements);
        return cellRect;
    }

    /**
     * Figure out the size of a single cell across the specified axis.
     * @param tableRect
     * @param axis
     * @param numElementsInTable
     * @return
     */
    private float calculateCellSize(RectF tableRect,
                                    Axis axis,
                                    int numElementsInTable) {
        int axisElements = 0;

        float axisSizePix = 0;
        switch (axis) {
            case ROW:
                axisElements = calculateNumRows(numElementsInTable);
                axisSizePix = tableRect.height();
                break;
            case COLUMN:
                axisElements = calculateNumColumns(numElementsInTable);
                axisSizePix = tableRect.width();
                break;
        }
        return axisSizePix / Math.max(axisElements, 1);
    }

    /**
     * @param totalElements Number of elements to be laid out in the table.
     * @return The number of rows the table will actually use; the configured row count
     * if there is one, otherwise the number of rows needed to fit totalElements into
     * the configured number of columns (rounded up).
     */
    protected int calculateNumRows(int totalElements) {
        if (numRows > 0) {
            return numRows;
        } else if (numColumns > 0) {
            return ceilDiv(totalElements, numColumns);
        } else {
            // unlimited rows and columns (impossible) so default a single row with n columns:
            return 1;
        }
    }

    /**
     * @param totalElements Number of elements to be laid out in the table.
     * @return The number of columns the table will actually use; the configured column count
     * if there is one, otherwise the number of columns needed to fit totalElements into
     * the configured number of rows (rounded up).
     */
    protected int calculateNumColumns(int totalElements) {
        if (numColumns > 0) {
            return numColumns;
        } else if (numRows > 0) {
            return ceilDiv(totalElements, numRows);
        } else {
            // unlimited rows and columns (impossible) so default a single row with n columns:
            return totalElements;
        }
    }

    private static int ceilDiv(int dividend, int divisor) {
        return (dividend + divisor - 1) / divisor;
    }



    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    private class TableModelIterator implements Iterator<RectF> {
        private boolean isOk = true;
        int lastColumn = 0;                     // most recent column iterated
        int lastRow = 0;                        // most recent row iterated
        int lastElement = 0;                    // last element index iterated
        private DynamicTableModel dynamicTableModel;
        private RectF tableRect;
        private RectF lastElementRect;
        private int totalElements;
        private TableOrder order;

        private int calculatedNumElements;
        private int calculatedRows;             // number of rows to be iterated
        private int calculatedColumns;          // number of columns to be iterated

        public TableModelIterator(DynamicTableModel dynamicTableModel, RectF tableRect, int totalElements) {
            this.dynamicTableModel = dynamicTableModel;
            this.tableRect = tableRect;
            this.totalElements = totalElements;
            order = dynamicTableModel.getOrder();

            // use the same row / column counts that size the cells so that the
            // table wraps where the cells run out:
            calculatedRows = dynamicTableModel.calculateNumRows(totalElements);
            calculatedColumns = dynamicTableModel.calculateNumColumns(totalElements);
            calculatedNumElements = calculatedRows * calculatedColumns;
            lastElementRect = dynamicTableModel.getCellRect(tableRect, totalElements);
        }

        @Override
        public boolean hasNext() {
            return isOk && lastElement < calculatedNumElements;
        }

        @Override
        public RectF next() {
            if(!hasNext()) {
                isOk = false;
                throw new IndexOutOfBoundsException();
            }

            if (lastElement == 0) {
                lastElement++;
                return lastElementRect;
            }

            RectF nextElementRect = new RectF(lastElementRect);

            switch (order) {
                case ROW_MAJOR:
                    if (lastColumn >= (calculatedColumns - 1)) {
                        // move to the begining of the next row down:
                        nextElementRect.offsetTo(tableRect.left, lastElementRect.bottom);
                        lastColumn = 0;
                        lastRow++;
                    } else {
                        // move to the next column over:
                        nextElementRect.offsetTo(lastElementRect.right, lastElementRect.top);
                        lastColumn++;
                    }
                    break;
                case COLUMN_MAJOR:
                    if (lastRow >= (calculatedRows - 1)) {
                        // move to the top of the next column over:
                        nextElementRect.offsetTo(lastElementRect.right, tableRect.top);
                        lastRow = 0;
                        lastColumn++;
                    } else {
                        // move to the next row down:
                        nextElementRect.offsetTo(lastElementRect.left, lastElementRect.bottom);
                        lastRow++;
                    }
                    break;
                // unknown/unsupported enum val:
                default:
                    isOk = false;
                    throw new IllegalArgumentException();
            }
            lastElement++;
            lastElementRect = nextElementRect;
            return nextElementRect;

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
