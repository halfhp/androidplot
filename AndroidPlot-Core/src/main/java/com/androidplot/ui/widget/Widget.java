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
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.*;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.PixelUtils;

/**
 * A Widget is a graphical sub-element of a Plot that can be positioned relative
 * to the bounds of the Plot.
 */
public abstract class Widget implements BoxModelable, Resizable {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private boolean clippingEnabled = true;
    private BoxModel boxModel = new BoxModel();
    private SizeMetrics sizeMetrics;
    private DisplayDimensions plotDimensions = new DisplayDimensions();
    private DisplayDimensions widgetDimensions = new DisplayDimensions();
    private boolean isVisible = true;
    private PositionMetrics positionMetrics;
    private LayoutManager layoutManager;

    public Widget(LayoutManager layoutManager, SizeMetric heightMetric, SizeMetric widthMetric) {
        this(layoutManager, new SizeMetrics(heightMetric, widthMetric));
    }

    public Widget(LayoutManager layoutManager, SizeMetrics sizeMetrics) {
        this.layoutManager = layoutManager;
        SizeMetrics oldSize = this.sizeMetrics;
        setSize(sizeMetrics);
        onMetricsChanged(oldSize, sizeMetrics);
    }

    public DisplayDimensions getWidgetDimensions() {
        return widgetDimensions;
    }

    public AnchorPosition getAnchor() {
        return getPositionMetrics().getAnchor();
    }

    public void setAnchor(AnchorPosition anchor) {
        getPositionMetrics().setAnchor(anchor);
    }


    /**
     * Same as {@link #position(float, com.androidplot.ui.XLayoutStyle, float, com.androidplot.ui.YLayoutStyle, com.androidplot.ui.AnchorPosition)}
     * but with the anchor parameter defaulted to the upper left corner.
     * @param x
     * @param xLayoutStyle
     * @param y
     * @param yLayoutStyle
     */
    public void position(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle) {
        position(x, xLayoutStyle, y, yLayoutStyle, AnchorPosition.LEFT_TOP);
    }

    /**
     * @param x            X-Coordinate of the top left corner of element.  When using RELATIVE, must be a value between 0 and 1.
     * @param xLayoutStyle LayoutType to use when orienting this element's X-Coordinate.
     * @param y            Y_VALS_ONLY-Coordinate of the top-left corner of element.  When using RELATIVE, must be a value between 0 and 1.
     * @param yLayoutStyle LayoutType to use when orienting this element's Y_VALS_ONLY-Coordinate.
     * @param anchor       The point of reference used by this positioning call.
     */
    public void position(float x, XLayoutStyle xLayoutStyle, float y,
                         YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setPositionMetrics(new PositionMetrics(x, xLayoutStyle, y, yLayoutStyle, anchor));
        layoutManager.addToTop(this);
    }

    /**
     * Can be overridden by subclasses to respond to resizing events.
     *
     * @param oldSize
     * @param newSize
     */
    protected void onMetricsChanged(SizeMetrics oldSize, SizeMetrics newSize) {
    }

    /**
     * Can be overridden by subclasses to handle any final resizing etc. that
     * can only be done after XML configuration etc. has completed.
     */
    public void onPostInit() {
    }

    /**
     * Determines whether or not point lies within this Widget.
     *
     * @param point
     * @return
     */
    public boolean containsPoint(PointF point) {
        //return outlineRect != null && outlineRect.contains(point.x, point.y);
        return widgetDimensions.canvasRect.contains(point.x, point.y);
    }

    public void setSize(SizeMetrics sizeMetrics) {
        this.sizeMetrics = sizeMetrics;
    }


    public void setWidth(float width) {
        sizeMetrics.getWidthMetric().setValue(width);
    }

    public void setWidth(float width, SizeLayoutType layoutType) {
        sizeMetrics.getWidthMetric().set(width, layoutType);
    }

    public void setHeight(float height) {
        sizeMetrics.getHeightMetric().setValue(height);
    }

    public void setHeight(float height, SizeLayoutType layoutType) {
        sizeMetrics.getHeightMetric().set(height, layoutType);
    }

    public SizeMetric getWidthMetric() {
        return sizeMetrics.getWidthMetric();
    }

    public SizeMetric getHeightMetric() {
        return sizeMetrics.getHeightMetric();
    }

    public float getWidthPix(float size) {
        return sizeMetrics.getWidthMetric().getPixelValue(size);
    }

    public float getHeightPix(float size) {
        return sizeMetrics.getHeightMetric().getPixelValue(size);
    }

    public RectF getMarginatedRect(RectF widgetRect) {
        return boxModel.getMarginatedRect(widgetRect);
    }

    public RectF getPaddedRect(RectF widgetMarginRect) {
        return boxModel.getPaddedRect(widgetMarginRect);
    }

    public void setMarginRight(float marginRight) {
        boxModel.setMarginRight(marginRight);
    }

    public void setMargins(float left, float top, float right, float bottom) {
        boxModel.setMargins(left, top, right, bottom);
    }

    public void setPadding(float left, float top, float right, float bottom) {
        boxModel.setPadding(left, top, right, bottom);
    }

    public float getMarginTop() {
        return boxModel.getMarginTop();
    }

    public void setMarginTop(float marginTop) {
        boxModel.setMarginTop(marginTop);
    }

    public float getMarginBottom() {
        return boxModel.getMarginBottom();
    }

    @Override
    public float getPaddingLeft() {
        return boxModel.getPaddingLeft();
    }

    @Override
    public void setPaddingLeft(float paddingLeft) {
        boxModel.setPaddingLeft(paddingLeft);
    }

    @Override
    public float getPaddingTop() {
        return boxModel.getPaddingTop();
    }

    @Override
    public void setPaddingTop(float paddingTop) {
        boxModel.setPaddingTop(paddingTop);
    }

    @Override
    public float getPaddingRight() {
        return boxModel.getPaddingRight();
    }

    @Override
    public void setPaddingRight(float paddingRight) {
        boxModel.setPaddingRight(paddingRight);
    }

    @Override
    public float getPaddingBottom() {
        return boxModel.getPaddingBottom();
    }

    @Override
    public void setPaddingBottom(float paddingBottom) {
        boxModel.setPaddingBottom(paddingBottom);
    }

    @SuppressWarnings("SameParameterValue")
    public void setMarginBottom(float marginBottom) {
        boxModel.setMarginBottom(marginBottom);
    }

    public float getMarginLeft() {
        return boxModel.getMarginLeft();
    }

    public void setMarginLeft(float marginLeft) {
        boxModel.setMarginLeft(marginLeft);
    }

    public float getMarginRight() {
        return boxModel.getMarginRight();
    }

    /**
     * Causes the pixel dimensions used for rendering this Widget
     * to be recalculated.  Should be called any time a parameter that factors
     * into this Widget's size or position is altered.
     */
    public synchronized void refreshLayout() {
        if(positionMetrics == null) {
            // make sure positionMetrics have been set.  this method can be
            // automatically called during xml configuration of certain params
            // before the widget is fully configured.
            return;
        }
        float elementWidth = getWidthPix(plotDimensions.paddedRect.width());
        float elementHeight = getHeightPix(plotDimensions.paddedRect.height());
        PointF coords = getElementCoordinates(elementHeight,
                elementWidth, plotDimensions.paddedRect, positionMetrics);

        RectF widgetRect = new RectF(coords.x, coords.y,
                coords.x + elementWidth, coords.y + elementHeight);
        RectF marginatedWidgetRect = getMarginatedRect(widgetRect);
        RectF paddedWidgetRect = getPaddedRect(marginatedWidgetRect);
        widgetDimensions = new DisplayDimensions(widgetRect,
                marginatedWidgetRect, paddedWidgetRect);
    }

    @Override
    public synchronized void layout(final DisplayDimensions plotDimensions) {
        this.plotDimensions = plotDimensions;
        refreshLayout();
    }

    public PointF getElementCoordinates(float height, float width, RectF viewRect, PositionMetrics metrics) {
            float x = metrics.getXPositionMetric().getPixelValue(viewRect.width()) + viewRect.left;
            float y = metrics.getYPositionMetric().getPixelValue(viewRect.height()) + viewRect.top;
            PointF point = new PointF(x, y);
            return PixelUtils.sub(point, getAnchorOffset(width, height, metrics.getAnchor()));
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

    public static PointF getAnchorCoordinates(RectF widgetRect, AnchorPosition anchorPosition) {
            return PixelUtils.add(new PointF(widgetRect.left, widgetRect.top),
                    getAnchorOffset(widgetRect.width(), widgetRect.height(), anchorPosition));
        }

        public static PointF getAnchorCoordinates(float x, float y, float width, float height, AnchorPosition anchorPosition) {
            return getAnchorCoordinates(new RectF(x, y, x+width, y+height), anchorPosition);
        }

    public void draw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        //outlineRect = widgetRect;
        if (isVisible()) {
            if (backgroundPaint != null) {
                drawBackground(canvas, widgetDimensions.canvasRect);
            }

            /* RectF marginatedRect = new RectF(outlineRect.left + marginLeft,
          outlineRect.top + marginTop,
          outlineRect.right - marginRight,
          outlineRect.bottom - marginBottom);*/

            /*RectF marginatedRect = boxModel.getMarginatedRect(widgetRect);
            RectF paddedRect = boxModel.getPaddedRect(marginatedRect);*/
            doOnDraw(canvas, widgetDimensions.paddedRect);

            if (borderPaint != null) {
                drawBorder(canvas, widgetDimensions.paddedRect);
            }
        }
    }

    protected void drawBorder(Canvas canvas, RectF paddedRect) {
        canvas.drawRect(paddedRect, borderPaint);
    }

    protected void drawBackground(Canvas canvas, RectF widgetRect) {
        canvas.drawRect(widgetRect, backgroundPaint);
    }

    /**
     * @param canvas     The Canvas to draw onto
     * @param widgetRect the size and coordinates of this widget
     */
    protected abstract void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException;

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public boolean isClippingEnabled() {
        return clippingEnabled;
    }

    public void setClippingEnabled(boolean clippingEnabled) {
        this.clippingEnabled = clippingEnabled;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public PositionMetrics getPositionMetrics() {
        return positionMetrics;
    }

    public void setPositionMetrics(PositionMetrics positionMetrics) {
        this.positionMetrics = positionMetrics;
    }
}
