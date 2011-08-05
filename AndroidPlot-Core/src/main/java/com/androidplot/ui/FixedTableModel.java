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
