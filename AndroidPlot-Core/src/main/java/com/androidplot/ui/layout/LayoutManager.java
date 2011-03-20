package com.androidplot.ui.layout;

import android.graphics.*;
import android.view.View;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.ArrangeableHash;
import com.androidplot.util.PointUtils;

public class LayoutManager extends ArrangeableHash<Widget, PositionMetrics>{

    //private HashMap<Widget, PositionMetrics> widgets;
    //private LinkedList<Widget> widgetOrder;

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

    //private View view;

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

    @Deprecated
    public LayoutManager(View view) {
        //this.view = view;
    }
    public LayoutManager() {
        //this.view = view;
    }

    public void disableAllMarkup() {
        setDrawOutlinesEnabled(false);
        setDrawAnchorsEnabled(false);
        setDrawMarginsEnabled(false);
        setDrawPaddingEnabled(false);
        setDrawOutlineShadowsEnabled(false);

    }


    /*
    public int size() {
        //return widgets.size();
        return widgetRegistry.size();
    }
    */

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
    
    /*
    protected Point getAnchorCoordinates(Canvas canvas, Widget element, AnchorPosition anchor) {
        //return getElementCoordinates(canvas, element, AnchorPosition.LEFT_TOP);
        Point anchorCoords = getElementCoordinates(canvas, element);
        anchorCoords.position(getAnchorOffset(element.getWidthPix(canvas.getWidth()), element.getHeightPix(canvas.getFontHeight()), anchor));
        return anchorCoords;
    }
    */

    public static PointF getAnchorCoordinates(RectF widgetRect, AnchorPosition anchorPosition) {
        //PointF point = new Point(widgetRect.left, widgetRect.top);
        //point.add(getAnchorOffset(widgetRect.width(), widgetRect.height(), anchorPosition));
        return PointUtils.add(new PointF(widgetRect.left, widgetRect.top),
                getAnchorOffset(widgetRect.width(), widgetRect.height(), anchorPosition));
        //return point;
    }

    /*public static Point getAnchorCoordinates(Point topLeft, Dimension size, AnchorPosition anchorPosition) {
        //return getAnchorCoordinates();
        Point point = new Point(topLeft);
        point.add(getAnchorOffset(size.getWidth(), size.getHeight(), anchorPosition));
        //Point offset = getAnchorOffset(width, height, anchorPosition);
        //point.setX(lastColumn)
        return point;
    }*/

    public static PointF getAnchorCoordinates(float x, float y, float width, float height, AnchorPosition anchorPosition) {
        //return getAnchorCoordinates(new Point(lastColumn, lastRow), new Dimension(width, height), anchorPosition);

        return getAnchorCoordinates(new RectF(x, y, x+width, y+height), anchorPosition);
        /*
        Point point = new Point(lastColumn, lastRow);
        point.position(getAnchorOffset(width, height, anchorPosition));

        return point;
        */
    }

    public static PointF getAnchorOffset(float width, float height, AnchorPosition anchorPosition) {
        PointF point = new PointF();
        switch (anchorPosition) {
            case LEFT_TOP:
                //point.set(lastColumn, lastRow);
                //component.doBeforeDraw(canvas, lastColumn, lastRow);
                //component.doBeforeDraw(canvas, metric.getxMetric().getPixelValue(canvas.getWidth()));
                break;
            case LEFT_MIDDLE:
                //component.doBeforeDraw(canvas, lastColumn, lastRow-(component.getHeightPix(height)/2));
                point.set(0, height / 2);
                break;
            case LEFT_BOTTOM:
                //component.doBeforeDraw(canvas, lastColumn, lastRow-component.getHeightPix(height));
                point.set(0, height);
                break;
            case RIGHT_TOP:
                //component.doBeforeDraw(canvas, lastColumn-component.getWidthPix(width), lastRow);
                point.set(width, 0);
                break;
            case RIGHT_BOTTOM:
                //component.doBeforeDraw(canvas, lastColumn-component.getWidthPix(width), lastRow-component.getHeightPix(height));
                point.set(width, height);
                break;
            case RIGHT_MIDDLE:
                //component.doBeforeDraw(canvas, lastColumn-component.getWidthPix(width), lastRow-(component.getHeightPix(height)/2));
                point.set(width, height / 2);
                break;
            case TOP_MIDDLE:
                //component.doBeforeDraw(canvas, lastColumn-(component.getWidthPix(width)/2), lastRow);
                point.set(width / 2, 0);
                break;
            case BOTTOM_MIDDLE:
                //component.doBeforeDraw(canvas, lastColumn-(component.getWidthPix(width)/2), lastRow-component.getHeightPix(height));
                point.set(width / 2, height);
                break;
            case CENTER:
                //component.doBeforeDraw(canvas, lastColumn-(component.getWidthPix(width)/2), lastRow-(component.getHeightPix(height)/2));
                point.set(width / 2, height / 2);
                break;
            default:
                throw new IllegalArgumentException("Unsupported anchor location: " + anchorPosition);
        }
        return point;
    }


    /*
    public Point getElementCoordinates(Canvas canvas, Widget element) {
        if(widgets.get(element) == null) {
            throw new IllegalArgumentException("Specified element not found within LayoutManager.");
        }
        float height = canvas.getFontHeight();
        float width = canvas.getWidth();
        //for(Widget element : widgets.keySet()) {
        PositionMetrics metrics = widgets.get(element);
        float lastColumn = metrics.getxMetric().getPixelValue(width);
        float lastRow = metrics.getyMetric().getPixelValue(height);

        float elementWidth = element.getWidthPix(width);
        float elementHeight = element.getHeightPix(height);

        //getAnchorCoordinates(lastColumn, lastRow, width, height, metrics.getAnchor());

        Point screenCoordinates = new Point(lastColumn, lastRow);
        screenCoordinates.sub(getAnchorOffset(elementWidth, elementHeight, metrics.getAnchor()));

        return screenCoordinates;
    }
    */

    //public Point getElementCoordinates(float height, float width, PositionMetrics metrics) {
    public PointF getElementCoordinates(float height, float width, RectF viewRect, PositionMetrics metrics) {
        float x = metrics.getxMetric().getPixelValue(viewRect.width()) + viewRect.left;
        float y = metrics.getyMetric().getPixelValue(viewRect.height()) + viewRect.top;

        //float elementWidth = element.getWidthPix(width);
        //float elementHeight = element.getHeightPix(height);

        //getAnchorCoordinates(lastColumn, lastRow, width, height, metrics.getAnchor());

        PointF point = new PointF(x, y);
        return PointUtils.sub(point, getAnchorOffset(width, height, metrics.getAnchor()));

        //return point;
    }

    public synchronized void draw(Canvas canvas, RectF canvasRect, RectF marginRect, RectF paddingRect) throws PlotRenderException {
        //for(Widget widget : widgetOrder) {
        //synchronized(widgetRegistry) {
        if(isDrawMarginsEnabled()) {
            drawSpacing(canvas, canvasRect, marginRect, marginPaint);
        }
        if(isDrawPaddingEnabled()) {
            drawSpacing(canvas, marginRect, paddingRect, paddingPaint);
        }
        for(Widget widget : getKeysAsList()) {
            int canvasState = canvas.save(Canvas.ALL_SAVE_FLAG); // preserve clipping etc
            try {
            //PositionMetrics metrics = widgets.get(widget);
            PositionMetrics metrics = get(widget);
            /*
            float elementWidth = widget.getWidthPix(canvas.getWidth());
            float elementHeight = widget.getHeightPix(canvas.getHeight());
            */
            float elementWidth = widget.getWidthPix(paddingRect.width());
            float elementHeight = widget.getHeightPix(paddingRect.height());
            PointF coords = getElementCoordinates(elementHeight, elementWidth, paddingRect, metrics);

            // remove the floating point to allow clipping to work:
            int t = (int)(coords.y + 0.5);
            int b = (int)(coords.y + elementHeight + 0.5);
            int l = (int)(coords.x + 0.5);
            int r = (int)(coords.x + elementWidth + 0.5);

            //RectF widgetRect = new RectF(coords.getX(), coords.getY(), coords.getX() + elementWidth, coords.getY() + elementHeight);
            RectF widgetRect = new RectF(l, t, r, b);

            if(drawOutlineShadowsEnabled) {
                //int masked = canvas.save();
                //canvas.restoreToCount(canvasState);
                //canvas.restoreToCount(canvasState);
                //canvas.clipRect(widgetRect, Region.Op.REPLACE);
                canvas.drawRect(widgetRect, outlineShadowPaint);
                //canvas.drawRect(widgetRect, outlinePaint);
                //canvas.restoreToCount(masked);
            }

            // not sure why this is, but the rect clipped by clipRect is 1 less than the one drawn by drawRect.
            // so this is necessary to avoid clipping borders:
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
                //int masked = canvas.save();
                //canvas.restoreToCount(canvasState);
                //canvas.drawRect(widgetRect, outlineShadowPaint);
                canvas.drawRect(widgetRect, outlinePaint);
                //canvas.restoreToCount(masked);
            }
            } finally {
                canvas.restoreToCount(canvasState);  // restore clipping etc.
            }

        }
        //}
        //canvas.restoreToCount(canvasState);  // restore clipping etc.
    }

    private void drawSpacing(Canvas canvas, RectF outer, RectF inner, Paint paint) {
        int saved = canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.clipRect(inner, Region.Op.DIFFERENCE);
        canvas.drawRect(outer, paint);
        canvas.restoreToCount(saved);

    }

    protected void drawAnchor(Canvas canvas, PointF coords) {
        float anchorSize = 4;
        //canvas.drawLine(coords.getX()-3, coords.getY(), coords.getX()+3, coords.getY(), anchorPaint);
        //canvas.drawLine(coords.getX(), coords.getY()-3, coords.getX(), coords.getY()+3, anchorPaint);
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
        /*
        if(element == null) {
            throw new NullPointerException("Attempt to position null as Widget element.");
        }

        widgets.put(element, new PositionMetrics(lastColumn, xLayoutStyle, lastRow, yLayoutStyle, anchor));
        if(!widgetOrder.contains(element)) {
            widgetOrder.addToBack(element);
        }
        */
    }

    /*
    public synchronized boolean moveToEnd(Widget element) {
        if(!widgetOrder.contains(element)) {
            return false;
        } else {
            //int widgetIndex = widgetOrder.indexOf(element);
            widgetOrder.remove(element);
            widgetOrder.addToBack(element);
            return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveAfter(Widget widgetToMove, Widget referenceWidget) {
        if(widgetToMove == referenceWidget) {
            throw new IllegalArgumentException("Illegal argument to moveAfter(A, B); A cannot be equal to B.");
        }
        if(!widgetOrder.contains(referenceWidget) || !widgetOrder.contains(widgetToMove)) {
            return false;
        } else {

            widgetOrder.remove(widgetToMove);
            int refIndex = widgetOrder.indexOf(referenceWidget);
            widgetOrder.add(refIndex+1, widgetToMove);
            return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveBefore(Widget widgetToMove, Widget referenceWidget) {
        if(widgetToMove == referenceWidget) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        }
        if(!widgetOrder.contains(referenceWidget) || !widgetOrder.contains(widgetToMove)) {
            return false;
        } else {

            widgetOrder.remove(widgetToMove);
            int refIndex = widgetOrder.indexOf(referenceWidget);
            widgetOrder.add(refIndex, widgetToMove);
            return true;
            //widgetOrder.remove(element);
        }
    }

    public synchronized boolean moveToFront(Widget widget) {
        if(!widgetOrder.contains(widget)) {
            return false;
        } else {
            //int widgetIndex = widgetOrder.indexOf(widget);
            widgetOrder.remove(widget);
            widgetOrder.addToFront(widget);
            return true;
            //widgetOrder.remove(widget);
        }
    }

    public synchronized boolean moveBack(Widget widget) {
        int widgetIndex = widgetOrder.indexOf(widget);
        if(widgetIndex == -1) {
            // widget not found:
            return false;
        }
        if(widgetIndex >= widgetOrder.size()-1) {
            // already at the top:
            return true;
        }

        Widget widgetAbove = widgetOrder.get(widgetIndex+1);
        return moveAfter(widget, widgetAbove);
    }

    public synchronized boolean moveForward(Widget widget) {
        int widgetIndex = widgetOrder.indexOf(widget);
        if(widgetIndex == -1) {
            // widget not found:
            return false;
        }
        if(widgetIndex <= 0) {
            // already at the bottom:
            return true;
        }

        Widget widgetBeneath = widgetOrder.get(widgetIndex-1);
        return moveBefore(widget, widgetBeneath);
    }
    */


    /*
    public boolean remove(Widget element) {

        if(widgets.containsKey(element)) {
            widgets.remove(element);
            widgetOrder.remove(element);
            return true;
        } else {
            return false;
        }

    }
    */

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
}
