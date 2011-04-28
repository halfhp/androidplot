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
        //Point[] seriesMap = new Point[longest];

        //int canvasState = canvas.save();
        //canvas.clipRect(plotArea, Line.Op.REPLACE);
        for(int i = 0; i < longest; i++) {
            seriesMap.clear();
            List<XYSeries> seriesList = getPlot().getSeriesListForRenderer(this.getClass());
            for(XYSeries series : seriesList) {
                if(i < series.size()) {
                    seriesMap.put(series.getY(i), series);
                }// else {
                //    seriesMap.put(null, series);
                //}
            }
            drawBars(canvas, plotArea, seriesMap, i);
        }

        //canvas.restoreToCount(canvasState);

        /*for(XYSeries series : getPlot().getSeriesListForRenderer(this.getClass())) {

            drawSeries(canvas, plotArea, series, getFormatter(series));
        }*/
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
            //drawSeries(canvas, plotArea, series, getFormatter(series));
        }
        return longest;
    }

    private void drawBars(Canvas canvas, RectF plotArea, TreeMap<Number, XYSeries> seriesMap, int x) {
        Paint p = new Paint();
        p.setColor(Color.RED);



        Object[] oa = seriesMap.entrySet().toArray();

        //(Map.Entry<Number, XYSeries>[])
        //Map.Entry<Number, XYSeries>[] entryArr = new Map.Entry<Number, XYSeries>[0];
        //entryArr = seriesMap.entrySet().toArray(entryArr);

        //seriesMap.entrySet().

        //
        //for (Map.Entry<Number, XYSeries> entry : seriesMap.entrySet()) {
        Map.Entry<Number, XYSeries> entry;
        for(int i = oa.length-1; i >= 0; i--) {
            entry = (Map.Entry<Number, XYSeries>) oa[i];
        //for (Map.Entry<Number, XYSeries> entry = seriesMap.lastEntry(); entry != null; entry = seriesMap.lowerEntry(entry.getKey())) {
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
