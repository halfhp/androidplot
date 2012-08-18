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
