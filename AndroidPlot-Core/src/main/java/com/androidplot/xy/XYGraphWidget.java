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
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;
import com.androidplot.util.ValPixConverter;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;

import java.text.DecimalFormat;
import java.text.Format;

/**
 * Displays graphical data annotated with domain and range tick markers.
 */
public class XYGraphWidget extends Widget {

    /**
     * Will be used in a future version.
     */
    public enum XYPlotOrientation {
        HORIZONTAL,
        VERTICAL
    }

    private static final int MARKER_LABEL_SPACING = 2;
    private static final int CURSOR_LABEL_SPACING = 2;  // space between cursor lines and label in pixels
    private float domainLabelWidth = 15;  // how many pixels is the area allocated for domain labels
    private float rangeLabelWidth = 41;  // ...
    private float domainLabelVerticalOffset = -5;
    private float rangeLabelHorizontalOffset = 1;  // not currently used since this margin can be adjusted via rangeLabelWidth
    private int ticksPerRangeLabel = 1;
    private int ticksPerDomainLabel = 1;
    private float gridPaddingTop = 0;
    private float gridPaddingBottom = 0;
    private float gridPaddingLeft = 0;
    private float gridPaddingRight = 0;
    private int domainLabelTickExtension = 5;
    private int rangeLabelTickExtension = 5;
    private Paint gridBackgroundPaint;
    private Paint gridLinePaint;
    private Paint domainLabelPaint;
    private Paint rangeLabelPaint;
    private Paint domainCursorPaint;
    private Paint rangeCursorPaint;
    private Paint cursorLabelPaint;
    private Paint cursorLabelBackgroundPaint;
    private XYPlot plot;
    private Format rangeValueFormat;
    private Format domainValueFormat;
    private Paint domainOriginLinePaint;
    private Paint rangeOriginLinePaint;
    private Paint domainOriginLabelPaint;
    private Paint rangeOriginLabelPaint;
    private RectF gridRect;
    private RectF paddedGridRect;
    private float domainCursorPosition;
    private float rangeCursorPosition;
    private boolean drawCursorLabelEnabled = true;
    private boolean drawMarkersEnabled = true;

    // TODO: consider typing this manager with a special axisLabelRegionFormatter
    //private ZHash<LineRegion, AxisValueLabelFormatter> domainLabelRegions;
    //private ZHash<LineRegion, AxisValueLabelFormatter> rangeLabelRegions;
    private ZHash<RectRegion, AxisValueLabelFormatter> axisValueLabelRegions;

    {
        gridBackgroundPaint = new Paint();
        gridBackgroundPaint.setColor(Color.rgb(140, 140, 140));
        gridBackgroundPaint.setStyle(Paint.Style.FILL);
        gridLinePaint = new Paint();
        gridLinePaint.setColor(Color.rgb(180, 180, 180));
        gridLinePaint.setAntiAlias(true);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        domainOriginLinePaint = new Paint();
        domainOriginLinePaint.setColor(Color.WHITE);
        domainOriginLinePaint.setAntiAlias(true);
        rangeOriginLinePaint = new Paint();
        rangeOriginLinePaint.setColor(Color.WHITE);
        rangeOriginLinePaint.setAntiAlias(true);
        domainOriginLabelPaint = new Paint();
        domainOriginLabelPaint.setColor(Color.WHITE);
        domainOriginLabelPaint.setAntiAlias(true);
        domainOriginLabelPaint.setTextAlign(Paint.Align.CENTER);
        rangeOriginLabelPaint = new Paint();
        rangeOriginLabelPaint.setColor(Color.WHITE);
        rangeOriginLabelPaint.setAntiAlias(true);
        rangeOriginLabelPaint.setTextAlign(Paint.Align.RIGHT);
        domainLabelPaint = new Paint();
        domainLabelPaint.setColor(Color.LTGRAY);
        domainLabelPaint.setAntiAlias(true);
        domainLabelPaint.setTextAlign(Paint.Align.CENTER);
        rangeLabelPaint = new Paint();
        rangeLabelPaint.setColor(Color.LTGRAY);
        rangeLabelPaint.setAntiAlias(true);
        rangeLabelPaint.setTextAlign(Paint.Align.RIGHT);
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
        //domainLabelRegions =  new ZHash<LineRegion, AxisValueLabelFormatter>();
        //rangeLabelRegions =  new ZHash<LineRegion, AxisValueLabelFormatter>();
        axisValueLabelRegions = new ZHash<RectRegion, AxisValueLabelFormatter>();
    }

    public XYGraphWidget(XYPlot plot, SizeMetrics sizeMetrics) {
        super(sizeMetrics);
        this.plot = plot;
    }

    public ZIndexable<RectRegion> getAxisValueLabelRegions() {
        return axisValueLabelRegions;
    }

    /**
     * Add a new Region used for rendering axis valuelabels.  Note that it is possible
     * to add multiple Region instances which overlap, in which cast the last region to
     * be added will be used.  It is up to the developer to guard against this often
     * undesireable situation.
     * @param region
     * @param formatter
     */
    public void addAxisValueLabelRegion(RectRegion region, AxisValueLabelFormatter formatter) {
        axisValueLabelRegions.addToTop(region, formatter);
    }

    /**
     * Convenience method - wraps addAxisValueLabelRegion, using Double.POSITIVE_INFINITY and
     * Double.NEGATIVE_INFINITY to mask off range axis value labels.
     * @param min
     * @param max
     * @param formatter
     *
     */
    public void addDomainAxisValueLabelRegion( double min, double max, AxisValueLabelFormatter formatter){
        addAxisValueLabelRegion(new RectRegion(min, max, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, null), formatter);
    }

    /**
     * Convenience method - wraps addAxisValueLabelRegion, using Double.POSITIVE_INFINITY and
     * Double.NEGATIVE_INFINITY to mask off domain axis value labels.
     * @param min
     * @param max
     * @param formatter
     */
    public void addRangeAxisValueLabelRegion(double min, double max, AxisValueLabelFormatter formatter){
        addAxisValueLabelRegion(new RectRegion(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, min, max, null), formatter);
    }




    /*public void addRangeLabelRegion(LineRegion region, AxisValueLabelFormatter formatter) {
        rangeLabelRegions.addToTop(region, formatter);
    }

    public boolean removeRangeLabelRegion(LineRegion region) {
        return rangeLabelRegions.remove(region);
    }*/

    /**
     * Returns the formatter associated with the first (bottom) Region containing x and y.
     * @param x
     * @param y
     * @return the formatter associated with the first (bottom) region containing x and y.  null otherwise.
     */
    public AxisValueLabelFormatter getAxisValueLabelFormatterForVal(double x, double y) {
        for(RectRegion r : axisValueLabelRegions.elements()) {
            if(r.containsValue(x, y)) {
                return axisValueLabelRegions.get(r);
            }
        }
        return null;
    }

    public AxisValueLabelFormatter getAxisValueLabelFormatterForDomainVal(double val) {
        for(RectRegion r : axisValueLabelRegions.elements()) {
            if(r.containsDomainValue(val)) {
                return axisValueLabelRegions.get(r);
            }
        }
        return null;
    }

    public AxisValueLabelFormatter getAxisValueLabelFormatterForRangeVal(double val) {
        for(RectRegion r : axisValueLabelRegions.elements()) {
            if(r.containsRangeValue(val)) {
                return axisValueLabelRegions.get(r);
            }
        }
        return null;
    }


    /**
     * Returns the formatter associated with the first (bottom-most) Region containing
     * value.
     * @param value
     * @return
     *//*
    public AxisValueLabelFormatter getXYAxisFormatterForRangeVal(double value) {
        return getRegionContainingVal(rangeLabelRegions, value);
    }

    *//**
     * Returns the formatter associated with the first (bottom-most) Region containing
     * value.
     * @param value
     * @return
     *//*
    public AxisValueLabelFormatter getXYAxisFormatterForDomainVal(double value) {
        return getRegionContainingVal(domainLabelRegions, value);
    }*/


    /*private AxisValueLabelFormatter getRegionContainingVal(ZHash<LineRegion, AxisValueLabelFormatter> zhash, double val) {
        for (LineRegion r : zhash.elements()) {
            if (r.contains(val)) {
                return rangeLabelRegions.get(r);
            }
        }
        // nothing found
        return null;
    }*/

    /**
     * Returns a RectF representing the grid area last drawn
     * by this plot.
     * @return
     */
    public RectF getGridRect() {
        return paddedGridRect;
    }
    private String getFormattedRangeValue(Number value) {
        return rangeValueFormat.format(value);
    }

    private String getFormattedDomainValue(Number value) {
        return domainValueFormat.format(value);
    }

    /**
     * Convenience method.  Wraps getYVal(float)
     * @param point
     * @return
     */
    public Double getYVal(PointF point) {
        return getYVal(point.y);
    }

    /**
     * Converts a y pixel to a y value.
     * @param yPix
     * @return
     */
    public Double getYVal(float yPix) {
        if(plot.getCalculatedMinY() == null || plot.getCalculatedMaxY() == null) {
            return null;
        }
        return ValPixConverter.pixToVal(yPix - paddedGridRect.top, plot.getCalculatedMinY().doubleValue(), plot.getCalculatedMaxY().doubleValue(), paddedGridRect.height(), true);
    }

    /**
     * Convenience method.  Wraps getXVal(float)
     * @param point
     * @return
     */
    public Double getXVal(PointF point) {
        return getXVal(point.x);
    }

    /**
     * Converts an x pixel into an x value.
     * @param xPix
     * @return
     */
    public Double getXVal(float xPix) {
        if(plot.getCalculatedMinX() == null || plot.getCalculatedMaxX() == null) {
            return null;
        }
        return ValPixConverter.pixToVal(xPix - paddedGridRect.left, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue(), paddedGridRect.width(), false);
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        gridRect = getGridRect(widgetRect); // used for drawing the background of the grid
        paddedGridRect = getPaddedGridRect(gridRect); // used for drawing lines etc.
        //if (!plot.isEmpty()) {
        // don't draw if we have no space to draw into
        if ((paddedGridRect.height() > 0.0f) && (paddedGridRect.width() > 0.0f)){
            if (plot.getCalculatedMinX() != null &&
                    plot.getCalculatedMaxX() != null &&
                    plot.getCalculatedMinY() != null &&
                    plot.getCalculatedMaxY() != null) {
                drawGrid(canvas);
                drawData(canvas);
                drawCursors(canvas);
                if(isDrawMarkersEnabled()) {
                    drawMarkers(canvas);
                }
            }
        }
        //}
    }

    private RectF getGridRect(RectF widgetRect) {
        return new RectF(widgetRect.left + rangeLabelWidth, widgetRect.top, widgetRect.right, widgetRect.bottom - domainLabelWidth);
    }

    private RectF getPaddedGridRect(RectF gridRect) {
        return new RectF(gridRect.left + gridPaddingLeft, gridRect.top + gridPaddingTop, gridRect.right - gridPaddingRight, gridRect.bottom - gridPaddingBottom);
    }

    private void drawTickText(Canvas canvas, XYAxisType axis, Number value, float xPix, float yPix, Paint labelPaint) {
        AxisValueLabelFormatter rf = null;
        String txt = null;
        double v = value.doubleValue();
        switch(axis) {
            case DOMAIN:
                rf = getAxisValueLabelFormatterForDomainVal(v);
                txt = getFormattedDomainValue(value);
                break;
            case RANGE:
                rf = getAxisValueLabelFormatterForRangeVal(v);
                txt = getFormattedRangeValue(value);
                break;
        }

        // if a matching region formatter was found, create a clone
        // of labelPaint and use the formatter's color.  Otherwise
        // just use labelPaint:
        Paint p = null;
        if(rf != null) {
           // p = rf.getPaint();
            p = new Paint(labelPaint);
            p.setColor(rf.getColor());
            //p.setColor(Color.RED);
        } else {
            p = labelPaint;
        }
        canvas.drawText(txt, xPix, yPix, p);
    }

    private void drawDomainTick(Canvas canvas, float xPix, Number xVal, Paint labelPaint, Paint linePaint, boolean drawLineOnly) {
        if (!drawLineOnly) {
            if (linePaint != null) {
                canvas.drawLine(xPix,
                        gridRect.top,
                        xPix, gridRect.bottom + domainLabelTickExtension,
                        linePaint);
            }
            if (labelPaint != null) {
                float fontHeight = FontUtils.getFontHeight(labelPaint);
                float yPix = gridRect.bottom + rangeLabelTickExtension + domainLabelVerticalOffset + fontHeight;
                drawTickText(canvas, XYAxisType.DOMAIN, xVal, xPix, yPix, labelPaint);
            }
        } else if (linePaint != null) {

            canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom, linePaint);

        }
    }

    public void drawRangeTick(Canvas canvas, float yPix, Number yVal, Paint labelPaint, Paint linePaint, boolean drawLineOnly) {
        if (!drawLineOnly) {
            if (linePaint != null) {
                canvas.drawLine(
                        gridRect.left - rangeLabelTickExtension,
                        yPix,
                        gridRect.right,
                        yPix,
                        linePaint);
            }
            if (labelPaint != null) {
                float xPix = gridRect.left - (rangeLabelTickExtension + rangeLabelHorizontalOffset);
                drawTickText(canvas, XYAxisType.RANGE, yVal, xPix, yPix, labelPaint);
            }
        } else if (linePaint != null) {
            canvas.drawLine(gridRect.left, yPix, gridRect.right, yPix, linePaint);
        }
    }

    /**
     * Draws the drid and domain/range labels for the plot.
     * @param canvas
     * @throws com.androidplot.exception.PlotRenderException
     */
    protected void drawGrid(Canvas canvas) throws PlotRenderException {

        if(gridBackgroundPaint != null) {
            canvas.drawRect(gridRect, gridBackgroundPaint);
        }

        float domainOriginF;
        if (plot.getDomainOrigin() != null) {
            double domainOriginVal = plot.getDomainOrigin().doubleValue();
            domainOriginF = ValPixConverter.valToPix(
                    domainOriginVal,
                    plot.getCalculatedMinX().doubleValue(),
                    plot.getCalculatedMaxX().doubleValue(),
                    paddedGridRect.width(),
                    false);
            domainOriginF += paddedGridRect.left;
        // if no origin is set, use the leftmost value visible on the grid:
        } else {
            domainOriginF = paddedGridRect.left;
        }

        XYStep domainStep = XYStepCalculator.getStep(plot, XYAxisType.DOMAIN, paddedGridRect, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue());

        // draw domain origin:
        if (domainOriginF >= paddedGridRect.left && domainOriginF <= paddedGridRect.right) {
            domainOriginLinePaint.setTextAlign(Paint.Align.CENTER);
            drawDomainTick(canvas, domainOriginF, plot.getDomainOrigin().doubleValue(), domainOriginLabelPaint, domainOriginLinePaint, false);
        }

        // draw ticks LEFT of origin:
        {
            int i = 1;
            double xVal;
            float xPix = domainOriginF - domainStep.getStepPix();
            for(; xPix >= paddedGridRect.left; xPix = domainOriginF - (i * domainStep.getStepPix())) {
                xVal = plot.getDomainOrigin().doubleValue() - i * domainStep.getStepVal();
                if(xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {
                    if(i % getTicksPerDomainLabel() == 0) {
                        drawDomainTick(canvas, xPix, xVal, domainLabelPaint, gridLinePaint, false);
                    } else {
                        drawDomainTick(canvas, xPix, xVal, domainLabelPaint, gridLinePaint, true);
                    }
                }
                i++;
            }
        }

        // draw ticks RIGHT of origin:
        {
            int i = 1;
            double xVal;
            float xPix = domainOriginF + domainStep.getStepPix();
            for(; xPix <= paddedGridRect.right; xPix = domainOriginF + (i * domainStep.getStepPix())) {
                xVal = plot.getDomainOrigin().doubleValue() + i * domainStep.getStepVal();
                if(xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {

                    if(i % getTicksPerDomainLabel() == 0) {
                        drawDomainTick(canvas, xPix, xVal, domainLabelPaint, gridLinePaint, false);
                    } else {
                        drawDomainTick(canvas, xPix, xVal, domainLabelPaint, gridLinePaint, true);
                    }
                }
                i++;
            }
        }

        // draw range origin:

        float rangeOriginF;
        if (plot.getRangeOrigin() != null) {
            // --------- NEW WAY ------
            double rangeOriginD = plot.getRangeOrigin().doubleValue();
            rangeOriginF = ValPixConverter.valToPix(
                    rangeOriginD,
                    plot.getCalculatedMinY().doubleValue(),
                    plot.getCalculatedMaxY().doubleValue(),
                    paddedGridRect.height(),
                    true);
            rangeOriginF += paddedGridRect.top;
            // if no origin is set, use the leftmost value visible on the grid
        } else {
            rangeOriginF = paddedGridRect.bottom;
        }

        XYStep rangeStep = XYStepCalculator.getStep(plot, XYAxisType.RANGE, paddedGridRect, plot.getCalculatedMinY().doubleValue(), plot.getCalculatedMaxY().doubleValue());

        // draw range origin:
        if (rangeOriginF >= paddedGridRect.top && rangeOriginF <= paddedGridRect.bottom) {
            rangeOriginLinePaint.setTextAlign(Paint.Align.RIGHT);
            drawRangeTick(canvas, rangeOriginF, plot.getRangeOrigin().doubleValue(), rangeOriginLabelPaint, rangeOriginLinePaint, false);
        }
        // draw ticks ABOVE origin:
        {
            int i = 1;
            double yVal;
            float yPix = rangeOriginF - rangeStep.getStepPix();
            for(; yPix >= paddedGridRect.top; yPix = rangeOriginF - (i * rangeStep.getStepPix())) {
                yVal = plot.getRangeOrigin().doubleValue() + i * rangeStep.getStepVal();
                if(yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                    if(i % getTicksPerRangeLabel() == 0) {
                        drawRangeTick(canvas, yPix, yVal, rangeLabelPaint, gridLinePaint, false);
                    } else {
                        drawRangeTick(canvas, yPix, yVal, rangeLabelPaint, gridLinePaint, true);
                    }
                }
                i++;
            }
        }

        // draw ticks BENEATH origin:
        {
            int i = 1;
            double yVal;
            float yPix = rangeOriginF + rangeStep.getStepPix();
            for(; yPix <= paddedGridRect.bottom; yPix = rangeOriginF + (i * rangeStep.getStepPix())) {
                yVal = plot.getRangeOrigin().doubleValue() - i * rangeStep.getStepVal();
                if(yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                    if(i % getTicksPerRangeLabel() == 0) {
                        drawRangeTick(canvas, yPix, yVal, rangeLabelPaint, gridLinePaint, false);
                    } else {
                        drawRangeTick(canvas, yPix, yVal, rangeLabelPaint, gridLinePaint, true);
                    }
                }
                i++;
            }
        }
    }

    /**
     * Renders the text associated with user defined markers
     * @param canvas
     * @param text
     * @param marker
     * @param x
     * @param y
     */
    private void drawMarkerText(Canvas canvas, String text, ValueMarker marker, float x, float y) {
        x += MARKER_LABEL_SPACING;
        y -= MARKER_LABEL_SPACING;
        RectF textRect = new RectF(FontUtils.getStringDimensions(text, marker.getTextPaint()));
        textRect.offsetTo(x, y-textRect.height());


        if(textRect.right > paddedGridRect.right) {
            textRect.offset(-(textRect.right - paddedGridRect.right), 0);
        }

        if(textRect.top < paddedGridRect.top) {
            textRect.offset(0, paddedGridRect.top - textRect.top);
        }

        canvas.drawText(text, textRect.left, textRect.bottom, marker.getTextPaint());

    }

    protected void drawMarkers(Canvas canvas) {
        for (YValueMarker marker : plot.getYValueMarkers()) {

            if (marker.getValue() != null) {
                double yVal = marker.getValue().doubleValue();
                float yPix = ValPixConverter.valToPix(
                        yVal,
                        plot.getCalculatedMinY().doubleValue(),
                        plot.getCalculatedMaxY().doubleValue(),
                        paddedGridRect.height(),
                        true);
                yPix += paddedGridRect.top;
                canvas.drawLine(paddedGridRect.left, yPix, paddedGridRect.right, yPix, marker.getLinePaint());

                //String text = getFormattedRangeValue(yVal);
                float xPix = marker.getTextPosition().getPixelValue(paddedGridRect.width());
                xPix += paddedGridRect.left;

                if(marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                } else {
                   drawMarkerText(canvas, getFormattedRangeValue(marker.getValue()), marker, xPix, yPix);
                }
            }
        }

        for(XValueMarker marker : plot.getXValueMarkers()) {
            if(marker.getValue() != null) {
                double xVal = marker.getValue().doubleValue();
                float xPix = ValPixConverter.valToPix(
                        xVal,
                        plot.getCalculatedMinX().doubleValue(),
                        plot.getCalculatedMaxX().doubleValue(),
                        paddedGridRect.width(),
                        false);
                xPix += paddedGridRect.left;
                canvas.drawLine(xPix, paddedGridRect.top, xPix, paddedGridRect.bottom, marker.getLinePaint());

                //String text = getFormattedDomainValue(xVal);
                float yPix = marker.getTextPosition().getPixelValue(paddedGridRect.height());
                yPix += paddedGridRect.top;
                if(marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                } else {
                   drawMarkerText(canvas, getFormattedDomainValue(marker.getValue()), marker, xPix, yPix);
                }
            }
        }
    }

    protected void drawCursors(Canvas canvas) {
        boolean hasDomainCursor = false;
        // draw the domain cursor:
        if(domainCursorPaint != null &&
                domainCursorPosition <= paddedGridRect.right &&
                domainCursorPosition >= paddedGridRect.left) {
            hasDomainCursor = true;
            canvas.drawLine(
                    domainCursorPosition,
                    paddedGridRect.top,
                    domainCursorPosition,
                    paddedGridRect.bottom,
                    domainCursorPaint);
        }

        boolean hasRangeCursor = false;
        // draw the range cursor:
        if(rangeCursorPaint != null &&
                rangeCursorPosition >= paddedGridRect.top &&
                rangeCursorPosition <= paddedGridRect.bottom) {
            hasRangeCursor = true;
            canvas.drawLine(
                    paddedGridRect.left,
                    rangeCursorPosition,
                    paddedGridRect.right,
                    rangeCursorPosition,
                    rangeCursorPaint);
        }

        if(drawCursorLabelEnabled && cursorLabelPaint != null && hasRangeCursor && hasDomainCursor) {


            String label =  "X=" + getDomainValueFormat().format(getDomainCursorVal());
            label += " Y=" + getRangeValueFormat().format(getRangeCursorVal());

            // convert the label dimensions rect into floating-point:
            RectF cursorRect = new RectF(FontUtils.getPackedStringDimensions(label, cursorLabelPaint));
            cursorRect.offsetTo(domainCursorPosition, rangeCursorPosition - cursorRect.height());

            // if we are too close to the right edge of the plot, we will move the
            // label to the left side of our cursor:
            if(cursorRect.right >= paddedGridRect.right) {
                cursorRect.offsetTo(domainCursorPosition - cursorRect.width(), cursorRect.top);
            }

            // same thing for the top edge of the plot:
            // dunno why but these rects can have negative values for top and bottom.
            if(cursorRect.top <= paddedGridRect.top) {
                cursorRect.offsetTo(cursorRect.left, rangeCursorPosition);
            }


            if(cursorLabelBackgroundPaint != null) {
                canvas.drawRect(cursorRect, cursorLabelBackgroundPaint);
            }

            canvas.drawText(label, cursorRect.left, cursorRect.bottom, cursorLabelPaint);
        }
    }


    /**
     * Draws lines and points for each element in the series.
     * @param canvas
     * @throws PlotRenderException
     */
    protected void drawData(Canvas canvas) throws PlotRenderException {
        // TODO: iterate through a XYSeriesRenderer list

        //int canvasState = canvas.save();
        try {
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(gridRect, android.graphics.Region.Op.INTERSECT);
            for (XYSeriesRenderer renderer : plot.getRendererList()) {
                renderer.render(canvas, paddedGridRect);
            }
            //canvas.restoreToCount(canvasState);
        } finally {
            canvas.restore();
        }
    }

    protected void drawPoint(Canvas canvas, PointF point, Paint paint) {
        canvas.drawPoint(point.x, point.y, paint);
    }

    public float getDomainLabelWidth() {
        return domainLabelWidth;
    }

    public void setDomainLabelWidth(float domainLabelWidth) {
        this.domainLabelWidth = domainLabelWidth;
    }

    public float getRangeLabelWidth() {
        return rangeLabelWidth;
    }

    public void setRangeLabelWidth(float rangeLabelWidth) {
        this.rangeLabelWidth = rangeLabelWidth;
    }

    public float getDomainLabelVerticalOffset() {
        return domainLabelVerticalOffset;
    }

    public void setDomainLabelVerticalOffset(float domainLabelVerticalOffset) {
        this.domainLabelVerticalOffset = domainLabelVerticalOffset;
    }

    public float getRangeLabelHorizontalOffset() {
        return rangeLabelHorizontalOffset;
    }

    public void setRangeLabelHorizontalOffset(float rangeLabelHorizontalOffset) {
        this.rangeLabelHorizontalOffset = rangeLabelHorizontalOffset;
    }

    public Paint getGridBackgroundPaint() {
        return gridBackgroundPaint;
    }

    public void setGridBackgroundPaint(Paint gridBackgroundPaint) {
        this.gridBackgroundPaint = gridBackgroundPaint;
    }

    public Paint getDomainLabelPaint() {
        return domainLabelPaint;
    }

    public void setDomainLabelPaint(Paint domainLabelPaint) {
        this.domainLabelPaint = domainLabelPaint;
    }

    public Paint getRangeLabelPaint() {
        return rangeLabelPaint;
    }

    public void setRangeLabelPaint(Paint rangeLabelPaint) {
        this.rangeLabelPaint = rangeLabelPaint;
    }

        public Paint getGridLinePaint() {
        return gridLinePaint;
    }

    /**
     * Creates a copy of gridLinePaint to be used for drawing
     * grid lines.  The copied instance will have it's style
     * attribute set to Paint.Style.STROKE.
     * @param gridLinePaint
     */
    public void setGridLinePaint(Paint gridLinePaint) {
        this.gridLinePaint = new Paint(gridLinePaint);
        this.gridLinePaint.setStyle(Paint.Style.STROKE);
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

    public int getDomainLabelTickExtension() {
        return domainLabelTickExtension;
    }

    public void setDomainLabelTickExtension(int domainLabelTickExtension) {
        this.domainLabelTickExtension = domainLabelTickExtension;
    }

    public int getRangeLabelTickExtension() {
        return rangeLabelTickExtension;
    }

    public void setRangeLabelTickExtension(int rangeLabelTickExtension) {
        this.rangeLabelTickExtension = rangeLabelTickExtension;
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

    /**
     * Deprecated - use getTicksPerRangeLabel() instead.
     * @return
     */
    @Deprecated
    public int getRangeTicksPerLabel() {
        return ticksPerRangeLabel;
    }

    /**
     * Deprecated - use setTicksPerRangeLabel() instead.
     * @param rangeTicksPerLabel
     */
    @Deprecated
    public void setRangeTicksPerLabel(int rangeTicksPerLabel) {
        this.ticksPerRangeLabel = rangeTicksPerLabel;
    }

    public void setGridPaddingTop(float gridPaddingTop) {
        this.gridPaddingTop = gridPaddingTop;
    }

    public float getGridPaddingBottom() {
        return gridPaddingBottom;
    }

    public void setGridPaddingBottom(float gridPaddingBottom) {
        this.gridPaddingBottom = gridPaddingBottom;
    }

    public float getGridPaddingLeft() {
        return gridPaddingLeft;
    }

    public void setGridPaddingLeft(float gridPaddingLeft) {
        this.gridPaddingLeft = gridPaddingLeft;
    }

    public float getGridPaddingRight() {
        return gridPaddingRight;
    }

    public void setGridPaddingRight(float gridPaddingRight) {
        this.gridPaddingRight = gridPaddingRight;
    }

        public float getGridPaddingTop() {
        return gridPaddingTop;
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

    public Paint getDomainOriginLabelPaint() {
        return domainOriginLabelPaint;
    }

    public void setDomainOriginLabelPaint(Paint domainOriginLabelPaint) {
        this.domainOriginLabelPaint = domainOriginLabelPaint;
    }

    public Paint getRangeOriginLabelPaint() {
        return rangeOriginLabelPaint;
    }

    public void setRangeOriginLabelPaint(Paint rangeOriginLabelPaint) {
        this.rangeOriginLabelPaint = rangeOriginLabelPaint;
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

    private class TickLabelArea {
        private float size; // size in pixels
        protected void draw(Canvas canvas) {
            // TODO
        }
    }

}
