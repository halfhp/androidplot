/*
 * Copyright 2015 AndroidPlot.com
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
import com.androidplot.ui.*;
import com.androidplot.util.FontUtils;

public class TextLabelWidget extends Widget {
    private String text;
    private Paint labelPaint;
    private TextOrientation orientation;
    private boolean autoPackEnabled = true;

    {
        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setAntiAlias(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        setClippingEnabled(false);
    }

    public TextLabelWidget(LayoutManager layoutManager, Size size) {
        this(layoutManager, size, TextOrientation.HORIZONTAL);
    }

    public TextLabelWidget(LayoutManager layoutManager, String title, Size size, TextOrientation orientation) {
        this(layoutManager, size, orientation);
        setText(title);
    }

    public TextLabelWidget(LayoutManager layoutManager, Size size, TextOrientation orientation) {
        super(layoutManager, new Size(0, SizeMode.ABSOLUTE, 0, SizeMode.ABSOLUTE));
        setSize(size);
        this.orientation = orientation;
    }

    @Override
    protected void onMetricsChanged(Size olds, Size news) {
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

    /**
     * Sets the dimensions of the widget to exactly contain the text contents
     */
    public void pack() {
        Rect size = FontUtils.getStringDimensions(text, getLabelPaint());
        if(size == null) {
            return;
        }
        switch(orientation) {
            case HORIZONTAL:
                setSize(new Size(size.height(), SizeMode.ABSOLUTE, size.width()+2, SizeMode.ABSOLUTE));
                break;
            case VERTICAL_ASCENDING:
            case VERTICAL_DESCENDING:
                setSize(new Size(size.width(), SizeMode.ABSOLUTE, size.height()+2, SizeMode.ABSOLUTE));
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

        float vOffset = labelPaint.getFontMetrics().descent;
        PointF start = getAnchorCoordinates(widgetRect,
                Anchor.CENTER);

        try {
            canvas.save();
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
            canvas.restore();
        }
    }

    public Paint getLabelPaint() {
        return labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;

        // when paint changes, packing params change too so run
        // to see if we need to resize:
        if(autoPackEnabled) {
            pack();
        }
    }

    public TextOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(TextOrientation orientation) {
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
        this.text = text;
        if(autoPackEnabled) {
            pack();
        }
    }

    public String getText() {
        return text;
    }
}
