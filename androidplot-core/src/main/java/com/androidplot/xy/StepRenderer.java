// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class StepRenderer extends LineAndPointRenderer<StepFormatter> {

    public StepRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        path.lineTo(thisPoint.x, lastPoint.y);
        path.lineTo(thisPoint.x, thisPoint.y);
    }
}
