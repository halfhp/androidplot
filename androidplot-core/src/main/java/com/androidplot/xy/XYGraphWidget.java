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

import android.content.res.*;
import android.graphics.*;

import com.androidplot.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.*;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;

/**
 * Displays graphical data (lines, points, etc.) annotated with domain and range tick markers.
 * The inner area of the graph upon which grid lines and points are rendered is called the "grid" area.
 */
public class XYGraphWidget extends Widget {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    /**
     * Fudge factor used to compensate for double<->float precision loss.
     */
    private static final float FUDGE = 0.00001f;

    private static final float DEFAULT_LINE_LABEL_TEXT_SIZE_PX = PixelUtils.spToPix(15);

    private static final int MARKER_LABEL_SPACING = TWO;

    /**
     * Line interval per range label
     */
    private int linesPerRangeLabel = ONE;

    /**
     * Line interval per domain label
     */
    private int linesPerDomainLabel = ONE;

    private Insets gridInsets = new Insets();

    /**
     * Insets defining the positioning of line labels relative to the edges of the graph
     */
    private Insets lineLabelInsets = new Insets();

    private RectF gridRect;

    private RectF labelRect;

    /**
     * how far lines extend on labeled "tick" lines
     */
    private float lineExtensionTop;
    private float lineExtensionBottom;
    private float lineExtensionLeft;
    private float lineExtensionRight;

    /**
     * Background color of the grid area
     */
    private Paint gridBackgroundPaint;

    /**
     * Color of range grid lines
     */
    private Paint rangeGridLinePaint;

    /**
     * Color of range sub-grid lines
     */
    private Paint rangeSubGridLinePaint;

    /**
     * Color of domain grid lines
     */
    private Paint domainGridLinePaint;

    /**
     * Color of domain sub-grid lines
     */
    private Paint domainSubGridLinePaint;

    private boolean isGridClippingEnabled = true;

    private Paint domainCursorPaint;
    private Paint rangeCursorPaint;
    private XYPlot plot;

    private Paint domainOriginLinePaint;
    private Paint rangeOriginLinePaint;

    private float domainCursorPosition;
    private float rangeCursorPosition;

    private boolean drawMarkersEnabled = true;
    private boolean drawGridOnTop;

    /**
     * Set of edges for which line labels should be displayed
     */
    private Set<Edge> lineLabelEdges = new HashSet<>();

    private RenderStack<? extends XYSeries, ? extends XYSeriesFormatter> renderStack;

    private CursorLabelFormatter cursorLabelFormatter;

    private HashMap<Edge, LineLabelStyle> lineLabelStyles = getDefaultLineLabelStyles();
    private HashMap<Edge, LineLabelRenderer> lineLabelRenderers = getDefaultLineLabelRenderers();

    public float getLineExtensionTop() {
        return lineExtensionTop;
    }

    public void setLineExtensionTop(float lineExtensionTop) {
        this.lineExtensionTop = lineExtensionTop;
    }

    public float getLineExtensionBottom() {
        return lineExtensionBottom;
    }

    public void setLineExtensionBottom(float lineExtensionBottom) {
        this.lineExtensionBottom = lineExtensionBottom;
    }

    public float getLineExtensionLeft() {
        return lineExtensionLeft;
    }

    public void setLineExtensionLeft(float lineExtensionLeft) {
        this.lineExtensionLeft = lineExtensionLeft;
    }

    public float getLineExtensionRight() {
        return lineExtensionRight;
    }

    public void setLineExtensionRight(float lineExtensionRight) {
        this.lineExtensionRight = lineExtensionRight;
    }

    public static class LineLabelRenderer {

        public void drawLabel(Canvas canvas, LineLabelStyle style, Number val, float x, float y, boolean isOrigin) {
            final int canvasState = canvas.save();
            try {
                final String txt = style.format.format(val.doubleValue());
                canvas.rotate(style.getRotation(), x, y);
                drawLabel(canvas, txt, style.getPaint(), x, y, isOrigin);
            } finally {
                canvas.restoreToCount(canvasState);
            }
        }

        protected void drawLabel(Canvas canvas, String text, Paint paint, float x, float y, boolean isOrigin) {
            canvas.drawText(text, x, y, paint);
        }
    }

    public static class LineLabelStyle {
        private Paint paint = new Paint();
        private float rotation = 0;
        private Format format = new DecimalFormat("0.0");

        {
            paint.setColor(Color.LTGRAY);
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(DEFAULT_LINE_LABEL_TEXT_SIZE_PX);
        }
        public Format getFormat() {
            return format;
        }

        public void setFormat(Format format) {
            this.format = format;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }
    }

    protected HashMap<Edge, LineLabelStyle> getDefaultLineLabelStyles() {
        HashMap<Edge, LineLabelStyle> defaults = new HashMap<>();
        defaults.put(Edge.TOP, new LineLabelStyle());
        defaults.put(Edge.BOTTOM, new LineLabelStyle());
        defaults.put(Edge.LEFT, new LineLabelStyle());
        defaults.put(Edge.RIGHT, new LineLabelStyle());
        return defaults;
    }

    protected HashMap<Edge, LineLabelRenderer> getDefaultLineLabelRenderers() {
        HashMap<Edge, LineLabelRenderer> defaults = new HashMap<>();
        defaults.put(Edge.TOP, new LineLabelRenderer());
        defaults.put(Edge.BOTTOM, new LineLabelRenderer());
        defaults.put(Edge.LEFT, new LineLabelRenderer());
        defaults.put(Edge.RIGHT, new LineLabelRenderer());
        return defaults;
    }

    public LineLabelRenderer getLineLabelRenderer(Edge edge) {
        return lineLabelRenderers.get(edge);
    }

    public void setLineLabelRenderer(Edge edge, LineLabelRenderer renderer) {
        lineLabelRenderers.put(edge, renderer);
    }

    public LineLabelStyle getLineLabelStyle(Edge edge) {
        return lineLabelStyles.get(edge);
    }

    public void setLineLabelStyle(Edge edge, LineLabelStyle style) {
        lineLabelStyles.put(edge, style);
    }

    public CursorLabelFormatter getCursorLabelFormatter() {
        return cursorLabelFormatter;
    }

    public void setCursorLabelFormatter(
            CursorLabelFormatter cursorLabelFormatter) {
        this.cursorLabelFormatter = cursorLabelFormatter;
    }

    public interface CursorLabelFormatter {

        /**
         *
         * @return The Paint to be used to draw the cursor text label.
         */
        Paint getTextPaint();

        /**
         *
         * @return Null if no background should be drawn,
         * the Paint used to draw the background otherwise.
         */
        Paint getBackgroundPaint();
        String getLabelText(Number x, Number y);
    }

    /**
     * Apply xml attrs
     * @param attrs
     */
    public void processAttrs(TypedArray attrs) {

        setDrawGridOnTop(attrs.getBoolean(R.styleable.xy_XYPlot_drawGridOnTop, isDrawGridOnTop()));
        int tlp = attrs.getInt(R.styleable.xy_XYPlot_lineLabels, 0);
        if(tlp != 0) {
            setLineLabelEdges(tlp);
        }

        setGridClippingEnabled(attrs.getBoolean(R.styleable.xy_XYPlot_gridClippingEnabled,
                isGridClippingEnabled()));

        final LineLabelStyle lineLabelStyleTop = getLineLabelStyle(Edge.TOP);
        final LineLabelStyle lineLabelStyleBottom = getLineLabelStyle(Edge.BOTTOM);
        final LineLabelStyle lineLabelStyleLeft = getLineLabelStyle(Edge.LEFT);
        final LineLabelStyle lineLabelStyleRight = getLineLabelStyle(Edge.RIGHT);

        lineLabelStyleTop.setRotation(attrs.getFloat(
                R.styleable.xy_XYPlot_lineLabelRotationTop,
                lineLabelStyleTop.getRotation()));

        lineLabelStyleBottom.setRotation(attrs.getFloat(
                R.styleable.xy_XYPlot_lineLabelRotationBottom,
                lineLabelStyleBottom.getRotation()));

        lineLabelStyleLeft.setRotation(attrs.getFloat(
                R.styleable.xy_XYPlot_lineLabelRotationLeft,
                lineLabelStyleLeft.getRotation()));

        lineLabelStyleRight.setRotation(attrs.getFloat(
                R.styleable.xy_XYPlot_lineLabelRotationRight,
                lineLabelStyleRight.getRotation()));

        setLineExtensionTop(attrs.getDimension(
                R.styleable.xy_XYPlot_lineExtensionTop, getLineExtensionTop()));
        setLineExtensionBottom(attrs.getDimension(
                R.styleable.xy_XYPlot_lineExtensionBottom, getLineExtensionBottom()));
        setLineExtensionLeft(attrs.getDimension(
                R.styleable.xy_XYPlot_lineExtensionLeft, getLineExtensionLeft()));
        setLineExtensionRight(attrs.getDimension(
                R.styleable.xy_XYPlot_lineExtensionRight, getLineExtensionRight()));

        AttrUtils.configureTextPaint(attrs, lineLabelStyleTop.getPaint(),
                R.styleable.xy_XYPlot_lineLabelTextColorTop,
                R.styleable.xy_XYPlot_lineLabelTextSizeTop,
                R.styleable.xy_XYPlot_lineLabelAlignTop);

        AttrUtils.configureTextPaint(attrs, lineLabelStyleBottom.getPaint(),
                R.styleable.xy_XYPlot_lineLabelTextColorBottom,
                R.styleable.xy_XYPlot_lineLabelTextSizeBottom,
                R.styleable.xy_XYPlot_lineLabelAlignBottom);

        AttrUtils.configureTextPaint(attrs, lineLabelStyleLeft.getPaint(),
                R.styleable.xy_XYPlot_lineLabelTextColorLeft,
                R.styleable.xy_XYPlot_lineLabelTextSizeLeft,
                R.styleable.xy_XYPlot_lineLabelAlignLeft);

        AttrUtils.configureTextPaint(attrs, lineLabelStyleRight.getPaint(),
                R.styleable.xy_XYPlot_lineLabelTextColorRight,
                R.styleable.xy_XYPlot_lineLabelTextSizeRight,
                R.styleable.xy_XYPlot_lineLabelAlignRight);

        AttrUtils.configureInsets(attrs, getGridInsets(),
                R.styleable.xy_XYPlot_gridInsetTop,
                R.styleable.xy_XYPlot_gridInsetBottom,
                R.styleable.xy_XYPlot_gridInsetLeft,
                R.styleable.xy_XYPlot_gridInsetRight);

        AttrUtils.configureInsets(attrs, getLineLabelInsets(),
                R.styleable.xy_XYPlot_lineLabelInsetTop,
                R.styleable.xy_XYPlot_lineLabelInsetBottom,
                R.styleable.xy_XYPlot_lineLabelInsetLeft,
                R.styleable.xy_XYPlot_lineLabelInsetRight);

        // graph size & position
        AttrUtils.configureWidget(attrs, this,
                R.styleable.xy_XYPlot_graphHeightMode, R.styleable.xy_XYPlot_graphHeight,
                R.styleable.xy_XYPlot_graphWidthMode, R.styleable.xy_XYPlot_graphWidth,
                R.styleable.xy_XYPlot_graphHorizontalPositioning, R.styleable.xy_XYPlot_graphHorizontalPosition,
                R.styleable.xy_XYPlot_graphVerticalPositioning, R.styleable.xy_XYPlot_graphVerticalPosition,
                R.styleable.xy_XYPlot_graphAnchor, R.styleable.xy_XYPlot_graphVisible);

        // domainLabel size & position
        AttrUtils.configureWidget(attrs, this,
                R.styleable.xy_XYPlot_domainTitleHeightMode, R.styleable.xy_XYPlot_domainTitleHeight,
                R.styleable.xy_XYPlot_domainTitleWidthMode, R.styleable.xy_XYPlot_domainTitleWidth,
                R.styleable.xy_XYPlot_domainTitleHorizontalPositioning, R.styleable.xy_XYPlot_domainTitleHorizontalPosition,
                R.styleable.xy_XYPlot_domainTitleVerticalPositioning, R.styleable.xy_XYPlot_domainTitleVerticalPosition,
                R.styleable.xy_XYPlot_domainTitleAnchor, R.styleable.xy_XYPlot_domainTitleVisible);

        // rangeLabel size & position
        AttrUtils.configureWidget(attrs, this,
                R.styleable.xy_XYPlot_rangeTitleHeightMode, R.styleable.xy_XYPlot_rangeTitleHeight,
                R.styleable.xy_XYPlot_rangeTitleWidthMode, R.styleable.xy_XYPlot_rangeTitleWidth,
                R.styleable.xy_XYPlot_rangeTitleHorizontalPositioning, R.styleable.xy_XYPlot_rangeTitleHorizontalPosition,
                R.styleable.xy_XYPlot_rangeTitleVerticalPositioning, R.styleable.xy_XYPlot_rangeTitleVerticalPosition,
                R.styleable.xy_XYPlot_rangeTitleAnchor, R.styleable.xy_XYPlot_rangeTitleVisible);

        // graphWidget
        AttrUtils.configureBoxModelable(attrs, this,
                R.styleable.xy_XYPlot_graphMarginTop, R.styleable.xy_XYPlot_graphMarginBottom,
                R.styleable.xy_XYPlot_graphMarginLeft, R.styleable.xy_XYPlot_graphMarginRight,
                R.styleable.xy_XYPlot_graphPaddingTop, R.styleable.xy_XYPlot_graphPaddingBottom,
                R.styleable.xy_XYPlot_graphPaddingLeft, R.styleable.xy_XYPlot_graphPaddingRight);

        // domainOriginLinePaint
        AttrUtils.configureLinePaint(attrs, getDomainOriginLinePaint(),
                R.styleable.xy_XYPlot_domainOriginLineColor,
                R.styleable.xy_XYPlot_domainOriginLineThickness);

        // rangeOriginLinePaint
        AttrUtils.configureLinePaint(attrs, getRangeOriginLinePaint(),
                R.styleable.xy_XYPlot_rangeOriginLineColor,
                R.styleable.xy_XYPlot_rangeOriginLineThickness);

        AttrUtils.configureLinePaint(attrs, getDomainGridLinePaint(),
                R.styleable.xy_XYPlot_domainLineColor,
                R.styleable.xy_XYPlot_domainLineThickness);

        AttrUtils.configureLinePaint(attrs, getRangeGridLinePaint(),
                R.styleable.xy_XYPlot_rangeLineColor,
                R.styleable.xy_XYPlot_rangeLineThickness);

        getBackgroundPaint().setColor(attrs.getColor(
                R.styleable.xy_XYPlot_graphBackgroundColor,
                getBackgroundPaint().getColor()));

        getGridBackgroundPaint().setColor(attrs.getColor(
                R.styleable.xy_XYPlot_gridBackgroundColor,
                getGridBackgroundPaint().getColor()));
    }

    /**
     * Grid insets
     */
    public Insets getGridInsets() {
        return gridInsets;
    }

    public void setGridInsets(Insets gridInsets) {
        this.gridInsets = gridInsets;
    }

    /**
     * Domain / Range label insets
     */
    public Insets getLineLabelInsets() {
        return lineLabelInsets;
    }

    public void setLineLabelInsets(Insets lineLabelInsets) {
        this.lineLabelInsets = lineLabelInsets;
    }

    public RectF getGridRect() {
        return gridRect;
    }

    public void setGridRect(RectF gridRect) {
        this.gridRect = gridRect;
    }

    public RectF getLabelRect() {
        return labelRect;
    }

    public void setLabelRect(RectF labelRect) {
        this.labelRect = labelRect;
    }

    public boolean isGridClippingEnabled() {
        return isGridClippingEnabled;
    }

    public void setGridClippingEnabled(boolean gridClippingEnabled) {
        isGridClippingEnabled = gridClippingEnabled;
    }

    public boolean isLineLabelEnabled(Edge position) {
        return lineLabelEdges.contains(position);
    }

    public void setLineLabelEdges(Edge... positions) {
        Set<Edge> positionSet = new HashSet<>();
        if(positions != null) {
            for(Edge position : positions) {
                positionSet.add(position);
            }
        }
        setLineLabelEdges(positionSet);
    }

    public void setLineLabelEdges(Set<Edge> positions) {
        this.lineLabelEdges = positions;
    }

    protected void setLineLabelEdges(int bitfield) {
        for(Edge tp : Edge.values()) {
            if((tp.value & bitfield) == tp.value) {
                lineLabelEdges.add(tp);
            }
        }
    }

    public enum Edge {
        LEFT(1),
        RIGHT(2),
        TOP(4),
        BOTTOM(8);

        private final int value;

        Edge(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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

        final Paint defaultLinePaint = new Paint();
        defaultLinePaint.setColor(Color.rgb(180, 180, 180));
        defaultLinePaint.setAntiAlias(true);
        defaultLinePaint.setStyle(Paint.Style.STROKE);

        rangeGridLinePaint = new Paint(defaultLinePaint);
        domainGridLinePaint = new Paint(defaultLinePaint);
        domainSubGridLinePaint = new Paint(defaultLinePaint);
        rangeSubGridLinePaint = new Paint(defaultLinePaint);
        domainOriginLinePaint = new Paint(defaultLinePaint);
        rangeOriginLinePaint = new Paint(defaultLinePaint);

        domainCursorPaint = new Paint();
        domainCursorPaint.setColor(Color.YELLOW);

        rangeCursorPaint = new Paint();
        rangeCursorPaint.setColor(Color.YELLOW);

        setMarginTop(7);
        setMarginRight(4);
        setMarginBottom(4);
        setClippingEnabled(true);
    }

    public XYGraphWidget(LayoutManager layoutManager, XYPlot plot, Size size) {
        super(layoutManager, size);
        this.plot = plot;
        renderStack = new RenderStack(plot);
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
        return ValPixConverter.pixToVal(yPix - gridRect.top, plot
                .getCalculatedMinY().doubleValue(), plot.getCalculatedMaxY()
                .doubleValue(), gridRect.height(), true);
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
        return ValPixConverter.pixToVal(xPix - gridRect.left, plot
                .getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX()
                .doubleValue(), gridRect.width(), false);
    }

    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect)
            throws PlotRenderException {

        if(gridRect == null) {
            gridRect = RectFUtils.applyInsets(widgetRect, gridInsets);
        }

        if(labelRect == null) {
            labelRect = RectFUtils.applyInsets(widgetRect, lineLabelInsets);
        }

        // don't draw if we have no space to draw into
        if (gridRect.height() > ZERO && gridRect.width() > ZERO) {
            if (plot.getCalculatedMinX() != null
                    && plot.getCalculatedMaxX() != null
                    && plot.getCalculatedMinY() != null
                    && plot.getCalculatedMaxY() != null) {
                if(drawGridOnTop) {
                    drawData(canvas);
                    drawGrid(canvas);
                } else {
                    drawGrid(canvas);
                    drawData(canvas);
                }
                drawCursors(canvas);
                if (isDrawMarkersEnabled()) {
                    drawMarkers(canvas);
                }
            }
        }
    }

    private void drawDomainLine(Canvas canvas, float xPix, Number xVal,
            Paint linePaint, boolean isOrigin) {

        // lines
        if (linePaint != null) {
                canvas.drawLine(xPix, gridRect.top - lineExtensionTop,
                        xPix, gridRect.bottom + lineExtensionBottom, linePaint);
        }

        // labels
        drawLineLabel(canvas, Edge.TOP, xVal, xPix, labelRect.top, isOrigin);
        drawLineLabel(canvas, Edge.BOTTOM, xVal, xPix, labelRect.bottom, isOrigin);
    }

    public void drawRangeLine(Canvas canvas, float yPix, Number yVal,
            Paint linePaint, boolean isOrigin) {
        // lines
        if (linePaint != null) {
            canvas.drawLine(gridRect.left - lineExtensionLeft, yPix,
                    gridRect.right + lineExtensionRight, yPix, linePaint);
        }

        // labels
        drawLineLabel(canvas, Edge.LEFT, yVal, labelRect.left, yPix, isOrigin);
        drawLineLabel(canvas, Edge.RIGHT, yVal, labelRect.right, yPix, isOrigin);
    }

    protected void drawLineLabel(Canvas canvas, Edge edge, Number val, float x, float y, boolean isOrigin) {
        if(isLineLabelEnabled(edge)) {
            getLineLabelRenderer(edge).drawLabel(canvas, getLineLabelStyle(edge), val, x, y, isOrigin);
        }
    }

    /**
     * Draws the drid and domain/range labels for the plot.
     *
     * @param canvas
     */
    protected void drawGrid(Canvas canvas) {
        if(!drawGridOnTop) {
            drawGridBackground(canvas);
        }

        double domainOrigin;
        if (plot.getDomainOrigin() != null) {
            double domainOriginVal = plot.getDomainOrigin().doubleValue();
            domainOrigin = ValPixConverter.valToPix(domainOriginVal, plot
                    .getCalculatedMinX().doubleValue(), plot
                    .getCalculatedMaxX().doubleValue(), gridRect.width(),
                    false);
            domainOrigin += gridRect.left;
            // if no origin is set, use the leftmost value visible on the grid:
        } else {
            domainOrigin = gridRect.left;
        }

        Step domainStep = XYStepCalculator.getStep(plot, Axis.DOMAIN,
                gridRect, plot.getCalculatedMinX().doubleValue(), plot
                        .getCalculatedMaxX().doubleValue());

        // draw domain origin:
        if (domainOrigin >= gridRect.left
                && domainOrigin <= gridRect.right) {
            drawDomainLine(canvas, (float) domainOrigin, plot.getDomainOrigin()
                    .doubleValue(), domainOriginLinePaint, true);
        }

        // draw lines LEFT of origin:
        double xPix = domainOrigin - domainStep.getStepPix();
        for (int i = ONE; xPix >= gridRect.left - FUDGE; xPix = domainOrigin
                - (i * domainStep.getStepPix())) {
            double xVal = plot.getDomainOrigin().doubleValue() - i
                    * domainStep.getStepVal();

            if (xPix <= gridRect.right) {
                final boolean isDomainTick = i% getLinesPerDomainLabel() == ZERO;
                final Paint lp = isDomainTick ? domainGridLinePaint : domainSubGridLinePaint;
                    drawDomainLine(canvas, (float) xPix, xVal, lp, false);
            }
            i++;
        }

        // draw lines RIGHT of origin:
        xPix = domainOrigin + domainStep.getStepPix();
        for (int i = ONE; xPix <= gridRect.right + FUDGE; xPix = domainOrigin
                + (i * domainStep.getStepPix())) {
            double xVal = plot.getDomainOrigin().doubleValue() + i
                    * domainStep.getStepVal();

            if (xPix >= gridRect.left) {
                    final boolean isDomainTick = i% getLinesPerDomainLabel() == ZERO;
                    final Paint lp = isDomainTick ? domainGridLinePaint : domainSubGridLinePaint;
                    drawDomainLine(canvas, (float) xPix, xVal, lp, false);
            }
            i++;
        }

        double rangeOrigin;
        if (plot.getRangeOrigin() != null) {
            double rangeOriginD = plot.getRangeOrigin().doubleValue();
            rangeOrigin = ValPixConverter.valToPix(rangeOriginD, plot
                    .getCalculatedMinY().doubleValue(), plot
                    .getCalculatedMaxY().doubleValue(),
                    gridRect.height(), true);
            rangeOrigin += gridRect.top;
            // if no origin is set, use the leftmost value visible on the grid
        } else {
            rangeOrigin = gridRect.bottom;
        }

        Step rangeStep = XYStepCalculator.getStep(plot, Axis.RANGE,
                gridRect, plot.getCalculatedMinY().doubleValue(), plot
                        .getCalculatedMaxY().doubleValue());

        // draw range origin:
        if (rangeOrigin >= gridRect.top && rangeOrigin <= gridRect.bottom) {
            drawRangeLine(canvas, (float) rangeOrigin, plot.getRangeOrigin()
                    .doubleValue(), rangeOriginLinePaint, true);
        }

        final double rangeStepPix = rangeStep.getStepPix();

        // draw lines ABOVE origin:
        double yPix = rangeOrigin - rangeStep.getStepPix();
        for (int i = ONE; yPix >= gridRect.top - FUDGE; yPix = rangeOrigin - (i * rangeStepPix)) {
            double yVal = plot.getRangeOrigin().doubleValue() + i
                    * rangeStep.getStepVal();

            if (yPix <= gridRect.bottom) {
                final boolean isRangeTick = i% getLinesPerRangeLabel() == ZERO;
                final Paint lp = isRangeTick ? rangeGridLinePaint : rangeSubGridLinePaint;
                drawRangeLine(canvas, (float)yPix, yVal, lp, false);
            }
            i++;
        }

        // draw lines BENEATH origin:
        yPix = rangeOrigin + rangeStep.getStepPix();
        for (int i = ONE; yPix <= gridRect.bottom + FUDGE; yPix = rangeOrigin + (i * rangeStepPix)) {
            double yVal = plot.getRangeOrigin().doubleValue() - i
                    * rangeStep.getStepVal();
            if (yPix >= gridRect.top) {
                final boolean isRangeTick = i% getLinesPerRangeLabel() == ZERO;
                final Paint lp = isRangeTick ? rangeGridLinePaint : rangeSubGridLinePaint;
                drawRangeLine(canvas, (float)yPix, yVal, lp, false);
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
        x += MARKER_LABEL_SPACING;
        y -= MARKER_LABEL_SPACING;
        RectF textRect = new RectF(FontUtils.getStringDimensions(text,
                marker.getTextPaint()));
        textRect.offsetTo(x, y - textRect.height());

        if (textRect.right > gridRect.right) {
            textRect.offset(-(textRect.right - gridRect.right), ZERO);
        }

        if (textRect.top < gridRect.top) {
            textRect.offset(0, gridRect.top - textRect.top);
        }

        canvas.drawText(text, textRect.left, textRect.bottom,
                marker.getTextPaint());
    }

    protected void drawMarkers(Canvas canvas) {
        for (YValueMarker marker : plot.getYValueMarkers()) {
            if (marker.getValue() != null) {
                double yVal = marker.getValue().doubleValue();
                float yPix = (float) ValPixConverter.valToPix(yVal, plot
                        .getCalculatedMinY().doubleValue(), plot
                        .getCalculatedMaxY().doubleValue(), gridRect.height(), true);
                yPix += gridRect.top;
                canvas.drawLine(gridRect.left, yPix,
                        gridRect.right, yPix, marker.getLinePaint());

                float xPix = marker.getTextPosition().getPixelValue(
                        gridRect.width());
                xPix += gridRect.left;

                if (marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                }
            }
        }

        for (XValueMarker marker : plot.getXValueMarkers()) {
            if (marker.getValue() != null) {
                double xVal = marker.getValue().doubleValue();
                float xPix = (float) ValPixConverter.valToPix(xVal, plot
                        .getCalculatedMinX().doubleValue(), plot
                        .getCalculatedMaxX().doubleValue(), gridRect.width(), false);
                xPix += gridRect.left;
                canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom, marker.getLinePaint());
                float yPix = marker.getTextPosition().getPixelValue(gridRect.height());
                yPix += gridRect.top;
                if (marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                }
            }
        }
    }

    protected void drawCursors(Canvas canvas) {
        boolean hasDomainCursor = false;
        // draw the domain cursor:
        if (domainCursorPaint != null
                && domainCursorPosition <= gridRect.right
                && domainCursorPosition >= gridRect.left) {
            hasDomainCursor = true;
            canvas.drawLine(domainCursorPosition, gridRect.top,
                    domainCursorPosition, gridRect.bottom,
                    domainCursorPaint);
        }

        boolean hasRangeCursor = false;
        // draw the range cursor:
        if (rangeCursorPaint != null
                && rangeCursorPosition >= gridRect.top
                && rangeCursorPosition <= gridRect.bottom) {
            hasRangeCursor = true;
            canvas.drawLine(gridRect.left, rangeCursorPosition,
                    gridRect.right, rangeCursorPosition, rangeCursorPaint);
        }

        if(getCursorLabelFormatter() != null && hasRangeCursor && hasDomainCursor) {
            final String label = getCursorLabelFormatter().
                    getLabelText(getDomainCursorVal(), getRangeCursorVal());

            // convert the label dimensions rect into floating-point:
            RectF cursorRect = new RectF(FontUtils.getPackedStringDimensions(
                    label, getCursorLabelFormatter().getTextPaint()));
            cursorRect.offsetTo(domainCursorPosition, rangeCursorPosition
                    - cursorRect.height());

            // if we are too close to the right edge of the plot, we will move
            // the label to the left side of our cursor:
            if (cursorRect.right >= gridRect.right) {
                cursorRect.offsetTo(domainCursorPosition - cursorRect.width(),
                        cursorRect.top);
            }

            // same thing for the top edge of the plot:
            // dunno why but these rects can have negative values for top and bottom.
            if (cursorRect.top <= gridRect.top) {
                cursorRect.offsetTo(cursorRect.left, rangeCursorPosition);
            }

            if (getCursorLabelFormatter().getBackgroundPaint() != null) {
                canvas.drawRect(cursorRect, getCursorLabelFormatter().getBackgroundPaint());
            }

            canvas.drawText(label, cursorRect.left, cursorRect.bottom,
                    getCursorLabelFormatter().getTextPaint());
        }
    }

    protected void drawGridBackground(Canvas canvas) {
        if(gridBackgroundPaint != null) {
            canvas.drawRect(gridRect, gridBackgroundPaint);
        }
    }

    /**
     * Draws lines and points for each element in the series.
     *
     * @param canvas
     * @throws PlotRenderException
     */
    protected void drawData(Canvas canvas) throws PlotRenderException {
        if (drawGridOnTop) {
            drawGridBackground(canvas);
        }
        try {
            if(isGridClippingEnabled) {
                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.clipRect(gridRect, android.graphics.Region.Op.INTERSECT);
            }

            renderStack.sync();

            for(RenderStack.StackElement thisElement : renderStack.getElements()) {
                if(thisElement.isEnabled()) {
                    Class<? extends XYSeriesRenderer> rendererClass =
                            thisElement.get().getFormatter().getRendererClass();
                    plot.getRenderer(rendererClass).render(
                            canvas, gridRect, thisElement.get(), renderStack);
                }
            }

        } finally {
            if(isGridClippingEnabled) {
                canvas.restore();
            }
        }
    }

    protected void drawPoint(Canvas canvas, PointF point, Paint paint) {
        canvas.drawPoint(point.x, point.y, paint);
    }

    public Paint getGridBackgroundPaint() {
        return gridBackgroundPaint;
    }

    public void setGridBackgroundPaint(Paint gridBackgroundPaint) {
        this.gridBackgroundPaint = gridBackgroundPaint;
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

    public int getLinesPerRangeLabel() {
        return linesPerRangeLabel;
    }

    public void setLinesPerRangeLabel(int linesPerRangeLabel) {
        this.linesPerRangeLabel = linesPerRangeLabel;
    }

    public int getLinesPerDomainLabel() {
        return linesPerDomainLabel;
    }

    public void setLinesPerDomainLabel(int linesPerDomainLabel) {
        this.linesPerDomainLabel = linesPerDomainLabel;
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

    public boolean isDrawGridOnTop() {
        return drawGridOnTop;
    }

    public void setDrawGridOnTop(boolean drawGridOnTop) {
        this.drawGridOnTop = drawGridOnTop;
    }

    public boolean isDrawMarkersEnabled() {
        return drawMarkersEnabled;
    }

    public void setDrawMarkersEnabled(boolean drawMarkersEnabled) {
        this.drawMarkersEnabled = drawMarkersEnabled;
    }

    /**
     * Checks whether the point exists within the visible grid space.
     *
     * @param x
     * @param y
     * @return
     * @since 1.0
     */
    public boolean containsPoint(float x, float y) {
        if (gridRect != null) {
            return gridRect.contains(x, y);
        }
        return false;
    }
}