package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
//import com.androidplot.util.Point;
import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.util.FontUtils;
import com.androidplot.util.ValPixConverter;
//import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;

import java.text.DecimalFormat;
import java.text.Format;

public class XYGraphWidget extends Widget {
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

    /**
     * Will be used in a future version.
     */
    public enum XYPlotOrientation {
        HORIZONTAL,
        VERTICAL
    }

    private static final int CURSOR_LABEL_SPACING = 2;  // space between cursor lines and label in pixels
    private float domainLabelWidth = 15;  // how many pixels is the area allocated for domain labels
    private float rangeLabelWidth = 41;  // ...

    private float domainLabelMargin = 3;
    private float rangeLabelMargin = 5;  // not currently used since this margin can be adjusted via rangeLabelWidth

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


 /*   private double minX;
    private double maxX;
    private double minY;
    private double maxY;*/


    {
        gridBackgroundPaint = new Paint();
        //gridBackgroundPaint.setColor(Color.rgb(100, 100, 100));
        gridBackgroundPaint.setColor(Color.rgb(140, 140, 140));
        gridBackgroundPaint.setStyle(Paint.Style.FILL);

        gridLinePaint = new Paint();
        gridLinePaint.setColor(Color.rgb(180, 180, 180));
        //gridLinePaint.setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        gridLinePaint.setAntiAlias(true);
        gridLinePaint.setStyle(Paint.Style.STROKE);

        domainOriginLinePaint = new Paint();
        domainOriginLinePaint.setColor(Color.WHITE);
        domainOriginLinePaint.setAntiAlias(true);
        //domainOriginLinePaint.setStrokeWidth(2);

        rangeOriginLinePaint = new Paint();
        rangeOriginLinePaint.setColor(Color.WHITE);
        rangeOriginLinePaint.setAntiAlias(true);
        //rangeOriginLinePaint.setStrokeWidth(2);

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

        //renderers = new TreeMap<XYRendererType, XYSeriesRenderer>();

        rangeValueFormat = new DecimalFormat("0.0");
        domainValueFormat = new DecimalFormat("0.0");
    }


    public XYGraphWidget(XYPlot plot, SizeMetrics sizeMetrics) {
        super(sizeMetrics);
        //super(new SizeMetrics(0, SizeLayoutType.ABSOLUTE, 0, SizeLayoutType.ABSOLUTE));
        this.plot = plot;

        //renderers.put(XYRendererType.LINE_AND_POINT, new LineAndPointRenderer(plot));
        //lpRenderer = new LineAndPointRenderer(plot);
        //this.setWidth(titlePaint.measureText(plot.getTitle()));
        //this.setHeight(titlePaint.getFontMetrics().top);
        //setSize(sizeMetrics);
        //this.orientation = orientation;
    }


    /**
     * Returns a RectF representing the grid area last drawn
     * by this plot.
     * @return
     */
    public RectF getGridRect() {
        return paddedGridRect;
    }
    private String getFormattedRangeValue(double value) {
        return rangeValueFormat.format(value);
    }

    private String getFormattedDomainValue(double value) {
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
        if (!plot.isEmpty()) {

            if (plot.getCalculatedMinX() != null &&
                    plot.getCalculatedMaxX() != null &&
                    plot.getCalculatedMinY() != null &&
                    plot.getCalculatedMaxY() != null) {
                drawGrid(canvas);
                drawData(canvas);
                drawCursors(canvas);
            }
        }
    }

    private RectF getGridRect(RectF widgetRect) {
        return new RectF(widgetRect.left + rangeLabelWidth, widgetRect.top, widgetRect.right, widgetRect.bottom - domainLabelWidth);
    }

    private RectF getPaddedGridRect(RectF gridRect) {
        return new RectF(gridRect.left + gridPaddingLeft, gridRect.top + gridPaddingTop, gridRect.right - gridPaddingRight, gridRect.bottom - gridPaddingBottom);
    }

    /**
     * Draws the drid and domain/range labels for the plot.
     * @param canvas
     * @throws com.androidplot.exception.PlotRenderException
     */
    protected void drawGrid(Canvas canvas) throws PlotRenderException {

        //RectF gridRect = getGridRect(widgetRect); // used for drawing the background of the grid
        //paddedGridRect = getPaddedGridRect(gridRect); // used for drawing lines etc.
        if(gridBackgroundPaint != null) {
            canvas.drawRect(gridRect, gridBackgroundPaint);
        }

        float domainOriginF;
        //double domainOriginVal;
        if (plot.getDomainOrigin() != null) {
            // --------- NEW WAY ------
            double domainOriginVal = plot.getDomainOrigin().doubleValue();
            domainOriginF = ValPixConverter.valToPix(
                    domainOriginVal,
                    plot.getCalculatedMinX().doubleValue(),
                    plot.getCalculatedMaxX().doubleValue(),
                    paddedGridRect.width(),
                    false);
            domainOriginF += paddedGridRect.left;
            // if no origin is set, use the leftmost value visible on the grid
        } else {
            domainOriginF = paddedGridRect.left;
            //domainOriginVal = plot.getCalculatedMinX().doubleValue();
        }

        XYStep domainStep = XYStepCalculator.getStep(plot, XYAxisType.DOMAIN, paddedGridRect, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue());

        //int canvasState = canvas.save();
        //canvas.clipRect(paddedGridRect, Region.Op.REPLACE);

        // draw domain origin:
        if (domainOriginF >= paddedGridRect.left && domainOriginF <= paddedGridRect.right) {
            domainOriginLinePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawLine(domainOriginF, gridRect.top, domainOriginF, gridRect.bottom + domainLabelTickExtension, domainOriginLinePaint);
            canvas.drawText(getFormattedDomainValue(plot.getDomainOrigin().doubleValue()), domainOriginF, getOutlineRect().bottom, domainOriginLabelPaint);
        }

        // draw ticks LEFT of origin:
        {
            int i = 1;
            //float xPix = domainStep.getStepPix();
            double xVal;
            float xPix = domainOriginF - domainStep.getStepPix();
            for(; xPix >= paddedGridRect.left; xPix = domainOriginF - (i * domainStep.getStepPix())) {
            //while (domainOriginF - xPix > paddedGridRect.top) {
                //xPix = (i * domainStep.getStepPix());
                xVal = plot.getDomainOrigin().doubleValue() - i * domainStep.getStepVal();
                if(xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {
                    if(i % getTicksPerDomainLabel() == 0) {
                        canvas.drawText(getFormattedDomainValue(xVal), xPix, getOutlineRect().bottom, domainLabelPaint);
                        canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom + domainLabelTickExtension, gridLinePaint);
                    } else {
                        canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom, gridLinePaint);
                    }
                }
                i++;
            }
        }

        // draw ticks RIGHT of origin:
        {
            int i = 1;
            //float xPix = domainStep.getStepPix();
            double xVal;
            float xPix = domainOriginF + domainStep.getStepPix();
            for(; xPix <= paddedGridRect.right; xPix = domainOriginF + (i * domainStep.getStepPix())) {
            //while (domainOriginF - xPix > paddedGridRect.top) {
                //xPix = (i * domainStep.getStepPix());
                xVal = plot.getDomainOrigin().doubleValue() + i * domainStep.getStepVal();
                if(xPix >= paddedGridRect.left && xPix <= paddedGridRect.right) {

                    if(i % getTicksPerDomainLabel() == 0) {
                        canvas.drawText(getFormattedDomainValue(xVal), xPix, getOutlineRect().bottom, domainLabelPaint);
                        canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom + domainLabelTickExtension, gridLinePaint);
                    } else {
                        canvas.drawLine(xPix, gridRect.top, xPix, gridRect.bottom, gridLinePaint);
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

        //int canvasState = canvas.save();
        //canvas.clipRect(paddedGridRect, Region.Op.REPLACE);

        // draw range origin:
        if (rangeOriginF >= paddedGridRect.top && rangeOriginF <= paddedGridRect.bottom) {
            rangeOriginLinePaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawLine(gridRect.left - rangeLabelTickExtension, rangeOriginF, gridRect.right, rangeOriginF, rangeOriginLinePaint);

            canvas.drawText(getFormattedRangeValue(plot.getRangeOrigin().doubleValue()), gridRect.left - rangeLabelTickExtension, rangeOriginF, rangeOriginLabelPaint);
        }


        // draw ticks ABOVE origin:
        {
            int i = 1;
            //float yPix = domainStep.getStepPix();
            double yVal;
            float yPix = rangeOriginF - rangeStep.getStepPix();
            for(; yPix >= paddedGridRect.top; yPix = rangeOriginF - (i * rangeStep.getStepPix())) {
                yVal = plot.getRangeOrigin().doubleValue() + i * rangeStep.getStepVal();
                if(yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                    if(i % getTicksPerRangeLabel() == 0) {                        
                        canvas.drawLine(gridRect.left-rangeLabelTickExtension, yPix, gridRect.right, yPix, gridLinePaint);
                        canvas.drawText(getFormattedRangeValue(yVal), gridRect.left - rangeLabelMargin, yPix, rangeLabelPaint);
                    } else {
                        canvas.drawLine(gridRect.left, yPix, gridRect.right, yPix, gridLinePaint);
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
                        canvas.drawLine(gridRect.left-rangeLabelTickExtension, yPix, gridRect.right, yPix, gridLinePaint);
                        canvas.drawText(getFormattedRangeValue(yVal), gridRect.left - rangeLabelMargin, yPix, rangeLabelPaint);
                    } else {
                        canvas.drawLine(gridRect.left, yPix, gridRect.right, yPix, gridLinePaint);
                    }
                }
                i++;
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

            //Rect cr = FontUtils.getPackedStringDimensions(label, cursorLabelPaint);

            // convert the label dimensions rect into floating-point:
            RectF cursorRect = new RectF(FontUtils.getPackedStringDimensions(label, cursorLabelPaint));
            cursorRect.offsetTo(domainCursorPosition, rangeCursorPosition - cursorRect.height());

            // if we are too close to the right edge of the plot, we will move the
            // label to the left side of our cursor:
            //float xpos = domainCursorPosition + CURSOR_LABEL_SPACING;
            //float labelEdgeRight = domainCursorPosition + cursorRect.width();
            if(cursorRect.right >= paddedGridRect.right) {
                //xpos = paddedGridRect.right - cursorRect.right;
                //xpos = (domainCursorPosition - cursorRect.width()) - CURSOR_LABEL_SPACING;
                cursorRect.offsetTo(domainCursorPosition - cursorRect.width(), cursorRect.top);
            }

            // same thing for the top edge of the plot:
            //float ypos = rangeCursorPosition - CURSOR_LABEL_SPACING;
            // dunno why but these rects can have negative values for top and bottom.
            //float labelEdgeTop = rangeCursorPosition - cursorRect.height();
            if(cursorRect.top <= paddedGridRect.top) {
                //ypos = rangeCursorPosition + cursorRect.height() + CURSOR_LABEL_SPACING;
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
        //RectF gridRect = new RectF(widgetRect.left + rangeLabelWidth, widgetRect.top, widgetRect.right, widgetRect.bottom-domainLabelWidth);
        //RectF gridRect = getGridRect(widgetRect); // used to calculate the paddedGridRect
        //RectF paddedGridRect = getPaddedGridRect(gridRect); // used for drawing everything else
        //this.lpRenderer.render(canvas, gridRect);

        int canvasState = canvas.save();
        canvas.clipRect(gridRect, Region.Op.REPLACE);
        for(XYSeriesRenderer renderer : plot.getRendererList()) {
            renderer.render(canvas, paddedGridRect);
        }
        canvas.restoreToCount(canvasState);
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

    public float getDomainLabelMargin() {
        return domainLabelMargin;
    }

    public void setDomainLabelMargin(float domainLabelMargin) {
        this.domainLabelMargin = domainLabelMargin;
    }

    public float getRangeLabelMargin() {
        return rangeLabelMargin;
    }

    public void setRangeLabelMargin(float rangeLabelMargin) {
        this.rangeLabelMargin = rangeLabelMargin;
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
    //LineAndPointRenderer lpRenderer;

    //private TreeMap<XYRendererType, XYSeriesRenderer> renderers;

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

    /*
    public Collection<XYSeriesRenderer> getRenderers() {
        return renderers.values();
    }
    */

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

    /**
     * Deprecated - use getTicksPerDomainLabel() instead.
     * @return
     */
    @Deprecated
    public int getDomainTicksPerLabel() {
        return ticksPerDomainLabel;
    }

    /**
     * Deprecated - use setTicksPerDomainLabel() instead.
     * @param domainTicksPerLabel
     */
    @Deprecated
    public void setDomainTicksPerLabel(int domainTicksPerLabel) {
        this.ticksPerDomainLabel = domainTicksPerLabel;
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
        setCursorPosition(point.x,  point.y);
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

}
