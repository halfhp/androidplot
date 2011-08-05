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

package com.androidplot.mock;

import android.graphics.RectF;
import mockit.Instantiation;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = RectF.class, instantiation = Instantiation.PerMockedInstance)
public final class MockRectF {
    //public float left;
    //public float top;
    //public float right;
    //public float bottom;

    public RectF it;

    @Mock
    public void $init() {

    }

    @Mock
    public void $init(RectF rhs) {
        it.left = rhs.left;
        it.top = rhs.top;
        it.right = rhs.right;
        it.bottom = rhs.bottom;
    }

    @Mock
    public void $init(float left, float top, float right, float bottom) {
        it.left = left;
        it.top = top;
        it.right = right;
        it.bottom = bottom;
        // do anything here

    }

    @Mock
    public void offset(float dx, float dy) {
        float w = width();
        float h = height();

        it.left = it.left + dx;
        it.right = it.right + dx;

        it.top = it.top + dy;
        it.bottom = it.bottom + dy;
    }

    @Mock
    public void offsetTo(float left, float top) {

        it.right = left + width();
        it.bottom = top + height();

        it.left = left;
        it.top = top;

        // do anything here
    }

    @Mock
    public float height() {
        return it.bottom - it.top;
    }

    @Mock
    public float width() {
        return it.right - it.left;
    }

    @Mock
    public String toString() {
        return null;
    }
}
