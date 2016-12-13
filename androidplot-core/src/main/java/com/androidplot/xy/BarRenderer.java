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
 * Renders the points in an XYSeries as bars.  See {@link Style} javadoc for details on supported
 * presentation styles.
 *
 */
public class BarRenderer<FormatterType extends BarFormatter> extends GroupRenderer<FormatterType> {

    private Style style = Style.OVERLAID;  // default Render Style
    private BarWidthMode barWidthMode = BarWidthMode.FIXED_WIDTH;  // default Width Style
    private float barWidth = 5;
    private float barGap = 1;

    /**
     * How bars should be laid out when in a group of 2 or more series.
     */
    public enum Style {

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
        FIXED_WIDTH,        // bar width is always barWidth
        VARIABLE_WIDTH      // bar width is calculated so that there is only barGap between each bar
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

    public void setStyle(Style renderStyle) {
        this.style = renderStyle;
    }

    public Style getStyle() {
        return this.style;
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
            case FIXED_WIDTH:
                setBarWidth(value);
                break;
            case VARIABLE_WIDTH:
                setBarGap(value);
                break;
            default:
                break;
        }
    }

    protected BarComparator getBarComparator() {
        return new BarComparator(getStyle());
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
                    if (axisMap.containsKey(bar.intX)) {
                        barGroup = axisMap.get(bar.intX);
                    } else {
                        barGroup = new BarGroup(bar.intX, plotArea);
                        axisMap.put(bar.intX, barGroup);
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

        // The default gap between each bar section
        int gap = (int) barGap;

        // Determine roughly how wide (rough_width) this bar should be. This is then used as a default width
        // when there are gaps in the data or for the first/last bars.
        float f_rough_width = ((plotArea.width() - ((axisMap.size() - 1) * gap)) / (axisMap
                .size() - 1));
        int rough_width = (int) f_rough_width;
        if (rough_width < 0) rough_width = 0;
        if (gap > rough_width) {
            gap = rough_width / 2;
        }

		/*
         * Calculate the dimensions of each barGroup and then draw each bar within it according to
		 * the Render Style and Width Style.
		 */
        for (Number key : axisMap.keySet()) {

            BarGroup barGroup = axisMap.get(key);

            // Determine the exact left and right X for the Bar Group
            switch (barWidthMode) {
                case FIXED_WIDTH:
                    // use intX and go halfwidth either side.
                    barGroup.leftX = barGroup.intX - (int) (barWidth / 2);
                    barGroup.width = (int) barWidth;
                    barGroup.rightX = barGroup.leftX + barGroup.width;
                    break;
                case VARIABLE_WIDTH:
                    if (barGroup.prev != null) {
                        if (barGroup.intX - barGroup.prev.intX - gap - 1 > (int) (rough_width * 1.5)) {
                            // use intX and go halfwidth either side.
                            barGroup.leftX = barGroup.intX - (rough_width / 2);
                            barGroup.width = rough_width;
                            barGroup.rightX = barGroup.leftX + barGroup.width;
                        } else {
                            // base left off prev right to get the gap correct.
                            barGroup.leftX = barGroup.prev.rightX + gap + 1;
                            if (barGroup.leftX > barGroup.intX) barGroup.leftX = barGroup.intX;
                            // base right off intX + halfwidth.
                            barGroup.rightX = barGroup.intX + (rough_width / 2);
                            // calculate the width
                            barGroup.width = barGroup.rightX - barGroup.leftX;
                        }
                    } else {
                        // use intX and go halfwidth either side.
                        barGroup.leftX = barGroup.intX - (rough_width / 2);
                        barGroup.width = rough_width;
                        barGroup.rightX = barGroup.leftX + barGroup.width;
                    }
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
            switch (style) {
                case OVERLAID:
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        final boolean isFalling = bar.yVal < rangeOrigin;
                        drawBar(canvas, bar, new RectF(
                                bar.barGroup.leftX,
                                isFalling ? basePositionY : bar.intY,
                                bar.barGroup.rightX,
                                isFalling ? bar.intY : basePositionY));
                    }
                    break;
                case SIDE_BY_SIDE:
                    int width = barGroup.width / barGroup.bars.size();
                    int leftX = barGroup.leftX;
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        final boolean isFalling = bar.yVal < rangeOrigin;
                        drawBar(canvas, bar, new RectF(
                                leftX,
                                isFalling ? basePositionY : bar.intY,
                                leftX + width,
                                isFalling ? bar.intY : basePositionY));
                        leftX = leftX + width;
                    }
                    break;
                case STACKED:
                    int bottom = (int) barGroup.plotArea.bottom;
                    Collections.sort(barGroup.bars, comparator);
                    for (Bar bar : barGroup.bars) {
                        // TODO: handling sub range-origin values for the purpose of labeling
                        final int height = (int) bar.barGroup.plotArea.bottom - bar.intY;
                        final int top = bottom - height;
                        drawBar(canvas, bar, new RectF(bar.barGroup.leftX, top, bar.barGroup.rightX, bottom));
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
                    rect.centerX() + plf.hOffset, bar.intY + plf.vOffset,
                    plf.getTextPaint());
        }
    }

    public static class Bar<FormatterType extends BarFormatter> {

        public final XYSeries series;
        private final FormatterType formatter;
        public final int seriesIndex;
        public final int seriesOrder;
        public final double yVal;
        public final int intX;
        public final int intY;
        protected BarGroup barGroup;

        // TODO: factor out plot param
        public Bar(XYPlot plot, XYSeries series, FormatterType formatter, int seriesOrder, int seriesIndex, RectF plotArea) {
            this.series = series;
            this.formatter = formatter;
            this.seriesIndex = seriesIndex;
            this.seriesOrder = seriesOrder;

            final double xVal = series.getX(seriesIndex).doubleValue();
            final double pixX = plot.getBounds().getxRegion()
                    .transform(xVal, plotArea.left, plotArea.right, false);
            this.intX = (int) Math.round(pixX);

            if (series.getY(seriesIndex) != null) {
                this.yVal = series.getY(seriesIndex).doubleValue();
                final double pixY = plot.getBounds().yRegion
                        .transform(yVal, plotArea.top, plotArea.bottom, true);
                this.intY = (int) Math.round(pixY);
            } else {
                this.yVal = 0;
                this.intY = (int) plotArea.bottom;
            }
        }

        public FormatterType getFormatter() {
            return formatter;
        }
    }

    private static class BarGroup {

        public ArrayList<Bar> bars;
        public int intX;
        public int width, leftX, rightX;
        public RectF plotArea;
        public BarGroup prev;

        public BarGroup(int intX, RectF plotArea) {
            // Setup the TreeMap with the required comparator
            this.bars = new ArrayList<>(); // create a comparator that compares series title given the index.
            this.intX = intX;
            this.plotArea = plotArea;
        }

        public void addBar(Bar bar) {
            bar.barGroup = this;
            this.bars.add(bar);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class BarComparator implements Comparator<Bar> {

        private final Style style;

        public BarComparator(Style style) {
            this.style = style;
        }

        @Override
        public int compare(Bar bar1, Bar bar2) {
            switch (style) {
                case OVERLAID:
                    return Integer.valueOf(bar1.intY).compareTo(bar2.intY);
                case SIDE_BY_SIDE:
                case STACKED:
                default:
                    return Integer.valueOf(bar1.seriesOrder).compareTo(bar2.seriesOrder);
            }
        }
    }

}
