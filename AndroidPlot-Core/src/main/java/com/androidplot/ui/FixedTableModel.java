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

public class FixedTableModel extends TableModel {
    private float cellWidth;
    private float cellHeight;
    protected FixedTableModel(float cellWidth, float cellHeight, TableOrder order) {
        super(order);
        setCellWidth(cellWidth);
        setCellHeight(cellHeight);
    }

    @Override
    public Iterator<RectF> getIterator(RectF tableRect, int totalElements) {
        return new FixedTableModelIterator(this, tableRect, totalElements);
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public float getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }

    private class FixedTableModelIterator implements Iterator<RectF> {

        private FixedTableModel model;
        private RectF tableRect;
        private RectF lastRect;
        private int numElements;
        private int lastElement;
        protected FixedTableModelIterator(FixedTableModel model, RectF tableRect, int numElements) {
            this.model = model;
            this.tableRect = tableRect;
            this.numElements = numElements;
            lastRect = new RectF(
                    tableRect.left,
                    tableRect.top,
                    tableRect.left + model.getCellWidth(),
                    tableRect.top + model.getCellHeight());
        }

        @Override
        public boolean hasNext() {
            // was this the last element or is there no room in either axis for another cell?
            return !(lastElement >= numElements || (isColumnFinished() && isRowFinished()));
        }

        private boolean isColumnFinished() {
            return lastRect.bottom + model.getCellHeight() > tableRect.height();
            }

        private boolean isRowFinished() {
            return lastRect.right + model.getCellWidth() > tableRect.width();
            }

        @Override
        public RectF next() {
            try {
                if (lastElement == 0) {
                    return lastRect;
                }

                if (lastElement >= numElements) {
                    throw new IndexOutOfBoundsException();
                }
                switch (model.getOrder()) {
                    case ROW_MAJOR:
                        if (isColumnFinished()) {
                            moveOverAndUp();
                        } else {
                            moveDown();
                        }
                        break;
                    case COLUMN_MAJOR:
                        if (isRowFinished()) {
                            moveDownAndBack();
                        } else {
                            moveOver();
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                return lastRect;
            } finally {
                lastElement++;
            }
        }

        private void moveDownAndBack() {
            //RectF rect = new RectF(lastRect);
            lastRect.offsetTo(tableRect.left, lastRect.bottom);
            //return rect;
        }

        private void moveOverAndUp() {
            //RectF rect = new RectF(lastRect);
            lastRect.offsetTo(lastRect.right, tableRect.top);
            //return rect;
        }

        private void moveOver() {
            //RectF rect = new RectF(lastRect);
            lastRect.offsetTo(lastRect.right, lastRect.top);
            //return rect;
        }

        private void moveDown() {
            //RectF rect = new RectF(lastRect);
            lastRect.offsetTo(lastRect.left, lastRect.bottom);
            //return rect;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
