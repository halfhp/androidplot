package com.androidplot.ui.layout;

import android.graphics.RectF;

import java.util.Iterator;

/**
 * Encapsulates the visual aspects of a table; number of rows and columns
 * and the height and width in pixels of each element within the table.
 * There is no support (yet) for variable size cells within a table;  all
 * cells within a table share the same dimensions.
 *
 * The TableModel provides an Iterator implementation which returns a RectF
 * of each subsequent cell, based on the orientation of the plot.  Tables with
 * an orientation of COLUMN_MAJOR are traversed left to right column by column until
 * the end of the row is reached, then proceeding to the next row.
 * Tables with an orientation of ROW_MAJOR are traversed top to bottom row by row
 * until the end of the row is reached, then proceeding to the next column.
 */
public class TableModel {

    public static class TableModelIterator implements Iterator<RectF> {
        private boolean isOk = true;
        int lastColumn = 0;                     // most recent column iterated
        int lastRow = 0;                        // most recent row iterated
        int lastElement = 0;                    // last element index iterated
        private TableModel tableModel;
        private RectF tableRect;
        private RectF lastElementRect;
        private int totalElements;
        private TableOrientation orientation;

        private int calculatedNumElements;
        private int calculatedRows;             // number of rows to be iterated
        private int calculatedColumns;          // number of columns to be iterated

        public TableModelIterator(TableModel tableModel, RectF tableRect, int totalElements) {
            this.tableModel = tableModel;
            this.tableRect = tableRect;
            this.totalElements = totalElements;
            orientation = tableModel.getOrientation();

            // unlimited columns:
            if(tableModel.getNumColumns() == 0 && tableModel.getNumRows() >= 1) {
                calculatedRows = tableModel.getNumRows();

                // round up:
                calculatedColumns = new Float((totalElements / (float) calculatedRows) + 0.5).intValue();
            } else if(tableModel.getNumRows() == 0 && tableModel.getNumColumns() >= 1) {
                //orientation = TableOrientation.ROW_MAJOR;
                calculatedColumns = tableModel.getNumColumns();
                calculatedRows = new Float((totalElements / (float) calculatedColumns) + 0.5).intValue();
            // unlimited rows and columns (impossible) so default a single row with n columns:
            }else if(tableModel.getNumColumns() == 0 && tableModel.getNumRows() == 0) {
                calculatedRows = 1;
                calculatedColumns = totalElements;
            } else {
                //orientation = tableModel.getOrientation();
                calculatedRows = tableModel.getNumRows();
                calculatedColumns = tableModel.getNumColumns();
            }
            calculatedNumElements = calculatedRows * calculatedColumns;
            lastElementRect = tableModel.getCellRect(tableRect, totalElements);
        }

        @Override
        public boolean hasNext() {
            if (isOk && lastElement < calculatedNumElements) {
                    /*lastElement < totalElements &&
                    lastColumn < calculatedColumns &&
                    lastRow < calculatedRows) {*/
                return true;
            } else {
                return false;
            }
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

            switch (orientation) {
                case ROW_MAJOR:
                    if (tableModel.getNumColumns() > 0 && lastColumn >= (tableModel.getNumColumns() - 1)) {
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
                    if (tableModel.getNumRows() > 0 && lastRow >= (tableModel.getNumRows() - 1)) {
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

    private enum Axis {
        ROW,
        COLUMN
    }

    //private float cellWidth;
    //private float cellHeight;
    //private TableSizingMethod rowSizingMethod;
    //private TableSizingMethod columnSizingMethod;
    private TableOrientation orientation;

    private int numRows;
    private int numColumns;

    /**
     * Convenience method.  Sets orientation to ROW_MAJOR.
     * @param numColumns
     * @param numRows
     */
    public TableModel(int numColumns, int numRows) {
        this(numColumns, numRows, TableOrientation.ROW_MAJOR);

    }

    public TableModel(int numColumns, int numRows, TableOrientation orientation) {
        this.numColumns = numColumns;
        //this.cellWidth = cellWidth;
        //this.rowSizingMethod = rowSizingMethod;
        this.numRows = numRows;
        //this.cellHeight = cellHeight;
        //this.columnSizingMethod = columnSizingMethod;
        this.orientation = orientation;
    }

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

    public TableOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(TableOrientation orientation) {
        this.orientation = orientation;
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
}
