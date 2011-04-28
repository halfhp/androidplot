package com.androidplot.xy;

/**
 * Base class of all XYRegionFormatters.
 */
public class XYRegionFormatter {

    private int color;

    public XYRegionFormatter(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
