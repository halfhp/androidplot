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
