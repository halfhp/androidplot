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

    //private float cellWidth;
    //private float cellHeight;
    //private TableSizingMethod rowSizingMethod;
    //private TableSizingMethod columnSizingMethod;


    private int numRows;
    private int numColumns;

    private Float cellWidth;
    private Float cellHeight;

    private CellSizingMethod rowSizingMethod;
    private CellSizingMethod columnSizingMethod;

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
        //this.cellWidth = cellWidth;
        //this.rowSizingMethod = rowSizingMethod;
        this.numRows = numRows;
        //this.cellHeight = cellHeight;
        //this.columnSizingMethod = columnSizingMethod;
        //this.order = order;
    }

    /*public DynamicTableModel(Number colVal, CellSizingMethod colSzMethod, Number rowVal, CellSizingMethod rowSzMethod, TableOrder order) {
        if(colVal == null || rowVal == null) {
            throw new NullPointerException();
        }
        columnSizingMethod = colSzMethod;
        switch(columnSizingMethod) {
            case FILL:
                numColumns = colVal.intValue();
                break;
            case FIXED:
                cellWidth = colVal.floatValue();
                break;
        }
        rowSzMethod = rowSzMethod;
    }*/

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
        //cellRect.bottom = getElementHeightPix(tableRect);
        cellRect.bottom = tableRect.top + calculateCellSize(tableRect, TableModel.Axis.ROW, numElements);
        //cellRect.right = getElementWidthPix(tableRect);
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
        //float elementSizeInPix = 0;
        int axisElements = 0;
        
        float axisSizePix = 0;
        switch (axis) {
            case ROW:
                //elementSizeInPix = cellHeight;
                axisElements = numRows;
                axisSizePix = tableRect.height();
                break;
            case COLUMN:
                //elementSizeInPix = cellWidth;
                axisElements = numColumns;
                axisSizePix = tableRect.width();
                break;
        }
        //if (elementSizeInPix != 0) {
        //    return elementSizeInPix;
        if(axisElements != 0) {
            return axisSizePix / axisElements;
        } else {
            return axisSizePix / numElementsInTable;
        }
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

/*    public void setCellWidth(Float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public Float getCellWidth() {
        return cellWidth;
    }

    public Float getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(Float cellHeight) {
        this.cellHeight = cellHeight;
    }*/

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

            // unlimited columns:
            if(dynamicTableModel.getNumColumns() == 0 && dynamicTableModel.getNumRows() >= 1) {
                calculatedRows = dynamicTableModel.getNumRows();

                // round up:
                calculatedColumns = new Float((totalElements / (float) calculatedRows) + 0.5).intValue();
            } else if(dynamicTableModel.getNumRows() == 0 && dynamicTableModel.getNumColumns() >= 1) {
                //order = TableOrder.ROW_MAJOR;
                calculatedColumns = dynamicTableModel.getNumColumns();
                calculatedRows = new Float((totalElements / (float) calculatedColumns) + 0.5).intValue();
            // unlimited rows and columns (impossible) so default a single row with n columns:
            }else if(dynamicTableModel.getNumColumns() == 0 && dynamicTableModel.getNumRows() == 0) {
                calculatedRows = 1;
                calculatedColumns = totalElements;
            } else {
                //order = dynamicTableModel.getOrder();
                calculatedRows = dynamicTableModel.getNumRows();
                calculatedColumns = dynamicTableModel.getNumColumns();
            }
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
                    if (dynamicTableModel.getNumColumns() > 0 && lastColumn >= (dynamicTableModel.getNumColumns() - 1)) {
                        // move to the begining of the next row down:// move to the begining of the next row down:
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
                    if (dynamicTableModel.getNumRows() > 0 && lastRow >= (dynamicTableModel.getNumRows() - 1)) {
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
