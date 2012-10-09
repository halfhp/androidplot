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

package com.androidplot.util;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Utility methods for converting pixel coordinates into real values and vice versa.
 */
public class ValPixConverter {
    private static final int ZERO = 0;


    public static float valToPix(double val, double min, double max, float lengthPix, boolean flip) {
        if(lengthPix <= ZERO) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        double range = range(min, max);
        double scale = lengthPix / range;
        double raw = val - min;
        float pix = (float)(raw * scale);

        if(flip) {
            pix = (lengthPix - pix);
        }
        return pix;
    }

    public static double range(double min, double max) {
        return (max-min);
    }

    
    public static double valPerPix(double min, double max, float lengthPix) {
        double valRange = range(min, max);
        return valRange/lengthPix;
    }

    /**
     * Convert a value in pixels to the type passed into min/max
     * @param pix
     * @param min
     * @param max
     * @param lengthPix
     * @param flip True if the axis should be reversed before calculated. This is the case
     * with the y axis for screen coords.
     * @return
     */
    public static double pixToVal(float pix, double min, double max, float lengthPix, boolean flip) {
        if(pix < ZERO) {
            throw new IllegalArgumentException("pixel values cannot be negative.");
        }

        if(lengthPix <= ZERO) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        float pMult = pix;
        if(flip) {
            pMult = lengthPix - pix;
        }
        double range = range(min, max);
        return ((range / lengthPix) * pMult) + min;
    }

    /**
     * Converts a real value into a pixel value.
     * @param x Real d (domain) component of the point to convert.
     * @param y Real y (range) component of the point to convert.
     * @param plotArea
     * @param minX Minimum visible real value on the d (domain) axis.
     * @param maxX Maximum visible real value on the y (domain) axis.
     * @param minY Minimum visible real value on the y (range) axis.
     * @param maxY Maximum visible real value on the y (range axis.
     * @return
     */
    public static PointF valToPix(Number x, Number y, RectF plotArea, Number minX, Number maxX, Number minY, Number maxY) {
        float pixX = ValPixConverter.valToPix(x.doubleValue(), minX.doubleValue(), maxX.doubleValue(), plotArea.width(), false) + (plotArea.left);
        float pixY = ValPixConverter.valToPix(y.doubleValue(), minY.doubleValue(), maxY.doubleValue(), plotArea.height(), true) + plotArea.top;
        return new PointF(pixX, pixY);
    }
}
