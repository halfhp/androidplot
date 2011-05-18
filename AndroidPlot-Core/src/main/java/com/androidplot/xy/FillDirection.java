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
