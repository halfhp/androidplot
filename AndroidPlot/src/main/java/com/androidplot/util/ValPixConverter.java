/*
Copyright 2010 Nick Fellows. All rights reserved.

Redistribution and use in source and binary forms, without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Nick Fellows ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NICK FELLOWS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Nick Fellows.
*/

package com.androidplot.util;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Converts pixel coordinates to and from Dataset values.
 */
public class ValPixConverter {


    /*
    public static float indexToPix(int index, int itemCount, int lengthPix) {
        if(index > (itemCount-1)) {
            throw new IndexOutOfBoundsException();
        }
        if(lengthPix <= 0) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        double scale = ((float) lengthPix) / itemCount;
        float pix = (float)(index * scale);
        return pix;
    }
    */


    public static float valToPix(double val, double min, double max, float lengthPix, boolean flip) {
        if(lengthPix <= 0) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        double range = range(min, max);
        double scale = lengthPix / range;
        double raw = val - min;
        float pix = (float)(raw * scale);  // the 0.5 turns floor into avg

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

    public static double pixToVal(float pix, double min, double max, int lengthPix, boolean flip) {
        if(pix < 0) {
            throw new IllegalArgumentException("pixel values cannot be negative.");
        }

        if(lengthPix <= 0) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        float pMult = pix;
        if(flip) {
            pMult = lengthPix - pix;
        }
        double range = range(min, max);
        return ((range / lengthPix) * pMult) + min;
    }

    public static PointF valToPix(Number x, Number y, RectF plotArea, Number minX, Number maxX, Number minY, Number maxY) {
        float pixX = ValPixConverter.valToPix(x.doubleValue(), minX.doubleValue(), maxX.doubleValue(), plotArea.width(), false) + (plotArea.left);
        float pixY = ValPixConverter.valToPix(y.doubleValue(), minY.doubleValue(), maxY.doubleValue(), plotArea.height(), true) + plotArea.top;
        return new PointF(pixX, pixY);
        //throw new UnsupportedOperationException("Not yet implemented.");
    }

    public static PointF pixToVal(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
