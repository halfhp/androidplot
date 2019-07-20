/*
 * Copyright 2015 AndroidPlot.com
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.RectFUtils;

/**
 * Renders the points in an XYSeries as bars.  See {@link BarOrientation} javadoc for details on supported
 * presentation styles.
 *
 */
public class BarRenderer<FormatterType extends BarFormatter> extends GroupRenderer<FormatterType> {

    private BarOrientation barOrientation = BarOrientation.OVERLAID;  // default Render Style
    private BarGroupWidthMode barGroupWidthMode = BarGroupWidthMode.FIXED_WIDTH; // default Width Style

    /**
     * Represents the size in pixels of either bar width or bar gap width, depending on the current
     * value of barWidthMode.
     */
    private float width = PixelUtils.dpToPix(3);

    /**
     * How bars should be laid out when in a group of 2 or more series.
     */
    public enum BarOrientation {

        /**
         * Bars are drawn overlapping one another, in the order their respective series
         * was added to the plot.
         */
        IN_ORDER,

        /**
         * Bars are drawn overlapping one another, with taller bars being drawn behind
         * the shorter ones.
         */
        OVERLAID,           // bars are overlaid in descending y-val order (largest val in back)

        /**
         * Bars are stacked on top of one another so that the sum of their yVals produces the final
         * height of that bar.
         */
        STACKED,            // bars are drawn stacked vertically on top of each other

        /**
         * Bars are drawn next to one another, grouped by iVal
         */
        SIDE_BY_SIDE        // bars are drawn horizontally next to each-other
    }

    /**
     * Mode with which to calculate the width of each bar.
     */
    public enum BarGroupWidthMode {
        FIXED_WIDTH,  // bar width is always barWidth
        FIXED_GAP     // bar width is calculated relative to a fixed gap width between each bar
    }

    public BarRenderer(XYPlot plot) {
        super(plot);
    }

    public void setBarOrientation(BarOrientation renderBarOrientation) {
        this.barOrientation = renderBarOrientation;
    }

    public BarOrientation getBarOrientation() {
        return this.barOrientation;
    }

    public BarGroupWidthMode getBarGroupWidthMode() {
        return this.barGroupWidthMode;
    }

    public float getBarGroupWidth() {
        return this.width;
    }

    public void setBarGroupWidth(BarGroupWidthMode mode, float width) {
        this.barGroupWidthMode = mode;
        this.width = width;
    }

    protected BarComparator getBarComparator(float rangeOriginPx) {
        return new BarComparator(getBarOrientation(), rangeOriginPx);
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, BarFormatter formatter) {
        if (formatter.hasFillPaint()) {
            canvas.drawRect(rect, formatter.getFillPaint());
        }
        canvas.drawRect(rect, formatter.getBorderPaint());
    }

    /**
     * Retrieves the BarFormatter instance that corresponds with the series passed in.
     * Can be overridden to return other BarFormatters as a result of touch events etc.
     * @param index index of the point being rendered.
     * @param series XYSeries to which the point being rendered belongs.
     * @return The desired getFormatter or null to use the default.
     */
    @SuppressWarnings("UnusedParameters")
    public FormatterType getFormatter(int index, XYSeries series) {
        return null;
    }

    @Override
    public void onRender(Canvas canvas, RectF plotArea, List<SeriesBundle<XYSeries,
            ? extends FormatterType>> sfList, int seriesSize, RenderStack stack) {

        List<BarGroup> barGroups = new ArrayList<>();

        /*
         * Build the axisMap (yVal,BarGroup)... a TreeMap of BarGroups
         * BarGroups represent a point on the X axis where a single or group of bars need to be drawn.
         */
        for(int i = 0; i < seriesSize; i++) {
            final BarGroup group  = new BarGroup(i, 0, plotArea);
            int seriesOrder = 0;
            for(SeriesBundle<XYSeries, ? extends FormatterType> bundle : sfList) {
                // TODO: is this null check really necessary?
                if(bundle.getSeries().getX(i) != null) {
                    Bar bar = new Bar(getPlot(), bundle.getSeries(),
                            bundle.getFormatter(), seriesOrder, i, plotArea);
                    group.addBar(bar);
                    group.centerPix = bar.xPix;
                }
                seriesOrder++;
            }
            barGroups.add(group);
        }

		/*
         * Calculate the dimensions of each barGroup and then draw each bar within it according to
		 * the Render Style and Width Style.
		 */
        final int groupCount = barGroups.size();
        for(BarGroup barGroup : barGroups) {

            // Determine the exact left and right X for the Bar Group
            switch (barGroupWidthMode) {
                case FIXED_WIDTH:
                    barGroup.leftPix = barGroup.centerPix - (width / 2);
                    barGroup.rightPix = barGroup.leftPix + width;
                    break;
                case FIXED_GAP:
                    float barWidth = plotArea.width();
                    if(groupCount > 1) {
                        barWidth = (barGroups.get(1).centerPix - barGroups.get(0).centerPix) - width;
                    }

                    final float halfWidth = barWidth / 2;
                    barGroup.leftPix = barGroup.centerPix - halfWidth;
                    barGroup.rightPix = barGroup.centerPix + halfWidth;
                    break;
                default:
                    break;
            }

    		/*
    		 * Draw the bars within the barGroup area.
    		 */
            double rangeOrigin = getPlot().getRangeOrigin().doubleValue();
            float rangeOriginPx = (float) getPlot().getBounds().yRegion
                    .transform(rangeOrigin, plotArea.top, plotArea.bottom, true);

            final BarComparator comparator = getBarComparator(rangeOriginPx);
            switch (barOrientation) {
                case IN_ORDER:
                case OVERLAID:
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        drawBar(canvas, bar, createBarRect(
                                bar.barGroup.leftPix,
                                bar.yPix,
                                bar.barGroup.rightPix,
                                rangeOriginPx, bar.formatter));
                    }
                    break;
                case SIDE_BY_SIDE:
                    final float width = barGroup.getWidth() / barGroup.bars.size();
                    float leftX = barGroup.leftPix;
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        drawBar(canvas, bar, createBarRect(
                                leftX, bar.yPix,
                                leftX + width, rangeOriginPx,
                                bar.formatter));
                        leftX = leftX + width;
                    }
                    break;
                case STACKED:
                    float bottom = (int) barGroup.plotArea.bottom;
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        // TODO: handling sub range-origin values for the purpose of labeling
                        final float height = (int) bar.barGroup.plotArea.bottom - bar.yPix;
                        final float top = bottom - height;
                        drawBar(canvas, bar, createBarRect(
                                bar.barGroup.leftPix, top,
                                bar.barGroup.rightPix, bottom,
                                bar.formatter));
                        bottom = top;
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected BarOrientation: " + barOrientation);
            }
        }
    }

    protected RectF createBarRect(float w1, float h1, float w2, float h2, BarFormatter formatter) {
        final RectF result = RectFUtils.createFromEdges(w1, h1,w2, h2);
        result.left += formatter.getMarginLeft();
        result.right -= formatter.getMarginRight();
        result.top += formatter.getMarginTop();
        result.bottom -= formatter.getMarginBottom();
        return result;
    }

    protected void drawBar(Canvas canvas, Bar<FormatterType> bar, RectF rect) {

        // null yVals are skipped:
        if(bar.getY() == null) {
            return;
        }

        BarFormatter formatter = getFormatter(bar.i, bar.series);
        if(formatter == null) {
            formatter = bar.formatter;
        }
        if(rect.height() > 0 && rect.width() > 0) {
            if (formatter.hasFillPaint()) {
                canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom,
                        formatter.getFillPaint());
            }

            if (formatter.hasLinePaint()) {
                canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom,
                        formatter.getBorderPaint());
            }
        }

        PointLabelFormatter plf =
                formatter.hasPointLabelFormatter() ? formatter.getPointLabelFormatter() : null;

        PointLabeler pointLabeler =
                formatter != null ? formatter.getPointLabeler() : null;

        if (plf != null && plf.hasTextPaint() && pointLabeler != null) {
            canvas.drawText(pointLabeler.getLabel(bar.series, bar.i),
                    rect.centerX() + plf.hOffset, bar.yPix + plf.vOffset,
                    plf.getTextPaint());
        }
    }

    /**
     *
     * @param <FormatterType>
     */
    public static class Bar<FormatterType extends BarFormatter> {

        public final XYSeries series;
        public final FormatterType formatter;
        public final int i;
        public final int seriesOrder;
        public final float xPix;
        public final float yPix;
        protected BarGroup barGroup;

        // TODO: factor out plot param
        public Bar(XYPlot plot, XYSeries series, FormatterType formatter, int seriesOrder, int i, RectF plotArea) {
            this.series = series;
            this.formatter = formatter;
            this.i = i;
            this.seriesOrder = seriesOrder;

            final double xVal = series.getX(i).doubleValue();
            xPix = (float) plot.getBounds().getxRegion()
                    .transform(xVal, plotArea.left, plotArea.right, false);

            if (series.getY(i) != null) {
                final double yVal = series.getY(i).doubleValue();
                this.yPix = (float) plot.getBounds().yRegion
                        .transform(yVal, plotArea.top, plotArea.bottom, true);
            } else {
                this.yPix = 0;
            }
        }

        public Number getY() {
            return series.getY(i);
        }
    }

    /**
     * A collection of one or more {@Bar} instances sharing a common iVal.
     */
    private static class BarGroup {

        public ArrayList<Bar> bars;
        public int i;
        public float centerPix;
        public float leftPix;
        public float rightPix;
        public RectF plotArea;

        public BarGroup(int i, float centerPix, RectF plotArea) {
            // Setup the TreeMap with the required comparator
            this.bars = new ArrayList<>(); // create a comparator that compares series title given the index.
            this.centerPix = centerPix;
            this.plotArea = plotArea;
            this.i = i;
        }

        public void addBar(Bar bar) {
            bar.barGroup = this;
            this.bars.add(bar);
        }

        protected float getWidth() {
            return rightPix - leftPix;
        }
    }

    /**
     * Used to determine the order in which bars of the same group will be drawn.
     */
    @SuppressWarnings("WeakerAccess")
    public static class BarComparator implements Comparator<Bar> {

        private final BarOrientation barOrientation;
        private final float rangeOriginPx;

        public BarComparator(BarOrientation barOrientation, float rangeOriginPx) {
            this.rangeOriginPx = rangeOriginPx;
            this.barOrientation = barOrientation;
        }

        @Override
        public int compare(Bar bar1, Bar bar2) {
            switch (barOrientation) {
                case OVERLAID:
                    if(bar1.yPix > rangeOriginPx && bar2.yPix > rangeOriginPx) {
                        return Float.valueOf(bar2.yPix).compareTo(bar1.yPix);
                    } else {
                        return Float.valueOf(bar1.yPix).compareTo(bar2.yPix);
                    }
                case IN_ORDER:
                case SIDE_BY_SIDE:
                case STACKED:
                default:
                    return Integer.valueOf(bar1.seriesOrder).compareTo(bar2.seriesOrder);
            }
        }
    }

}
