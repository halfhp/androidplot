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

package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;

import com.androidplot.ui.widget.Widget;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.LinkedLayerList;

public class LayoutManager extends LinkedLayerList<Widget>
        implements View.OnTouchListener, Resizable {
    private boolean drawAnchorsEnabled = false;
    private Paint anchorPaint;
    private boolean drawOutlinesEnabled = false;
    private Paint outlinePaint;
    private boolean drawOutlineShadowsEnabled = false;
    private Paint outlineShadowPaint;
    private boolean drawMarginsEnabled = false;
    private Paint marginPaint;
    private boolean drawPaddingEnabled = false;
    private Paint paddingPaint;
    private DisplayDimensions displayDims = new DisplayDimensions();

    {
        anchorPaint = new Paint();
        anchorPaint.setStyle(Paint.Style.FILL);
        anchorPaint.setColor(Color.GREEN);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.GREEN);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStrokeWidth(2);
        marginPaint = new Paint();
        marginPaint.setColor(Color.YELLOW);
        marginPaint.setStyle(Paint.Style.FILL);
        marginPaint.setAlpha(200);
        paddingPaint= new Paint();
        paddingPaint.setColor(Color.BLUE);
        paddingPaint.setStyle(Paint.Style.FILL);
        paddingPaint.setAlpha(200);
    }

    /**
     * Invoked immediately following XML configuration.
     */
    public synchronized void onPostInit() {
        for(Widget w : elements()) {
            w.onPostInit();
        }
    }

    public LayoutManager() {
    }

    public void setMarkupEnabled(boolean enabled) {
        setDrawOutlinesEnabled(enabled);
        setDrawAnchorsEnabled(enabled);
        setDrawMarginsEnabled(enabled);
        setDrawPaddingEnabled(enabled);
        setDrawOutlineShadowsEnabled(enabled);
    }

    public void draw(Canvas canvas) {
        if(isDrawMarginsEnabled()) {
            drawSpacing(canvas, displayDims.canvasRect, displayDims.marginatedRect, marginPaint);
        }
        if (isDrawPaddingEnabled()) {
            drawSpacing(canvas, displayDims.marginatedRect, displayDims.paddedRect, paddingPaint);
        }
        for (Widget widget : elements()) {
            try {
                canvas.save();
                PositionMetrics metrics = widget.getPositionMetrics();
                float elementWidth = widget.getWidthPix(displayDims.paddedRect.width());
                float elementHeight = widget.getHeightPix(displayDims.paddedRect.height());
                PointF coords = Widget.calculateCoordinates(elementHeight,
                        elementWidth, displayDims.paddedRect, metrics);

                DisplayDimensions dims = widget.getWidgetDimensions();

                if (drawOutlineShadowsEnabled) {
                    canvas.drawRect(dims.canvasRect, outlineShadowPaint);
                }

                // not positive why this is, but the rect clipped by clipRect is 1 less than the one drawn by drawRect.
                // so this is necessary to avoid clipping borders.  I suspect that its a floating point
                // jitter issue.
                if (widget.isClippingEnabled()) {
                    canvas.clipRect(dims.canvasRect, Region.Op.INTERSECT);
                }
                widget.draw(canvas);

                if (drawMarginsEnabled) {
                    drawSpacing(canvas, dims.canvasRect, dims.marginatedRect, getMarginPaint());
                }

                if (drawPaddingEnabled) {
                    drawSpacing(canvas, dims.marginatedRect, dims.paddedRect, getPaddingPaint());
                }

                if (drawAnchorsEnabled) {
                    PointF anchorCoords =
                            Widget.getAnchorCoordinates(coords.x, coords.y, elementWidth,
                                    elementHeight, metrics.getAnchor());
                    drawAnchor(canvas, anchorCoords);
                }


                if (drawOutlinesEnabled) {
                    canvas.drawRect(dims.canvasRect, outlinePaint);
                }
            } finally {
                canvas.restore();
            }
        }
    }

    private static void drawSpacing(Canvas canvas, RectF outer, RectF inner, Paint paint) {
        try {
            canvas.save();
            canvas.clipRect(inner, Region.Op.DIFFERENCE);
            canvas.drawRect(outer, paint);
        } finally {
            canvas.restore();
        }
    }

    protected void drawAnchor(Canvas canvas, PointF coords) {
        float anchorSize = 4;
        canvas.drawRect(coords.x-anchorSize, coords.y-anchorSize, coords.x+anchorSize, coords.y+anchorSize, anchorPaint);

    }

    public boolean isDrawOutlinesEnabled() {
        return drawOutlinesEnabled;
    }

    public void setDrawOutlinesEnabled(boolean drawOutlinesEnabled) {
        this.drawOutlinesEnabled = drawOutlinesEnabled;
    }

    public Paint getOutlinePaint() {
        return outlinePaint;
    }

    public void setOutlinePaint(Paint outlinePaint) {
        this.outlinePaint = outlinePaint;
    }

    public boolean isDrawAnchorsEnabled() {
        return drawAnchorsEnabled;
    }

    public void setDrawAnchorsEnabled(boolean drawAnchorsEnabled) {
        this.drawAnchorsEnabled = drawAnchorsEnabled;
    }

    public boolean isDrawMarginsEnabled() {
        return drawMarginsEnabled;
    }

    public void setDrawMarginsEnabled(boolean drawMarginsEnabled) {
        this.drawMarginsEnabled = drawMarginsEnabled;
    }

    public Paint getMarginPaint() {
        return marginPaint;
    }

    public void setMarginPaint(Paint marginPaint) {
        this.marginPaint = marginPaint;
    }

    public boolean isDrawPaddingEnabled() {
        return drawPaddingEnabled;
    }

    public void setDrawPaddingEnabled(boolean drawPaddingEnabled) {
        this.drawPaddingEnabled = drawPaddingEnabled;
    }

    public Paint getPaddingPaint() {
        return paddingPaint;
    }

    public void setPaddingPaint(Paint paddingPaint) {
        this.paddingPaint = paddingPaint;
    }

    public boolean isDrawOutlineShadowsEnabled() {
        return drawOutlineShadowsEnabled;
    }

    public void setDrawOutlineShadowsEnabled(boolean drawOutlineShadowsEnabled) {
        this.drawOutlineShadowsEnabled = drawOutlineShadowsEnabled;
        if(drawOutlineShadowsEnabled && outlineShadowPaint == null) {
            // use a default shadow effect in the case where none has been set:
            outlineShadowPaint = new Paint();
            outlineShadowPaint.setColor(Color.DKGRAY);
            outlineShadowPaint.setStyle(Paint.Style.FILL);
            outlineShadowPaint.setShadowLayer(3, 5, 5, Color.BLACK);
        }
    }

    public Paint getOutlineShadowPaint() {
        return outlineShadowPaint;
    }

    public void setOutlineShadowPaint(Paint outlineShadowPaint) {
        this.outlineShadowPaint = outlineShadowPaint;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * Recalculates layouts for all widgets using last set
     * DisplayDimensions.  Care should be excersized when choosing when
     * to call this method as it is a relatively slow operation.
     */
    public void refreshLayout() {
        for (Widget widget : elements()) {
            widget.layout(displayDims);
        }
    }

    @Override
    public void layout(final DisplayDimensions dims) {
        this.displayDims = dims;

        refreshLayout();
    }
}
