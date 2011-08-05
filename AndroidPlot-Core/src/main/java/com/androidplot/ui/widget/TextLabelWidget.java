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

package com.androidplot.ui.widget;

import android.graphics.*;
import com.androidplot.ui.*;
import com.androidplot.util.FontUtils;

public abstract class TextLabelWidget extends Widget {


    //private Plot plot;
    //private String label;
    private Paint labelPaint;

    private TextOrientationType orientation;

    {
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setAntiAlias(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public TextLabelWidget(SizeMetrics sizeMetrics) {
        this(sizeMetrics, TextOrientationType.HORIZONTAL);
    }

    public TextLabelWidget(SizeMetrics sizeMetrics, TextOrientationType orientation) {
        super(new SizeMetrics(0, SizeLayoutType.ABSOLUTE, 0, SizeLayoutType.ABSOLUTE));
        //this.plot = plot;
        //this.setWidth(labelPaint.measureText(plot.getTitle()));
        //this.setHeight(labelPaint.getFontMetrics().top);
        setSize(sizeMetrics);
        this.orientation = orientation;
    }

    protected abstract String getText();

    /**
     * Sets the dimensions of the widget to exactly contain the text contents
     */
    public void pack() {
        Rect size = FontUtils.getStringDimensions(getText(), getLabelPaint());
        if(size == null) {
            return;
        }
        switch(orientation) {
            case HORIZONTAL:
                this.setSize(new SizeMetrics(size.height(), SizeLayoutType.ABSOLUTE, size.width()+2, SizeLayoutType.ABSOLUTE));
                break;
            case VERTICAL_ASCENDING:
            case VERTICAL_DESCENDING:
                this.setSize(new SizeMetrics(size.width(), SizeLayoutType.ABSOLUTE, size.height()+2, SizeLayoutType.ABSOLUTE));
                break;
        }

    }

    /**
     * Do not call this method directly.  It is indirectly invoked every time a plot is
     * redrawn.
     * @param canvas The Canvas to draw onto
     * @param widgetRect the size and coordinates of this widget
     */
    @Override
    public void doOnDraw(Canvas canvas, RectF widgetRect) {
        String label = getText();
        FontUtils.getStringDimensions(label, labelPaint);
        float vOffset = labelPaint.getFontMetrics().descent;
        PointF start = LayoutManager.getAnchorCoordinates(widgetRect, AnchorPosition.CENTER);

        // BEGIN ROTATION CALCULATION
        //int canvasState = canvas.save(Canvas.ALL_SAVE_FLAG);

        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.translate(start.x, start.y);
            switch (orientation) {
                case HORIZONTAL:
                    break;
                case VERTICAL_ASCENDING:
                    canvas.rotate(-90);
                    break;
                case VERTICAL_DESCENDING:
                    canvas.rotate(90);
                    break;
                default:

                    throw new UnsupportedOperationException("Orientation " + orientation + " not yet implemented for TextLabelWidget.");
            }
            canvas.drawText(label, 0, vOffset, labelPaint);
        } finally {
            //canvas.restoreToCount(canvasState);
            canvas.restore();
        }

        // END ROTATION CALCULATION
    }

    public Paint getLabelPaint() {
        return labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    public TextOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(TextOrientationType orientation) {
        this.orientation = orientation;
    }
}
