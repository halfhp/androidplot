package com.androidplot.xy;

import android.graphics.Color;

public class XYAxisRegionFormatter extends XYRegionFormatter {
    //private Paint textPaint;
    //private int color;

    {
        setColor(Color.WHITE);
    }

    public XYAxisRegionFormatter(int color) {
        super(color);
    }
}
