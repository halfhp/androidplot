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

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.ZHash;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.YLayoutStyle;

import java.util.HashMap;

public class LayoutManager extends ZHash<Widget, PositionMetrics>
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

    // cache of widget rects
    private HashMap<Widget, DisplayDimensions> widgetRects;

    {
        widgetRects = new HashMap<Widget, DisplayDimensions>();
        anchorPaint = new Paint();
        anchorPaint.setStyle(Paint.Style.FILL);
        anchorPaint.setColor(Color.GREEN);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.GREEN);
        outlinePaint.setStyle(Paint.Style.STROKE);
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
        for(Widget w : getKeysAsList()) {
            w.onPostInit();
        }
    }

    /*@Deprecated
    public LayoutManager(View view) {
    }*/

    public LayoutManager() {
    }

    public void setMarkupEnabled(boolean enabled) {
        setDrawOutlinesEnabled(enabled);
        setDrawAnchorsEnabled(enabled);
        setDrawMarginsEnabled(enabled);
        setDrawPaddingEnabled(enabled);
        setDrawOutlineShadowsEnabled(enabled);

    }

    public AnchorPosition getElementAnchor(Widget element) {
        //return widgets.get(element).getAnchor();
        return get(element).getAnchor();
    }

    public boolean setElementAnchor(Widget element, AnchorPosition anchor) {
        //PositionMetrics metrics = widgets.get(element);
        PositionMetrics metrics = get(element);
        if(metrics == null) {
            return false;
        }
        metrics.setAnchor(anchor);
        return true;
    }

    public static PointF getAnchorCoordinates(RectF widgetRect, AnchorPosition anchorPosition) {
        return PixelUtils.add(new PointF(widgetRect.left, widgetRect.top),
                getAnchorOffset(widgetRect.width(), widgetRect.height(), anchorPosition));
    }

    public static PointF getAnchorCoordinates(float x, float y, float width, float height, AnchorPosition anchorPosition) {
        return getAnchorCoordinates(new RectF(x, y, x+width, y+height), anchorPosition);
    }

    public static PointF getAnchorOffset(float width, float height, AnchorPosition anchorPosition) {
        PointF point = new PointF();
        switch (anchorPosition) {
            case LEFT_TOP:
                break;
            case LEFT_MIDDLE:
                point.set(0, height / 2);
                break;
            case LEFT_BOTTOM:
                point.set(0, height);
                break;
            case RIGHT_TOP:
                point.set(width, 0);
                break;
            case RIGHT_BOTTOM:
                point.set(width, height);
                break;
            case RIGHT_MIDDLE:
                point.set(width, height / 2);
                break;
            case TOP_MIDDLE:
                point.set(width / 2, 0);
                break;
            case BOTTOM_MIDDLE:
                point.set(width / 2, height);
                break;
            case CENTER:
                point.set(width / 2, height / 2);
                break;
            default:
                throw new IllegalArgumentException("Unsupported anchor location: " + anchorPosition);
        }
        return point;
    }


    public PointF getElementCoordinates(float height, float width, RectF viewRect, PositionMetrics metrics) {
        float x = metrics.getxPositionMetric().getPixelValue(viewRect.width()) + viewRect.left;
        float y = metrics.getyPositionMetric().getPixelValue(viewRect.height()) + viewRect.top;
        PointF point = new PointF(x, y);
        return PixelUtils.sub(point, getAnchorOffset(width, height, metrics.getAnchor()));
    }

    public void draw(Canvas canvas) throws PlotRenderException {
        if(isDrawMarginsEnabled()) {
            drawSpacing(canvas, displayDims.canvasRect, displayDims.marginatedRect, marginPaint);
        }
        if (isDrawPaddingEnabled()) {
            drawSpacing(canvas, displayDims.marginatedRect, displayDims.paddedRect, paddingPaint);
        }
        for (Widget widget : getKeysAsList()) {
            //int canvasState = canvas.save(Canvas.ALL_SAVE_FLAG); // preserve clipping etc
            try {
                canvas.save(Canvas.ALL_SAVE_FLAG);
                PositionMetrics metrics = get(widget);
                float elementWidth = widget.getWidthPix(displayDims.paddedRect.width());
                float elementHeight = widget.getHeightPix(displayDims.paddedRect.height());
                PointF coords = getElementCoordinates(elementHeight,
                        elementWidth, displayDims.paddedRect, metrics);

                //RectF widgetRect = new RectF(coords.x, coords.y, coords.x + elementWidth, coords.y + elementHeight);
                DisplayDimensions dims = widgetRects.get(widget);
                //RectF widgetRect = widgetRects.get(widget);

                if (drawOutlineShadowsEnabled) {
                    canvas.drawRect(dims.canvasRect, outlineShadowPaint);
                }

                // not positive why this is, but the rect clipped by clipRect is 1 less than the one drawn by drawRect.
                // so this is necessary to avoid clipping borders.  I suspect that its a floating point
                // jitter issue.
                if (widget.isClippingEnabled()) {
                    //RectF clipRect = new RectF(l-1, t-1, r + 1, b + 1);
                    //canvas.clipRect(clipRect, Region.Op.REPLACE);
                    canvas.clipRect(dims.canvasRect, Region.Op.INTERSECT);
                }
                widget.draw(canvas, dims.canvasRect);

                //RectF marginatedWidgetRect = widget.getMarginatedRect(dims.canvasRect);
                //RectF paddedWidgetRect = widget.getPaddedRect(marginatedWidgetRect);

                if (drawMarginsEnabled) {
                    drawSpacing(canvas, dims.canvasRect, dims.marginatedRect, getMarginPaint());
                }

                if (drawPaddingEnabled) {
                    drawSpacing(canvas, dims.marginatedRect, dims.paddedRect, getPaddingPaint());
                }

                if (drawAnchorsEnabled) {
                    PointF anchorCoords = getAnchorCoordinates(coords.x, coords.y, elementWidth, elementHeight, metrics.getAnchor());
                    drawAnchor(canvas, anchorCoords);
                }


                if (drawOutlinesEnabled) {
                    outlinePaint.setAntiAlias(true);
                    canvas.drawRect(dims.canvasRect, outlinePaint);
                }
            } finally {
                //canvas.restoreToCount(canvasState);  // restore clipping etc.
                canvas.restore();
            }
        }
    }

    private void drawSpacing(Canvas canvas, RectF outer, RectF inner, Paint paint) {
        //int saved = canvas.save(Canvas.ALL_SAVE_FLAG);
        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(inner, Region.Op.DIFFERENCE);
            canvas.drawRect(outer, paint);
            //canvas.restoreToCount(saved);
        } finally {
            canvas.restore();
        }
    }

    protected void drawAnchor(Canvas canvas, PointF coords) {
        float anchorSize = 4;
        canvas.drawRect(coords.x-anchorSize, coords.y-anchorSize, coords.x+anchorSize, coords.y+anchorSize, anchorPaint);

    }

    /**
     *
     * @param element The Widget to position.  Used for positioning both new and existing widgets.
     * @param x X-Coordinate of the top left corner of element.  When using RELATIVE, must be a value between 0 and 1.
     * @param xLayoutStyle LayoutType to use when orienting this element's X-Coordinate.
     * @param y Y_VALS_ONLY-Coordinate of the top-left corner of element.  When using RELATIVE, must be a value between 0 and 1.
     * @param yLayoutStyle LayoutType to use when orienting this element's Y_VALS_ONLY-Coordinate.
     */
    public void position(Widget element, float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle) {
        position(element, x, xLayoutStyle, y, yLayoutStyle, AnchorPosition.LEFT_TOP);
    }

    public void position(Widget element, float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        addToTop(element, new PositionMetrics(x, xLayoutStyle, y, yLayoutStyle, anchor));
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

    private void delegateOnTouchEvt(View v, MotionEvent event) {

    }

    @Override
    public void layout(final DisplayDimensions dims) {
        this.displayDims = dims;

        widgetRects.clear();
        for (Widget widget : getKeysAsList()) {
            PositionMetrics metrics = get(widget);
            float elementWidth = widget.getWidthPix(displayDims.paddedRect.width());
            float elementHeight = widget.getHeightPix(displayDims.paddedRect.height());
            PointF coords = getElementCoordinates(elementHeight,
                    elementWidth, displayDims.paddedRect, metrics);

            RectF canvasRect = new RectF(coords.x, coords.y, coords.x + elementWidth, coords.y + elementHeight);
            RectF marginatedWidgetRect = widget.getMarginatedRect(canvasRect);
            RectF paddedWidgetRect = widget.getPaddedRect(marginatedWidgetRect);
            DisplayDimensions dd = new DisplayDimensions(canvasRect, marginatedWidgetRect, paddedWidgetRect);
            widgetRects.put(widget, dd);
            widget.layout(dd);
        }
    }
}
