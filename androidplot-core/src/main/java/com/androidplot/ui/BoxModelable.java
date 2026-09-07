// SPDX-License-Identifier: Apache-2.0

package com.androidplot.ui;

import android.graphics.RectF;

/**
 * Defines the properties of a <a href="http://www.w3.org/TR/CSS21/box.html">BoxModel</a> as used
 * by Androidplot.  Essentially, the BoxModel composes three nested (but not necessarily concentric) rectangles:
 * * The bounding box, which is the outer-most box.
 * * The marginated box, which is calculated by applying the margin insets to the bounding box.
 * * The padded box, which is calculated by applying the padding insets to the marginated box.
 */
public interface BoxModelable {
    /**
     * Returns a RectF instance describing the inner edge of the margin layer.
     * @param boundsRect
     * @return
     */
    RectF getMarginatedRect(RectF boundsRect);

    /**
     * Returns a RectF instance describing the inner edge of the padding layer.
     * @param marginRect
     * @return
     */
    RectF getPaddedRect(RectF marginRect);


    void setMargins(float left, float top, float right, float bottom);

    void setPadding(float left, float top, float right, float bottom);

    float getMarginLeft();

    void setMarginLeft(float marginLeft);

    float getMarginTop();

    void setMarginTop(float marginTop);

    float getMarginRight();

    void setMarginRight(float marginRight);

    float getMarginBottom();

    void setMarginBottom(float marginBottm);

    float getPaddingLeft();

    void setPaddingLeft(float paddingLeft);

    float getPaddingTop();

    void setPaddingTop(float paddingTop);

    float getPaddingRight();

    void setPaddingRight(float paddingRight);

    float getPaddingBottom();

    void setPaddingBottom(float paddingBottom);
}
