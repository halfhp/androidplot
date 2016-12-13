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
import java.util.Map.Entry;
import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesBundle;

/**
 * Renders the points in an XYSeries as bars.  See {@link BarOrientation} javadoc for details on supported
 * presentation styles.
 *
 */
public class BarRenderer<FormatterType extends BarFormatter> extends GroupRenderer<FormatterType> {

    private BarOrientation barOrientation = BarOrientation.OVERLAID;  // default Render Style
    private BarWidthMode barWidthMode = BarWidthMode.FIXED_BAR_WIDTH;  // default Width Style
    private float barWidth = 5;
    private float barGap = 1;

    /**
     * How bars should be laid out when in a group of 2 or more series.
     */
    public enum BarOrientation {

        /**
         * Bars are drawn "overlapping" one another, with taller bars being drawn behind
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
    public enum BarWidthMode {
        FIXED_BAR_WIDTH,  // bar width is always barWidth
        FIXED_GAP_WIDTH   // bar width is calculated relative to a fixed gap width between each bar
    }

    public BarRenderer(XYPlot plot) {
        super(plot);
    }

    /**
     * Sets the width of the bars when using the FIXED_WIDTH render style
     * @param barWidth
     */
    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * Sets the size of the gap between the bar (or bar groups) when using the VARIABLE_WIDTH render style
     * @param barGap
     */
    public void setBarGap(float barGap) {
        this.barGap = barGap;
    }

    public void setBarOrientation(BarOrientation renderBarOrientation) {
        this.barOrientation = renderBarOrientation;
    }

    public BarOrientation getBarOrientation() {
        return this.barOrientation;
    }

    public void setBarWidthMode(BarWidthMode widthStyle) {
        this.barWidthMode = widthStyle;
    }

    public BarWidthMode getBarWidthMode() {
        return this.barWidthMode;
    }

    public void setBarWidth(BarWidthMode mode, float value) {
        setBarWidthMode(mode);
        switch (mode) {
            case FIXED_BAR_WIDTH:
                setBarWidth(value);
                break;
            case FIXED_GAP_WIDTH:
                setBarGap(value);
                break;
            default:
                break;
        }
    }

    protected BarComparator getBarComparator() {
        return new BarComparator(getBarOrientation());
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

        TreeMap<Number, BarGroup> axisMap = new TreeMap<Number, BarGroup>();

        /*
         * Build the axisMap (yVal,BarGroup)... a TreeMap of BarGroups
         * BarGroups represent a point on the X axis where a single or group of bars need to be drawn.
         */
        int seriesOrder = 0;
        for (SeriesBundle<XYSeries, ? extends FormatterType> thisPair : sfList) {
            BarGroup barGroup;

            // For each value in the series
            for (int i = 0; i < seriesSize; i++) {

                if (thisPair.getSeries().getX(i) != null) {

                    // get a new bar object
                    Bar bar = new Bar(getPlot(), thisPair.getSeries(), thisPair.getFormatter(), seriesOrder, i, plotArea);

                    // Find or create the barGroup
                    if (axisMap.containsKey(bar.xPix)) {
                        barGroup = axisMap.get(bar.xPix);
                    } else {
                        barGroup = new BarGroup(bar.xPix, plotArea);
                        axisMap.put(bar.xPix, barGroup);
                    }
                    barGroup.addBar(bar);
                }
            }
            seriesOrder++;
        }

        // Loop through the axisMap linking up prev pointers
        BarGroup prev, current;
        prev = null;
        for (Entry<Number, BarGroup> mapEntry : axisMap.entrySet()) {
            current = mapEntry.getValue();
            current.prev = prev;
            prev = current;
        }

		/*
         * Calculate the dimensions of each barGroup and then draw each bar within it according to
		 * the Render Style and Width Style.
		 */
        for (Number key : axisMap.keySet()) {

            BarGroup barGroup = axisMap.get(key);

            // Determine the exact left and right X for the Bar Group
            switch (barWidthMode) {
                case FIXED_BAR_WIDTH:
                    barGroup.leftPix = barGroup.centerPix - (barWidth / 2);
                    barGroup.rightPix = barGroup.leftPix + barWidth;
                    break;
                case FIXED_GAP_WIDTH:
                    final float totalGapWidth = axisMap.size() * barGap;
                    final float barGroupWidth = (plotArea.width() - totalGapWidth) / axisMap.size();
                    final float barGroupHalfWidth = barGroupWidth / 2;
                    barGroup.leftPix = barGroup.centerPix - barGroupHalfWidth;
                    barGroup.rightPix = barGroup.centerPix + barGroupHalfWidth;
                    break;
                default:
                    break;
            }

    		/*
    		 * Draw the bars within the barGroup area.
    		 */
            double rangeOrigin = getPlot().getRangeOrigin().doubleValue();
            float basePositionY = (float) getPlot().getBounds().yRegion
                    .transform(rangeOrigin, plotArea.top, plotArea.bottom, true);

            final BarComparator comparator = getBarComparator();
            switch (barOrientation) {
                case OVERLAID:
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        final boolean isFalling = bar.yVal < rangeOrigin;
                        drawBar(canvas, bar, new RectF(
                                bar.barGroup.leftPix,
                                isFalling ? basePositionY : bar.yPix,
                                bar.barGroup.rightPix,
                                isFalling ? bar.yPix : basePositionY));
                    }
                    break;
                case SIDE_BY_SIDE:
                    final float width = barGroup.getWidth() / barGroup.bars.size();
                    float leftX = barGroup.leftPix;
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        final boolean isFalling = bar.yVal < rangeOrigin;
                        drawBar(canvas, bar, new RectF(
                                leftX,
                                isFalling ? basePositionY : bar.yPix,
                                leftX + width,
                                isFalling ? bar.yPix : basePositionY));
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
                        drawBar(canvas, bar, new RectF(bar.barGroup.leftPix, top, bar.barGroup.rightPix, bottom));
                        bottom = top;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void drawBar(Canvas canvas, Bar bar, RectF rect) {
        BarFormatter formatter = getFormatter(bar.seriesIndex, bar.series);
        if(formatter == null) {
            formatter = bar.getFormatter();
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

        PointLabelFormatter plf = formatter.hasPointLabelFormatter()
                                  ? formatter.getPointLabelFormatter() : null;

        PointLabeler pointLabeler = formatter != null
                                    ? formatter.getPointLabeler() : null;

        if (plf != null && plf.hasTextPaint() && pointLabeler != null) {
            canvas.drawText(pointLabeler.getLabel(bar.series, bar.seriesIndex),
                    rect.centerX() + plf.hOffset, bar.yPix + plf.vOffset,
                    plf.getTextPaint());
        }
    }

    public static class Bar<FormatterType extends BarFormatter> {

        public final XYSeries series;
        private final FormatterType formatter;
        public final int seriesIndex;
        public final int seriesOrder;
        public final double yVal;
        public final float xPix;
        public final float yPix;
        protected BarGroup barGroup;

        // TODO: factor out plot param
        public Bar(XYPlot plot, XYSeries series, FormatterType formatter, int seriesOrder, int seriesIndex, RectF plotArea) {
            this.series = series;
            this.formatter = formatter;
            this.seriesIndex = seriesIndex;
            this.seriesOrder = seriesOrder;

            final double xVal = series.getX(seriesIndex).doubleValue();
            xPix = (float) plot.getBounds().getxRegion()
                    .transform(xVal, plotArea.left, plotArea.right, false);

            if (series.getY(seriesIndex) != null) {
                this.yVal = series.getY(seriesIndex).doubleValue();
                this.yPix = (float) plot.getBounds().yRegion
                        .transform(yVal, plotArea.top, plotArea.bottom, true);
            } else {
                this.yVal = 0;
                this.yPix = plotArea.bottom;
            }
        }

        public FormatterType getFormatter() {
            return formatter;
        }
    }

    private static class BarGroup {

        public ArrayList<Bar> bars;
        public float centerPix;
        //public float widthPix;
        public float leftPix;
        public float rightPix;
        public RectF plotArea;
        public BarGroup prev;

        public BarGroup(float centerPix, RectF plotArea) {
            // Setup the TreeMap with the required comparator
            this.bars = new ArrayList<>(); // create a comparator that compares series title given the index.
            this.centerPix = centerPix;
            this.plotArea = plotArea;
        }

        public void addBar(Bar bar) {
            bar.barGroup = this;
            this.bars.add(bar);
        }

        protected float getWidth() {
            return rightPix - leftPix;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class BarComparator implements Comparator<Bar> {

        private final BarOrientation barOrientation;

        public BarComparator(BarOrientation barOrientation) {
            this.barOrientation = barOrientation;
        }

        @Override
        public int compare(Bar bar1, Bar bar2) {
            switch (barOrientation) {
                case OVERLAID:
                    return Float.valueOf(bar1.yPix).compareTo(bar2.yPix);
                case SIDE_BY_SIDE:
                case STACKED:
                default:
                    return Integer.valueOf(bar1.seriesOrder).compareTo(bar2.seriesOrder);
            }
        }
    }

}
