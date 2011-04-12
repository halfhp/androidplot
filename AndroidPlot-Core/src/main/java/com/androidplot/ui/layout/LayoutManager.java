package com.androidplot.ui.layout;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.ArrangeableHash;
import com.androidplot.util.PixelUtils;

public class LayoutManager extends ArrangeableHash<Widget, PositionMetrics> implements View.OnTouchListener {
    private boolean drawAnchorsEnabled = true;
    private Paint anchorPaint;
    private boolean drawOutlinesEnabled = true;
    private Paint outlinePaint;
    private boolean drawOutlineShadowsEnabled = true;
    private Paint outlineShadowPaint;
    private boolean drawMarginsEnabled = true;
    private Paint marginPaint;
    private boolean drawPaddingEnabled = true;
    private Paint paddingPaint;

    {
        anchorPaint = new Paint();
        anchorPaint.setStyle(Paint.Style.FILL);
        anchorPaint.setColor(Color.GREEN);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.GREEN);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlineShadowPaint = new Paint();
        outlineShadowPaint.setColor(Color.DKGRAY);
        outlineShadowPaint.setStyle(Paint.Style.FILL);
        outlineShadowPaint.setShadowLayer(3, 5, 5, Color.BLACK);
        marginPaint = new Paint();
        marginPaint.setColor(Color.YELLOW);
        marginPaint.setStyle(Paint.Style.FILL);
        marginPaint.setAlpha(200);
        paddingPaint= new Paint();
        paddingPaint.setColor(Color.BLUE);
        paddingPaint.setStyle(Paint.Style.FILL);
        paddingPaint.setAlpha(200);
    }

    /*@Deprecated
    public LayoutManager(View view) {
    }*/

    public LayoutManager() {
    }

    public void disableAllMarkup() {
        setDrawOutlinesEnabled(false);
        setDrawAnchorsEnabled(false);
        setDrawMarginsEnabled(false);
        setDrawPaddingEnabled(false);
        setDrawOutlineShadowsEnabled(false);

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

    public synchronized void draw(Canvas canvas, RectF canvasRect, RectF marginRect, RectF paddingRect) throws PlotRenderException {
        if(isDrawMarginsEnabled()) {
            drawSpacing(canvas, canvasRect, marginRect, marginPaint);
        }
        if(isDrawPaddingEnabled()) {
            drawSpacing(canvas, marginRect, paddingRect, paddingPaint);
        }
        for(Widget widget : getKeysAsList()) {
            int canvasState = canvas.save(Canvas.ALL_SAVE_FLAG); // preserve clipping etc
            try {
            PositionMetrics metrics = get(widget);
            float elementWidth = widget.getWidthPix(paddingRect.width());
            float elementHeight = widget.getHeightPix(paddingRect.height());
            PointF coords = getElementCoordinates(elementHeight, elementWidth, paddingRect, metrics);

            // remove the floating point to allow clipping to work:
            int t = (int)(coords.y + 0.5);
            int b = (int)(coords.y + elementHeight + 0.5);
            int l = (int)(coords.x + 0.5);
            int r = (int)(coords.x + elementWidth + 0.5);
            RectF widgetRect = new RectF(l, t, r, b);

            if(drawOutlineShadowsEnabled) {
                canvas.drawRect(widgetRect, outlineShadowPaint);
            }

            // not positive why this is, but the rect clipped by clipRect is 1 less than the one drawn by drawRect.
            // so this is necessary to avoid clipping borders.  I suspect that its a floating point
            // jitter issue.
            if(widget.isClippingEnabled()) {
                RectF clipRect = new RectF(l, t, r + 1, b + 1);
                canvas.clipRect(clipRect, Region.Op.REPLACE);
            }
            widget.draw(canvas, widgetRect);

            RectF marginatedWidgetRect = widget.getMarginatedRect(widgetRect);
            RectF paddedWidgetRect = widget.getPaddedRect(marginatedWidgetRect);

            if(drawMarginsEnabled) {
                drawSpacing(canvas, widgetRect, marginatedWidgetRect, getMarginPaint());
            }

            if(drawPaddingEnabled) {
                drawSpacing(canvas, marginatedWidgetRect, paddedWidgetRect, getPaddingPaint());
            }

            if(drawAnchorsEnabled) {
                PointF anchorCoords = getAnchorCoordinates(coords.x, coords.y, elementWidth, elementHeight, metrics.getAnchor());
                drawAnchor(canvas, anchorCoords);
            }


            if(drawOutlinesEnabled) {
                canvas.drawRect(widgetRect, outlinePaint);
            }
            } finally {
                canvas.restoreToCount(canvasState);  // restore clipping etc.
            }
        }
    }

    private void drawSpacing(Canvas canvas, RectF outer, RectF inner, Paint paint) {
        int saved = canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.clipRect(inner, Region.Op.DIFFERENCE);
        canvas.drawRect(outer, paint);
        canvas.restoreToCount(saved);
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
        addLast(element, new PositionMetrics(x, xLayoutStyle, y, yLayoutStyle, anchor));
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
}
