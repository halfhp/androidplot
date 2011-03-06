package com.androidplot.ui.layout;

import android.graphics.RectF;

/**
 * Encapsulates the functionality of a BoxModel.
 * See http://www.w3.org/TR/CSS21/box.html for a good explanation of what
 * the box model is.
 */
public class BoxModel {

    private float marginLeft;
    private float marginTop;
    private float marginRight;
    private float marginBottom;
    

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;
    //private RectF marginRect;
    //private RectF paddingRect;

    public BoxModel(float marginLeft, float marginTop, float marginRight, float marginBottom,
                    float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;

        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
    }

    public RectF getMarginRect(RectF boundsRect) {
        return new RectF( boundsRect.left + getMarginLeft(),
                boundsRect.top + getMarginTop(),
                boundsRect.right - getMarginRight(),
                boundsRect.bottom - getMarginBottom());
    }


    public RectF getPaddingRect(RectF boundsRect) {
        //RectF marginRect = getMarginRect();
        RectF marginRect = getMarginRect(boundsRect);
        return new RectF(marginRect.left + getPaddingLeft(),
                marginRect.top+getPaddingTop(),
                marginRect.right - getPaddingRight(),
                marginRect.bottom - getPaddingBottom());
    }


    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public float getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public float getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public float getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
}
