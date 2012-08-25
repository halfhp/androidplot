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

package com.androidplot;

import android.graphics.Canvas;

public interface PlotListener {
    /**
     *
     * @param event
     * @deprecated Deprecated since 0.5.1 - Users of this method should
     *             transition functionality into onAfterDraw(Plot).
     *
     */
    @Deprecated
    public void onPlotUpdate(PlotEvent event);

    public void onBeforeDraw(Plot source, Canvas canvas);

    public void onAfterDraw(Plot source, Canvas canvas);
}
