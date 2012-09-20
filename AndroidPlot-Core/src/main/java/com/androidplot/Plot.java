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

package com.androidplot;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.androidplot.series.Series;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.*;
import com.androidplot.ui.Formatter;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.TitleWidget;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.DataRenderer;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.YLayoutStyle;

import java.util.*;

/**
 * Base class for all other Plot implementations..
 */
public abstract class Plot<SeriesType extends Series, FormatterType extends Formatter, RendererType extends DataRenderer>
        extends View implements Resizable{

    private static final String ATTR_TITLE = "title";

    /*public RectF getCanvasRect() {
        return canvasRect;
    }

    public RectF getMarginatedRect() {
        return marginatedRect;
    }

    public RectF getPaddedRect() {
        return paddedRect;
    }*/

    public DisplayDimensions getDisplayDimensions() {
        return displayDims;
    }

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


    private DisplayDimensions displayDims = new DisplayDimensions();
    /*// we initialize here to avoid null check in onDraw().
    private RectF canvasRect = new RectF(0,0,0,0);
    private RectF marginatedRect = new RectF(0,0,0,0);
    private RectF paddedRect = new RectF(0,0,0,0);*/

    /**
     * Used for caching renderer instances.  Note that once a renderer is initialized it remains initialized
     * for the life of the application; does not and should not be destroyed until the application exits.
     */
    private LinkedList<RendererType> renderers;

    /**
     * Associates lists series and formatter pairs with the class of the Renderer used to render them.
     */
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

    /**
     * Can be overridden by derived classes to control hardware acceleration state.
     * Note that this setting is only used on Honeycomb and later environments.
     * @return True if hardware acceleration is allowed, false otherwise.
     */
    protected boolean isHwAccelerationSupported() {
        return false;
    }

    private void postInit() {
        //if(!isHwAccelerationSupported()) {
           //if(getLayerType() != View.LAYER_TYPE_SOFTWARE)
           // setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //}


        titleWidget = new TitleWidget(this, new SizeMetrics(25, SizeLayoutType.ABSOLUTE, 100, SizeLayoutType.ABSOLUTE), TextOrientationType.HORIZONTAL);
        layoutManager = new LayoutManager();
        layoutManager.position(titleWidget, 0, XLayoutStyle.RELATIVE_TO_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
    }

    /**
     * Parse XML Attributes
     * @param context
     * @param attrs
     */
    private void loadAttrs(Context context, AttributeSet attrs) {

        String titleAttr = attrs.getAttributeValue(null, ATTR_TITLE);
        String[] split = titleAttr.split("/");

        // is this a localized resource?
        if (split[0].equalsIgnoreCase("@string")) {
            String pack = split[0].replace("@", "");
            String name = split[1];
            int id = context.getResources().getIdentifier(name, pack, context.getPackageName());
            this.title = context.getResources().getString(id);
        } else {
            this.title = titleAttr;
        }
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

    protected void notifyListenersBeforeDraw(Canvas canvas) {
        synchronized (listeners) {
            for (PlotListener listener : listeners) {
                listener.onBeforeDraw(this, canvas);
            }
        }
    }

    protected void notifyListenersAfterDraw(Canvas canvas, PlotEvent event) {
        synchronized (listeners) {
            for (PlotListener listener : listeners) {
                listener.onAfterDraw(this, canvas);
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
     * Return null or throw instance of IllegalArgumentException if clazz is not recognized
     * as a RendererType.
     * @param clazz
     * @return
     */
    //protected abstract RendererType doGetRendererInstance(Class clazz);



    /**
     * @param series
     */
    public synchronized boolean addSeries(SeriesType series, FormatterType formatter) {
        //RendererType rt = null;
        //rendererClass.cast(rt);
        Class rendererClass = formatter.getRendererClass();
        SeriesAndFormatterList<SeriesType, FormatterType> sfList = seriesRegistry.get(rendererClass);
        
        // if there is no list for this renderer type, we need to getInstance one:
        if(sfList == null) {
            // if rendererClass is an invalid type, getInstance will throw an IllegalArgumentException:
            // make sure there is not already an instance of this renderer available:
            if(getRenderer(rendererClass) == null) {
                renderers.add((RendererType)formatter.getRendererInstance(this));
                //renderers.add(getRendererInstance(rendererClass));
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

    public synchronized boolean removeSeries(SeriesType series, Class rendererClass) {
    	
        boolean result = seriesRegistry.get(rendererClass).remove(series);
        
        if(seriesRegistry.get(rendererClass).size() <= 0) {
            seriesRegistry.remove(rendererClass);
        }
        return result;
    }

    /**
     * Remove all occorrences of series from all renderers
     * @param series
     */
    public synchronized void removeSeries(SeriesType series) {

        // remove all occurrences of series from all renderers:
        for(Class rendererClass : seriesRegistry.keySet()) {
            seriesRegistry.get(rendererClass).remove(series);
        }       

        // remove empty SeriesAndFormatterList instances from the registry:
        for(Iterator<SeriesAndFormatterList<SeriesType,FormatterType>> it = seriesRegistry.values().iterator(); it.hasNext();) {
            if(it.next().size() <= 0) {
                it.remove();
            }
        }
    }

    /**
     * Remove all series from all renderers
     */
    public void clear() {
        for(Iterator<SeriesAndFormatterList<SeriesType,FormatterType>> it = seriesRegistry.values().iterator(); it.hasNext();) {
            it.next();
            it.remove();
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
            List<SeriesType> seriesList = getSeriesListForRenderer(renderer.getClass());
            if (seriesList != null) {
                for (SeriesType series : seriesList) {
                    seriesSet.add(series);
                }
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

    /**
     * @deprecated Since 0.5.1 - Users should transition to using {@link #addListener(PlotListener)}
     */
    @Deprecated
    protected abstract void doBeforeDraw();

    /**
     * @deprecated Since 0.5.1 - Users should transition to using {@link #addListener(PlotListener)}
     */
    @Deprecated
    protected abstract void doAfterDraw();

    @Override
    public synchronized void layout(final DisplayDimensions dims) {
        displayDims = dims;
        layoutManager.layout(displayDims);
    }

    @Override
    protected synchronized void onSizeChanged (int w, int h, int oldw, int oldh) {

        // disable hardware acceleration if it's not explicitly supported
        // by the current Plot implementation. this check only applies to
        // honeycomb and later environments.
        if (Build.VERSION.SDK_INT >= 11) {
            if (!isHwAccelerationSupported() && isHardwareAccelerated()) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }

        RectF cRect = new RectF(0, 0, getWidth(), getHeight());
        RectF mRect = boxModel.getMarginatedRect(cRect);
        RectF pRect = boxModel.getPaddedRect(mRect);

        layout(new DisplayDimensions(cRect, mRect, pRect));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Called whenever the plot needs to be drawn via the Handler, which invokes invalidate().
     * Should never be called directly.
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {

        doBeforeDraw();
        notifyListenersBeforeDraw(canvas);
        try {

            //validateDimensions(canvas);
            if (backgroundPaint != null) {
                drawBackground(canvas, displayDims.marginatedRect);
            }
            synchronized(this) {
                layoutManager.draw(canvas);
            }
            if (isDrawBorderEnabled() && getBorderPaint() != null) {
                drawBorder(canvas, displayDims.marginatedRect);
            }
        } catch (PlotRenderException e) {
            e.printStackTrace();
        } finally {
            doAfterDraw();
            notifyListenersAfterDraw(canvas, new PlotEvent(this, PlotEvent.Type.PLOT_REDRAWN));
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
            if (radiusX == null || radiusY == null){
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
