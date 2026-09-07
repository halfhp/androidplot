// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

/**
 * INCREMENTAL_VALUE - (default) draw a tick every n values.
 * INCREMENTAL_PIXEL - draw a tick every n pixels.
 * SUBDIVIDE - draw n number of evenly spaced lines.
 * INCREMENT_BY_FIT choose increment from a list of possible values
 */
public enum StepMode {
    SUBDIVIDE,           // default
    INCREMENT_BY_VAL,
    INCREMENT_BY_PIXELS,
    INCREMENT_BY_FIT
}
