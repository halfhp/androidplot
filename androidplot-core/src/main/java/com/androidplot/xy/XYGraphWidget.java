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

import android.graphics.*;

import com.androidplot.NumberLabelFormatter;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.BoxModel;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.RenderStack;
import com.androidplot.ui.Size;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.*;

import java.text.DecimalFormat;
import java.text.Format;

/**
 * Displays graphical data (lines, points, etc.) annotated with domain and range tick markers.
 * The inner area of the graph upon which grid ticks, lines and points are rendered is called the "grid" area.
 */
public class XYGraphWidget extends Widget {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final float FLOAT_ONE = 1;

    private static final int MARKER_LABEL_SPACING = TWO;

    // max size of domain tick labels  in pixels
    private float domainTickLabelWidth;
    private float rangeTickLabelWidth;
    private float domainTickLabelVerticalOffset = -5;
    private float domainTickLabelHorizontalOffset = ZERO;

    // allows tweaking of text position
    private float rangeTickLabelHorizontalOffset = FLOAT_ONE;

    // allows tweaking of text position
    private float rangeTickLabelVerticalOffset = ZERO;

    private int ticksPerRangeLabel = ONE;
    private int ticksPerDomainLabel = ONE;

    private BoxModel gridBox = new BoxModel();
    private DisplayDimensions gridDimensions;

    private boolean showDomainLabels;
    private boolean showRangeLabels;

    private float domainTickExtension;
    private float rangeTickExtension;
    private float domainLabelSubTickExtension = ZERO;
    private float rangeLabelSubTickExtension = ZERO;
    private Paint gridBackgroundPaint;
    private Paint rangeGridLinePaint;
    private Paint rangeSubGridLinePaint;
    private Paint domainGridLinePaint;
    private Paint domainSubGridLinePaint;
    private Paint domainTickLabelPaint;
    private Paint rangeTickLabelPaint;
    private Paint domainCursorPaint;
    private Paint rangeCursorPaint;
    private Paint cursorLabelPaint;
    private Paint cursorLabelBackgroundPaint;
    private XYPlot plot;
    private Format rangeValueFormat;
    private Format domainValueFormat;
    private Paint domainOriginLinePaint;
    private Paint rangeOriginLinePaint;
    private Paint domainOriginTickLabelPaint;
    private Paint rangeOriginTickLabelPaint;

    private float domainCursorPosition;
    private float rangeCursorPosition;

    private boolean drawCursorLabelEnabled = true;
    private boolean drawMarkersEnabled = true;

    private boolean rangeAxisLeft = true;
    private boolean domainAxisBottom = true;

    private boolean rangeTick = true;
    private boolean rangeSubTick = true;
    private boolean domainTick = true;
    private boolean domainSubTick = true;

    private float rangeLabelOrientation;
    private float domainLabelOrientation;

    private Mapping<Paint, Number> domainTickLabelPaintMap;
    private Mapping<Paint, Number> rangeTickLabelPaintMap;

    private LayerHash<RectRegion, NumberLabelFormatter> tickLabelRegionFormatters;

    private RenderStack<? extends XYSeries, ? extends XYSeriesFormatter> renderStack;

    private static final float DEFAULT_TICK_LABEL_TEXT_SIZE_PX = PixelUtils.spToPix(15);

    public float getRangeLabelOrientation() {
        return rangeLabelOrientation;
    }

    public void setRangeLabelOrientation(float rangeLabelOrientation) {
        this.rangeLabelOrientation = rangeLabelOrientation;
    }

    public float getDomainLabelOrientation() {
        return domainLabelOrientation;
    }

    public void setDomainLabelOrientation(float domainLabelOrientation) {
        this.domainLabelOrientation = domainLabelOrientation;
    }

    /**
     *
     * @return
     */
    public Mapping<Paint, Number> getDomainTickLabelPaintMap() {
        return domainTickLabelPaintMap;
    }

    /**
     * Set a mapping to override the Paint used to draw domain labels.  The mapping should
     * return null for values that should not be overridden.
     * @param domainTickLabelPaintMap
     */
    public void setDomainTickLabelPaintMap(Mapping<Paint, Number> domainTickLabelPaintMap) {
        this.domainTickLabelPaintMap = domainTickLabelPaintMap;
    }

    public Mapping<Paint, Number> getRangeTickLabelPaintMap() {
        return rangeTickLabelPaintMap;
    }

    /**
     * Set a mapping to override the Paint used to draw range labels.  The mapping should
     * return null for values that should not be overridden.
     * @param rangeLabelTickPaintMap
     */
    public void setRangeLabelTickPaintMap(Mapping<Paint, Number> rangeLabelTickPaintMap) {
        this.rangeTickLabelPaintMap = rangeLabelTickPaintMap;
    }

    public BoxModel getGridBox() {
        return gridBox;
    }

    public void setGridBox(BoxModel gridBox) {
        this.gridBox = gridBox;
    }

    public Paint getDomainCursorPaint() {
        return domainCursorPaint;
    }

    /**
     *
     * @param domainCursorPaint The {@link Paint} used to draw the domain cursor line.
     *                          Set to null (default) to disable.
     */
    public void setDomainCursorPaint(Paint domainCursorPaint) {
        this.domainCursorPaint = domainCursorPaint;
    }

    public Paint getRangeCursorPaint() {
        return rangeCursorPaint;
    }

    /**
     *
     * @param rangeCursorPaint The {@link Paint} used to draw the range cursor line.
     *                         Set to null (default) to disable.
     */
    public void setRangeCursorPaint(Paint rangeCursorPaint) {
        this.rangeCursorPaint = rangeCursorPaint;
    }

    {
        gridBackgroundPaint = new Paint();
        gridBackgroundPaint.setColor(Color.rgb(140, 140, 140));
        gridBackgroundPaint.setStyle(Paint.Style.FILL);

        rangeGridLinePaint = new Paint();
        rangeGridLinePaint.setColor(Color.rgb(180, 180, 180));
        rangeGridLinePaint.setAntiAlias(true);
        rangeGridLinePaint.setStyle(Paint.Style.STROKE);

        domainGridLinePaint = new Paint(rangeGridLinePaint);
        domainSubGridLinePaint = new Paint(domainGridLinePaint);
        rangeSubGridLinePaint = new Paint(rangeGridLinePaint);

        domainOriginLinePaint = new Paint();
        domainOriginLinePaint.setColor(Color.WHITE);
        domainOriginLinePaint.setAntiAlias(true);

        rangeOriginLinePaint = new Paint();
        rangeOriginLinePaint.setColor(Color.WHITE);
        rangeOriginLinePaint.setAntiAlias(true);

        domainOriginTickLabelPaint = new Paint();
        domainOriginTickLabelPaint.setColor(Color.WHITE);
        domainOriginTickLabelPaint.setAntiAlias(true);
        domainOriginTickLabelPaint.setTextAlign(Paint.Align.CENTER);
        domainOriginTickLabelPaint.setTextSize(DEFAULT_TICK_LABEL_TEXT_SIZE_PX);

        rangeOriginTickLabelPaint = new Paint();
        rangeOriginTickLabelPaint.setColor(Color.WHITE);
        rangeOriginTickLabelPaint.setAntiAlias(true);
        rangeOriginTickLabelPaint.setTextAlign(Paint.Align.RIGHT);
        rangeOriginTickLabelPaint.setTextSize(DEFAULT_TICK_LABEL_TEXT_SIZE_PX);

        domainTickLabelPaint = new Paint();
        domainTickLabelPaint.setColor(Color.LTGRAY);
        domainTickLabelPaint.setAntiAlias(true);
        domainTickLabelPaint.setTextAlign(Paint.Align.CENTER);
        domainTickLabelPaint.setTextSize(DEFAULT_TICK_LABEL_TEXT_SIZE_PX);

        rangeTickLabelPaint = new Paint();
        rangeTickLabelPaint.setColor(Color.LTGRAY);
        rangeTickLabelPaint.setAntiAlias(true);
        rangeTickLabelPaint.setTextAlign(Paint.Align.RIGHT);
        rangeTickLabelPaint.setTextSize(DEFAULT_TICK_LABEL_TEXT_SIZE_PX);

        domainCursorPaint = new Paint();
        domainCursorPaint.setColor(Color.YELLOW);

        rangeCursorPaint = new Paint();
        rangeCursorPaint.setColor(Color.YELLOW);

        cursorLabelPaint = new Paint();
        cursorLabelPaint.setColor(Color.YELLOW);
        cursorLabelBackgroundPaint = new Paint();
        cursorLabelBackgroundPaint.setColor(Color.argb(100, 50, 50, 50));
        setMarginTop(7);
        setMarginRight(4);
        setMarginBottom(4);
        rangeValueFormat = new DecimalFormat("0.0");
        domainValueFormat = new DecimalFormat("0.0");
        tickLabelRegionFormatters = new LayerHash<>();
        setClippingEnabled(true);
    }

    public XYGraphWidget(LayoutManager layoutManager, XYPlot plot, Size size) {
        super(layoutManager, size);
        this.plot = plot;
        renderStack = new RenderStack(plot);
    }

    public Layerable<RectRegion> getTickLabelRegionFormatters() {
        return tickLabelRegionFormatters;
    }

    /**
     * Add a new Region used for rendering tick labels. Note that it is
     * possible to add multiple Region instances which overlap, in which case
     * the last region to be added will be used. It is up to the developer to
     * guard against this often undesirable situation.
     * 
     * @param region
     * @param formatter
     */
    public void addTickLabelFormatter(RectRegion region, NumberLabelFormatter formatter) {
        tickLabelRegionFormatters.addToTop(region, formatter);
    }

    /**
     * Convenience method - wraps addDomainTickLabelFormatter, using
     * Double.POSITIVE_INFINITY and Double.NEGATIVE_INFINITY to mask off range
     * axis value labels.
     * 
     * @param min
     * @param max
     * @param formatter
     * 
     */
    public void addDomainTickLabelFormatter(double min, double max, NumberLabelFormatter formatter) {
        addTickLabelFormatter(new RectRegion(min, max,
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, null),
                formatter);
    }

    /**
     * Convenience method - wraps addDomainTickLabelFormatter, using
     * Double.POSITIVE_INFINITY and Double.NEGATIVE_INFINITY to mask off domain
     * axis value labels.
     * 
     * @param min
     * @param max
     * @param formatter
     */
    public void addRangeTickLabelFormatter(double min, double max, NumberLabelFormatter formatter) {
        addTickLabelFormatter(new RectRegion(Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, min, max, null), formatter);
    }

    /**
     * Returns the formatter associated with the first (bottom) Region
     * containing x and y.
     * 
     * @param x
     * @param y
     * @return the formatter associated with the first (bottom) region
     *         containing x and y. null otherwise.
     */
    public NumberLabelFormatter getTickLabelFormatter(double x, double y) {
        for (RectRegion r : tickLabelRegionFormatters.elements()) {
            if (r.containsValue(x, y)) {
                return tickLabelRegionFormatters.get(r);
            }
        }
        return null;
    }

    /**
     *
     * @param val domain value
     * @return
     */
    public NumberLabelFormatter getDomainTickLabelFormatter(
            double val) {
        for (RectRegion r : tickLabelRegionFormatters.elements()) {
            if (r.containsDomainValue(val)) {
                return tickLabelRegionFormatters.get(r);
            }
        }
        return null;
    }

    /**
     *
     * @param val range value
     * @return
     */
    public NumberLabelFormatter getRangeTickLabelFormatter(
            double val) {
        for (RectRegion r : tickLabelRegionFormatters.elements()) {
            if (r.containsRangeValue(val)) {
                return tickLabelRegionFormatters.get(r);
            }
        }
        return null;
    }

    private String getFormattedRangeValue(Number value) {
        return rangeValueFormat.format(value);
    }

    private String getFormattedDomainValue(Number value) {
        return domainValueFormat.format(value);
    }

    /**
     * Convenience method. Wraps getYVal(float)
     * 
     * @param point
     * @return
     */
    public Double getYVal(PointF point) {
        return getYVal(point.y);
    }

    /**
     * Converts a y pixel to a y value.
     * 
     * @param yPix
     * @return
     */
    public Double getYVal(float yPix) {
        if (plot.getCalculatedMinY() == null
                || plot.getCalculatedMaxY() == null) {
            return null;
        }
        return ValPixConverter.pixToVal(yPix - gridDimensions.paddedRect.top, plot
                .getCalculatedMinY().doubleValue(), plot.getCalculatedMaxY()
                .doubleValue(), gridDimensions.paddedRect.height(), true);
    }

    /**
     * Convenience method. Wraps getXVal(float)
     * 
     * @param point
     * @return
     */
    public Double getXVal(PointF point) {
        return getXVal(point.x);
    }

    /**
     * Converts an x pixel into an x value.
     * 
     * @param xPix
     * @return
     */
    public Double getXVal(float xPix) {
        if (plot.getCalculatedMinX() == null
                || plot.getCalculatedMaxX() == null) {
            return null;
        }
        return ValPixConverter.pixToVal(xPix - gridDimensions.paddedRect.left, plot
                .getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX()
                .doubleValue(), gridDimensions.paddedRect.width(), false);
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect)
            throws PlotRenderException {

        calculateGridDimensions(widgetRect);

        // don't draw if we have no space to draw into
        if ((gridDimensions.paddedRect.height() > ZERO) && (gridDimensions.paddedRect.width() > ZERO)) {
            if (plot.getCalculatedMinX() != null
                    && plot.getCalculatedMaxX() != null
                    && plot.getCalculatedMinY() != null
                    && plot.getCalculatedMaxY() != null) {
                drawGrid(canvas);
                drawData(canvas);
                drawCursors(canvas);
                if (isDrawMarkersEnabled()) {
                    drawMarkers(canvas);
                }
            }
        }
    }

    private void calculateGridDimensions(RectF widgetRect) {
        RectF r = new RectF(widgetRect.left + ((rangeAxisLeft) ?
                rangeTickLabelWidth : ONE),
                widgetRect.top + ((domainAxisBottom) ? ONE : domainTickLabelWidth),
                widgetRect.right - ((rangeAxisLeft) ? ONE : rangeTickLabelWidth),
                widgetRect.bottom - ((domainAxisBottom) ? domainTickLabelWidth : ONE));

        // don't calculate if nothing has changed:
        if(gridDimensions == null || !RectFUtils.areIdentical(r, gridDimensions.canvasRect)) {
            RectF mRect = gridBox.getMarginatedRect(r);
            RectF pRect = gridBox.getPaddedRect(mRect);
            gridDimensions = new DisplayDimensions(r, mRect, pRect);
        }
    }

    private void drawTickText(Canvas canvas, Axis axis, Number value,
                              float xPix, float yPix, Paint labelPaint) {
        NumberLabelFormatter formatter;
        String txt;
        double v = value.doubleValue();

        int canvasState = canvas.save();
        try {
            switch (axis) {
                case DOMAIN:
                    formatter = getDomainTickLabelFormatter(v);
                    txt = getFormattedDomainValue(value);
                    canvas.rotate(getDomainLabelOrientation(), xPix, yPix);
                    break;
                case RANGE:
                    formatter = getRangeTickLabelFormatter(v);
                    txt = getFormattedRangeValue(value);
                    canvas.rotate(getRangeLabelOrientation(), xPix, yPix);
                    break;
                default:
                    throw new RuntimeException("Invalid axis type: " + axis);
            }

            // if a matching region formatter was found, create a clone
            // of labelPaint and use the formatter's color. Otherwise
            // just use labelPaint:
            Paint p;
            if (formatter != null) {
                p = formatter.getPaint(value);
            } else {
                p = labelPaint;
            }
            canvas.drawText(txt, xPix, yPix, p);
        } finally {
            canvas.restoreToCount(canvasState);
        }
    }

    private void drawDomainTick(Canvas canvas, float xPix, Number xVal,
            Paint labelPaint, Paint linePaint, boolean drawLineOnly) {

        final RectF gridRect = gridDimensions.paddedRect;
        if (!drawLineOnly) {
            if (linePaint != null && (domainTick || domainTickExtension > ZERO)) {
                if (domainAxisBottom){
                    canvas.drawLine(xPix, domainTick ? gridRect.top : gridRect.bottom,
                            xPix, gridRect.bottom + domainTickExtension, linePaint);
                } else {
                    canvas.drawLine(xPix, gridRect.top - domainTickExtension,
                            xPix, domainTick ? gridRect.bottom : gridRect.top, linePaint);
                }
            }
            if (labelPaint != null) {
                float fontHeight = FontUtils.getFontHeight(labelPaint);
                float yPix;
                if (domainAxisBottom){
                    yPix = gridRect.bottom + domainTickExtension
                            + domainTickLabelVerticalOffset + fontHeight;
                } else {
                    yPix = gridRect.top - domainTickExtension
                            - domainTickLabelVerticalOffset;
                }
                drawTickText(canvas, Axis.DOMAIN, xVal,
                        xPix + domainTickLabelHorizontalOffset, yPix,
                        labelPaint);
            }
        } else if (linePaint != null && (domainSubTick || domainLabelSubTickExtension > ZERO)) {
            if (domainAxisBottom){
                canvas.drawLine(xPix, domainSubTick ? gridRect.top : gridRect.bottom,
                        xPix, gridRect.bottom + domainLabelSubTickExtension, linePaint);
            } else {
                canvas.drawLine(xPix, gridRect.top - domainLabelSubTickExtension,
                        xPix, domainSubTick ? gridRect.bottom : gridRect.top, linePaint);
            }
        }
    }

    public void drawRangeTick(Canvas canvas, float yPix, Number yVal,
            Paint labelPaint, Paint linePaint, boolean drawLineOnly) {
        final RectF gridRect = gridDimensions.paddedRect;
        if (!drawLineOnly) {
            if (linePaint != null && (rangeTick || rangeTickExtension > ZERO)) {
                if (rangeAxisLeft){
                canvas.drawLine(gridRect.left - rangeTickExtension, yPix,
                        rangeTick ? gridRect.right : gridRect.left, yPix, linePaint);
                } else {
                    canvas.drawLine(rangeTick ? gridRect.left : gridRect.right, yPix,
                            gridRect.right + rangeTickExtension, yPix, linePaint);
                }
            }
            if (labelPaint != null) {
                float xPix;
                if (rangeAxisLeft){
                    xPix = gridRect.left
                            - (rangeTickExtension + rangeTickLabelHorizontalOffset);
                } else {
                    xPix = gridRect.right
                            + (rangeTickExtension + rangeTickLabelHorizontalOffset);
                }
                drawTickText(canvas, Axis.RANGE, yVal, xPix, yPix - rangeTickLabelVerticalOffset,
                        labelPaint);
            }
        } else if (linePaint != null && (rangeSubTick || rangeLabelSubTickExtension > ZERO)) {
            if (rangeAxisLeft){
                canvas.drawLine(gridRect.left - rangeLabelSubTickExtension, yPix,
                        rangeSubTick ? gridRect.right : gridRect.left, yPix, linePaint);
            } else {
                canvas.drawLine(rangeTick ? gridRect.left : gridRect.right, yPix,
                        gridRect.right + rangeLabelSubTickExtension, yPix, linePaint);
            }
        }
    }

    /**
     * Draws the drid and domain/range labels for the plot.
     * 
     * @param canvas
     */
    protected void drawGrid(Canvas canvas) {
        final RectF paddedGridRect = gridDimensions.paddedRect;
        if (gridBackgroundPaint != null) {
            canvas.drawRect(gridDimensions.paddedRect, gridBackgroundPaint);
        }

        float domainOriginF;
        if (plot.getDomainOrigin() != null) {
            double domainOriginVal = plot.getDomainOrigin().doubleValue();
            domainOriginF = ValPixConverter.valToPix(domainOriginVal, plot
                    .getCalculatedMinX().doubleValue(), plot
                    .getCalculatedMaxX().doubleValue(), paddedGridRect.width(),
                    false);
            domainOriginF += paddedGridRect.left;
            // if no origin is set, use the leftmost value visible on the grid:
        } else {
            domainOriginF = paddedGridRect.left;
        }

        XYStep domainStep = XYStepCalculator.getStep(plot, Axis.DOMAIN,
                paddedGridRect, plot.getCalculatedMinX().doubleValue(), plot
                        .getCalculatedMaxX().doubleValue());

        // draw domain origin:
        if (domainOriginF >= paddedGridRect.left
                && domainOriginF <= paddedGridRect.right) {
            if (domainOriginLinePaint != null){
                domainOriginLinePaint.setTextAlign(Paint.Align.CENTER);
            }

            Paint olp = domainTickLabelPaintMap != null ?
                    domainTickLabelPaintMap.get(plot.getDomainOrigin()) : domainOriginTickLabelPaint;
            if(olp == null) {
                olp = domainTickLabelPaint;
            }
            drawDomainTick(canvas, domainOriginF, plot.getDomainOrigin()
                    .doubleValue(), olp, domainOriginLinePaint, !showDomainLabels);
        }

        // draw ticks LEFT of origin:
        float xPix = domainOriginF - domainStep.getStepPix();
        for (int i = ONE; xPix >= paddedGridRect.left; xPix = domainOriginF
                - (i * domainStep.getStepPix())) {
            double xVal = plot.getDomainOrigin().doubleValue() - i
                    * domainStep.getStepVal();
            Paint dlp = domainTickLabelPaintMap != null ?
                    domainTickLabelPaintMap.get(xVal) : domainTickLabelPaint;
            if (dlp == null) {
                dlp = domainTickLabelPaint;
            }
            if (xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {
                if (i % getTicksPerDomainLabel() == ZERO) {
                    drawDomainTick(canvas, xPix, xVal, dlp, domainGridLinePaint, !showDomainLabels);
                } else {
                    drawDomainTick(canvas, xPix, xVal, dlp, domainSubGridLinePaint, true);
                }
            }
            i++;
        }

        // draw ticks RIGHT of origin:
        xPix = domainOriginF + domainStep.getStepPix();
        for (int i = ONE; xPix <= paddedGridRect.right; xPix = domainOriginF
                + (i * domainStep.getStepPix())) {
            double xVal = plot.getDomainOrigin().doubleValue() + i
                    * domainStep.getStepVal();

            Paint dlp = domainTickLabelPaintMap != null ?
                    domainTickLabelPaintMap.get(xVal) : domainTickLabelPaint;
            if (dlp == null) {
                dlp = domainTickLabelPaint;
            }
            if (xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {

                if (i % getTicksPerDomainLabel() == ZERO) {
                    drawDomainTick(canvas, xPix, xVal, dlp, domainGridLinePaint, !showDomainLabels);
                } else {
                    drawDomainTick(canvas, xPix, xVal, dlp, domainSubGridLinePaint, true);
                }
            }
            i++;
        }

        // draw range origin:
        float rangeOriginF;
        if (plot.getRangeOrigin() != null) {
            double rangeOriginD = plot.getRangeOrigin().doubleValue();
            rangeOriginF = ValPixConverter.valToPix(rangeOriginD, plot
                    .getCalculatedMinY().doubleValue(), plot
                    .getCalculatedMaxY().doubleValue(),
                    paddedGridRect.height(), true);
            rangeOriginF += paddedGridRect.top;
            // if no origin is set, use the leftmost value visible on the grid
        } else {
            rangeOriginF = paddedGridRect.bottom;
        }

        XYStep rangeStep = XYStepCalculator.getStep(plot, Axis.RANGE,
                paddedGridRect, plot.getCalculatedMinY().doubleValue(), plot
                        .getCalculatedMaxY().doubleValue());

        // draw range origin:
        if (rangeOriginF >= paddedGridRect.top
                && rangeOriginF <= paddedGridRect.bottom) {
            if (rangeOriginLinePaint != null){
                rangeOriginLinePaint.setTextAlign(Paint.Align.RIGHT);
            }

            Paint olp = rangeTickLabelPaintMap != null ?
                    rangeTickLabelPaintMap.get(plot.getRangeOrigin()) : rangeOriginTickLabelPaint;
            if(olp == null) {
                olp = rangeTickLabelPaint;
            }
            drawRangeTick(canvas, rangeOriginF, plot.getRangeOrigin()
                    .doubleValue(), olp,
                    rangeOriginLinePaint, !showRangeLabels);
        }

        // draw ticks ABOVE origin:
        float yPix = rangeOriginF - rangeStep.getStepPix();
        for (int i = ONE; yPix >= paddedGridRect.top; yPix = rangeOriginF
                - (i * rangeStep.getStepPix())) {
            double yVal = plot.getRangeOrigin().doubleValue() + i
                    * rangeStep.getStepVal();

            Paint rlp = rangeTickLabelPaintMap != null ?
                    rangeTickLabelPaintMap.get(yVal) : rangeTickLabelPaint;
            if (rlp == null) {
                rlp = rangeTickLabelPaint;
            }
            if (yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                if (i % getTicksPerRangeLabel() == ZERO) {
                    drawRangeTick(canvas, yPix, yVal, rlp,
                            rangeGridLinePaint, !showRangeLabels);
                } else {
                    drawRangeTick(canvas, yPix, yVal, rlp,
                            rangeSubGridLinePaint, true);
                }
            }
            i++;
        }

        // draw ticks BENEATH origin:
        yPix = rangeOriginF + rangeStep.getStepPix();
        for (int i = ONE; yPix <= paddedGridRect.bottom; yPix = rangeOriginF
                + (i * rangeStep.getStepPix())) {
            double yVal = plot.getRangeOrigin().doubleValue() - i
                    * rangeStep.getStepVal();

            Paint rlp = rangeTickLabelPaintMap != null ?
                    rangeTickLabelPaintMap.get(yVal) : rangeTickLabelPaint;
            if (rlp == null) {
                rlp = rangeTickLabelPaint;
            }
            if (yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                if (i % getTicksPerRangeLabel() == ZERO) {
                    drawRangeTick(canvas, yPix, yVal, rlp,
                            rangeGridLinePaint, !showRangeLabels);
                } else {
                    drawRangeTick(canvas, yPix, yVal, rlp,
                            rangeSubGridLinePaint, true);
                }
            }
            i++;
        }
    }

    /**
     * Renders the text associated with user defined markers
     * 
     * @param canvas
     * @param text
     * @param marker
     * @param x
     * @param y
     */
    private void drawMarkerText(Canvas canvas, String text, ValueMarker marker,
            float x, float y) {
        final RectF paddedRect = gridDimensions.paddedRect;
        x += MARKER_LABEL_SPACING;
        y -= MARKER_LABEL_SPACING;
        RectF textRect = new RectF(FontUtils.getStringDimensions(text,
                marker.getTextPaint()));
        textRect.offsetTo(x, y - textRect.height());

        if (textRect.right > paddedRect.right) {
            textRect.offset(-(textRect.right - paddedRect.right), ZERO);
        }

        if (textRect.top < paddedRect.top) {
            textRect.offset(0, paddedRect.top - textRect.top);
        }

        canvas.drawText(text, textRect.left, textRect.bottom,
                marker.getTextPaint());
    }

    protected void drawMarkers(Canvas canvas) {
        for (YValueMarker marker : plot.getYValueMarkers()) {

            final RectF paddedRect = gridDimensions.paddedRect;

            if (marker.getValue() != null) {
                double yVal = marker.getValue().doubleValue();
                float yPix = ValPixConverter.valToPix(yVal, plot
                        .getCalculatedMinY().doubleValue(), plot
                        .getCalculatedMaxY().doubleValue(), paddedRect
                        .height(), true);
                yPix += paddedRect.top;
                canvas.drawLine(paddedRect.left, yPix,
                        paddedRect.right, yPix, marker.getLinePaint());

                float xPix = marker.getTextPosition().getPixelValue(
                        paddedRect.width());
                xPix += paddedRect.left;

                if (marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                } else {
                    drawMarkerText(canvas,
                            getFormattedRangeValue(marker.getValue()), marker,
                            xPix, yPix);
                }
            }
        }

        for (XValueMarker marker : plot.getXValueMarkers()) {

            final RectF paddedRect = gridDimensions.paddedRect;

            if (marker.getValue() != null) {
                double xVal = marker.getValue().doubleValue();
                float xPix = ValPixConverter.valToPix(xVal, plot
                        .getCalculatedMinX().doubleValue(), plot
                        .getCalculatedMaxX().doubleValue(), paddedRect
                        .width(), false);
                xPix += paddedRect.left;
                canvas.drawLine(xPix, paddedRect.top, xPix,
                        paddedRect.bottom, marker.getLinePaint());

                float yPix = marker.getTextPosition().getPixelValue(
                        paddedRect.height());
                yPix += paddedRect.top;
                if (marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                } else {
                    drawMarkerText(canvas,
                            getFormattedDomainValue(marker.getValue()), marker,
                            xPix, yPix);
                }
            }
        }
    }

    protected void drawCursors(Canvas canvas) {

        final RectF paddedRect = gridDimensions.paddedRect;
        boolean hasDomainCursor = false;
        // draw the domain cursor:
        if (domainCursorPaint != null
                && domainCursorPosition <= paddedRect.right
                && domainCursorPosition >= paddedRect.left) {
            hasDomainCursor = true;
            canvas.drawLine(domainCursorPosition, paddedRect.top,
                    domainCursorPosition, paddedRect.bottom,
                    domainCursorPaint);
        }

        boolean hasRangeCursor = false;
        // draw the range cursor:
        if (rangeCursorPaint != null
                && rangeCursorPosition >= paddedRect.top
                && rangeCursorPosition <= paddedRect.bottom) {
            hasRangeCursor = true;
            canvas.drawLine(paddedRect.left, rangeCursorPosition,
                    paddedRect.right, rangeCursorPosition, rangeCursorPaint);
        }

        if (drawCursorLabelEnabled && cursorLabelPaint != null
                && hasRangeCursor && hasDomainCursor) {

            String label = "X="
                    + getDomainValueFormat().format(getDomainCursorVal());
            label += " Y=" + getRangeValueFormat().format(getRangeCursorVal());

            // convert the label dimensions rect into floating-point:
            RectF cursorRect = new RectF(FontUtils.getPackedStringDimensions(
                    label, cursorLabelPaint));
            cursorRect.offsetTo(domainCursorPosition, rangeCursorPosition
                    - cursorRect.height());

            // if we are too close to the right edge of the plot, we will move
            // the label to the left side of our cursor:
            if (cursorRect.right >= paddedRect.right) {
                cursorRect.offsetTo(domainCursorPosition - cursorRect.width(),
                        cursorRect.top);
            }

            // same thing for the top edge of the plot:
            // dunno why but these rects can have negative values for top and bottom.
            if (cursorRect.top <= paddedRect.top) {
                cursorRect.offsetTo(cursorRect.left, rangeCursorPosition);
            }

            if (cursorLabelBackgroundPaint != null) {
                canvas.drawRect(cursorRect, cursorLabelBackgroundPaint);
            }

            canvas.drawText(label, cursorRect.left, cursorRect.bottom,
                    cursorLabelPaint);
        }
    }

    /**
     * Draws lines and points for each element in the series.
     * 
     * @param canvas
     * @throws PlotRenderException
     */
    protected void drawData(Canvas canvas) throws PlotRenderException {
        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(gridDimensions.marginatedRect, android.graphics.Region.Op.INTERSECT);
            renderStack.sync();

            for(RenderStack.StackElement thisElement : renderStack.getElements()) {
                if(thisElement.isEnabled()) {
                    plot.getRenderer(thisElement.get().getFormatter().getRendererClass()).
                            render(canvas, gridDimensions.paddedRect, thisElement.get(), renderStack);
                }
            }

        } finally {
            canvas.restore();
        }
    }

    protected void drawPoint(Canvas canvas, PointF point, Paint paint) {
        canvas.drawPoint(point.x, point.y, paint);
    }

    public float getDomainTickLabelWidth() {
        return domainTickLabelWidth;
    }

    public void setDomainTickLabelWidth(float domainTickLabelWidth) {
        this.domainTickLabelWidth = domainTickLabelWidth;
    }

    public float getRangeTickLabelWidth() {
        return rangeTickLabelWidth;
    }

    public void setRangeTickLabelWidth(float rangeTickLabelWidth) {
        this.rangeTickLabelWidth = rangeTickLabelWidth;
    }

    public float getDomainTickLabelVerticalOffset() {
        return domainTickLabelVerticalOffset;
    }

    public void setDomainTickLabelVerticalOffset(float domainTickLabelVerticalOffset) {
        this.domainTickLabelVerticalOffset = domainTickLabelVerticalOffset;
    }

    public float getDomainTickLabelHorizontalOffset() {
        return domainTickLabelHorizontalOffset;
    }

    public void setDomainTickLabelHorizontalOffset(float domainTickLabelHorizontalOffset) {
        this.domainTickLabelHorizontalOffset = domainTickLabelHorizontalOffset;
    }

    public float getRangeTickLabelHorizontalOffset() {
        return rangeTickLabelHorizontalOffset;
    }

    public void setRangeTickLabelHorizontalOffset(float rangeTickLabelHorizontalOffset) {
        this.rangeTickLabelHorizontalOffset = rangeTickLabelHorizontalOffset;
    }

    public float getRangeTickLabelVerticalOffset() {
        return rangeTickLabelVerticalOffset;
    }

    public void setRangeTickLabelVerticalOffset(float rangeTickLabelVerticalOffset) {
        this.rangeTickLabelVerticalOffset = rangeTickLabelVerticalOffset;
    }

    public Paint getGridBackgroundPaint() {
        return gridBackgroundPaint;
    }

    public void setGridBackgroundPaint(Paint gridBackgroundPaint) {
        this.gridBackgroundPaint = gridBackgroundPaint;
    }

    public Paint getDomainTickLabelPaint() {
        return domainTickLabelPaint;
    }

    public void setDomainTickLabelPaint(Paint domainTickLabelPaint) {
        this.domainTickLabelPaint = domainTickLabelPaint;
    }

    public Paint getRangeTickLabelPaint() {
        return rangeTickLabelPaint;
    }

    public void setRangeTickLabelPaint(Paint rangeTickLabelPaint) {
        this.rangeTickLabelPaint = rangeTickLabelPaint;
    }

    /**
     * Get the paint used to draw the domain grid line.
     */
    public Paint getDomainGridLinePaint() {
        return domainGridLinePaint;
    }

    /**
     * Set the paint used to draw the domain grid line.
     * @param gridLinePaint
     */
    public void setDomainGridLinePaint(Paint gridLinePaint) {
        this.domainGridLinePaint = gridLinePaint;
    }

    /**
     * Get the paint used to draw the range grid line.
     */
    public Paint getRangeGridLinePaint() {
        return rangeGridLinePaint;
    }

    /**
     * Get the paint used to draw the domain grid line.
     */
    public Paint getDomainSubGridLinePaint() {
        return domainSubGridLinePaint;
    }
    
    /**
     * Set the paint used to draw the domain grid line.
     * @param gridLinePaint
     */
    public void setDomainSubGridLinePaint(Paint gridLinePaint) {
        this.domainSubGridLinePaint = gridLinePaint;
    }

    /**
     * Set the Paint used to draw the range grid line.
     * @param gridLinePaint
     */
    public void setRangeGridLinePaint(Paint gridLinePaint) {
        this.rangeGridLinePaint = gridLinePaint;
    }

    /**
     * Get the paint used to draw the range grid line.
     */
    public Paint getRangeSubGridLinePaint() {
        return rangeSubGridLinePaint;
    }

    /**
     * Set the Paint used to draw the range grid line.
     * @param gridLinePaint
     */
    public void setRangeSubGridLinePaint(Paint gridLinePaint) {
        this.rangeSubGridLinePaint = gridLinePaint;
    }
    
    // TODO: make a generic renderer queue.

    public Format getRangeValueFormat() {
        return rangeValueFormat;
    }

    public void setRangeValueFormat(Format rangeValueFormat) {
        this.rangeValueFormat = rangeValueFormat;
    }

    public Format getDomainValueFormat() {
        return domainValueFormat;
    }

    public void setDomainValueFormat(Format domainValueFormat) {
        this.domainValueFormat = domainValueFormat;
    }

    public float getDomainTickExtension() {
        return domainTickExtension;
    }

    public void setDomainTickExtension(float domainTickExtension) {
        this.domainTickExtension = domainTickExtension;
    }

    public float getRangeTickExtension() {
        return rangeTickExtension;
    }

    public float getDomainLabelSubTickExtension() {
        return domainLabelSubTickExtension;
    }

    public void setDomainLabelSubTickExtension(float domainLabelSubTickExtension) {
        this.domainLabelSubTickExtension = domainLabelSubTickExtension;
    }

    public void setRangeTickExtension(float rangeTickExtension) {
        this.rangeTickExtension = rangeTickExtension;
    }

    public float getRangeLabelSubTickExtension() {
        return rangeLabelSubTickExtension;
    }

    public void setRangeLabelSubTickExtension(float rangeLabelSubTickExtension) {
        this.rangeLabelSubTickExtension = rangeLabelSubTickExtension;
    }

    public int getTicksPerRangeLabel() {
        return ticksPerRangeLabel;
    }

    public void setTicksPerRangeLabel(int ticksPerRangeLabel) {
        this.ticksPerRangeLabel = ticksPerRangeLabel;
    }

    public int getTicksPerDomainLabel() {
        return ticksPerDomainLabel;
    }

    public void setTicksPerDomainLabel(int ticksPerDomainLabel) {
        this.ticksPerDomainLabel = ticksPerDomainLabel;
    }

    public Paint getDomainOriginLinePaint() {
        return domainOriginLinePaint;
    }

    public void setDomainOriginLinePaint(Paint domainOriginLinePaint) {
        this.domainOriginLinePaint = domainOriginLinePaint;
    }

    public Paint getRangeOriginLinePaint() {
        return rangeOriginLinePaint;
    }

    public void setRangeOriginLinePaint(Paint rangeOriginLinePaint) {
        this.rangeOriginLinePaint = rangeOriginLinePaint;
    }

    public Paint getDomainOriginTickLabelPaint() {
        return domainOriginTickLabelPaint;
    }

    public void setDomainOriginTickLabelPaint(Paint domainOriginTickLabelPaint) {
        this.domainOriginTickLabelPaint = domainOriginTickLabelPaint;
    }

    public Paint getRangeOriginTickLabelPaint() {
        return rangeOriginTickLabelPaint;
    }

    public void setRangeOriginTickLabelPaint(Paint rangeOriginTickLabelPaint) {
        this.rangeOriginTickLabelPaint = rangeOriginTickLabelPaint;
    }

    public void setCursorPosition(float x, float y) {
        setDomainCursorPosition(x);
        setRangeCursorPosition(y);
    }

    public void setCursorPosition(PointF point) {
        setCursorPosition(point.x, point.y);
    }

    public float getDomainCursorPosition() {
        return domainCursorPosition;
    }

    public Double getDomainCursorVal() {
        return getXVal(getDomainCursorPosition());
    }

    public void setDomainCursorPosition(float domainCursorPosition) {
        this.domainCursorPosition = domainCursorPosition;
    }

    public float getRangeCursorPosition() {
        return rangeCursorPosition;
    }

    public Double getRangeCursorVal() {
        return getYVal(getRangeCursorPosition());
    }

    public void setRangeCursorPosition(float rangeCursorPosition) {
        this.rangeCursorPosition = rangeCursorPosition;
    }

    public Paint getCursorLabelPaint() {
        return cursorLabelPaint;
    }

    public void setCursorLabelPaint(Paint cursorLabelPaint) {
        this.cursorLabelPaint = cursorLabelPaint;
    }

    public Paint getCursorLabelBackgroundPaint() {
        return cursorLabelBackgroundPaint;
    }

    public void setCursorLabelBackgroundPaint(Paint cursorLabelBackgroundPaint) {
        this.cursorLabelBackgroundPaint = cursorLabelBackgroundPaint;
    }

    public boolean isDrawMarkersEnabled() {
        return drawMarkersEnabled;
    }

    public void setDrawMarkersEnabled(boolean drawMarkersEnabled) {
        this.drawMarkersEnabled = drawMarkersEnabled;
    }

    public boolean isRangeAxisLeft() {
        return rangeAxisLeft;
    }

    public void setRangeAxisLeft(boolean rangeAxisLeft) {
        this.rangeAxisLeft = rangeAxisLeft;
    }

    public boolean isDomainAxisBottom() {
        return domainAxisBottom;
    }

    public void setDomainAxisBottom(boolean domainAxisBottom) {
        this.domainAxisBottom = domainAxisBottom;
    }
    
    public boolean isRangeTick() {
        return rangeTick;
    }

    public void setRangeTick(boolean rangeTick) {
        this.rangeTick = rangeTick;
    }

    public boolean isRangeSubTick() {
        return rangeSubTick;
    }

    public void setRangeSubTick(boolean rangeSubTick) {
        this.rangeSubTick = rangeSubTick;
    }

    public boolean isDomainTick() {
        return domainTick;
    }

    public void setDomainTick(boolean domainTick) {
        this.domainTick = domainTick;
    }

    public boolean isDomainSubTick() {
        return domainSubTick;
    }

    public void setDomainSubTick(boolean domainSubTick) {
        this.domainSubTick = domainSubTick;
    }

    /*
     * set the position of the range axis labels.  Set the labelPaint textSizes before setting this.
     * This call sets the various vertical and horizontal offsets and widths to good defaults.
     * 
     * @param rangeAxisLeft axis labels are on the left hand side not the right hand side.
     * @param rangeAxisOverlay axis labels are overlaid on the plot, not external to it.
     * @param tickSize the size of the tick extensions for none overlaid axis.
     * @param maxLableString Sample label representing the biggest size space needs to be allocated for.
     */
    public void setRangeAxisPosition(boolean rangeAxisLeft, boolean rangeAxisOverlay, int tickSize, String maxLableString){
        setRangeAxisLeft(rangeAxisLeft);
        
        if (rangeAxisOverlay) {
            setRangeTickLabelWidth(1);    // needs to be at least 1 to display grid line.
            setRangeTickLabelHorizontalOffset(-TWO);
            setRangeTickLabelVerticalOffset(TWO);    // get above the line
            Paint p = getRangeTickLabelPaint();
            if (p != null) {
                p.setTextAlign(((rangeAxisLeft)?Paint.Align.LEFT:Paint.Align.RIGHT));
            }
            Paint po = getRangeOriginTickLabelPaint();
            if (po != null) {
                po.setTextAlign(((rangeAxisLeft)?Paint.Align.LEFT:Paint.Align.RIGHT));
            }
            setRangeTickExtension(ZERO);
        } else {
            setRangeTickLabelWidth(ONE);    // needs to be at least 1 to display grid line.
                                      // if we have a paint this gets bigger.
            setRangeTickLabelHorizontalOffset(FLOAT_ONE);
            setRangeTickExtension(tickSize);
            Paint p = getRangeTickLabelPaint();
            if (p != null) {
                p.setTextAlign(((!rangeAxisLeft)?Paint.Align.LEFT:Paint.Align.RIGHT));
                Rect r = FontUtils.getPackedStringDimensions(maxLableString,p);
                setRangeTickLabelVerticalOffset(r.top / TWO);
                setRangeTickLabelWidth(r.right + getRangeTickExtension());
            }
            Paint po = getRangeOriginTickLabelPaint();
            if (po != null) {
                po.setTextAlign(((!rangeAxisLeft)?Paint.Align.LEFT:Paint.Align.RIGHT));
            }
        }
    }
    
    /*
     * set the position of the domain axis labels.  Set the labelPaint textSizes before setting this.
     * This call sets the various vertical and horizontal offsets and widths to good defaults.
     * 
     * @param domainAxisBottom axis labels are on the bottom not the top of the plot.
     * @param domainAxisOverlay axis labels are overlaid on the plot, not external to it.
     * @param tickSize the size of the tick extensions for non overlaid axis.
     * @param maxLableString Sample label representing the biggest size space needs to be allocated for.
     */
    public void setDomainAxisPosition(boolean domainAxisBottom, boolean domainAxisOverlay, int tickSize, String maxLabelString){
        setDomainAxisBottom(domainAxisBottom);
        if (domainAxisOverlay) {
            setDomainTickLabelWidth(ONE);    // needs to be at least 1 to display grid line.
            setDomainTickLabelVerticalOffset(TWO);    // get above the line
            setDomainTickExtension(ZERO);
            Paint p = getDomainTickLabelPaint();
            if (p != null) {
                Rect r = FontUtils.getPackedStringDimensions(maxLabelString,p);
                if (domainAxisBottom){
                    setDomainTickLabelVerticalOffset(TWO * r.top);
                } else {
                    setDomainTickLabelVerticalOffset(r.top - FLOAT_ONE);
                }
            }
        } else {
            setDomainTickLabelWidth(1);    // needs to be at least 1 to display grid line.
                                       // if we have a paint this gets bigger.
            setDomainTickExtension(tickSize);
            Paint p = getDomainTickLabelPaint();
            if (p != null) {
                float fontHeight = FontUtils.getFontHeight(p);
                if (domainAxisBottom){
                    setDomainTickLabelVerticalOffset(-4.0f);
                } else {
                    setDomainTickLabelVerticalOffset(FLOAT_ONE);
                }
                setDomainTickLabelWidth(fontHeight + getDomainTickExtension());
            }
        }
    }

    public DisplayDimensions getGridDimensions() {
        return gridDimensions;
    }

    /**
     *
     * @return
     * @since 0.9.8
     */
    public boolean isShowDomainLabels() {
        return showDomainLabels;
    }

    /**
     *
     * @param showDomainLabels
     * @since 0.9.8
     */
    public void setShowDomainLabels(boolean showDomainLabels) {
        this.showDomainLabels = showDomainLabels;
    }

    /**
     *
     * @return
     * @since 0.9.8
     */
    public boolean isShowRangeLabels() {
        return showRangeLabels;
    }

    /**
     *
     * @param showRangeLabels
     * @since 0.9.8
     */
    public void setShowRangeLabels(boolean showRangeLabels) {
        this.showRangeLabels = showRangeLabels;
    }
}
