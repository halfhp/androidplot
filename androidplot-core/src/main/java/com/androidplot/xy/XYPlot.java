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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.androidplot.*;
import com.androidplot.ui.*;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.AttrUtils;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.SeriesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A View to graphically display x/y coordinates.
 */
public class XYPlot extends Plot<XYSeries, XYSeriesFormatter, XYSeriesRenderer, XYSeriesBundle, XYSeriesRegistry> {

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

    private static final int DEFAULT_PLOT_LEFT_MARGIN_DP = 1;
    private static final int DEFAULT_PLOT_RIGHT_MARGIN_DP = 1;
    private static final int DEFAULT_PLOT_TOP_MARGIN_DP = 1;
    private static final int DEFAULT_PLOT_BOTTOM_MARGIN_DP = 1;

    private BoundaryMode domainOriginBoundaryMode;
    private BoundaryMode rangeOriginBoundaryMode;

    // widgets
    private XYLegendWidget legend;
    private XYGraphWidget graph;
    private TextLabelWidget domainTitle;
    private TextLabelWidget rangeTitle;

    private StepModel domainStepModel;
    private StepModel rangeStepModel;

    private XYConstraints constraints = new XYConstraints();

    // min/max used for displaying data
    private RectRegion bounds = RectRegion.withDefaults(new RectRegion(-1, 1, -1, 1));

    // previous calculated min/max vals.
    // primarily used for GROW/SHRINK operations.
    private Number prevMinX;
    private Number prevMaxX;
    private Number prevMinY;
    private Number prevMaxY;

    /**
     * The inner and outer limits define a kind of picture-frame shape area that is used as the valid
     * region for setting domain/range boundaries.  If the set boundaries exceed one of these limits
     * then the limit value is used instead of the boundary.  This is most commonly used to constrain
     * panning & zooming to a specific range on both axes.
     */
    private final RectRegion innerLimits = new RectRegion();
    private final RectRegion outerLimits = new RectRegion();

    private Number userDomainOrigin;
    private Number userRangeOrigin;

    private XYCoords calculatedOrigin = new XYCoords();

    @SuppressWarnings("FieldCanBeLocal")
    private Number domainOriginExtent = null;

    @SuppressWarnings("FieldCanBeLocal")
    private Number rangeOriginExtent = null;

    private ArrayList<YValueMarker> yValueMarkers;
    private ArrayList<XValueMarker> xValueMarkers;

    private PreviewMode previewMode;

    public enum PreviewMode {
        LineAndPoint,
        Candlestick,
        Bar
    }

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
        legend = new XYLegendWidget(
                getLayoutManager(),
                this,
                new Size(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_H_DP),
                        SizeMode.ABSOLUTE, 0.5f, SizeMode.RELATIVE),
                new DynamicTableModel(0, 1),
                new Size(
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP),
                        SizeMode.ABSOLUTE));

        graph = new XYGraphWidget(
                getLayoutManager(),
                this,
                new Size(
                        PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_H_DP),
                        SizeMode.FILL,
                        PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_W_DP),
                        SizeMode.FILL));

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        graph.setBackgroundPaint(backgroundPaint);


        domainTitle = new TextLabelWidget(
                getLayoutManager(),
                new Size(
                        PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_H_DP),
                        SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_W_DP),
                        SizeMode.ABSOLUTE),
                TextOrientation.HORIZONTAL);
        rangeTitle = new TextLabelWidget(
                getLayoutManager(),
                new Size(
                        PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_H_DP),
                        SizeMode.ABSOLUTE,
                        PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_W_DP),
                        SizeMode.ABSOLUTE),
                TextOrientation.VERTICAL_ASCENDING);

        legend.position(
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.RIGHT_BOTTOM);

        graph.position(
                PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
                PixelUtils.dpToPix(DEFAULT_GRAPH_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_CENTER,
                Anchor.RIGHT_MIDDLE);

        domainTitle.position(
                PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                PixelUtils.dpToPix(DEFAULT_DOMAIN_LABEL_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_BOTTOM,
                Anchor.LEFT_BOTTOM);

        rangeTitle.position(
                PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_X_OFFSET_DP),
                HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                PixelUtils.dpToPix(DEFAULT_RANGE_LABEL_WIDGET_Y_OFFSET_DP),
                VerticalPositioning.ABSOLUTE_FROM_CENTER,
                Anchor.LEFT_MIDDLE);

        getLayoutManager().moveToTop(getTitle());
        getLayoutManager().moveToTop(getLegend());

        getDomainTitle().pack();
        getRangeTitle().pack();
        setPlotMarginLeft(PixelUtils.dpToPix(DEFAULT_PLOT_LEFT_MARGIN_DP));
        setPlotMarginRight(PixelUtils.dpToPix(DEFAULT_PLOT_RIGHT_MARGIN_DP));
        setPlotMarginTop(PixelUtils.dpToPix(DEFAULT_PLOT_TOP_MARGIN_DP));
        setPlotMarginBottom(PixelUtils.dpToPix(DEFAULT_PLOT_BOTTOM_MARGIN_DP));

        xValueMarkers = new ArrayList<>();
        yValueMarkers = new ArrayList<>();

        domainStepModel = new StepModel(StepMode.SUBDIVIDE, 10);
        rangeStepModel = new StepModel(StepMode.SUBDIVIDE, 10);
    }

    @Override
    protected void onAfterConfig() {
        // display some generic series data in editors that support it:
        if(isInEditMode()) {

            switch (previewMode) {
                case LineAndPoint: {
                    addSeries(new SimpleXYSeries(Arrays.asList(1, 2, 3, 3, 4),
                                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Red"),
                            new LineAndPointFormatter(Color.RED, null, null, null));
                    addSeries(new SimpleXYSeries(Arrays.asList(2, 1, 4, 2, 5),
                                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Green"),
                            new LineAndPointFormatter(Color.GREEN, null, null, null));
                    addSeries(new SimpleXYSeries(Arrays.asList(3, 3, 2, 3, 3),
                                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Blue"),
                            new LineAndPointFormatter(Color.BLUE, null, null, null));
                }
                break;
                case Candlestick: {
                    CandlestickSeries candlestickSeries = new CandlestickSeries(
                            new CandlestickSeries.Item(1, 10, 2, 9),
                            new CandlestickSeries.Item(4, 18, 6, 5),
                            new CandlestickSeries.Item(3, 11, 5, 10),
                            new CandlestickSeries.Item(2, 17, 2, 15),
                            new CandlestickSeries.Item(6, 11, 11, 7),
                            new CandlestickSeries.Item(8, 16, 10, 15));
                    CandlestickMaker.make(this, new CandlestickFormatter(), candlestickSeries);
                }
                break;
                case Bar: {
                    throw new UnsupportedOperationException("Not yet implemented.");
                }
                default:
                    throw new UnsupportedOperationException("Unexpected preview mode: " + previewMode);
            }
        }
    }

    @Override
    protected void processAttrs(TypedArray attrs) {
        this. previewMode = PreviewMode.values()[attrs.getInt(
                R.styleable.xy_XYPlot_previewMode, PreviewMode.LineAndPoint.ordinal())];

        String domainLabelAttr = attrs.getString(R.styleable.xy_XYPlot_domainTitle);
        if(domainLabelAttr != null) {
            getDomainTitle().setText(domainLabelAttr);
        }

        String rangeLabelAttr = attrs.getString(R.styleable.xy_XYPlot_rangeTitle);
        if(rangeLabelAttr != null) {
            getRangeTitle().setText(rangeLabelAttr);
        }

        AttrUtils.configureStep(attrs, getDomainStepModel(),
                R.styleable.xy_XYPlot_domainStepMode, R.styleable.xy_XYPlot_domainStep);

        AttrUtils.configureStep(attrs, getRangeStepModel(),
                R.styleable.xy_XYPlot_rangeStepMode, R.styleable.xy_XYPlot_rangeStep);

        // domainLabelPaint
        AttrUtils.configureTextPaint(attrs, getDomainTitle().getLabelPaint(),
                R.styleable.xy_XYPlot_domainTitleTextColor, R.styleable.xy_XYPlot_domainTitleTextSize);

        // rangeLabelPaint
        AttrUtils.configureTextPaint(attrs, getRangeTitle().getLabelPaint(),
                R.styleable.xy_XYPlot_rangeTitleTextColor, R.styleable.xy_XYPlot_rangeTitleTextSize);

        // legendWTextPaint
        AttrUtils.configureTextPaint(attrs, getLegend().getTextPaint(),
                R.styleable.xy_XYPlot_legendTextColor,
                R.styleable.xy_XYPlot_legendTextSize);

        // legendIconSize
        AttrUtils.configureSize(attrs, getLegend().getIconSize(),
                R.styleable.xy_XYPlot_legendIconHeightMode, R.styleable.xy_XYPlot_legendIconHeight,
                R.styleable.xy_XYPlot_legendIconWidthMode, R.styleable.xy_XYPlot_legendIconWidth);

        // legend size & position
        AttrUtils.configureWidget(attrs, getLegend(),
                R.styleable.xy_XYPlot_legendHeightMode, R.styleable.xy_XYPlot_legendHeight,
                R.styleable.xy_XYPlot_legendWidthMode, R.styleable.xy_XYPlot_legendWidth,
                R.styleable.xy_XYPlot_legendHorizontalPositioning, R.styleable.xy_XYPlot_legendHorizontalPosition,
                R.styleable.xy_XYPlot_legendVerticalPositioning, R.styleable.xy_XYPlot_legendVerticalPosition,
                R.styleable.xy_XYPlot_legendAnchor, R.styleable.xy_XYPlot_legendVisible);

        getGraph().processAttrs(attrs);
    }

    @Override
    protected void notifyListenersBeforeDraw(Canvas canvas) {
        super.notifyListenersBeforeDraw(canvas);

        calculateMinMaxVals();

        // this call must be AFTER the notify so that if the listener
        // is a synchronized series, it has the opportunity to
        // place a read lock on it's data.
        getRegistry().estimate(this); // TODO: clean this mechanism up!!!
    }

    /**
     * Checks whether the point is within the plot's graph area.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean containsPoint(float x, float y) {
        return getGraph().containsPoint(x, y);
    }

    /**                                                           `
     * Convenience method - wraps containsPoint(PointF).
     *
     * @param point
     * @return
     */
    public boolean containsPoint(PointF point) {
        return containsPoint(point.x, point.y);
    }

    public void setCursorPosition(PointF point) {
        getGraph().setCursorPosition(point);
    }

    public void setCursorPosition(float x, float y) {
        getGraph().setCursorPosition(x, y);
    }

    /**
     * Convert a screen xVal into a series xVal.
     * @param xPix
     * @return
     * @deprecated Use {@link #screenToSeriesY(float)}.
     */
    @Deprecated
    public Number getXVal(float xPix) {
        return getGraph().screenToSeriesX(xPix);
    }

    /**
     * Convert a screen yVal into a series yVal.
     * @param yPix
     * @return
     * @deprecated Use {@link #screenToSeriesY(float)}.
     */
    public Number getYVal(float yPix) {
        return getGraph().screenToSeriesY(yPix);
    }

    /**
     * Convert the y coord of a PointF into a series yVal.
     * @param point
     * @return
     * @deprecated Use {@link #screenToSeriesY(float)}.
     */
    @Deprecated
    public Number getYVal(PointF point) {
        return getGraph().screenToSeriesY(point);
    }

    /**
     * Convert the x coord of a PointF into a series xVal.
     * @param point
     * @return
     * @deprecated Use {@link #screenToSeriesY(float)}.
     */
    @Deprecated
    public Number getXVal(PointF point) {
        return getGraph().screenToSeriesX(point);
    }

    public Number screenToSeriesX(float x) {
        return getGraph().screenToSeriesX(x);
    }

    public Number screenToSeriesY(float y) {
        return getGraph().screenToSeriesY(y);
    }

    /**
     * Convert a series xVal into a screen x coord
     * @param x
     * @return
     */
    public float seriesToScreenX(Number x) {
        return getGraph().seriesToScreenX(x);
    }

    /**
     * Convert a series yVal into a screen y coord
     * @param y
     * @return
     */
    public float seriesToScreenY(Number y) {
        return getGraph().seriesToScreenY(y);
    }

    /**
     * Convert a series xy value into a screen point.
     * @param xy
     * @return
     */
    public PointF seriesToScreen(XYCoords xy) {
        return getGraph().seriesToScreen(xy);
    }

    /**
     * Convert a screen point into a series xy value.
     * @param point
     * @return
     */
    public XYCoords screentoSeries(PointF point) {
        return getGraph().screenToSeries(point);
    }

    public void calculateMinMaxVals() {
        prevMinX = bounds.isMinXSet() ? bounds.getMinX() : null;
        prevMaxX = bounds.isMaxXSet() ? bounds.getMaxX() : null;
        prevMinY = bounds.isMinYSet() ? bounds.getMinY() : null;
        prevMaxY = bounds.isMaxYSet() ? bounds.getMaxY() : null;

        bounds.setMinX(constraints.getMinX());
        bounds.setMaxX(constraints.getMaxX());
        bounds.setMinY(constraints.getMinY());
        bounds.setMaxY(constraints.getMaxY());

        // only calculate if we must:
        if(!bounds.isFullyDefined()) {

            RectRegion b = SeriesUtils.minMax(constraints, getRegistry().getSeriesList());

            if(!bounds.isMinXSet()) {
                bounds.setMinX(b.getMinX());
            }
            if(!bounds.isMaxXSet()) {
                bounds.setMaxX(b.getMaxX());
            }

            if(!bounds.isMinYSet()) {
                bounds.setMinY(b.getMinY());
            }
            if(!bounds.isMaxYSet()) {
                bounds.setMaxY(b.getMaxY());
            }
        }

        // at this point we now know what points are going to be visible on our
        // plot, but we still need to make corrections based on modes being used:
        // (grow, shrink etc.)
        switch (constraints.getDomainFramingModel()) {
            case ORIGIN:
                updateDomainMinMaxForOriginModel();
                break;
            case EDGE:
                bounds.setMaxX(applyUserMinMax(getCalculatedUpperBoundary(
                        constraints.getDomainUpperBoundaryMode(), prevMaxX, bounds.getMaxX()),
                        innerLimits.getMaxX(), outerLimits.getMaxX()));
                bounds.setMinX(applyUserMinMax(getCalculatedLowerBoundary(
                        constraints.getDomainLowerBoundaryMode(),
                        prevMinX, bounds.getMinX()),
                        outerLimits.getMinX(), innerLimits.getMinX()));
                break;
            default:
                throw new UnsupportedOperationException(
                        "Domain Framing Model not yet supported: " + constraints.getDomainFramingModel());
        }

        switch (constraints.getRangeFramingModel()) {
            case ORIGIN:
                updateRangeMinMaxForOriginModel();
                break;
            case EDGE:
            	if (getRegistry().size() > 0) {
                    bounds.setMaxY(applyUserMinMax(getCalculatedUpperBoundary(
                            constraints.getRangeUpperBoundaryMode(),
                            prevMaxY, bounds.getMaxY()), innerLimits.getMaxY(), outerLimits.getMaxY()));
                    bounds.setMinY(applyUserMinMax(getCalculatedLowerBoundary(
                            constraints.getRangeLowerBoundaryMode(),
                            prevMinY, bounds.getMinY()), outerLimits.getMinY(), innerLimits.getMinY()));
            	}
                break;
            default:
                throw new UnsupportedOperationException(
                        "Range Framing Model not yet supported: " + constraints.getRangeFramingModel());
        }


        calculatedOrigin.x = userDomainOrigin != null ?
                userDomainOrigin : bounds.getMinX();

        calculatedOrigin.y = this.userRangeOrigin != null ?
                userRangeOrigin : bounds.getMinY();
    }

    protected Number getCalculatedUpperBoundary(BoundaryMode mode, Number previousMax, Number calculatedMax) {
        switch (mode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(previousMax == null || calculatedMax.doubleValue() > previousMax.doubleValue())) {
                    calculatedMax = previousMax;
                }
                break;
            case SHRINK:
                if (!(previousMax == null || calculatedMax.doubleValue() < previousMax.doubleValue())) {
                    calculatedMax = previousMax;
                }
                break;
            default:
                throw new UnsupportedOperationException("BoundaryMode not supported: " + mode);
        }
        return calculatedMax;
    }

    protected Number getCalculatedLowerBoundary(BoundaryMode mode, Number previousMin, Number calculatedMin) {
        switch (mode) {
            case FIXED:
                break;
            case AUTO:
                break;
            case GROW:
                if (!(previousMin == null || calculatedMin.doubleValue() < previousMin.doubleValue())) {
                    return previousMin;
                }
                break;
            case SHRINK:
                if (!(previousMin == null || calculatedMin.doubleValue() > previousMin.doubleValue())) {
                    return previousMin;
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "BoundaryMode not supported: " + mode);
        }
        return calculatedMin;
    }

    /**
     * Apply user supplied min and max to the calculated boundary value.
     *
     * @param value
     * @param min
     * @param max
     */
    private Number applyUserMinMax(Number value, Number min, Number max) {
        value = (((min == null) || (value == null) || (value.doubleValue() > min.doubleValue()))
                ? value
                : min);
        value = (((max == null) || (value == null) || (value.doubleValue() < max.doubleValue()))
                ? value
                : max);
        return value;
    }

    /**
     * Centers the domain axis on origin.
     *
     * @param origin
     */
    public void centerOnDomainOrigin(@NonNull Number origin) {
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
    public void centerOnDomainOrigin(@NonNull Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin param cannot be null.");
        }
        constraints.setDomainFramingModel(XYFramingModel.ORIGIN);
        setUserDomainOrigin(origin);
        domainOriginExtent = extent;
        domainOriginBoundaryMode = mode;

        Number[] minMax = getOriginMinMax(domainOriginBoundaryMode, userDomainOrigin, domainOriginExtent);
        constraints.setMinX(minMax[0]);
        constraints.setMaxX(minMax[1]);
    }

    /**
     * Centers the range axis on origin.
     *
     * @param origin
     */
    public void centerOnRangeOrigin(@NonNull Number origin) {
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
    public void centerOnRangeOrigin(@NonNull Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin param cannot be null.");
        }
        constraints.setRangeFramingModel(XYFramingModel.ORIGIN);
        setUserRangeOrigin(origin);
        rangeOriginExtent = extent;
        rangeOriginBoundaryMode = mode;

        Number[] minMax = getOriginMinMax(rangeOriginBoundaryMode, userRangeOrigin, rangeOriginExtent);
        constraints.setMinY(minMax[0]);
        constraints.setMaxY(minMax[1]);
    }

    /**
     *
     * @param mode
     * @param origin
     * @param extent
     * @return result[0] is min, result[1] is max
     */
    protected Number[] getOriginMinMax(BoundaryMode mode, Number origin, Number extent) {
        if (mode == BoundaryMode.FIXED) {
            double o = origin.doubleValue();
            double e = extent.doubleValue();
            return new Number[] {o - e, o + e};
        }
        return new Number[] {null, null};
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
        double maxXDelta = distance(bounds.getMaxX().doubleValue(), origin);
        double minXDelta = distance(bounds.getMinX().doubleValue(), origin);
        double delta = maxXDelta > minXDelta ? maxXDelta : minXDelta;
        double dlb = origin - delta;
        double dub = origin + delta;
        switch (domainOriginBoundaryMode) {
            case AUTO:
                bounds.setMinX(dlb);
                bounds.setMaxX(dub);

                break;
            // if fixed, then the value already exists within "user" vals.
            case FIXED:
                break;
            case GROW: {

                if (prevMinX == null || dlb < prevMinX.doubleValue()) {
                    bounds.setMinX(dlb);
                } else {
                    bounds.setMinX(prevMinX);
                }

                if (prevMaxX == null || dub > prevMaxX.doubleValue()) {
                    bounds.setMaxX(dub);
                } else {
                    bounds.setMaxX(prevMaxX);
                }
            }
            break;
            case SHRINK:
                if (prevMinX == null || dlb > prevMinX.doubleValue()) {
                    bounds.setMinX(dlb);
                } else {
                    bounds.setMinX(prevMinX);
                }

                if (prevMaxX == null || dub < prevMaxX.doubleValue()) {
                    bounds.setMaxX(dub);
                } else {
                    bounds.setMaxX(prevMaxX);
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
                double maxYDelta = distance(bounds.getMaxY().doubleValue(), origin);
                double minYDelta = distance(bounds.getMinY().doubleValue(), origin);
                if (maxYDelta > minYDelta) {
                    bounds.setMinY(origin - maxYDelta);
                    bounds.setMaxY(origin + maxYDelta);
                } else {
                    bounds.setMinY(origin - minYDelta);
                    bounds.setMaxY(origin + minYDelta);
                }
                break;
            case FIXED:
            case GROW:
            case SHRINK:
            default:
                throw new UnsupportedOperationException(
                        "Range Origin Boundary Mode not yet supported: " + rangeOriginBoundaryMode);
        }
    }

    /**
     * Convenience method - wraps XYGraphWidget.getLinesPerRangeLabel().
     * Equivalent to getGraphWidget().getLinesPerRangeLabel().
     *
     * @return
     */
    public int getLinesPerRangeLabel() {
        return graph.getLinesPerRangeLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setLinesPerRangeLabel().
     * Equivalent to getGraphWidget().setLinesPerRangeLabel().
     *
     * @param linesPerLabel
     */
    public void setLinesPerRangeLabel(int linesPerLabel) {
        graph.setLinesPerRangeLabel(linesPerLabel);
    }

    /**
     * Convenience method - wraps XYGraphWidget.getLinesPerDomainLabel().
     * Equivalent to getGraphWidget().getLinesPerDomainLabel().
     *
     * @return
     */
    public int getLinesPerDomainLabel() {
        return graph.getLinesPerDomainLabel();
    }

    /**
     * Convenience method - wraps XYGraphWidget.setLinesPerDomainLabel().
     * Equivalent to getGraphWidget().setLinesPerDomainLabel().
     *
     * @param linesPerDomainLabel
     */
    public void setLinesPerDomainLabel(int linesPerDomainLabel) {
        graph.setLinesPerDomainLabel(linesPerDomainLabel);
    }

    public StepMode getDomainStepMode() {
        return domainStepModel.getMode();
    }

    public void setDomainStepMode(StepMode domainStepMode) {
        domainStepModel.setMode(domainStepMode);
    }

    public double getDomainStepValue() {
        return domainStepModel.getValue();
    }

    public void setDomainStepValue(double domainStepValue) {
        domainStepModel.setValue(domainStepValue);
    }

    public void setDomainStep(StepMode mode, double value) {
        setDomainStepMode(mode);
        setDomainStepValue(value);
    }

    public StepMode getRangeStepMode() {
        return rangeStepModel.getMode();
    }

    public void setRangeStepMode(StepMode rangeStepMode) {
        rangeStepModel.setMode(rangeStepMode);
    }

    public double getRangeStepValue() {
        return rangeStepModel.getValue();
    }

    public void setRangeStepValue(double rangeStepValue) {
        rangeStepModel.setValue(rangeStepValue);
    }

    public void setRangeStep(StepMode mode, double value) {
        setRangeStepMode(mode);
        setRangeStepValue(value);
    }

    public XYLegendWidget getLegend() {
        return legend;
    }

    public void setLegend(XYLegendWidget legend) {
        this.legend = legend;
    }

    public XYGraphWidget getGraph() {
        return graph;
    }

    public void setGraph(XYGraphWidget graph) {
        this.graph = graph;
    }

    public TextLabelWidget getDomainTitle() {
        return domainTitle;
    }

    public void setDomainTitle(TextLabelWidget domainTitle) {
        this.domainTitle = domainTitle;
    }

    public void setDomainLabel(String domainLabel) {
        getDomainTitle().setText(domainLabel);
    }

    public TextLabelWidget getRangeTitle() {
        return rangeTitle;
    }

    public void setRangeTitle(TextLabelWidget rangeTitle) {
        this.rangeTitle = rangeTitle;
    }

    public void setRangeLabel(String rangeLabel) {
        getRangeTitle().setText(rangeLabel);
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
        constraints.setDomainUpperBoundaryMode(mode);
    }

    protected synchronized void setUserMaxX(Number maxX) {
        constraints.setMaxX(maxX);
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
        constraints.setDomainLowerBoundaryMode(mode);
    }

    protected synchronized void setUserMinX(Number minX) {
        constraints.setMinX(minX);
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
    }

    protected synchronized void setRangeUpperBoundaryMode(BoundaryMode mode) {
        constraints.setRangeUpperBoundaryMode(mode);
    }

    protected synchronized void setUserMaxY(Number maxY) {
        constraints.setMaxY(maxY);
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
        constraints.setRangeLowerBoundaryMode(mode);
    }

    protected synchronized void setUserMinY(Number minY) {
        constraints.setMinY(minY);
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

    public XYCoords getOrigin() {
        return calculatedOrigin;
    }

    public Number getDomainOrigin() {
        return calculatedOrigin.x;
    }

    public Number getRangeOrigin() {
        return calculatedOrigin.y;
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

    @SuppressWarnings("SameParameterValue")
    protected void setDomainFramingModel(@NonNull XYFramingModel model) {
        constraints.setDomainFramingModel(model);
    }

    @SuppressWarnings("SameParameterValue")
    protected void setRangeFramingModel(@NonNull XYFramingModel model) {
        constraints.setRangeFramingModel(model);
    }

    /**
     *
     * @return The current min/max values for real domain and range that fall within the visible
     * graph space.
     */
    public RectRegion getBounds() {
        return bounds;
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
        return removeXMarkers() + removeYMarkers();
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

    public RectRegion getInnerLimits() {
        return innerLimits;
    }

    public RectRegion getOuterLimits() {
        return outerLimits;
    }

    public StepModel getDomainStepModel() {
        return domainStepModel;
    }

    public void setDomainStepModel(StepModel domainStepModel) {
        this.domainStepModel = domainStepModel;
    }

    public StepModel getRangeStepModel() {
        return rangeStepModel;
    }

    public void setRangeStepModel(StepModel rangeStepModel) {
        this.rangeStepModel = rangeStepModel;
    }

    @Override
    protected XYSeriesRegistry getRegistryInstance() {
        XYSeriesRegistry registry = new XYSeriesRegistry();
        return registry;
    }
}