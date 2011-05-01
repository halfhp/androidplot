package com.androidplot;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.TextOrientationType;
import com.androidplot.ui.widget.TitleWidget;
import com.androidplot.ui.layout.*;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.widget.DataRenderer;

import java.util.*;

/**
 * Base class for all other Plot implementations..
 */
public abstract class Plot<SeriesType extends Series, FormatterType, RendererType extends DataRenderer> extends View {

    public enum BorderStyle {
        ROUNDED,
        SQUARE,
        NONE
    }
    protected String title;
    private BoxModel boxModel = new BoxModel(3, 3, 3, 3, 3, 3, 3, 3);
    private BorderStyle borderStyle = Plot.BorderStyle.ROUNDED;
    private float borderRadiusX = 15;
    private float borderRadiusY = 15;
    private boolean drawBorderEnabled = true;
    private Paint borderPaint;

    private Paint backgroundPaint;
    private LayoutManager layoutManager;
    private TitleWidget titleWidget;
    private LinkedList<RendererType> renderers;
    private LinkedHashMap<Class, SeriesAndFormatterList<SeriesType,FormatterType>> seriesRegistry;

    private final ArrayList<PlotListener> listeners;

    {
        listeners = new ArrayList<PlotListener>();
        seriesRegistry = new LinkedHashMap<Class, SeriesAndFormatterList<SeriesType,FormatterType>>();
        renderers = new LinkedList<RendererType>();
        borderPaint = new Paint();
        borderPaint.setColor(Color.rgb(150, 150, 150));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3.3f);
        borderPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

    }



    /**
     * Required by super-class. See android.view.View.
     * @param context Android Context used to doBeforeDraw this Plot.
     */
    public Plot(Context context, String title) {
        super(context);       
        this.title = title;
        postInit();
    }

    /**
     * Required by super-class. See android.view.View.
     * @param context
     * @param attrs
     */
    public Plot(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttrs(context, attrs);
        postInit();
        //context.o
    }

    /**
     * Required by super-class. See android.view.View.
     * @param context
     * @param attrs
     * @param defStyle
     */
    public Plot(Context context, AttributeSet attrs, int
            defStyle) {
        super(context, attrs, defStyle);
        loadAttrs(context, attrs);
        postInit();
    }

    private void postInit() {
        titleWidget = new TitleWidget(this, new SizeMetrics(25, SizeLayoutType.ABSOLUTE, 100, SizeLayoutType.ABSOLUTE), TextOrientationType.HORIZONTAL);
        layoutManager = new LayoutManager();
        layoutManager.position(titleWidget, 0, XLayoutStyle.RELATIVE_TO_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
    }

    private void loadAttrs(Context context, AttributeSet attrs) {
        this.title = attrs.getAttributeValue(null, "title");
    }

    public boolean addListener(PlotListener listener) {
        synchronized (listeners) {
            return !listeners.contains(listener) && listeners.add(listener);
        }
    }

    public boolean removeListener(PlotListener listener) {
        synchronized(listeners) {
            return listeners.remove(listener);
        }
    }

    protected void notifyListeners(PlotEvent event) {
        synchronized (listeners) {
            for (PlotListener listener : listeners) {
                listener.onPlotUpdate(event);
            }
        }
    }

    /**
     * Convenience method - wraps LayoutManager.position().
     * @param element
     * @param x
     * @param xLayoutStyle
     * @param y
     * @param yLayoutStyle
     */
    public void position(Widget element, float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle) {
        layoutManager.position(element, x, xLayoutStyle, y, yLayoutStyle, AnchorPosition.LEFT_TOP);
    }

    /**
     * Convenience method - wraps LayoutManager.positon().
     * @param element
     * @param x
     * @param xLayoutStyle
     * @param y
     * @param yLayoutStyle
     * @param anchor
     */
    public void position(Widget element, float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        layoutManager.position(element, x, xLayoutStyle, y, yLayoutStyle, anchor);
    }

    /**
     * Throw an instance of IllegalArgumentException if
     * cl is not a recognized RendererType.
     * @param cl
     * @return
     */
    protected RendererType getRendererInstance(Class cl) {
        RendererType rendererInstance = doGetRendererInstance(cl);
        if(rendererInstance == null) {
            throw new IllegalArgumentException("Unrecognized Renderer: " + cl.getCanonicalName());
        } else {
            return rendererInstance;
        }
    }

    /**
     * Return null or throw instance of IllegalArgumentException if clazz is not recognized
     * as a RendererType.
     * @param clazz
     * @return
     */
    protected abstract RendererType doGetRendererInstance(Class clazz);


    /**
     * Will *immediately* throw ClassCastException if rendererClass is not an instance of RendererType.
     * @param series
     * @param rendererClass
     */
    public boolean addSeries(SeriesType series, Class rendererClass, FormatterType formatter) {
        RendererType rt = null;
        rendererClass.cast(rt);
        SeriesAndFormatterList<SeriesType, FormatterType> sfList = seriesRegistry.get(rendererClass);
        
        // if there is no list for this renderer type, we need to getInstance one:
        if(sfList == null) {
            // if rendererClass is an invalid type, getInstance will throw an IllegalArgumentException:
            // make sure there is not already an instance of this renderer available:
            if(getRenderer(rendererClass) == null) {
                renderers.add(getRendererInstance(rendererClass));
            }
            sfList = new SeriesAndFormatterList<SeriesType,FormatterType>();
            seriesRegistry.put(rendererClass, sfList);
        }

        // do nothing if this series already associated with the renderer:
        if(sfList.contains(series)) {
            return false;
        } else {
            sfList.add(series, formatter);
            return true;
        }
    }

    public boolean removeSeries(SeriesType series, Class rendererClass) {
    	
        boolean result = seriesRegistry.get(rendererClass).remove(series);
        
        if(seriesRegistry.get(rendererClass).size() <= 0) {
            seriesRegistry.remove(rendererClass);
        }
        return result;
    }

    public void removeSeries(SeriesType series) {

        for(Class rendererClass : seriesRegistry.keySet()) {
            seriesRegistry.get(rendererClass).remove(series);
        }       
        
        for(Iterator<SeriesAndFormatterList<SeriesType,FormatterType>> it = seriesRegistry.values().iterator(); it.hasNext();) {
            if(it.next().size() <= 0) {
                it.remove();
            }
        }
    }

    public boolean isEmpty() {
        return seriesRegistry.isEmpty();
    }

    public FormatterType getFormatter(SeriesType series, Class rendererClass) {
        return seriesRegistry.get(rendererClass).getFormatter(series);
        //throw new UnsupportedOperationException();
    }

    public boolean setFormatter(SeriesType series, Class rendererClass, FormatterType formatter) {
        throw new UnsupportedOperationException();
    }

  

    public SeriesAndFormatterList<SeriesType,FormatterType> getSeriesAndFormatterListForRenderer(Class rendererClass) {
        return seriesRegistry.get(rendererClass);
    }

    /**
     * Returns a list of all series assigned to the various renderers within the Plot.
     * The returned List will contain no duplicates.
     * @return
     */
    public Set<SeriesType> getSeriesSet() {
        Set<SeriesType> seriesSet = new LinkedHashSet<SeriesType>();
        for (DataRenderer renderer : getRendererList()) {
            for (SeriesType series : getSeriesListForRenderer(renderer.getClass())) {
                seriesSet.add(series);
            }
        }
        return seriesSet;
    }

    public List<SeriesType> getSeriesListForRenderer(Class rendererClass) {
        SeriesAndFormatterList<SeriesType,FormatterType> lst = seriesRegistry.get(rendererClass);
        if(lst == null) {
            return null;
        } else {
            return lst.getSeriesList();
        }
    }

    public RendererType getRenderer(Class rendererClass) {
        for(RendererType renderer : renderers) {
            if(renderer.getClass() == rendererClass) {
                return renderer;
            }
        }
        return null;
    }

    public List<RendererType> getRendererList() {
        return renderers;
        //throw new UnsupportedOperationException();
    }

    public void disableAllMarkup() {
        this.layoutManager.disableAllMarkup();
    }

    /**
     * Causes the plot to be redrawn.  Should only be called by the main (UI) thead.
     * Non-UI threads should call postRedraw().
     * @throws InterruptedException
     */
    public void redraw() {
        //redraw(true);
        invalidate();
    }

    /**
     * Invalidates the Plot's underlying View and blocks until it is redrawn.
     * For a non-blocking version of this method use postRedraw(false).
     * This method should only be called from threads that are not the primary (UI) thread.
     * Use redraw() instead for calls made from the primary thread.
     * @throws InterruptedException
     */
    public void postRedraw() throws InterruptedException {
        postRedraw(true);
    }

    public void postRedraw(boolean isBlocking) throws InterruptedException {
        synchronized (this) {
            //handler.sendMessage(handler.obtainMessage());
            postInvalidate();
            if (isBlocking) {
                wait();
            }
        }
    }

    protected abstract void doBeforeDraw();
    protected abstract void doAfterDraw();

    /**
     * Called whenever the plot needs to be drawn via the Handler, which invokes invalidate().
     * Should never be called directly.
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {

        doBeforeDraw();
        try {
            RectF canvasRect = new RectF(0, 0, getWidth(), getHeight());
            RectF marginatedRect = boxModel.getMarginatedRect(canvasRect);
            RectF paddedRect = boxModel.getPaddedRect(marginatedRect);
            if (backgroundPaint != null) {
                drawBackground(canvas, marginatedRect);
            }
            synchronized(this) {
                layoutManager.draw(canvas, canvasRect, marginatedRect, paddedRect);
            }
            if (isDrawBorderEnabled() && getBorderPaint() != null) {
                drawBorder(canvas, marginatedRect);
            }
        } catch (PlotRenderException e) {
            e.printStackTrace();
        } finally {
            doAfterDraw();
            notifyListeners(new PlotEvent(this, PlotEvent.Type.PLOT_REDRAWN));
            synchronized(this) {
                notify();
            }
        }
    }

    /**
     * Sets the visual style of the plot's border.
     * @param style
     * @param radiusX Sets the X radius for BorderStyle.ROUNDED.  Use null for all other styles.
     * @param radiusY Sets the Y radius for BorderStyle.ROUNDED.  Use null for all other styles.
     */
    public void setBorderStyle(BorderStyle style, Float radiusX, Float radiusY) {
        if (style == Plot.BorderStyle.ROUNDED) {
            if (radiusX != null || radiusY != null){
                throw new IllegalArgumentException("radiusX and radiusY cannot be null when using BorderStyle.ROUNDED");
            }
            this.borderRadiusX = radiusX;
            this.borderRadiusY = radiusY;
        }
        this.borderStyle = style;
    }

    /**
     * Draws the plot's outer border.
     * @param canvas
     * @throws PlotRenderException
     */
    protected void drawBorder(Canvas canvas, RectF dims) throws PlotRenderException {


        switch(borderStyle) {
            case ROUNDED:
                canvas.drawRoundRect(dims, borderRadiusX, borderRadiusY, borderPaint);
                break;
            case SQUARE:
                canvas.drawRect(dims, borderPaint);
                break;
            default:
                }
    }

    protected void drawBackground(Canvas canvas, RectF dims) throws PlotRenderException {
        switch(borderStyle) {
            case ROUNDED:
                canvas.drawRoundRect(dims, borderRadiusX, borderRadiusY, backgroundPaint);
                break;
            case SQUARE:
                canvas.drawRect(dims, backgroundPaint);
                break;
            default:
                }
    }

    /**
     *
     * @return The displayed title of this Plot.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title  The title to display on this Plot.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * Deprecated.  Use getBorderPaint instead.  If return is null then
     * borders are disabled.
     * @return
     */
    public boolean isDrawBorderEnabled() {
        return drawBorderEnabled;
    }

    /**
     * Deprecated - Use setBorderPaint(null) instead.
     * @param drawBorderEnabled
     */
    public void setDrawBorderEnabled(boolean drawBorderEnabled) {
        this.drawBorderEnabled = drawBorderEnabled;
    }

    public TitleWidget getTitleWidget() {
        return titleWidget;
    }

    public void setTitleWidget(TitleWidget titleWidget) {
        this.titleWidget = titleWidget;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    /**
     * Convenience method - wraps the individual setMarginXXX methods into a single method.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setPlotMargins(float left, float top, float right, float bottom) {
        setPlotMarginLeft(left);
        setPlotMarginTop(top);
        setPlotMarginRight(right);
        setPlotMarginBottom(bottom);
    }

    /**
     * Convenience method - wraps the individual setPaddingXXX methods into a single method.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setPlotPadding(float left, float top, float right, float bottom) {
        setPlotPaddingLeft(left);
        setPlotPaddingTop(top);
        setPlotPaddingRight(right);
        setPlotPaddingBottom(bottom);
    }

    public float getPlotMarginTop() {
        return boxModel.getMarginTop();
    }

    public void setPlotMarginTop(float plotMarginTop) {
        boxModel.setMarginTop(plotMarginTop);
    }

    public float getPlotMarginBottom() {
        return boxModel.getMarginBottom();
    }

    public void setPlotMarginBottom(float plotMarginBottom) {
        boxModel.setMarginBottom(plotMarginBottom);
    }

    public float getPlotMarginLeft() {
        return boxModel.getMarginLeft();
    }

    public void setPlotMarginLeft(float plotMarginLeft) {
        boxModel.setMarginLeft(plotMarginLeft);
    }

    public float getPlotMarginRight() {
        return boxModel.getMarginRight();
    }

    public void setPlotMarginRight(float plotMarginRight) {
        boxModel.setMarginRight(plotMarginRight);
    }

    public float getPlotPaddingTop() {
        return boxModel.getPaddingTop();
    }

    public void setPlotPaddingTop(float plotPaddingTop) {
        boxModel.setPaddingTop(plotPaddingTop);
    }

    public float getPlotPaddingBottom() {
        return boxModel.getPaddingBottom();
    }

    public void setPlotPaddingBottom(float plotPaddingBottom) {
        boxModel.setPaddingBottom(plotPaddingBottom);
    }

    public float getPlotPaddingLeft() {
        return boxModel.getPaddingLeft();
    }

    public void setPlotPaddingLeft(float plotPaddingLeft) {
        boxModel.setPaddingLeft(plotPaddingLeft);
    }

    public float getPlotPaddingRight() {
        return boxModel.getPaddingRight();
    }

    public void setPlotPaddingRight(float plotPaddingRight) {
        boxModel.setPaddingRight(plotPaddingRight);
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    /**
     * Set's the paint used to draw the border.  Note that this method
     * copies borderPaint and set's the copy's Paint.Style attribute to
     * Paint.Style.STROKE.
     * @param borderPaint
     */
    public void setBorderPaint(Paint borderPaint) {
        if(borderPaint == null) {
            this.borderPaint = null;
        } else {
            this.borderPaint = new Paint(borderPaint);
            this.borderPaint.setStyle(Paint.Style.STROKE);
        }
    }
}
