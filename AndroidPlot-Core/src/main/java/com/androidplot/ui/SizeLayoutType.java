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