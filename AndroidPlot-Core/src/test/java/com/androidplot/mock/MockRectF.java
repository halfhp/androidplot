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

package com.androidplot.mock;

import android.graphics.RectF;
import mockit.Instantiation;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = RectF.class, stubs="", inverse=true)
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
