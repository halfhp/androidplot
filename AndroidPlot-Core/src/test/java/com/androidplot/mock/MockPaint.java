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

import android.graphics.Paint;
import android.graphics.RectF;
import mockit.Instantiation;
import mockit.Mock;
import mockit.MockClass;

@MockClass(realClass = Paint.class)
public final class MockPaint {
    int color = 0;

    @Mock
    public void setColor(int color) {
        this.color = color;
    }

    @Mock
    public int getColor() {
        return color;
    }
}
