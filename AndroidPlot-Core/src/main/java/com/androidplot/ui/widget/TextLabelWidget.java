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

package com.androidplot.ui.widget;

import android.graphics.*;
import android.util.Log;
import com.androidplot.ui.*;
import com.androidplot.util.FontUtils;

public class TextLabelWidget extends Widget {
    private static final String TAG = TextLabelWidget.class.getName();

    private String text;
    private Paint labelPaint;

    private TextOrientationType orientation;

    private boolean autoPackEnabled = true;

    {
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setAntiAlias(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public TextLabelWidget(LayoutManager layoutManager, SizeMetrics sizeMetrics) {
        this(layoutManager, sizeMetrics, TextOrientationType.HORIZONTAL);
    }

    public TextLabelWidget(LayoutManager layoutManager, String title, SizeMetrics sizeMetrics, TextOrientationType orientation) {
        this(layoutManager, sizeMetrics, orientation);
        setText(title);
    }

    public TextLabelWidget(LayoutManager layoutManager, SizeMetrics sizeMetrics, TextOrientationType orientation) {
        super(layoutManager, new SizeMetrics(0, SizeLayoutType.ABSOLUTE, 0, SizeLayoutType.ABSOLUTE));
        //this.plot = plot;
        //this.setWidth(labelPaint.measureText(plot.getTitle()));
        //this.setHeight(labelPaint.getFontMetrics().top);
        setSize(sizeMetrics);
        this.orientation = orientation;
    }

    @Override
    protected void onMetricsChanged(SizeMetrics olds, SizeMetrics news) {
        if(autoPackEnabled) {
            pack();
        }
    }

    @Override
    public void onPostInit() {
       if(autoPackEnabled) {
           pack();
       }
    }

    //protected abstract String getText();

    /**
     * Sets the dimensions of the widget to exactly contain the text contents
     */
    public void pack() {
        Log.d(TAG, "Packing...");
        Rect size = FontUtils.getStringDimensions(text, getLabelPaint());
        if(size == null) {
            Log.w(TAG, "Attempt to pack empty text.");
            return;
        }
        switch(orientation) {
            case HORIZONTAL:
                setSize(new SizeMetrics(size.height(), SizeLayoutType.ABSOLUTE, size.width()+2, SizeLayoutType.ABSOLUTE));
                break;
            case VERTICAL_ASCENDING:
            case VERTICAL_DESCENDING:
                setSize(new SizeMetrics(size.width(), SizeLayoutType.ABSOLUTE, size.height()+2, SizeLayoutType.ABSOLUTE));
                break;
        }
        refreshLayout();

    }

    /**
     * Do not call this method directly.  It is indirectly invoked every time a plot is
     * redrawn.
     * @param canvas The Canvas to draw onto
     * @param widgetRect the size and coordinates of this widget
     */
    @Override
    public void doOnDraw(Canvas canvas, RectF widgetRect) {
        if(text == null || text.length() == 0) {
            return;
        }
        //FontUtils.getStringDimensions(text, labelPaint);
        float vOffset = labelPaint.getFontMetrics().descent;
        PointF start = getAnchorCoordinates(widgetRect,
                AnchorPosition.CENTER);

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
            canvas.drawText(text, 0, vOffset, labelPaint);
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

        // when paint changes, packing params change too so check
        // to see if we need to resize:
        if(autoPackEnabled) {
            pack();
        }
    }

    public TextOrientationType getOrientation() {
        return orientation;
    }

    public void setOrientation(TextOrientationType orientation) {
        this.orientation = orientation;
        if(autoPackEnabled) {
            pack();
        }
    }

    public boolean isAutoPackEnabled() {
        return autoPackEnabled;
    }

    public void setAutoPackEnabled(boolean autoPackEnabled) {
        this.autoPackEnabled = autoPackEnabled;
        if(autoPackEnabled) {
            pack();
        }
    }

    public void setText(String text) {
        Log.d(TAG, "Setting textLabel to: " + text);
        this.text = text;
        if(autoPackEnabled) {
            pack();
        }
    }

    public String getText() {
        return text;
    }
}
