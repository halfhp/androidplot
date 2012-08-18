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
 * SizeLayoutType is an enumeration of algorithms available for calculating an arbitrary dimension of a widget.
 * Each algorithm also takes a single value called "val" in this doc.
 * ABSOLUTE - Val is treated as absolute.  If val is 5 then the size of the widget along the associated axis is 5 pixels.
 *
 * RELATIVE - Val represents the percentage of the display that the widget should fill along the associated axis.  For example,
 * if the total size of the owning plot is 120 pixels and val is set to 50 then the size of the widget along the associated axis
 * is 60; 50% of 120 = 60.
 *
 * FILL - Widget completely fills along the associated axis, minus
 */
public enum SizeLayoutType {
    ABSOLUTE,
    RELATIVE,
    FILL
}