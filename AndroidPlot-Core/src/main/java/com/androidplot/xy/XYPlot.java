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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import com.androidplot.Plot;
import com.androidplot.ui.*;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * A View to graphically display x/y coordinates.
 */
public class XYPlot extends Plot<XYSeries, XYSeriesFormatter, XYSeriesRenderer> {

    private BoundaryMode domainOriginBoundaryMode;
    private BoundaryMode rangeOriginBoundaryMode;

    // widgets
    private XYLegendWidget legendWidget;
    private XYGraphWidget graphWidget;
    private TextLabelWidget domainLabelWidget;
    private TextLabelWidget rangeLabelWidget;

    private XYStepMode domainStepMode = XYStepMode.SUBDIVIDE;
    private double domainStepValue = 10;

    private XYStepMode rangeStepMode = XYStepMode.SUBDIVIDE;
    private double rangeStepValue = 10;

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

    // uses set boundary min and max values
    // should be null if not used.
    private Number rangeTopMin = null;
    private Number rangeTopMax = null;
    private Number rangeBottomMin = null;
    private Number rangeBottomMax = null;
    private Number domainLeftMin = null;
    private Number domainLeftMax = null;
    private Number domainRightMin = null;
    private Number domainRightMax = null;

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

    @SuppressWarnings("FieldCanBeLocal")
    private Number domainOriginExtent = null;
    @SuppressWarnings("FieldCanBeLocal")
    private Number rangeOriginExtent = null;

    private BoundaryMode domainUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode domainLowerBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeUpperBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeLowerBoundaryMode = BoundaryMode.AUTO;

    private boolean drawDomainOriginEnabled = true;
    private boolean drawRangeOriginEnabled = true;

    private ArrayList<YValueMarker> yValueMarkers;
    private ArrayList<XValueMarker> xValueMarkers;

    private RectRegion defaultBounds;


    private static final int DEFAULT_LEGEND_WIDGET_H_DP = 10;
    private static final int DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP = 7;
    private static final int DEFAULT_GRAPH_WIDGET_H_DP = 18;
    private static final int DEFAULT_GRAPH_WIDGET_W_DP = 10;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_H_DP = 10;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_W_DP = 80;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_H_DP = 50;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_W_DP = 10;

    private static final int DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_LEGEND_WIDGET_X_OFFSET_DP = 40;
    private static final int DEFAULT_GRAPH_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_GRAPH_WIDGET_X_OFFSET_DP = 0;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_X_OFFSET_DP = 20;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_X_OFFSET_DP = 0;

    private static final int DEFAULT_GRAPH_WIDGET_TOP_MARGIN_DP = 3;
    private static final int DEFAULT_GRAPH_WIDGET_RIGHT_MARGIN_DP = 3;
    private static final int DEFAULT_PLOT_LEFT_MARGIN_DP = 2;
    private static final int DEFAULT_PLOT_RIGHT_MARGIN_DP = 2;
    private static final int DEFAULT_PLOT_BOTTOM_MARGIN_DP = 2;

    public XYPlot(Context context, String title) {
        super(context, title);
    }

    public XYPlot(Context context, String title, RenderMode mode) {
        super(context, title, mode);
    }

    public XYPlot(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public XYPlot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onPreInit() {
        legendWidget = new XYLegendWidget(
                getLayoutManager(),
                this,
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_H_DP),
                        SizeLayoutType.ABSOLUTE, 0.5f, SizeLayoutType.RELATIVE),
                new DynamicTableModel(0, 1),
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeLayoutType.ABSOLUTE));

        graphWidget = new XYGraphWidget(
                getLayoutManager(),
                this,
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_H_DP),
                        SizeLayoutType.FILL,
                        PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_W_DP),
                        SizeLayoutType.FILL));

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        graphWidget.setBackgroundPaint(backgroundPaint);


        domainLabelWidget = new TextLabelWidget(
                getLayoutManager(),
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_H_DP),
                        SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_W_DP),
                        SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);
        rangeLabelWidget = new TextLabelWidget(
                getLayoutManager(),
                new SizeMetrics(
                        PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_H_DP),
                        SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_W_DP),
                        SizeLayoutType.ABSOLUTE),
                TextOrientationType.VERTICAL_ASCENDING);

        legendWidget.position(
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_X_OFFSET_DP),
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP),
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.RIGHT_BOTTOM);

        graphWidget.position(
                PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_X_OFFSET_DP),
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_Y_OFFSET_DP),
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.RIGHT_MIDDLE);

        domainLabelWidget.position(
                PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_X_OFFSET_DP),
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_Y_OFFSET_DP),
                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
                AnchorPosition.LEFT_BOTTOM);

        rangeLabelWidget.position(
                PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_X_OFFSET_DP),
                XLayoutStyle.ABSOLUTE_FROM_LEFT,
                PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_Y_OFFSET_DP),
                YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_MIDDLE);

        getLayoutManager().moveToTop(getTitleWidget());
        getLayoutManager().moveToTop(getLegendWidget());
        graphWidget.setMarginTop(PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_TOP_MARGIN_DP));
        graphWidget.setMarginRight(PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_RIGHT_MARGIN_DP));

        getDomainLabelWidget().pack();
        getRangeLabelWidget().pack();
        setPlotMarginLeft(PixelUtils.dpToPix(DEFAULT_PLOT_LEFT_MARGIN_DP));
        setPlotMarginRight(PixelUtils.dpToPix(DEFAULT_PLOT_RIGHT_MARGIN_DP));
        setPlotMarginBottom(PixelUtils.dpToPix(DEFAULT_PLOT_BOTTOM_MARGIN_DP));

        xValueMarkers = new ArrayList<XValueMarker>();
        yValueMarkers = new ArrayList<YValueMarker>();

        setDefaultBounds(new RectRegion(-1, 1, -1, 1));
    }


    public void setGridPadding(float left, float top, float right, float bottom) {
        getGraphWidget().setGridPaddingTop(top);
        getGraphWidget().setGridPaddingBottom(bottom);
        getGraphWidget().setGridPaddingLeft(left);
        getGraphWidget().setGridPaddingRight(right);
    }

    @Override
    protected void notifyListenersBeforeDraw(Canvas canvas) {
        super.notifyListenersBeforeDraw(canvas);

        // this call must be AFTER the notify so that if the listener
        // is a synchronized series, it has the opportunity to
        // place a read lock on it's data.
        calculateMinMaxVals();
    }

    /**
     * Checks whether the point is within the plot's graph area.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean containsPoint(float x, float y) {
        if (getGraphWidget().getGridRect() != null) {
            return getGraphWidget().getGridRect().contains(x, y);
        }
        return false;
    }


    /**
     * Convenience method - wraps containsPoint(PointF).
     *
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
        return getGraphWidget().getYVal(point);
    }

    public Number getXVal(PointF point) {
        return getGraphWidget().getXVal(point);
    }

    private boolean isXValWithinView(double xVal) {
        return (userMinY == null || xVal >= userMinY.doubleValue()) &&
                userMaxY == null || xVal <= userMaxY.doubleValue();
    }

    private boolean isPointVisible(Number x, Number y) {
        // values without both an x and y val arent visible
        if (x == null || y == null) {
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
        for (final XYSeries series : getSeriesSet()) {
            // step through each point in each series:
            for (int i = 0; i < series.size(); i++) {
                Number thisX = series.getX(i);
                Number thisY = series.getY(i);
                if (isPointVisible(thisX, thisY)) {
                    // only calculate if a static value has not been set:
                    if (userMinX == null) {
                        if (thisX != null && (calculatedMinX == null ||
                                thisX.doubleValue() < calculatedMinX.doubleValue())) {
                            calculatedMinX = thisX;
                        }
                    }

                    if (userMaxX == null) {
                        if (thisX != null && (calculatedMaxX == null ||
                                thisX.doubleValue() > calculatedMaxX.doubleValue())) {
                            calculatedMaxX = thisX;
                        }
                    }

                    if (userMinY == null) {
                        if (thisY != null && (calculatedMinY == null ||
                                thisY.doubleValue() < calculatedMinY.doubleValue())) {
                            calculatedMinY = thisY;
                        }
                    }

                    if (userMaxY == null) {
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
        switch (domainFramingModel) {
            case ORIGIN:
                updateDomainMinMaxForOriginModel();
                break;
            case EDGE:
                updateDomainMinMaxForEdgeModel();
                calculatedMinX = ApplyUserMinMax(calculatedMinX, domainLeftMin,
                        domainLeftMax);
                calculatedMaxX = ApplyUserMinMax(calculatedMaxX,
                        domainRightMin, domainRightMax);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Domain Framing Model not yet supported: " + domainFramingModel);
        }

        switch (rangeFramingModel) {
            case ORIGIN:
                updateRangeMinMaxForOriginModel();
                break;
            case EDGE:
            	if (getSeriesSet().size() > 0) {
	                updateRangeMinMaxForEdgeModel();
	                calculatedMinY = ApplyUserMinMax(calculatedMinY,
	                        rangeBottomMin, rangeBottomMax);
	                calculatedMaxY = ApplyUserMinMax(calculatedMaxY, rangeTopMin,
	                        rangeTopMax);
            	}
                break;
            default:
                throw new UnsupportedOperationException(
                        "Range Framing Model not yet supported: " + domainFramingModel);
        }

        calculatedDomainOrigin = userDomainOrigin != null ? userDomainOrigin : getCalculatedMinX();
        calculatedRangeOrigin = this.userRangeOrigin != null ? userRangeOrigin : getCalculatedMinY();
    }

    /**
     * Should ONLY be called from updateMinMax.
     * Results are undefined otherwise.
     */
    private void updateDomainMinMaxForEdgeModel() {
        switch (domainUpperBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(prevMaxX == null || (calculatedMaxX.doubleValue() > prevMaxX.doubleValue()))) {
                    calculatedMaxX = prevMaxX;
                }
                break;
            case SHRINNK:
                if (!(prevMaxX == null || calculatedMaxX.doubleValue() < prevMaxX.doubleValue())) {
                    calculatedMaxX = prevMaxX;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "DomainUpperBoundaryMode not yet implemented: " + domainUpperBoundaryMode);
        }

        switch (domainLowerBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(prevMinX == null || calculatedMinX.doubleValue() < prevMinX.doubleValue())) {
                    calculatedMinX = prevMinX;
                }
                break;
            case SHRINNK:
                if (!(prevMinX == null || calculatedMinX.doubleValue() > prevMinX.doubleValue())) {
                    calculatedMinX = prevMinX;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "DomainLowerBoundaryMode not supported: " + domainLowerBoundaryMode);
        }
    }

    public void updateRangeMinMaxForEdgeModel() {
        switch (rangeUpperBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(prevMaxY == null || calculatedMaxY.doubleValue() > prevMaxY.doubleValue())) {
                    calculatedMaxY = prevMaxY;
                }
                break;
            case SHRINNK:
                if (!(prevMaxY == null || calculatedMaxY.doubleValue() < prevMaxY.doubleValue())) {
                    calculatedMaxY = prevMaxY;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "RangeUpperBoundaryMode not supported: " + rangeUpperBoundaryMode);
        }

        switch (rangeLowerBoundaryMode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(prevMinY == null || calculatedMinY.doubleValue() < prevMinY.doubleValue())) {
                    calculatedMinY = prevMinY;
                }
                break;
            case SHRINNK:
                if (!(prevMinY == null || calculatedMinY.doubleValue() > prevMinY.doubleValue())) {
                    calculatedMinY = prevMinY;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "RangeLowerBoundaryMode not supported: " + rangeLowerBoundaryMode);
        }
    }

    /**
     * Apply user supplied min and max to the calculated boundary value.
     *
     * @param value
     * @param min
     * @param max
     */
    private Number ApplyUserMinMax(Number value, Number min, Number max) {
        value = (((min == null) || (value.doubleValue() > min.doubleValue()))
                ? value
                : min);
        value = (((max == null) || (value.doubleValue() < max.doubleValue()))
                ? value
                : max);
        return value;
    }

    /**
     * Centers the domain axis on origin.
     *
     * @param origin
     */
    public void centerOnDomainOrigin(Number origin) {
        centerOnDomainOrigin(origin, null, BoundaryMode.AUTO);
    }

    /**
     * Centers the domain on origin, calculating the upper and lower boundaries of the axis
     * using mode and extent.
     *
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
     *
     * @param origin
     */
    public void centerOnRangeOrigin(Number origin) {
        centerOnRangeOrigin(origin, null, BoundaryMode.AUTO);
    }

    /**
     * Centers the domain on origin, calculating the upper and lower boundaries of the axis
     * using mode and extent.
     *
     * @param origin
     * @param extent
     * @param mode
     */
    @SuppressWarnings("SameParameterValue")
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
     *
     * @param x
     * @param y
     * @return
     */
    private double distance(double x, double y) {
        if (x > y) {
            return x - y;
        } else {
            return y - x;
        }
    }

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
                if (maxYDelta > minYDelta) {
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
                throw new UnsupportedOperationException(
                        "Range Origin Boundary Mode not yet supported: " + rangeOriginBoundaryMode);
        }
    }

    /**
     * Convenience method - wraps XYGraphWidget.getTicksPerRangeLabel().
     * Equivalent to getGraphWidget().getTicksPerRangeLabel().
     *
     * @return
     */
    public int getTicksPerRangeLabel() {
        return graphWidget.getTicksPerRangeLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setTicksPerRangeLabel().
     * Equivalent to getGraphWidget().setTicksPerRangeLabel().
     *
     * @param ticksPerRangeLabel
     */
    public void setTicksPerRangeLabel(int ticksPerRangeLabel) {
        graphWidget.setTicksPerRangeLabel(ticksPerRangeLabel);
    }

    /**
     * Convenience method - wraps XYGraphWidget.getTicksPerDomainLabel().
     * Equivalent to getGraphWidget().getTicksPerDomainLabel().
     *
     * @return
     */
    public int getTicksPerDomainLabel() {
        return graphWidget.getTicksPerDomainLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setTicksPerDomainLabel().
     * Equivalent to getGraphWidget().setTicksPerDomainLabel().
     *
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
        return getDomainLabelWidget().getText();
    }

    public void setDomainLabel(String domainLabel) {
        getDomainLabelWidget().setText(domainLabel);
    }

    public String getRangeLabel() {
        return getRangeLabelWidget().getText();
    }

    public void setRangeLabel(String rangeLabel) {
        getRangeLabelWidget().setText(rangeLabel);
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

    public TextLabelWidget getDomainLabelWidget() {
        return domainLabelWidget;
    }

    public void setDomainLabelWidget(TextLabelWidget domainLabelWidget) {
        this.domainLabelWidget = domainLabelWidget;
    }

    public TextLabelWidget getRangeLabelWidget() {
        return rangeLabelWidget;
    }

    public void setRangeLabelWidget(TextLabelWidget rangeLabelWidget) {
        this.rangeLabelWidget = rangeLabelWidget;
    }

    /**
     * Convenience method - wraps XYGraphWidget.getRangeValueFormat().
     *
     * @return
     */
    public Format getRangeValueFormat() {
        return graphWidget.getRangeValueFormat();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setRangeValueFormat().
     *
     * @param rangeValueFormat
     */
    public void setRangeValueFormat(Format rangeValueFormat) {
        graphWidget.setRangeValueFormat(rangeValueFormat);
    }

    /**
     * Convenience method - wraps XYGraphWidget.getDomainValueFormat().
     *
     * @return
     */
    public Format getDomainValueFormat() {
        return graphWidget.getDomainValueFormat();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setDomainValueFormat().
     *
     * @param domainValueFormat
     */
    public void setDomainValueFormat(Format domainValueFormat) {
        graphWidget.setDomainValueFormat(domainValueFormat);
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param lowerBoundary
     * @param upperBoundary
     * @param mode
     */
    public synchronized void setDomainBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setDomainBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param lowerBoundary
     * @param lowerBoundaryMode
     * @param upperBoundary
     * @param upperBoundaryMode
     */
    public synchronized void setDomainBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode,
                                                 Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setDomainLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setDomainUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param lowerBoundary
     * @param upperBoundary
     * @param mode
     */
    public synchronized void setRangeBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setRangeBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param lowerBoundary
     * @param lowerBoundaryMode
     * @param upperBoundary
     * @param upperBoundaryMode
     */
    public synchronized void setRangeBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode,
                                                Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setRangeLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setRangeUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    protected synchronized void setDomainUpperBoundaryMode(BoundaryMode mode) {
        this.domainUpperBoundaryMode = mode;
    }

    protected synchronized void setUserMaxX(Number boundary) {
        // Ifor 12/10/2011
        // you want null for auto grow and shrink
        //if(boundary == null) {
        //    throw new NullPointerException("Boundary value cannot be null.");
        //}
        this.userMaxX = boundary;
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param boundary
     * @param mode
     */
    public synchronized void setDomainUpperBoundary(Number boundary, BoundaryMode mode) {
        setUserMaxX((mode == BoundaryMode.FIXED) ? boundary : null);
        setDomainUpperBoundaryMode(mode);
        setDomainFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setDomainLowerBoundaryMode(BoundaryMode mode) {
        this.domainLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinX(Number boundary) {
        // Ifor 12/10/2011
        // you want null for auto grow and shrink
        //if(boundary == null) {
        //    throw new NullPointerException("Boundary value cannot be null.");
        //}
        this.userMinX = boundary;
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param boundary
     * @param mode
     */
    public synchronized void setDomainLowerBoundary(Number boundary, BoundaryMode mode) {
        setUserMinX((mode == BoundaryMode.FIXED) ? boundary : null);
        setDomainLowerBoundaryMode(mode);
        setDomainFramingModel(XYFramingModel.EDGE);
        //updateMinMaxVals();
    }

    protected synchronized void setRangeUpperBoundaryMode(BoundaryMode mode) {
        this.rangeUpperBoundaryMode = mode;
    }

    protected synchronized void setUserMaxY(Number boundary) {
        // Ifor 12/10/2011
        // you want null for auto grow and shrink
        //if(boundary == null) {
        //    throw new NullPointerException("Boundary value cannot be null.");
        //}
        this.userMaxY = boundary;
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param boundary
     * @param mode
     */
    public synchronized void setRangeUpperBoundary(Number boundary, BoundaryMode mode) {
        setUserMaxY((mode == BoundaryMode.FIXED) ? boundary : null);
        setRangeUpperBoundaryMode(mode);
        setRangeFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setRangeLowerBoundaryMode(BoundaryMode mode) {
        this.rangeLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinY(Number boundary) {
        // Ifor 12/10/2011
        // you want null for auto grow and shrink
        //if(boundary == null) {
        //    throw new NullPointerException("Boundary value cannot be null.");
        //}
        this.userMinY = boundary;
    }

    /**
     * Setup the boundary mode, boundary values only applicable in FIXED mode.
     *
     * @param boundary
     * @param mode
     */
    public synchronized void setRangeLowerBoundary(Number boundary, BoundaryMode mode) {
        setUserMinY((mode == BoundaryMode.FIXED) ? boundary : null);
        setRangeLowerBoundaryMode(mode);
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
        if (origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userDomainOrigin = origin;
    }

    public synchronized void setUserRangeOrigin(Number origin) {
        if (origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userRangeOrigin = origin;
    }

    public XYFramingModel getDomainFramingModel() {
        return domainFramingModel;
    }

    @SuppressWarnings("SameParameterValue")
    protected void setDomainFramingModel(XYFramingModel domainFramingModel) {
        this.domainFramingModel = domainFramingModel;
    }

    public XYFramingModel getRangeFramingModel() {

        return rangeFramingModel;
    }

    @SuppressWarnings("SameParameterValue")
    protected void setRangeFramingModel(XYFramingModel rangeFramingModel) {
        this.rangeFramingModel = rangeFramingModel;
    }

    /**
     * CalculatedMinX value after the the framing model has been applied.
     *
     * @return
     */
    public Number getCalculatedMinX() {
        return calculatedMinX != null ? calculatedMinX : getDefaultBounds().getMinX();
    }

    /**
     * CalculatedMaxX value after the the framing model has been applied.
     *
     * @return
     */
    public Number getCalculatedMaxX() {
        return calculatedMaxX != null ? calculatedMaxX : getDefaultBounds().getMaxX();
    }

    /**
     * CalculatedMinY value after the the framing model has been applied.
     *
     * @return
     */
    public Number getCalculatedMinY() {
        return calculatedMinY != null ? calculatedMinY : getDefaultBounds().getMinY();
    }

    /**
     * CalculatedMaxY value after the the framing model has been applied.
     *
     * @return
     */
    public Number getCalculatedMaxY() {
        return calculatedMaxY != null ? calculatedMaxY : getDefaultBounds().getMaxY();
    }

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

    /**
     * Appends the specified marker to the end of plot's yValueMarkers list.
     *
     * @param marker The YValueMarker to be added.
     * @return true if the object was successfully added, false otherwise.
     */
    public boolean addMarker(YValueMarker marker) {
        if (yValueMarkers.contains(marker)) {
            return false;
        } else {
            return yValueMarkers.add(marker);
        }
    }

    /**
     * Removes the specified marker from the plot.
     *
     * @param marker
     * @return The YValueMarker removed if successfull,  null otherwise.
     */
    public YValueMarker removeMarker(YValueMarker marker) {
        int markerIndex = yValueMarkers.indexOf(marker);
        if (markerIndex == -1) {
            return null;
        } else {
            return yValueMarkers.remove(markerIndex);
        }
    }

    /**
     * Convenience method - combines removeYMarkers() and removeXMarkers().
     *
     * @return
     */
    public int removeMarkers() {
        int removed = removeXMarkers();
        removed += removeYMarkers();
        return removed;
    }

    /**
     * Removes all YValueMarker instances from the plot.
     *
     * @return
     */
    public int removeYMarkers() {
        int numMarkersRemoved = yValueMarkers.size();
        yValueMarkers.clear();
        return numMarkersRemoved;
    }

    /**
     * Appends the specified marker to the end of plot's xValueMarkers list.
     *
     * @param marker The XValueMarker to be added.
     * @return true if the object was successfully added, false otherwise.
     */
    public boolean addMarker(XValueMarker marker) {
        return !xValueMarkers.contains(marker) && xValueMarkers.add(marker);
    }

    /**
     * Removes the specified marker from the plot.
     *
     * @param marker
     * @return The XValueMarker removed if successfull,  null otherwise.
     */
    public XValueMarker removeMarker(XValueMarker marker) {
        int markerIndex = xValueMarkers.indexOf(marker);
        if (markerIndex == -1) {
            return null;
        } else {
            return xValueMarkers.remove(markerIndex);
        }
    }

    /**
     * Removes all XValueMarker instances from the plot.
     *
     * @return
     */
    public int removeXMarkers() {
        int numMarkersRemoved = xValueMarkers.size();
        xValueMarkers.clear();
        return numMarkersRemoved;
    }

    protected List<YValueMarker> getYValueMarkers() {
        return yValueMarkers;
    }

    protected List<XValueMarker> getXValueMarkers() {
        return xValueMarkers;
    }

    public RectRegion getDefaultBounds() {
        return defaultBounds;
    }

    public void setDefaultBounds(RectRegion defaultBounds) {
        this.defaultBounds = defaultBounds;
    }

    /**
     * @return the rangeTopMin
     */
    public Number getRangeTopMin() {
        return rangeTopMin;
    }

    /**
     * @param rangeTopMin the rangeTopMin to set
     */
    public synchronized void setRangeTopMin(Number rangeTopMin) {
        this.rangeTopMin = rangeTopMin;
    }

    /**
     * @return the rangeTopMax
     */
    public Number getRangeTopMax() {
        return rangeTopMax;
    }

    /**
     * @param rangeTopMax the rangeTopMax to set
     */
    public synchronized void setRangeTopMax(Number rangeTopMax) {
        this.rangeTopMax = rangeTopMax;
    }

    /**
     * @return the rangeBottomMin
     */
    public Number getRangeBottomMin() {
        return rangeBottomMin;
    }

    /**
     * @param rangeBottomMin the rangeBottomMin to set
     */
    public synchronized void setRangeBottomMin(Number rangeBottomMin) {
        this.rangeBottomMin = rangeBottomMin;
    }

    /**
     * @return the rangeBottomMax
     */
    public Number getRangeBottomMax() {
        return rangeBottomMax;
    }

    /**
     * @param rangeBottomMax the rangeBottomMax to set
     */
    public synchronized void setRangeBottomMax(Number rangeBottomMax) {
        this.rangeBottomMax = rangeBottomMax;
    }

    /**
     * @return the domainLeftMin
     */
    public Number getDomainLeftMin() {
        return domainLeftMin;
    }

    /**
     * @param domainLeftMin the domainLeftMin to set
     */
    public synchronized void setDomainLeftMin(Number domainLeftMin) {
        this.domainLeftMin = domainLeftMin;
    }

    /**
     * @return the domainLeftMax
     */
    public Number getDomainLeftMax() {
        return domainLeftMax;
    }

    /**
     * @param domainLeftMax the domainLeftMax to set
     */
    public synchronized void setDomainLeftMax(Number domainLeftMax) {
        this.domainLeftMax = domainLeftMax;
    }

    /**
     * @return the domainRightMin
     */
    public Number getDomainRightMin() {
        return domainRightMin;
    }

    /**
     * @param domainRightMin the domainRightMin to set
     */
    public synchronized void setDomainRightMin(Number domainRightMin) {
        this.domainRightMin = domainRightMin;
    }

    /**
     * @return the domainRightMax
     */
    public Number getDomainRightMax() {
        return domainRightMax;
    }

    /**
     * @param domainRightMax the domainRightMax to set
     */
    public synchronized void setDomainRightMax(Number domainRightMax) {
        this.domainRightMax = domainRightMax;
    }
}