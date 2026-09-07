// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.Path;
import android.graphics.PointF;
import androidx.annotation.NonNull;

/**
 * Renders a point as a line with the vertices marked.  Requires 2 or more points to
 * be rendered.
 */
public class StepRenderer extends LineAndPointRenderer<StepFormatter> {

    public StepRenderer(@NonNull XYPlot plot) {
        super(plot);
    }

    @Override
    protected void appendToPath(@NonNull Path path, @NonNull PointF thisPoint, @NonNull PointF lastPoint) {
        path.lineTo(thisPoint.x, lastPoint.y);
        path.lineTo(thisPoint.x, thisPoint.y);
    }
}
