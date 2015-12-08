/*
 * Copyright 2015 AndroidPlot.com
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

import android.graphics.RectF;

/**
 * Convenience methods for dealing with {@link android.graphics.RectF}
 */
public abstract class RectFUtils {


    /**
     * Determine if two {@link RectF} instances are equal.  Must be used in place
     * of default equality operation due to a bug that exists in older versions of Android:
     * http://stackoverflow.com/questions/13517852/rectf-equals-fails-on-android-versions-below-jelly-bean
     * @param r1 May not be null
     * @param r2 May not be null
     * @return True if r1 and r2 are identical, false otherwise.
     */
    public static boolean areIdentical(RectF r1, RectF r2) {
        return r1.left == r2.left &&
                r1.top == r2.top &&
                r1.right == r2.right &&
                r1.bottom == r2.bottom;
    }
}
