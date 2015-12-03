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

/**
 * Defines which edge is used to close a fill path for drawing lines.
 *
 * TOP - Use the top edge of the plot.
 * BOTTOM - Use the bottom edge of the plot.
 * LEFT - (Not implemented) Use the left edge of the plot.
 * RIGHT - (Not implemented) Use the right edge of the plot.
 * DOMAIN_ORIGIN - (Not implemented) Use the domain origin line.
 * RANGE_ORIGIN - Use the range origin line.
 */
public enum FillDirection {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    DOMAIN_ORIGIN,
    RANGE_ORIGIN
}
