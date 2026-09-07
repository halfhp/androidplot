// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;
import androidx.annotation.NonNull;

/**
 * Convenience implementation of {@link BoxModelable}.
 */
public class BoxModel implements BoxModelable{

    private float marginLeft;
    private float marginTop;
    private float marginRight;
    private float marginBottom;
    

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;

    /**
     * Default with 0 for all padding / margin values
     */
    public BoxModel() {
        // nothing to do
    }

    @SuppressWarnings("SameParameterValue")
    public BoxModel(float marginLeft, float marginTop, float marginRight, float marginBottom,
                    float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;

        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
    }

    /**
     * Returns a RectF instance describing the inner edge of the margin layer.
     * @param boundsRect
     * @return
     */
    @NonNull
    public RectF getMarginatedRect(@NonNull RectF boundsRect) {
        return new RectF( boundsRect.left + getMarginLeft(),
                boundsRect.top + getMarginTop(),
                boundsRect.right - getMarginRight(),
                boundsRect.bottom - getMarginBottom());
    }

    /**
     * Returns a RectF instance describing the inner edge of the padding layer.
     * @param marginRect
     * @return
     */
    @NonNull
    public RectF getPaddedRect(@NonNull RectF marginRect) {
        return new RectF(marginRect.left + getPaddingLeft(),
                marginRect.top + getPaddingTop(),
                marginRect.right - getPaddingRight(),
                marginRect.bottom - getPaddingBottom());
    }

    @Override
    public void setMargins(float left, float top, float right, float bottom) {
        setMarginLeft(left);
        setMarginTop(top);
        setMarginRight(right);
        setMarginBottom(bottom);
    }

    @Override
    public void setPadding(float left, float top, float right, float bottom) {
        setPaddingLeft(left);
        setPaddingTop(top);
        setPaddingRight(right);
        setPaddingBottom(bottom);
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
