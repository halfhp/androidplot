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

import android.content.Context;
import android.graphics.Paint;
import com.androidplot.util.Configurator;

/**
 * Base class of all XYRegionFormatters.
 */
public class XYRegionFormatter {

    //private int color;
    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    /**
     * Provided as a convenience to users; allows instantiation and xml configuration
     * to take place in a single line
     *
     * @param ctx
     * @param xmlCfgId Id of the xml config file within /res/xml
     */
    public XYRegionFormatter(Context ctx, int xmlCfgId) {
        // prevent configuration of classes derived from this one:
        if (getClass().equals(XYRegionFormatter.class)) {
            Configurator.configure(ctx, this, xmlCfgId);
        }
    }

    public XYRegionFormatter(int color) {
        //paint = new Paint();
        paint.setColor(color);
        //paint.setStyle(Paint.Style.FILL);
        //paint.setAntiAlias(true);
        //this.color = color;
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * Advanced users can use this method to access the Paint instance to add transparency etc.
     * @return
     */
    public Paint getPaint() {
        return paint;
    }
}
