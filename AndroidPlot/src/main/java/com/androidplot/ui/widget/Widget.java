package com.androidplot.ui.widget;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.layout.*;
import com.androidplot.util.Dimension;
import com.androidplot.util.Point;

/**
 * Created by IntelliJ IDEA.
 * User: nfellows
 * Date: 12/29/10
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Widget {

    private Paint borderPaint;
    private Paint backgroundPaint;
    //private Paint anchorPaint;
    private boolean drawBorderEnabled = false;
    private boolean drawBackgroundEnabled = false;

    private boolean clippingEnabled = true;
    //private boolean drawAnchorEnabled = true;

    //private RectF margins;

    {
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

        //margins = new RectF(3, 3, 3, 3);

        //anchorPaint = new Paint();
        //anchorPaint.setColor(Color.YELLOW);
    }







    private float marginTop = 0;
    private float marginBottom = 0;
    private float marginLeft = 0;
    private float marginRight = 0;


    //private PositionMetrics metric;
    //private SizeMetric widthMetric;
    //private SizeMetric heightMetric;
    private SizeMetrics sizeMetrics;
    //private AnchorPosition anchorLocation;


    /*
    public Widget(float width, SizeLayoutType widthLayoutType, float height, SizeLayoutType heightLayoutType) {
        //metric = new PositionMetrics(width, widthLayoutType, height, heightLayoutType);
        this(width, widthLayoutType, height, heightLayoutType);
    }
    */

    /*
    public Widget(float width, SizeLayoutType widthLayoutType, float height, SizeLayoutType heightLayoutType) {
        sizeMetrics = new SizeMetrics(width, widthLayoutType, height, heightLayoutType);
    }
    */

    public Widget(SizeMetric heightMetric, SizeMetric widthMetric) {
        sizeMetrics = new SizeMetrics(heightMetric, widthMetric);
    }

    public Widget(SizeMetrics sizeMetrics) {
        this.sizeMetrics = sizeMetrics;
    }

    public void setSize(SizeMetrics sizeMetrics) {
        this.sizeMetrics = sizeMetrics;
    }


    public void setWidth(float width) {
        //widthMetric.
        //widthMetric.setValue(width);
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

    /*
    public AnchorPosition getAnchorLocation() {
        return anchorLocation;
    }

    public void setAnchorLocation(AnchorPosition anchorLocation) {
        this.anchorLocation = anchorLocation;
    }
    */

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    //public void draw(Canvas canvas, Dimension viewSize, Dimension size, Point coords) throws PlotRenderException {
    /*public void draw(Canvas canvas, Dimension viewSize, RectF widgetRect) throws PlotRenderException {
        //float width = getWidthPix(canvas.getWidth());
        //float height = getHeightPix(canvas.getFontHeight());

        if(drawBackgroundEnabled) {
            drawBackground(canvas, viewSize, widgetRect);
        }

        RectF marginatedRect = new RectF(widgetRect.left + marginLeft,
                widgetRect.top + marginTop,
                widgetRect.right - marginRight,
                widgetRect.bottom - marginBottom);

        //doBeforeDraw(canvas, viewSize, widgetRect);
        doOnDraw(canvas, viewSize, marginatedRect);
        
        if(drawBorderEnabled) {
            drawBorder(canvas, viewSize, widgetRect);
        }


    }*/

     public void draw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        //float width = getWidthPix(canvas.getWidth());
        //float height = getHeightPix(canvas.getFontHeight());

        if(drawBackgroundEnabled) {
            drawBackground(canvas, widgetRect);
        }

        RectF marginatedRect = new RectF(widgetRect.left + marginLeft,
                widgetRect.top + marginTop,
                widgetRect.right - marginRight,
                widgetRect.bottom - marginBottom);

        //doBeforeDraw(canvas, viewSize, widgetRect);
        doOnDraw(canvas, marginatedRect);

        if(drawBorderEnabled) {
            drawBorder(canvas, widgetRect);
        }


    }

    //protected void drawBorder(Canvas canvas, Dimension viewSize, Dimension size, Point coords) {
    protected void drawBorder(Canvas canvas, RectF widgetRect) {

        //canvas.drawRect(coords.getX(), coords.getY(), coords.getX() + size.getWidth(), coords.getY() + size.getFontHeight(), borderPaint);
        canvas.drawRect(widgetRect, borderPaint);
        //canvas.drawRect(0, 0, 10, 10, borderPaint);
    }

    protected void drawBackground(Canvas canvas, RectF widgetRect) {

        //canvas.drawRect(coords.getX(), coords.getY(), coords.getX() + size.getWidth(), coords.getY() + size.getFontHeight(), borderPaint);
        canvas.drawRect(widgetRect, backgroundPaint);
        //canvas.drawRect(0, 0, 10, 10, borderPaint);
    }

    /**
     *
     * @param canvas The Canvas to draw onto
     * @param widgetRect the size and coordinates of this widget
     */
    //protected abstract void doBeforeDraw(Canvas canvas, Dimension viewSize, Dimension size, Point coords) throws PlotRenderException;
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

    public boolean isDrawBorderEnabled() {
        return drawBorderEnabled;
    }

    public void setDrawBorderEnabled(boolean drawBorderEnabled) {
        this.drawBorderEnabled = drawBorderEnabled;
    }

    public boolean isDrawBackgroundEnabled() {
        return drawBackgroundEnabled;
    }

    public void setDrawBackgroundEnabled(boolean drawBackgroundEnabled) {
        this.drawBackgroundEnabled = drawBackgroundEnabled;
    }

    public boolean isClippingEnabled() {
        return clippingEnabled;
    }

    public void setClippingEnabled(boolean clippingEnabled) {
        this.clippingEnabled = clippingEnabled;
    }
}
