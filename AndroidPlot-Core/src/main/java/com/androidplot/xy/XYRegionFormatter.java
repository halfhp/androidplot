package com.androidplot.xy;

import android.graphics.Paint;

/**
 * Base class of all XYRegionFormatters.
 */
public class XYRegionFormatter {

    //private int color;
    private Paint paint;

    public XYRegionFormatter(int color) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //this.color = color;
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * Advanced users can use this method to access the Paint instance to add transparency etc.
     * @return
     */
    public Paint getPaint() {
        return paint;
    }
}
