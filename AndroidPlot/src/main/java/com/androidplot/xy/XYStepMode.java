package com.androidplot.xy;

/**
 * INCREMENTAL_VALUE - (default) doBeforeDraw a tick every n values.
 * INCREMENTAL_PIXEL - doBeforeDraw a tick every n pixels.
 * SUBDIVIDE - doBeforeDraw n number of evenly spaced ticks.
 */
public enum XYStepMode {
    SUBDIVIDE,           // default
    INCREMENT_BY_VAL,
    INCREMENT_BY_PIXELS
}
