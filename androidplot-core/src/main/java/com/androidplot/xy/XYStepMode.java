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
 * INCREMENTAL_VALUE - (default) draw a tick every n values.
 * INCREMENTAL_PIXEL - draw a tick every n pixels.
 * SUBDIVIDE - draw n number of evenly spaced ticks.
 */
public enum XYStepMode {
    SUBDIVIDE,           // default
    INCREMENT_BY_VAL,
    INCREMENT_BY_PIXELS
}
