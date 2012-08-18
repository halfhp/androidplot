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

package com.androidplot.ui;

/**
 * Enumeration of possible anchor positions that a {@link com.androidplot.ui.widget.Widget} can use.  There are a total
 * 8 possible anchor positions representing each corner of the Widget and the point exactly between each corner.
 */
public enum AnchorPosition {
    TOP_MIDDLE,
    LEFT_TOP,    // default
    LEFT_MIDDLE,
    LEFT_BOTTOM,
    RIGHT_TOP,
    RIGHT_MIDDLE,
    RIGHT_BOTTOM,
    BOTTOM_MIDDLE,
    CENTER
}
