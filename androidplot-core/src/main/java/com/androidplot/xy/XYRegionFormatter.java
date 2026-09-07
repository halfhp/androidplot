// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.content.Context;
import android.graphics.Paint;

import com.halfhp.fig.*;

/**
 * Base class of all XYRegionFormatters.
 */
public class XYRegionFormatter {

    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    /**
     * Provided as a convenience to users; allows instantiation and xml configuration
     * to take place in a single line
     *
     * @param ctx
     * @param xmlCfgId Id of the xml config file within /res/xml
     */
    public XYRegionFormatter(Context ctx, int xmlCfgId) {
        // prevent configuration of classes derived from this one:
        if (getClass().equals(XYRegionFormatter.class)) {
            try {
                Fig.configure(ctx, this, xmlCfgId);
            } catch (FigException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public XYRegionFormatter(int color) {
        paint.setColor(color);
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
