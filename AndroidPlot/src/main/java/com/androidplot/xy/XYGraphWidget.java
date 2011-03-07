package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
//import com.androidplot.util.Point;
import com.androidplot.ui.layout.SizeMetrics;
import com.androidplot.util.ValPixConverter;
//import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;

import java.text.DecimalFormat;
import java.text.Format;

public class XYGraphWidget extends Widget {


    public float getGridPaddingTop() {
        return gridPaddingTop;
    }



    /**
     * Will be used in a future version.
     */
    public enum XYPlotOrientation {
        HORIZONTAL,
        VERTICAL
    }

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

    private XYPlot plot;

    private Format rangeValueFormat;
    private Format domainValueFormat;

    private Paint domainOriginPaint;
    private Paint rangeOriginPaint;


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

        domainOriginPaint = new Paint();
        domainOriginPaint.setColor(Color.WHITE);
        domainOriginPaint.setAntiAlias(true);
        //domainOriginPaint.setStrokeWidth(2);

        rangeOriginPaint = new Paint();
        rangeOriginPaint.setColor(Color.WHITE);
        rangeOriginPaint.setAntiAlias(true);
        //rangeOriginPaint.setStrokeWidth(2);

        domainLabelPaint = new Paint();
        domainLabelPaint.setColor(Color.LTGRAY);
        domainLabelPaint.setAntiAlias(true);
        domainLabelPaint.setTextAlign(Paint.Align.CENTER);
        rangeLabelPaint = new Paint();
        rangeLabelPaint.setColor(Color.LTGRAY);
        rangeLabelPaint.setAntiAlias(true);
        rangeLabelPaint.setTextAlign(Paint.Align.RIGHT);
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

    private String getFormattedRangeValue(double value) {
        return rangeValueFormat.format(value);
    }

    private String getFormattedDomainValue(double value) {
        return domainValueFormat.format(value);
    }

   /* public XYSeriesRenderer getRenderer(XYRendererType type) {
        return renderers.get(type);
    }*/

   /* private void updateMinMaxVals() {
        Number minX = null;
        Number maxX = null;
        Number minY = null;
        Number maxY = null;
        for(XYSeriesRenderer renderer : renderers.values()) {
            renderer.recalculateMinMaxVals();
            if(minX == null || renderer.getActualMinX() < minX.doubleValue()) {
                minX = renderer.getActualMinX();
            }
            if(maxX == null || renderer.getActualMaxX() > maxX.doubleValue() ) {
                maxX = renderer.getActualMaxX();
            }
            if(minY == null || renderer.getActualMinY() < minY.doubleValue()) {
                minY = renderer.getActualMinY();
            }
            if(maxY == null || renderer.getActualMaxY() > maxY.doubleValue() ) {
                maxY = renderer.getActualMaxY();
            }
        }
        this.minX = minX.doubleValue();
        this.maxX = maxX.doubleValue();
        this.minY = minY.doubleValue();
        this.maxY = maxY.doubleValue();
    }*/

    /*// TODO: add a parameter to specify which renderer to associate with
    public boolean addSeries(XYDataset series, int series, XYRendererType type, XYSeriesFormatter formatter) {
        XYSeriesRenderer renderer = renderers.get(type);
        return renderer.addSeries(renderer.getBundle(series, series, formatter));
        //return lpRenderer.addSeries(new LineAndPointRenderBundle(series, series, formatter));
    }

    // TODO: same as above
    public boolean removeSeries(XYDataset series, int series, XYRendererType type) {
        return renderers.get(type).removeSeries(series, series);
    }*/

    //public void setUserDomainOrigin(Number domainValue, float position, DomainOriginStyle style) {
    //}



    @Override
    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        if (!plot.isEmpty()) {
            //plot.updateMinMaxVals();
            if (plot.getCalculatedMinX() != null &&
                    plot.getCalculatedMaxX() != null &&
                    plot.getCalculatedMinY() != null &&
                    plot.getCalculatedMaxY() != null) {
                drawGrid(canvas, widgetRect);
                //drawMarkers(canvas);
                drawData(canvas, widgetRect);
            }
        }
        //drawDataAsPath(canvas, viewSize, widgetRect);
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
    protected void drawGrid(Canvas canvas, RectF widgetRect) throws PlotRenderException {

        RectF gridRect = getGridRect(widgetRect); // used for drawing the background of the grid
        RectF paddedGridRect = getPaddedGridRect(gridRect); // used for drawing lines etc.
        canvas.drawRect(gridRect, gridBackgroundPaint);

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
            domainOriginPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawLine(domainOriginF, gridRect.top, domainOriginF, gridRect.bottom + domainLabelTickExtension, domainOriginPaint);
            canvas.drawText(getFormattedDomainValue(plot.getDomainOrigin().doubleValue()), domainOriginF, widgetRect.bottom, domainOriginPaint);
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
                        canvas.drawText(getFormattedDomainValue(xVal), xPix, widgetRect.bottom, domainLabelPaint);
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
                        canvas.drawText(getFormattedDomainValue(xVal), xPix, widgetRect.bottom, domainLabelPaint);
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
            rangeOriginPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawLine(gridRect.left - rangeLabelTickExtension, rangeOriginF, gridRect.right, rangeOriginF, rangeOriginPaint);

            canvas.drawText(getFormattedRangeValue(plot.getRangeOrigin().doubleValue()), gridRect.left - rangeLabelTickExtension, rangeOriginF, rangeOriginPaint);
        }


        // draw ticks ABOVE origin:
        {
            int i = 1;
            //float yPix = domainStep.getStepPix();
            double yVal;
            float yPix = rangeOriginF - rangeStep.getStepPix();
            for(; yPix >= paddedGridRect.top; yPix = rangeOriginF - (i * rangeStep.getStepPix())) {
            //while (domainOriginF - yPix > paddedGridRect.top) {
                //yPix = (i * domainStep.getStepPix());
                yVal = plot.getRangeOrigin().doubleValue() + i * rangeStep.getStepVal();
                if(yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                    //canvas.drawLine(yPix, widgetRect.top, yPix, (widgetRect.bottom - domainLabelWidth) + domainLabelTickExtension, gridLinePaint);
                    //canvas.drawLine((widgetRect.left + rangeLabelWidth)-rangeLabelTickExtension, yPix, widgetRect.right, yPix, gridLinePaint);
                    if(i % getTicksPerRangeLabel() == 0) {
                        //canvas.drawText(getFormattedDomainValue(yVal), yPix, widgetRect.bottom, domainLabelPaint);
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
            //float yPix = domainStep.getStepPix();
            double yVal;
            float yPix = rangeOriginF + rangeStep.getStepPix();
            for(; yPix <= paddedGridRect.bottom; yPix = rangeOriginF + (i * rangeStep.getStepPix())) {
            //while (domainOriginF - yPix > paddedGridRect.top) {
                //yPix = (i * domainStep.getStepPix());
                yVal = plot.getRangeOrigin().doubleValue() - i * rangeStep.getStepVal();
                if(yPix >= paddedGridRect.top && yPix <= paddedGridRect.bottom) {
                    //canvas.drawLine(yPix, widgetRect.top, yPix, (widgetRect.bottom - domainLabelWidth) + domainLabelTickExtension, gridLinePaint);
                    //canvas.drawLine((widgetRect.left + rangeLabelWidth)-rangeLabelTickExtension, yPix, widgetRect.right, yPix, gridLinePaint);
                    if(i % getTicksPerRangeLabel() == 0) {
                        //canvas.drawText(getFormattedDomainValue(yVal), yPix, widgetRect.bottom, domainLabelPaint);
                        canvas.drawLine(gridRect.left-rangeLabelTickExtension, yPix, gridRect.right, yPix, gridLinePaint);
                        canvas.drawText(getFormattedRangeValue(yVal), gridRect.left - rangeLabelMargin, yPix, rangeLabelPaint);
                    } else {
                        canvas.drawLine(gridRect.left, yPix, gridRect.right, yPix, gridLinePaint);
                    }
                }
                i++;
            }
        }

        // ------------------------

        //canvas.restoreToCount(canvasState);


        // vertical lines
        /*XYStep domainStep = XYStepCalculator.getStep(plot, XYAxisType.DOMAIN, paddedGridRect, plot.getCalculatedMinX().doubleValue(), plot.getCalculatedMaxX().doubleValue());
        //for (int i = 0; i <= domainStep.getStepCount(); i++) {
        for (int i = 0; i < domainStep.getStepCount(); i++) {
            //float x = (widgetRect.left + rangeLabelWidth) + i * domainStep.getStepPix();
            float x = paddedGridRect.left + i * domainStep.getStepPix();
            double val = plot.getCalculatedMinX().doubleValue() + i * domainStep.getStepVal();

            // domain tick labels:
            if (i % getTicksPerDomainLabel() == 0) {
                canvas.drawLine(x, widgetRect.top, x, (widgetRect.bottom - domainLabelWidth) + domainLabelTickExtension, gridLinePaint);
                canvas.drawText(getFormattedDomainValue(val), x, widgetRect.bottom, domainLabelPaint);
            } else {
                canvas.drawLine(x, widgetRect.top, x, widgetRect.bottom - domainLabelWidth, gridLinePaint);
            }

        }*/

        /*// horizontal lines
        double rangeStepCount;
        double rangeStepPix;
        double rangeStepVal;
        switch (plot.getRangeStepMode()) {
            case INCREMENT_BY_VAL:
            case INCREMENT_BY_PIXELS:
            case SUBDIVIDE:
                rangeStepCount = plot.getDomainStepValue();
                rangeStepPix = ((double) widgetRect.height() / rangeStepCount);
                break;
            default:
                throw new PlotRenderException("Unknown XYStepMode: " + plot.getRangeStepMode());
        }

        XYStep rangeStep = XYStepCalculator.getStep(plot, XYAxisType.RANGE, paddedGridRect, plot.getCalculatedMinY().doubleValue(), plot.getCalculatedMaxY().doubleValue());
        float yFlipped;
        for (int i = 0; i < rangeStep.getStepCount(); i++) {
            //yFlipped = ((widgetRect.bottom - domainLabelWidth) - (i * rangeStep.getStepPix()));
            yFlipped = (paddedGridRect.bottom - (i * rangeStep.getStepPix()));

            // range tick labels
            if (i % getTicksPerRangeLabel() == 0) {
                double val = plot.getCalculatedMinY().doubleValue() + i * rangeStep.getStepVal();
                canvas.drawLine((widgetRect.left + rangeLabelWidth)-rangeLabelTickExtension, yFlipped, widgetRect.right, yFlipped, gridLinePaint);
                canvas.drawText(getFormattedRangeValue(val), (widgetRect.left + rangeLabelWidth) - rangeLabelMargin, yFlipped, rangeLabelPaint);

            } else {
                canvas.drawLine(widgetRect.left + rangeLabelWidth, yFlipped, widgetRect.right, yFlipped, gridLinePaint);
            }
        }*/

        /*// draw domain origin:
        if(plot.isDrawDomainOriginEnabled() && plot.getUserDomainOrigin() != null) {
            //double domainOriginD = plot.getUserDomainOrigin().doubleValue();
            *//*float domainOriginF = ValPixConverter.valToPix(
                    domainOriginD,
                    plot.getCalculatedMinX().doubleValue(),
                    plot.getCalculatedMaxX().doubleValue(),
                    paddedGridRect.width(),
                    false);*//*


            canvas.drawLine(domainOriginF, widgetRect.top, domainOriginF, widgetRect.bottom - domainLabelWidth, domainOriginPaint);
        }*/

        /*// draw range origin:
        if(plot.isDrawRangeOriginEnabled() && plot.getUserRangeOrigin() != null) {
            double originD = plot.getUserRangeOrigin().doubleValue();
            float originF = ValPixConverter.valToPix(
                    originD,
                    plot.getCalculatedMinY().doubleValue(),
                    plot.getCalculatedMaxY().doubleValue(),
                    paddedGridRect.height(),
                    true);
            originF += paddedGridRect.top;

            canvas.drawLine(widgetRect.left + rangeLabelWidth, originF, widgetRect.right, originF, rangeOriginPaint);
        }*/
    }

    /**
     * Draws the domain and range markers, if enabled.
     * @param canvas
     */
    /*protected void drawMarkers(Canvas canvas) {
        XYPlotFormat format = plot.getFormat();
        //XYPlotLayout layout = plot.getLayout();
        //XYDatasetGroup datasets = plot.getDatasets();
        if(format.isXMarkerEnabled()) {
            float x = ValPixConverter.valToPix(format.getxMarkerVal(), plot.getActualMinX(), plot.getActualMaxX(), layout.getDataWidth(), false) + layout.getDataEdgeLeft();

            canvas.drawLine(x, layout.getDataEdgeTop(), x, layout.getDataEdgeBottom(), format.getxMarkerPaint());
        }

        if(format.isYMarkerEnabled()) {
            //try {
                float y = ValPixConverter.valToPix(format.getyMarkerVal(), plot.getActualMinY(), plot.getActualMaxY(), layout.getDataHeight(), true) + layout.getDataEdgeTop();
                canvas.drawLine(layout.getDataEdgeLeft(), y, layout.getDataEdgeRight(), y, format.getxMarkerPaint());
            //} catch (NoDataException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //}


        }
    }
*/
/**
     * Draws lines and points for each element in the series.
     * @param canvas
     * @throws PlotRenderException
     */
    protected void drawData(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        // TODO: iterate through a XYSeriesRenderer list
        //RectF gridRect = new RectF(widgetRect.left + rangeLabelWidth, widgetRect.top, widgetRect.right, widgetRect.bottom-domainLabelWidth);
        RectF gridRect = getGridRect(widgetRect); // used to calculate the paddedGridRect
        RectF paddedGridRect = getPaddedGridRect(gridRect); // used for drawing everything else
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

    /*protected void drawDataAsPath(Canvas canvas, Dimension viewSize, RectF widgetRect) throws PlotRenderException {
        XYPlotFormat format = plot.getFormat();
        //XYPlotLayout layout = plot.getLayout();
        XYDatasetGroup datasets = plot.getDatasets();

        Dimension gridSize = new Dimension(widgetRect.width() - this.rangeLabelWidth, widgetRect.height() - this.domainLabelWidth);

        for (int d = 0; d < datasets.size(); d++) {
            XYDatasetSeriesBundle bundle = datasets.get(d);
            if (bundle != null) {

                try {
                    // lock for read
                    bundle.getDataset().readLock();
                    Point lastPoint = null;
                    Point thisPoint = new Point();


                    Path path = null;
                    for (int i = 0; i < bundle.getItemCount(); i++) {
                        //System.out.println("I=" +i);

                        Number y = bundle.getY(i);
                        Number x = bundle.getX(i);
                        // make sure this point exists before we plot it:
                        if (y != null) {
                            //float pixX = ValPixConverter.indexToPix(i, bundle.getItemCount(), layout.getDataWidth()) + layout.getDataEdgeLeft();
                            float pixX = ValPixConverter.valToPix(x.doubleValue(), bundle.getActualMinX().doubleValue(), bundle.getActualMaxX().doubleValue(), gridSize.getWidth(), false) + (widgetRect.left + rangeLabelWidth);
                            float pixY = ValPixConverter.valToPix(y.doubleValue(), datasets.getActualMinY().doubleValue(), datasets.getActualMaxY().doubleValue(), gridSize.getHeight(), true) + widgetRect.top;
                            thisPoint = new Point((int) (pixX + 0.5), (int) (pixY + 0.5));

                            if(path == null) {
                                path = new Path();
                                path.moveTo(pixX, pixY);
                            } else {
                                path.lineTo(pixX, pixY);
                                path.quadTo(lastPoint.getX(), lastPoint.getY(),
                                        pixX,
                                        pixY);
                            }
                            if (bundle.getFormat().isDrawLinesEnabled() && lastPoint != null) {
                                //canvas.drawLine(lastPoint.getX(), lastPoint.getY(), thisPoint.getX(), thisPoint.getY(), bundle.getFormat().getLinePaint());
                            }
                            if (bundle.getFormat().isDrawVerticesEnabled() && bundle.getFormat().getLinePaint() != null) {
                                //System.out.println("POINT!");
                                canvas.drawPoint( pixX, pixY, bundle.getFormat().getVertexPaint());

                                //System.out.println("POINT: Xp=" + pixX + " Yp=" + pixY);
                                //System.out.println("POINT: Xv=" + x.doubleValue() + " Yv=" + y.doubleValue());
                            }
                            lastPoint = thisPoint;
                        } else {
                            lastPoint = null;
                            //path.close();
                            //path.moveTo(0, 0);
                            //Path newPath
                            //path.setLastPoint(thisPoint.getX(), thisPoint.getY());
                            canvas.drawPath(path, bundle.getFormat().getLinePaint());
                            path = null;
                        }
                    }
                    //path.close();
                    canvas.drawPath(path, bundle.getFormat().getLinePaint());
                } catch (NoDataException ex) {

                } finally {
                    bundle.getDataset().readUnlock();
                }
            }

        }
    }*/

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

    public void setGridLinePaint(Paint gridLinePaint) {
        this.gridLinePaint = gridLinePaint;
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

}
