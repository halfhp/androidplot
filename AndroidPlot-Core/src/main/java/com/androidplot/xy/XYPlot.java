/*
Copyright 2010 Nick Fellows. All rights reserved.

Redistribution and use in source and binary forms, without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Nick Fellows ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NICK FELLOWS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Nick Fellows.
*/

package com.androidplot.xy;

import android.content.Context;
//import android.graphics.*;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
//import com.androidplot.xy.ui.widget.renderer.XYRendererType;
import com.androidplot.ui.widget.RangeLabelWidget;
import com.androidplot.ui.widget.DomainLabelWidget;
import com.androidplot.ui.layout.*;
import com.androidplot.ui.widget.TextOrientationType;
import com.androidplot.util.ValPixConverter;

import java.text.Format;

/**
 * A View to graphically display x/y coordinates.
 */
public class XYPlot extends Plot<XYSeries, XYSeriesFormatter, XYSeriesRenderer> {

    private BoundaryMode domainOriginBoundaryMode;
    private BoundaryMode rangeOriginBoundaryMode;

    public boolean isDrawDomainOriginEnabled() {
        return drawDomainOriginEnabled;
    }

    public void setDrawDomainOriginEnabled(boolean drawDomainOriginEnabled) {
        this.drawDomainOriginEnabled = drawDomainOriginEnabled;
    }

    public boolean isDrawRangeOriginEnabled() {
        return drawRangeOriginEnabled;
    }

    public void setDrawRangeOriginEnabled(boolean drawRangeOriginEnabled) {
        this.drawRangeOriginEnabled = drawRangeOriginEnabled;
    }

    // widgets
    private XYLegendWidget legendWidget;
    private XYGraphWidget graphWidget;
    private DomainLabelWidget domainLabelWidget;
    private RangeLabelWidget rangeLabelWidget;

    private XYStepMode domainStepMode = XYStepMode.SUBDIVIDE;
    private double domainStepValue = 10;

    private XYStepMode rangeStepMode = XYStepMode.SUBDIVIDE;
    private double rangeStepValue = 10;

    private String domainLabel = "domain";
    private String rangeLabel = "range";

    // mabsolute  in/max of all series assigned to this plot.
    // may be removed soon.
/*    private Number actualMinX;
    private Number actualMaxX;
    private Number actualMinY;
    private Number actualMaxY;*/

    // user settable min/max values
    private Number userMinX;
    private Number userMaxX;
    private Number userMinY;
    private Number userMaxY;

    // these are the final min/max used for dispplaying data
    private Number calculatedMinX;
    private Number calculatedMaxX;
    private Number calculatedMinY;
    private Number calculatedMaxY;

    // previous calculated min/max vals.
    // primarily used for GROW/SHRINK operations.
    private Number prevMinX;
    private Number prevMaxX;
    private Number prevMinY;
    private Number prevMaxY;

    // used for  calculating the domain/range extents that will be displayed on the plot.
    // using boundaries and origins are mutually exclusive.  because of this,
    // setting one will disable the other.  when only setting the FramingModel,
    // the origin or boundary is set to the current value of the plot.
    private XYFramingModel domainFramingModel = XYFramingModel.EDGE;
    private XYFramingModel rangeFramingModel = XYFramingModel.EDGE;

    private Number userDomainOrigin;
    private Number userRangeOrigin;

    private Number calculatedDomainOrigin;
    private Number calculatedRangeOrigin;

    private Number domainOriginExtent = null;
    private Number rangeOriginExtent = null;

    private BoundaryMode domainUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode domainLowerBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeLowerBoundaryMode = BoundaryMode.AUTO;

    private boolean drawDomainOriginEnabled = true;
    private boolean drawRangeOriginEnabled = true;

    {
        legendWidget = new XYLegendWidget(
                this,
                new SizeMetrics(14, SizeLayoutType.ABSOLUTE, 0.5f, SizeLayoutType.RELATIVE),
                new DynamicTableModel(0, 1),
                new SizeMetrics(12, SizeLayoutType.ABSOLUTE, 12, SizeLayoutType.ABSOLUTE));

        graphWidget = new XYGraphWidget(
                this,
                new SizeMetrics(20, SizeLayoutType.FILL, 12, SizeLayoutType.FILL));
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        graphWidget.setBackgroundPaint(backgroundPaint);

        domainLabelWidget = new DomainLabelWidget(
                this,
                new SizeMetrics(12, SizeLayoutType.ABSOLUTE, 100, SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);
        rangeLabelWidget = new RangeLabelWidget(
                this,
                new SizeMetrics(60, SizeLayoutType.ABSOLUTE, 12, SizeLayoutType.ABSOLUTE),
                TextOrientationType.VERTICAL_ASCENDING);

        getLayoutManager().position(
                legendWidget,
                40,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.RIGHT_BOTTOM);

        getLayoutManager().position(
                graphWidget,
                3,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.RIGHT_MIDDLE);

        getLayoutManager().position(
                domainLabelWidget,
                25,
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.LEFT_BOTTOM);

        getLayoutManager().position(
                rangeLabelWidget,
                0,
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                0,
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_MIDDLE);

        getLayoutManager().moveToTop(getTitleWidget());
        getLayoutManager().moveToTop(getLegendWidget());
        graphWidget.setMarginTop(10);
        graphWidget.setMarginRight(10);

        getTitleWidget().pack();
        getDomainLabelWidget().pack();
        getRangeLabelWidget().pack();
        setPlotMarginLeft(2);
        setPlotMarginRight(2);
        setPlotMarginBottom(2);
    }


    public XYPlot(Context context, String title) {
        super(context, title);
    }

    public XYPlot(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public XYPlot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridPadding(float left, float top, float right, float bottom) {
        getGraphWidget().setGridPaddingTop(top);
        getGraphWidget().setGridPaddingBottom(bottom);
        getGraphWidget().setGridPaddingLeft(left);
        getGraphWidget().setGridPaddingRight(right);
    }

    @Override
    protected XYSeriesRenderer doGetRendererInstance(Class clazz) {
        if (clazz == LineAndPointRenderer.class) {
            return new LineAndPointRenderer(this);
        } else if (clazz == BarRenderer.class) {
            return new BarRenderer(this);
        } else if (clazz == StepRenderer.class) {
            return new StepRenderer(this);
        } else {
            return null;
        }
    }

    @Override
    protected void doBeforeDraw() {
        calculateMinMaxVals();
    }

    @Override
    protected void doAfterDraw() {

    }

    /**
     * Checks whether the point is within the plot's graph area.
     * @param x
     * @param y
     * @return
     */
    public boolean containsPoint(float x, float y) {
        if(getGraphWidget().getGridRect() != null) {
            return getGraphWidget().getGridRect().contains(x, y);
        }
        return false;
    }


    /**
     * Convenience method - wraps containsPoint(PointF).
     * @param point
     * @return
     */
    public boolean containsPoint(PointF point) {
        return containsPoint(point.x, point.y);
    }

    public void setCursorPosition(PointF point) {
        getGraphWidget().setCursorPosition(point);
    }

    public void setCursorPosition(float x, float y) {
        getGraphWidget().setCursorPosition(x, y);
    }

    public Number getYVal(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
        //ValPixConverter.pixToVal(point.y, )
    }

    public Number getXVal(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private boolean isXValWithinView(double xVal) {
        return (userMinY == null || xVal >= userMinY.doubleValue()) &&
                userMaxY == null || xVal <= userMaxY.doubleValue();
    }

    private boolean isPointVisible(Number x, Number y) {
        // values without both an x and y val arent visible
        if( x == null || y == null) {
            return false;
        }
        return isValWithinRange(y.doubleValue(), userMinY, userMaxY) &&
                isValWithinRange(x.doubleValue(), userMinX, userMaxX);
    }

    private boolean isValWithinRange(double val, Number min, Number max) {
        boolean isAboveMinThreshold = min == null || val >= min.doubleValue();
        boolean isBelowMaxThreshold = max == null || val <= max.doubleValue();
        return isAboveMinThreshold &&
                isBelowMaxThreshold;
    }

    public void calculateMinMaxVals() {
        prevMinX = calculatedMinX;
        prevMaxX = calculatedMaxX;
        prevMinY = calculatedMinY;
        prevMaxY = calculatedMaxY;

        calculatedMinX = userMinX;
        calculatedMaxX = userMaxX;
        calculatedMinY = userMinY;
        calculatedMaxY = userMaxY;

        // next we go through each series to update our min/max values:
        for (XYSeries series : getSeriesSet()) {
            // step through each point in each series:
            for (int i = 0; i < series.size(); i++) {
                Number thisX = series.getX(i);
                Number thisY = series.getY(i);
                if (isPointVisible(thisX, thisY)) {
                    // only calculate if a static value has not been set:
                    if (userMinX == null) {
                        if (thisX != null && (calculatedMinX == null || thisX.doubleValue() < calculatedMinX.doubleValue())) {
                            calculatedMinX = thisX;
                        }
                    }

                    if (userMaxX == null) {
                        //Number thisMaxX = series.getMaxX();
                        if (thisX != null && (calculatedMaxX == null || thisX.doubleValue() > calculatedMaxX.doubleValue())) {
                            calculatedMaxX = thisX;
                        }
                    }

                    if (userMinY == null) {
                        //Number thisMinY = series.getMinY();
                        if (thisY != null && (calculatedMinY == null || thisY.doubleValue() < calculatedMinY.doubleValue())) {
                            calculatedMinY = thisY;
                        }
                    }

                    if (userMaxY == null) {
                        //Number thisMaxY = series.getMaxY();
                        if (thisY != null && (calculatedMaxY == null || thisY.doubleValue() > calculatedMaxY.doubleValue())) {
                            calculatedMaxY = thisY;
                        }
                    }
                }
            }
        }

        // at this point we now know what points are going to be visible on our
        // plot, but we still need to make corrections based on modes being used:
        // (grow, shrink etc.)
        switch(domainFramingModel) {
            case ORIGIN:
                updateDomainMinMaxForOriginModel();
                break;
            case EDGE:
                updateDomainMinMaxForEdgeModel();
                break;
            default:
                throw new UnsupportedOperationException("Domain Framing Model not yet supported: " + domainFramingModel);
        }

        switch(rangeFramingModel) {
            case ORIGIN:
                updateRangeMinMaxForOriginModel();
                break;
            case EDGE:
                updateRangeMinMaxForEdgeModel();
                break;
            default:
                throw new UnsupportedOperationException("Range Framing Model not yet supported: " + domainFramingModel);
        }

        calculatedDomainOrigin = userDomainOrigin != null ? userDomainOrigin : getCalculatedMinX();
        calculatedRangeOrigin = this.userRangeOrigin != null ? userRangeOrigin : getCalculatedMinY();
    }

    /**
     * Should ONLY be called from updateMinMax.
     * Results are undefined otherwise.
     */
    private void updateDomainMinMaxForEdgeModel() {
        switch(domainUpperBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if(!(prevMaxX == null || (calculatedMaxX.doubleValue() > prevMaxX.doubleValue()))) {
                    calculatedMaxX = prevMaxX;
                }
                break;
            case SHRINNK:
                if(!(prevMaxX == null || calculatedMaxX.doubleValue() < prevMaxX.doubleValue())) {
                    calculatedMaxX = prevMaxX;
                }
                break;
            default:
                throw new UnsupportedOperationException("DomainUpperBoundaryMode not yet implemented: " + domainUpperBoundaryMode);
        }

        switch (domainLowerBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if(!(prevMinX == null || calculatedMinX.doubleValue() < prevMinX.doubleValue())) {
                    calculatedMinX = prevMinX;
                }
                break;
            case SHRINNK:
                if(!(prevMinX == null || calculatedMinX.doubleValue() > prevMinX.doubleValue())) {
                    calculatedMinX = prevMinX;
                }
                break;
            default:
                throw new UnsupportedOperationException("DomainLowerBoundaryMode not supported: " + domainLowerBoundaryMode);
        }
    }

    public void updateRangeMinMaxForEdgeModel() {
        switch (rangeUpperBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if(!(prevMaxY == null || calculatedMaxY.doubleValue() > prevMaxY.doubleValue())) {
                    calculatedMaxY = prevMaxY;
                }
                break;
            case SHRINNK:
                if(!(prevMaxY == null || calculatedMaxY.doubleValue() < prevMaxY.doubleValue())) {
                   calculatedMaxY = prevMaxY;
                }
                break;
            default:
                throw new UnsupportedOperationException("RangeUpperBoundaryMode not supported: " + rangeUpperBoundaryMode);
        }

        switch (rangeLowerBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if(!(prevMinY == null || calculatedMinY.doubleValue() < prevMinY.doubleValue())) {
                    calculatedMinY = prevMinY;
                }
                break;
            case SHRINNK:
                if(!(prevMinY == null || calculatedMinY.doubleValue() > prevMinY.doubleValue())) {
                    calculatedMinY = prevMinY;
                }
                break;
            default:
                throw new UnsupportedOperationException("RangeLowerBoundaryMode not supported: " + rangeLowerBoundaryMode);
        }
    }

    /**
     * Centers the domain axis on origin.
     * @param origin
     */
    public void centerOnDomainOrigin(Number origin) {
        centerOnDomainOrigin(origin, null, BoundaryMode.AUTO);
    }

    /**
     * Centers the domain on origin, calculating the upper and lower boundaries of the axis
     * using mode and extent.
     * @param origin
     * @param extent
     * @param mode
     */
    public void centerOnDomainOrigin(Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new NullPointerException("Origin param cannot be null.");
        }
        domainFramingModel = XYFramingModel.ORIGIN;
        setUserDomainOrigin(origin);
        domainOriginExtent = extent;
        domainOriginBoundaryMode = mode;

        if (domainOriginBoundaryMode == BoundaryMode.FIXED) {
            double domO = userDomainOrigin.doubleValue();
            double domE = domainOriginExtent.doubleValue();
            userMaxX = domO + domE;
            userMinX = domO - domE;
        } else {
            userMaxX = null;
            userMinX = null;
        }
    }

    /**
     * Centers the range axis on origin.
     * @param origin
     */
    public void centerOnRangeOrigin(Number origin) {
        centerOnRangeOrigin(origin, null, BoundaryMode.AUTO);
    }

    /**
     * Centers the domain on origin, calculating the upper and lower boundaries of the axis
     * using mode and extent.
     * @param origin
     * @param extent
     * @param mode
     */
    public void centerOnRangeOrigin(Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new NullPointerException("Origin param cannot be null.");
        }
        rangeFramingModel = XYFramingModel.ORIGIN;
        setUserRangeOrigin(origin);
        rangeOriginExtent = extent;
        rangeOriginBoundaryMode = mode;

        if (rangeOriginBoundaryMode == BoundaryMode.FIXED) {
            double raO = userRangeOrigin.doubleValue();
            double raE = rangeOriginExtent.doubleValue();
            userMaxY = raO + raE;
            userMinY = raO - raE;
        } else {
            userMaxY = null;
            userMinY = null;
        }
    }

    /**
     * Returns the distance between x and y.
     * Result is never a negative number.
     * @param x
     * @param y
     * @return
     */
    private double distance(double x, double y) {
        if(x > y) {
            return x - y;
        } else {
            return y - x;
        }
    }

    /*private double min(double a, double b) {
        if(a < b) {
            return a;
        } else {
            return b;
        }
    }*/

    /*private double max(double a, double b) {
        if(a > b) {
            return a;
        } else {
            return b;
        }
    }*/

    public void updateDomainMinMaxForOriginModel() {
        double origin = userDomainOrigin.doubleValue();
        double maxXDelta = distance(calculatedMaxX.doubleValue(), origin);
        double minXDelta = distance(calculatedMinX.doubleValue(), origin);
        double delta = maxXDelta > minXDelta ? maxXDelta : minXDelta;
        double dlb = origin - delta;
        double dub = origin + delta;
        switch (domainOriginBoundaryMode) {
            case AUTO:
                calculatedMinX = dlb;
                calculatedMaxX = dub;

                break;
            // if fixed, then the value already exists within "user" vals.
            case FIXED:
                break;
            case GROW: {

                if (prevMinX == null || dlb < prevMinX.doubleValue()) {
                    calculatedMinX = dlb;
                } else {
                    calculatedMinX = prevMinX;
                }

                if (prevMaxX == null || dub > prevMaxX.doubleValue()) {
                    calculatedMaxX = dub;
                } else {
                    calculatedMaxX = prevMaxX;
                }
            }
            break;
            case SHRINNK:
                if (prevMinX == null || dlb > prevMinX.doubleValue()) {
                    calculatedMinX = dlb;
                } else {
                    calculatedMinX = prevMinX;
                }

                if (prevMaxX == null || dub < prevMaxX.doubleValue()) {
                    calculatedMaxX = dub;
                } else {
                    calculatedMaxX = prevMaxX;
                }
                break;
            default:
                throw new UnsupportedOperationException("Domain Origin Boundary Mode not yet supported: " + domainOriginBoundaryMode);
        }
    }

    public void updateRangeMinMaxForOriginModel() {
        switch (rangeOriginBoundaryMode) {
            case AUTO:
                double origin = userRangeOrigin.doubleValue();
                double maxYDelta = distance(calculatedMaxY.doubleValue(), origin);
                double minYDelta = distance(calculatedMinY.doubleValue(), origin);
                if(maxYDelta > minYDelta) {
                    calculatedMinY = origin - maxYDelta;
                    calculatedMaxY = origin + maxYDelta;
                } else {
                    calculatedMinY = origin - minYDelta;
                    calculatedMaxY = origin + minYDelta;
                }
                break;
            case FIXED:
            case GROW:
            case SHRINNK:
            default:
                throw new UnsupportedOperationException("Range Origin Boundary Mode not yet supported: " + rangeOriginBoundaryMode);
        }
    }

    public boolean addSeries(XYSeries series, LineAndPointFormatter formatter) {
        return addSeries(series, LineAndPointRenderer.class, formatter);
    }

    public boolean addSeries(XYSeries series, BarFormatter formatter) {
        return addSeries(series, BarRenderer.class, formatter);
    }

    public boolean addSeries(XYSeries series, StepFormatter formatter) {
        return addSeries(series, StepRenderer.class, formatter);
    }
    
    /**
     * Convenience method - wraps XYGraphWidget.getTicksPerRangeLabel().
     * Equivalent to getGraphWidget().getTicksPerRangeLabel().
     * @return
     */
    public int getTicksPerRangeLabel() {
        return graphWidget.getTicksPerRangeLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setTicksPerRangeLabel().
     * Equivalent to getGraphWidget().setTicksPerRangeLabel().
     * @param ticksPerRangeLabel
     */
    public void setTicksPerRangeLabel(int ticksPerRangeLabel) {
       graphWidget.setTicksPerRangeLabel(ticksPerRangeLabel);
    }

    /**
     * Convenience method - wraps XYGraphWidget.getTicksPerDomainLabel().
     * Equivalent to getGraphWidget().getTicksPerDomainLabel().
     * @return
     */
    public int getTicksPerDomainLabel() {
        return graphWidget.getTicksPerDomainLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setTicksPerDomainLabel().
     * Equivalent to getGraphWidget().setTicksPerDomainLabel().
     * @param ticksPerDomainLabel
     */
    public void setTicksPerDomainLabel(int ticksPerDomainLabel) {
        graphWidget.setTicksPerDomainLabel(ticksPerDomainLabel);
    }

    public XYStepMode getDomainStepMode() {
        return domainStepMode;
    }

    public void setDomainStepMode(XYStepMode domainStepMode) {
        this.domainStepMode = domainStepMode;
    }

    public double getDomainStepValue() {
        return domainStepValue;
    }

    public void setDomainStepValue(double domainStepValue) {
        this.domainStepValue = domainStepValue;
    }

    public void setDomainStep(XYStepMode mode, double value) {
        setDomainStepMode(mode);
        setDomainStepValue(value);
    }

    public XYStepMode getRangeStepMode() {
        return rangeStepMode;
    }

    public void setRangeStepMode(XYStepMode rangeStepMode) {
        this.rangeStepMode = rangeStepMode;
    }

    public double getRangeStepValue() {
        return rangeStepValue;
    }

    public void setRangeStepValue(double rangeStepValue) {
        this.rangeStepValue = rangeStepValue;
    }

    public void setRangeStep(XYStepMode mode, double value) {
        setRangeStepMode(mode);
        setRangeStepValue(value);
    }

    public String getDomainLabel() {
        return domainLabel;
    }

    public void setDomainLabel(String domainLabel) {
        this.domainLabel = domainLabel;
        if(getDomainLabelWidget() != null) {
            getDomainLabelWidget().pack();
        }
    }

    public String getRangeLabel() {
        return rangeLabel;
    }

    public void setRangeLabel(String rangeLabel) {
        this.rangeLabel = rangeLabel;
        if(getRangeLabelWidget() != null) {
            getRangeLabelWidget().pack();
        }
    }

    public XYLegendWidget getLegendWidget() {
        return legendWidget;
    }

    public void setLegendWidget(XYLegendWidget legendWidget) {
        this.legendWidget = legendWidget;
    }

    public XYGraphWidget getGraphWidget() {
        return graphWidget;
    }

    public void setGraphWidget(XYGraphWidget graphWidget) {
        this.graphWidget = graphWidget;
    }

    public DomainLabelWidget getDomainLabelWidget() {
        return domainLabelWidget;
    }

    public void setDomainLabelWidget(DomainLabelWidget domainLabelWidget) {
        this.domainLabelWidget = domainLabelWidget;
    }

    public RangeLabelWidget getRangeLabelWidget() {
        return rangeLabelWidget;
    }

    public void setRangeLabelWidget(RangeLabelWidget rangeLabelWidget) {
        this.rangeLabelWidget = rangeLabelWidget;
    }

    /**
     * Convenience method - wraps XYGraphWidget.getRangeValueFormat().
     * @return
     */
    public Format getRangeValueFormat() {
        return graphWidget.getRangeValueFormat();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setRangeValueFormat().
     * @param rangeValueFormat
     */
    public void setRangeValueFormat(Format rangeValueFormat) {
        graphWidget.setRangeValueFormat(rangeValueFormat);
    }

    /**
     * Convenience method - wraps XYGraphWidget.getDomainValueFormat().
     * @return
     */
    public Format getDomainValueFormat() {
        return graphWidget.getDomainValueFormat();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setDomainValueFormat().
     * @param domainValueFormat
     */
    public void setDomainValueFormat(Format domainValueFormat) {
        graphWidget.setDomainValueFormat(domainValueFormat);
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setDomainBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setDomainLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setDomainUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setRangeBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setRangeLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setRangeUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    public synchronized void setDomainUpperBoundaryMode(BoundaryMode mode) {
        this.domainUpperBoundaryMode = mode;
    }

    public synchronized void setUserMaxX(Number boundary) {
        if(boundary == null) {
            throw new NullPointerException("Boundary value cannot be null.");
        }
        this.userMaxX = boundary;
    }

    public synchronized void setDomainUpperBoundary(Number boundary, BoundaryMode mode) {
        setDomainUpperBoundaryMode(mode);
        setUserMaxX(boundary);
        setDomainFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setDomainLowerBoundaryMode(BoundaryMode mode) {
        this.domainLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinX(Number boundary) {
        if(boundary == null) {
            throw new NullPointerException("Boundary value cannot be null.");
        }
        this.userMinX = boundary;
    }

    public synchronized void setDomainLowerBoundary(Number boundary, BoundaryMode mode) {
        setDomainLowerBoundaryMode(mode);
        setUserMinX(boundary);
        setDomainFramingModel(XYFramingModel.EDGE);
        //updateMinMaxVals();
    }

    protected synchronized void setRangeUpperBoundaryMode(BoundaryMode mode) {
        this.rangeUpperBoundaryMode = mode;
    }

    protected synchronized void setUserMaxY(Number boundary) {
        if(boundary == null) {
            throw new NullPointerException("Boundary value cannot be null.");
        }
        this.userMaxY = boundary;
    }

    public synchronized void setRangeUpperBoundary(Number boundary, BoundaryMode mode) {
        setRangeUpperBoundaryMode(mode);
        setUserMaxY(boundary);
        setRangeFramingModel(XYFramingModel.EDGE);
        //updateMinMaxVals();
    }

    protected synchronized void setRangeLowerBoundaryMode(BoundaryMode mode) {
        this.rangeLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinY(Number boundary) {
        if(boundary == null) {
            throw new NullPointerException("Boundary value cannot be null.");
        }
        this.userMinY = boundary;
    }

    public synchronized void setRangeLowerBoundary(Number boundary, BoundaryMode mode) {
        setRangeLowerBoundaryMode(mode);
        setUserMinY(boundary);
        setRangeFramingModel(XYFramingModel.EDGE);
    }

    private Number getUserMinX() {
        return userMinX;
    }

    private Number getUserMaxX() {
        return userMaxX;
    }

    private Number getUserMinY() {
        return userMinY;
    }

    private Number getUserMaxY() {
        return userMaxY;
    }

    public Number getDomainOrigin() {
        return calculatedDomainOrigin;
    }

    public Number getRangeOrigin() {
        return calculatedRangeOrigin;
    }

    protected BoundaryMode getDomainUpperBoundaryMode() {
        return domainUpperBoundaryMode;
    }

    protected BoundaryMode getDomainLowerBoundaryMode() {
        return domainLowerBoundaryMode;
    }

    protected BoundaryMode getRangeUpperBoundaryMode() {
        return rangeUpperBoundaryMode;
    }

    protected BoundaryMode getRangeLowerBoundaryMode() {
        return rangeLowerBoundaryMode;
    }

    public synchronized void setUserDomainOrigin(Number origin) {
        if(origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userDomainOrigin = origin;
    }

    public synchronized void setUserRangeOrigin(Number origin) {
        if(origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userRangeOrigin = origin;
    }

    public XYFramingModel getDomainFramingModel() {
        return domainFramingModel;
    }

    protected void setDomainFramingModel(XYFramingModel domainFramingModel) {
        this.domainFramingModel = domainFramingModel;
    }

    public XYFramingModel getRangeFramingModel() {

        return rangeFramingModel;
    }

    protected void setRangeFramingModel(XYFramingModel rangeFramingModel) {
        this.rangeFramingModel = rangeFramingModel;
    }

    public Number getCalculatedMinX() {
        return calculatedMinX;
    }

    public Number getCalculatedMaxX() {
        return calculatedMaxX;
    }

    public Number getCalculatedMinY() {
        return calculatedMinY;
    }

    public Number getCalculatedMaxY() {
        return calculatedMaxY;
    }
}
