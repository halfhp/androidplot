/*
 * Copyright 2013 AndroidPlot.com
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

package com.androidplot.pie;

import android.content.Context;
import android.util.AttributeSet;
import com.androidplot.Plot;
import com.androidplot.Series;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.util.PixelUtils;

public class PieChart extends Plot<Series<Number>, SegmentFormatter, PieRenderer> {

    private static final int DEFAULT_PIE_WIDGET_H_DP = 18;
    private static final int DEFAULT_PIE_WIDGET_W_DP = 10;

    private PieWidget pieWidget;

    public PieChart(Context context, String title) {
        super(context, title);
        postInit(context, null);
    }

    public PieChart(Context context, String title, RenderMode mode) {
        super(context, title, mode);
        postInit(context, null);
    }

    public PieChart(Context context, AttributeSet attributes) {
        super(context, attributes);
        postInit(context, attributes);
    }

    private void postInit(Context context, AttributeSet attrs) {
        pieWidget = new PieWidget(
                this,
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_H_DP),
                        SizeLayoutType.FILL,
                        PixelUtils.dpToPix(DEFAULT_PIE_WIDGET_W_DP),
                        SizeLayoutType.FILL));
        // TODO: can't remember why this getClass() check is neccessary.  test if it actually is...
        if (getClass().equals(PieChart.class) && attrs != null) {
            loadAttrs(context, attrs);
        }
    }

    @Override
    protected void doBeforeDraw() {
    }

    @Override
    protected void doAfterDraw() {
    }
}
