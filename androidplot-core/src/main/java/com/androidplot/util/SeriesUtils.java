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

import com.androidplot.xy.XYSeries;

/**
 * Created by nick_f on 7/24/14.
 */
public class SeriesUtils {

    /**
     *
     * @param series
     * @return The largest yVal in the series or null if the series contains no non-null yVals.
     */
    public static Number getMaxY(XYSeries series) {
        Number max = null;
        for(int i = 0; i < series.size(); i++) {
            Number thisNumber = series.getY(i);
            if(max == null || thisNumber != null && thisNumber.doubleValue() >  max.doubleValue()) {
                max = thisNumber;
            }
        }
        return max;
    }
}
