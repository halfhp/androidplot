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

package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Interface used by LineAndPointRenderer to draw a Point onto the display.  Those wishing to customize
 * the graphics of points or how labels are rendered may implement this interface.
 */
public interface PointFormatter {

    /**
     * Draws the point specified onto the canvas.
     * @param canvas
     * @param value Actual value of the point.
     * @param coords Screen coordinates of the point.
     */
    public void draw(Canvas canvas, Number value, PointF coords);


}
