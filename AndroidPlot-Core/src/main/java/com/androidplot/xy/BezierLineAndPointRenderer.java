/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
 */

package com.androidplot.xy;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * WARNING: This is an unfinished class.  Series drawn by this implementation may look nice
 * but they are not yet accurate representations of the control points from which they are derived.
 */
public class BezierLineAndPointRenderer extends LineAndPointRenderer<BezierLineAndPointFormatter> {
    public BezierLineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    @Override
    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        //path.lineTo(thisPoint.x, thisPoint.y);

        // bezier curve version:
        PointF mid = new PointF();
        mid.set((lastPoint.x + thisPoint.x) / 2, (lastPoint.y + thisPoint.y) / 2);
        path.quadTo((lastPoint.x + mid.x) / 2, lastPoint.y, mid.x, mid.y);
        //path.quadTo((mid.x + thisPoint.x) / 2, thisPoint.y, thisPoint.x, thisPoint.y);
        path.quadTo((mid.x + thisPoint.x) / 2, lastPoint.y, thisPoint.x, thisPoint.y);

    }
}
