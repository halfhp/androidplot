/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.xy;

import android.graphics.*;
import android.util.Pair;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.series.XYSeries;
import com.androidplot.util.ValPixConverter;

import java.util.*;

/**
 * Renders a point as a Bar
 */
public class BarRenderer<T extends BarFormatter> extends XYSeriesRenderer<T> {

    private BarWidthStyle style = BarWidthStyle.FIXED_WIDTH;
    private float barWidth = 5;

    // compares two Numbers by first checking for null then converting to double.
    // used to determine which value from each series should be drawn first in the case
    // of stacked render mode.
    private static final Comparator numberComparator = new Comparator<Number>() {
        @Override
        public int compare(Number number, Number number2) {
            if (number == null) {
                if (number2 == null) {
                    return 0;
                }
                return -1;
            } else if (number2 == null) {
                // dont need to check for double null because the opening if would
                // have already caught it.
                return 1;
            } else {
                double lhs = number.doubleValue();
                double rhs = number2.doubleValue();

                if (lhs < rhs) {
                    return -1;
                } else if (lhs > rhs) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };

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

        List<XYSeries> sl = getPlot().getSeriesListForRenderer(this.getClass());

        // dont try to render anything if there's nothing to render.
        if(sl == null) {
            return;
        }

        int longest = getLongestSeries(sl);
        if (longest == 0) {
            return;  // no data, nothing to do.
        }

        TreeMap<Number, XYSeries> seriesMap = new TreeMap<Number, XYSeries>(numberComparator);
        for (int i = 0; i < longest; i++) {
            seriesMap.clear();
            List<XYSeries> seriesList = getPlot().getSeriesListForRenderer(this.getClass());
            for (XYSeries series : seriesList) {
                if (i < series.size()) {
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

    private int getLongestSeries(List<XYSeries> seriesList) {
        int longest = 0;

        for(XYSeries series : seriesList) {
            int seriesSize = series.size();
            if(seriesSize > longest) {
                longest = seriesSize;
            }
        }
        return longest;
    }


    private int getLongestSeries() {
        return getLongestSeries(getPlot().getSeriesListForRenderer(this.getClass()));
    }

    private void drawBars(Canvas canvas, RectF plotArea, TreeMap<Number, XYSeries> seriesMap, int x) {
        Paint p = new Paint();
        p.setColor(Color.RED);
        Object[] oa = seriesMap.entrySet().toArray();
        //Map.Entry<Number, XYSeries> entry;

        for(int i = oa.length-1; i >= 0; i--) {
                    //entry = (Map.Entry<Number, XYSeries>) oa[i];
        drawBar(canvas, plotArea, x,
                ((Map.Entry<Number, XYSeries>)oa[i]).getValue());
        }
        /*for(int i = oa.length-1; i >= 0; i--) {
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
        }*/
    }

    /**
     * Retrieves the BarFormatter instance that corresponds with the series passed in.
     * Can be overridden to return other BarFormatters as a result of touch events etc.
     * @param index index of the point being rendered.
     * @param series XYSeries to which the point being rendered belongs.
     * @return
     */
    protected T getFormatter(int index, XYSeries series) {
        return getFormatter(series);
    }

    protected void drawBar(Canvas canvas, RectF plotArea, int index, XYSeries series) {
        Number xVal = series.getX(index);
        Number yVal = series.getY(index);
        BarFormatter formatter = getFormatter(index, series); // TODO: make this more efficient
        if (yVal != null && xVal != null) {  // make sure there's a real value to draw
            switch (style) {
                case FIXED_WIDTH:
                    float halfWidth = barWidth / 2;
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
