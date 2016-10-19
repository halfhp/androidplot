package com.androidplot.util;

import android.content.*;
import android.graphics.*;

import com.androidplot.xy.*;

/**
 * Created by halfhp on 10/21/16.
 */
public class InstrumentedXYPlot extends XYPlot {

    // may be set by consumers if necessary
    public Canvas canvas = new Canvas();

    public InstrumentedXYPlot(Context context) {
        super(context, "a test plot");
    }

    @Override
    public void redraw() {
        super.onDraw(canvas);
    }
}
