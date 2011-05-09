package com.androidplot.ui;

import android.graphics.RectF;

/**
 * Encapsulates the functionality of a BoxModel.
 * See http://www.w3.org/TR/CSS21/box.html for a good explanation of how
 * the box model works.
 */
public interface BoxModelable {
    /**
     * Returns a RectF instance describing the inner edge of the margin layer.
     * @param boundsRect
     * @return
     */
    public RectF getMarginatedRect(RectF boundsRect);

    /**
     * Returns a RectF instance describing the inner edge of the padding layer.
     * @param marginRect
     * @return
     */
    public RectF getPaddedRect(RectF marginRect);


    public void setMargins(float left, float top, float right, float bottom);

    public void setPadding(float left, float top, float right, float bottom);

    public float getMarginLeft();

    public void setMarginLeft(float marginLeft);

    public float getMarginTop();

    public void setMarginTop(float marginTop);

    public float getMarginRight();

    public void setMarginRight(float marginRight);

    public float getMarginBottom();

    public float getPaddingLeft();

    public void setPaddingLeft(float paddingLeft);

    public float getPaddingTop();

    public void setPaddingTop(float paddingTop);

    public float getPaddingRight();

    public void setPaddingRight(float paddingRight);

    public float getPaddingBottom();

    public void setPaddingBottom(float paddingBottom);
}
