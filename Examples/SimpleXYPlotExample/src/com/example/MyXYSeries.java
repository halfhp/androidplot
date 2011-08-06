/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.example;

import com.androidplot.series.XYSeries;

public class MyXYSeries implements XYSeries {
    private static final int[] vals = {0, 25, 55, 2, 80, 30, 99, 0, 44, 6};

    // f(x) = x
    @Override
    public Number getX(int index) {
        return index;
    }


/*    // range begins at 0
    @Override
    public Number getMinX() {
        return 0;
    }

    // range ends at 9
    @Override
    public Number getMaxX() {
        return 9;
    }*/

    @Override
    public String getTitle() {
        return "Some Numbers";
    }

    // range consists of all the values in vals
    @Override
    public int size() {
        return vals.length;
    }

    // return vals[index]
    @Override
    public Number getY(int index) {
        // make sure index isnt something unexpected:
        if(index < 0 || index > 9) {
            throw new IllegalArgumentException("Only values between 0 and 9 are allowed.");
        }
        return vals[index];
    }

    /*// smallest value in vals is 0
    @Override
    public Number getMinY() {
        return 0;
    }

    // largest value in vals is 99
    @Override
    public Number getMaxY() {
        return 99;
    }*/
}
