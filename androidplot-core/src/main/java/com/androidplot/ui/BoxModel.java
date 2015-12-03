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

import android.graphics.RectF;

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
    //private RectF marginRect;
    //private RectF paddingRect;

    public BoxModel() {
        
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
    public RectF getMarginatedRect(RectF boundsRect) {
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
    public RectF getPaddedRect(RectF marginRect) {
        return new RectF(marginRect.left + getPaddingLeft(),
                marginRect.top+getPaddingTop(),
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
