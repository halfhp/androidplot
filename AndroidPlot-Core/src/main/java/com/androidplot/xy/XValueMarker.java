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

import android.graphics.Paint;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.YPositionMetric;

public class XValueMarker extends ValueMarker<YPositionMetric> {

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     */
    public XValueMarker(Number value, String text) {
        super(value, text, new YPositionMetric(3, YLayoutStyle.ABSOLUTE_FROM_TOP));
    }

    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public XValueMarker(Number value, String text, YPositionMetric textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }


    /**
     *
     * @param value
     * @param text Set to null to use the plot's default formatter.
     * @param textPosition
     * @param linePaint
     * @param textPaint
     */
    public XValueMarker(Number value, String text, YPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, textPosition, linePaint, textPaint);
    }
}
