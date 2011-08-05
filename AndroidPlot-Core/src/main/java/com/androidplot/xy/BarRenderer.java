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

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.series.XYSeries;
import com.androidplot.util.ValPixConverter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Renders a point as a Bar
 */
public class BarRenderer extends XYSeriesRenderer<BarFormatter> {

    private BarWidthStyle style = BarWidthStyle.FIXED_WIDTH;
    private float barWidth = 5;

    public enum BarRenderStyle {
        STACKED,            // bars are overlaid in descending y-val order (largest val in back)
        SIDE_BY_SIDE        // bars are drawn next to each-other
    }

    public enum BarWidthStyle {
        FIXED_WIDTH,
        FIXED_SPACING
    }


    public BarRenderer(XYPlot plot) {
        super(plot);
    }

    /**
     * Sets the width of the bars draw.
     * @param barWidth
     */
    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {
        int longest = getLongestSeries();
        if(longest == 0) {
            return;  // no data, nothing to do.
        }
        TreeMap<Number, XYSeries> seriesMap = new TreeMap<Number, XYSeries>();
        for(int i = 0; i < longest; i++) {
            seriesMap.clear();
            List<XYSeries> seriesList = getPlot().getSeriesListForRenderer(this.getClass());
            for(XYSeries series : seriesList) {
                if(i < series.size()) {
                    seriesMap.put(series.getY(i), series);
                }
            }
            drawBars(canvas, plotArea, seriesMap, i);
        }
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, BarFormatter formatter) {
        canvas.drawRect(rect, formatter.getFillPaint());
        canvas.drawRect(rect, formatter.getBorderPaint());
    }

    private int getLongestSeries() {
        int longest = 0;

        for(XYSeries series : getPlot().getSeriesListForRenderer(this.getClass())) {
            int seriesSize = series.size();
            if(seriesSize > longest) {
                longest = seriesSize;
            }
        }
        return longest;
    }

    private void drawBars(Canvas canvas, RectF plotArea, TreeMap<Number, XYSeries> seriesMap, int x) {
        Paint p = new Paint();
        p.setColor(Color.RED);
        Object[] oa = seriesMap.entrySet().toArray();
        Map.Entry<Number, XYSeries> entry;
        for(int i = oa.length-1; i >= 0; i--) {
            entry = (Map.Entry<Number, XYSeries>) oa[i];
            BarFormatter formatter = getFormatter(entry.getValue()); // TODO: make this more efficient
            Number yVal = null;
            Number xVal = null;
            if(entry.getValue() != null) {
                yVal = entry.getValue().getY(x);
                xVal = entry.getValue().getX(x);
            }  
          
            if (yVal != null && xVal != null) {  // make sure there's a real value to draw
                switch (style) {
                    case FIXED_WIDTH:
                        float halfWidth = barWidth/2;
                        float pixX = ValPixConverter.valToPix(xVal.doubleValue(), getPlot().getCalculatedMinX().doubleValue(), getPlot().getCalculatedMaxX().doubleValue(), plotArea.width(), false) + (plotArea.left);
                        float pixY = ValPixConverter.valToPix(yVal.doubleValue(), getPlot().getCalculatedMinY().doubleValue(), getPlot().getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
                        canvas.drawRect(pixX - halfWidth, pixY, pixX + halfWidth, plotArea.bottom, formatter.getFillPaint());
                        canvas.drawRect(pixX - halfWidth, pixY, pixX + halfWidth, plotArea.bottom, formatter.getBorderPaint());
                        break;
                    default:
                        throw new UnsupportedOperationException("Not yet implemented.");
                }
            }
        }
    }
}
