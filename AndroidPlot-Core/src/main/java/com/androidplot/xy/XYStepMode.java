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
