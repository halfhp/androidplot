package com.androidplot.xy;

import android.graphics.Color;

public class AxisValueLabelFormatter {
    //private Paint textPaint;
    private int color;


    {
        //setColor(Color.WHITE);
    }

    public AxisValueLabelFormatter(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
