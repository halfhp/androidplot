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
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.*;
import com.androidplot.ui.Formatter;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.ui.widget.Widget;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.Configurator;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.PixelUtils;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;

import java.util.*;

/**
 * Base class for all other Plot implementations..
 */
public abstract class Plot<SeriesType extends Series, FormatterType extends Formatter, RendererType extends SeriesRenderer>
        extends View implements Resizable{
    private static final String TAG = Plot.class.getName();
    private static final String XML_ATTR_PREFIX      = "androidplot";

    private static final String ATTR_TITLE           = "title";
    private static final String ATTR_RENDER_MODE     = "renderMode";

    public DisplayDimensions getDisplayDimensions() {
        return displayDims;
    }

    public enum BorderStyle {
        ROUNDED,
        SQUARE,
        NONE
    }

    /**
     * The RenderMode used by a Plot to draw it's self onto the screen.  The RenderMode can be set
     * in two ways.
     *
     * In an xml layout:
     *
     * <code>
     * <com.androidplot.xy.XYPlot
     * android:id="@+id/mySimpleXYPlot"
     * android:layout_width="fill_parent"
     * android:layout_height="fill_parent"
     * title="@string/sxy_title"
     * renderMode="useBackgroundThread"/>
     * </code>
     *
     * Programatically:
     *
     * <code>
     * XYPlot myPlot = new XYPlot(context "MyPlot", Plot.RenderMode.USE_MAIN_THREAD);
     * </code>
     *
     * A Plot's  RenderMode cannot be changed after the plot has been initialized.
     * @since 0.5.1
     */
    public enum RenderMode {
        /**
         * Use a second thread and an off-screen buffer to do drawing.  This is the preferred method
         * of drawing dynamic data and static data that consists of a large number of points.  This mode
         * provides more efficient CPU utilization at the cost of increased memory usage.  As of
         * version 0.5.1 this is the default RenderMode.
         *
         * XML value: use_background_thread
         * @since 0.5.1
         */
        USE_BACKGROUND_THREAD,

        /**
         * Do everything in the primary thread.  This is the preferred method of drawing static charts
         * and dynamic data that consists of a small number of points. This mode uses less memory at
         * the cost of poor CPU utilization.
         *
         * XML value: use_main_thread
         * @since 0.5.1
         */
        USE_MAIN_THREAD
    }
    private BoxModel boxModel = new BoxModel(3, 3, 3, 3, 3, 3, 3, 3);
    private BorderStyle borderStyle = Plot.BorderStyle.SQUARE;
    private float borderRadiusX = 15;
    private float borderRadiusY = 15;
    private boolean drawBorderEnabled = true;
    private Paint borderPaint;
    private Paint backgroundPaint;
    private LayoutManager layoutManager;
    private TextLabelWidget titleWidget;
    private DisplayDimensions displayDims = new DisplayDimensions();
    private RenderMode renderMode = RenderMode.USE_MAIN_THREAD;
    private final BufferedCanvas pingPong = new BufferedCanvas();

    // used to get rid of flickering when drawing offScreenBitmap to the visible Canvas.
    private final Object renderSynch = new Object();

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

    private Thread renderThread;
    private boolean keepRunning = false;
    private boolean isIdle = true;

    {
        listeners = new ArrayList<PlotListener>();
        seriesRegistry = new LinkedHashMap<Class, SeriesAndFormatterList<SeriesType,FormatterType>>();
        renderers = new LinkedList<RendererType>();
        borderPaint = new Paint();
        borderPaint.setColor(Color.rgb(150, 150, 150));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1.0f);
        borderPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }


    /**
     *  Any rendering that utilizes a buffer from this class should synchronize rendering on the instance of this class
     *  that is being used.
     */
    private class BufferedCanvas {
        private volatile Bitmap bgBuffer;  // all drawing is done on this buffer.
        private volatile Bitmap fgBuffer;
        private Canvas canvas = new Canvas();

        /**
         * Call this method once drawing on a Canvas retrieved by {@link #getCanvas()} to mark
         * the buffer as fully rendered.  Failure to call this method will result in nothing being drawn.
         */
        public synchronized void swap() {
            Bitmap tmp = bgBuffer;
            bgBuffer = fgBuffer;
            fgBuffer = tmp;
        }

        public synchronized void resize(int h, int w) {
            if (w <= 0 || h <= 0) {
                bgBuffer = null;
                fgBuffer = null;
            } else {
                bgBuffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
                fgBuffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            }
        }


        /**
         * Get a Canvas for drawing.  Actual drawing should be synchronized on the instance
         * of BufferedCanvas being used.
         * @return The Canvas instance to draw onto.  Returns null if drawing buffers have not
         *         been initialized a la {@link #resize(int, int)}.
         */
        public synchronized Canvas getCanvas() {
            if(bgBuffer != null) {
                canvas.setBitmap(bgBuffer);
                return canvas;
            } else {
                return null;
            }
        }

        /**
         * @return The most recent fully rendered Bitmsp
         */
        public Bitmap getBitmap() {
            return fgBuffer;
        }
    }

    /**
     * Convenience constructor - wraps {@link #Plot(android.content.Context, String, com.androidplot.Plot.RenderMode)}.
     * RenderMode is set to {@link RenderMode#USE_BACKGROUND_THREAD}.
     * @param context
     * @param title The display title of this Plot.
     */
    public Plot(Context context, String title) {
        this(context, title, RenderMode.USE_MAIN_THREAD);
    }

    /**
     * Used for programmatic instantiation.
     * @param context
     * @param title The display title of this Plot.
     */
    public Plot(Context context, String title, RenderMode mode) {
        super(context);
        this.renderMode = mode;
        init(null, null);
        setTitle(title);
    }


    /**
     * Required by super-class. Extending class' implementations should add
     * the following code immediately before exiting to ensure that loadAttrs
     * is called only once by the derived class:
     * <code>
     * if(getClass().equals(DerivedPlot.class) {
     *     loadAttrs(context, attrs);
     * }
     * </code>
     *
     * See {@link com.androidplot.xy.XYPlot#XYPlot(android.content.Context, android.util.AttributeSet)}
     * for an example.
     * @param context
     * @param attrs
     */
    public Plot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Required by super-class. Extending class' implementations should add
     * the following code immediately before exiting to ensure that loadAttrs
     * is called only once by the derived class:
     * <code>
     * if(getClass().equals(DerivedPlot.class) {
     *     loadAttrs(context, attrs);
     * }
     * </code>
     *
     * See {@link com.androidplot.xy.XYPlot#XYPlot(android.content.Context, android.util.AttributeSet, int)}
     * for an example.
     * @param context
     * @param attrs
     * @param defStyle
     */
    public Plot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Can be overridden by derived classes to control hardware acceleration state.
     * Note that this setting is only used on Honeycomb and later environments.
     * @return True if hardware acceleration is allowed, false otherwise.
     * @since 0.5.1
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isHwAccelerationSupported() {
        return false;
    }

    /**
     * Sets the render mode used by the Plot.
     * WARNING: This method is not currently designed for general use outside of Configurator.
     * Attempting to reassign the render mode at runtime will result in unexpected behavior.
     * @param mode
     */
    public void setRenderMode(RenderMode mode) {
        this.renderMode = mode;
    }

    /**
     * Concrete implementations should do any final setup / initialization
     * here.  Immediately following this method's invocation, AndroidPlot assumes
     * that the Plot instance is ready for final configuration via the Configurator.
     */
    protected abstract void onPreInit();


    private void init(Context context, AttributeSet attrs) {
        PixelUtils.init(getContext());
        layoutManager = new LayoutManager();
        titleWidget = new TextLabelWidget(layoutManager, new SizeMetrics(25,
                SizeLayoutType.ABSOLUTE, 100,
                SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);
        titleWidget.position(0, XLayoutStyle.RELATIVE_TO_CENTER, 0,
                YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);

        onPreInit();
        // make sure the title widget is always the topmost widget:
        layoutManager.moveToTop(titleWidget);
        if(context != null && attrs != null) {
            loadAttrs(attrs);
        }

        layoutManager.onPostInit();
        Log.d(TAG, "AndroidPlot RenderMode: " + renderMode);
        if (renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            renderThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    keepRunning = true;
                    while (keepRunning) {
                        isIdle = false;
                        synchronized (pingPong) {
                            Canvas c = pingPong.getCanvas();
                            renderOnCanvas(c);
                            pingPong.swap();
                        }
                        synchronized (renderSynch) {
                            postInvalidate();
                            // prevent this thread from becoming an orphan
                            // after the view is destroyed
                            if (keepRunning) {
                                try {
                                    renderSynch.wait();
                                } catch (InterruptedException e) {
                                    keepRunning = false;
                                }
                            }
                        }
                    }
                    System.out.println("AndroidPlot render thread finished.");
                }
            });
        }
    }

    /**
     * Parse XML Attributes.  Should only be called once and at the end of the base class constructor.
     *
     * @param attrs
     */
    private void loadAttrs(AttributeSet attrs) {

        if (attrs != null) {
            // filter out androidplot prefixed attrs:
            HashMap<String, String> attrHash = new HashMap<String, String>();
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attrName = attrs.getAttributeName(i);

                // case insensitive check to see if this attr begins with our prefix:
                if (attrName.toUpperCase().startsWith(XML_ATTR_PREFIX.toUpperCase())) {
                    attrHash.put(attrName.substring(XML_ATTR_PREFIX.length() + 1), attrs.getAttributeValue(i));
                }
            }
            Configurator.configure(getContext(), this, attrHash);
        }
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public synchronized boolean addListener(PlotListener listener) {
        return !listeners.contains(listener) && listeners.add(listener);
    }

    public synchronized boolean removeListener(PlotListener listener) {
        return listeners.remove(listener);
    }

    protected void notifyListenersBeforeDraw(Canvas canvas) {
        for (PlotListener listener : listeners) {
            listener.onBeforeDraw(this, canvas);
        }
    }

    protected void notifyListenersAfterDraw(Canvas canvas) {
        for (PlotListener listener : listeners) {
            listener.onAfterDraw(this, canvas);
        }
    }

    /**
     * @param series
     */
    public synchronized boolean addSeries(SeriesType series, FormatterType formatter) {
        Class rendererClass = formatter.getRendererClass();
        SeriesAndFormatterList<SeriesType, FormatterType> sfList = seriesRegistry.get(rendererClass);
        
        // if there is no list for this renderer type, we need to getInstance one:
        if(sfList == null) {
            // make sure there is not already an instance of this renderer available:
            if(getRenderer(rendererClass) == null) {
                renderers.add((RendererType) formatter.getRendererInstance(this));
            }
            sfList = new SeriesAndFormatterList<SeriesType,FormatterType>();
            seriesRegistry.put(rendererClass, sfList);
        }

        // if this series implements PlotListener, add it as a listener:
        if(series instanceof PlotListener) {
            addListener((PlotListener)series);
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

        // if series implements PlotListener, remove it from listeners:
        if(series instanceof PlotListener) {
            removeListener((PlotListener) series);
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

        // if series implements PlotListener, remove it from listeners:
        if (series instanceof PlotListener) {
            removeListener((PlotListener) series);
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
        for (SeriesRenderer renderer : getRendererList()) {
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
    }

    public void setMarkupEnabled(boolean enabled) {
        this.layoutManager.setMarkupEnabled(enabled);
    }

    /**
     * Causes the plot to be redrawn.
     * @since 0.5.1
     */
    public void redraw() {

        if (renderMode == RenderMode.USE_BACKGROUND_THREAD) {

            // only enter synchronized block if the call is expected to block OR
            // if the render thread is idle, so we know that we won't have to wait to
            // obtain a lock.
            if (isIdle) {
                synchronized (renderSynch) {
                    renderSynch.notify();
                }
            }
        } else if(renderMode == RenderMode.USE_MAIN_THREAD) {

            // are we on the UI thread?
            if (Looper.myLooper() == Looper.getMainLooper()) {
                invalidate();
            } else {
                postInvalidate();
            }
        } else {
            throw new IllegalArgumentException("Unsupported Render Mode: " + renderMode);
        }
    }

    @Override
    public synchronized void layout(final DisplayDimensions dims) {
        displayDims = dims;
        layoutManager.layout(displayDims);
    }

    @Override
    protected void onDetachedFromWindow() {
        synchronized(renderSynch) {
            keepRunning = false;
            renderSynch.notify();
        }
    }


    @Override
    protected synchronized void onSizeChanged (int w, int h, int oldw, int oldh) {

        // update pixel conversion values
        PixelUtils.init(getContext());

        // disable hardware acceleration if it's not explicitly supported
        // by the current Plot implementation. this check only applies to
        // honeycomb and later environments.
        if (Build.VERSION.SDK_INT >= 11) {
            if (!isHwAccelerationSupported() && isHardwareAccelerated()) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }

        // pingPong is only used in background rendering mode.
        if(renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            pingPong.resize(h, w);
        }

        RectF cRect = new RectF(0, 0, w, h);
        RectF mRect = boxModel.getMarginatedRect(cRect);
        RectF pRect = boxModel.getPaddedRect(mRect);

        layout(new DisplayDimensions(cRect, mRect, pRect));
        super.onSizeChanged(w, h, oldw, oldh);
        if(renderThread != null && !renderThread.isAlive()) {
            renderThread.start();
        }
    }

    /**
     * Called whenever the plot needs to be drawn via the Handler, which invokes invalidate().
     * Should never be called directly; use {@link #redraw()} instead.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            synchronized(pingPong) {
                Bitmap bmp = pingPong.getBitmap();
                if(bmp != null) {
                    canvas.drawBitmap(bmp, 0, 0, null);
                }
            }
        } else if (renderMode == RenderMode.USE_MAIN_THREAD) {
            renderOnCanvas(canvas);
        } else {
            throw new IllegalArgumentException("Unsupported Render Mode: " + renderMode);
        }
    }

    /**
     * Renders the plot onto a canvas.  Used by both main thread to draw directly
     * onto the View's canvas as well as by background draw to render onto a
     * Bitmap buffer.  At the end of the day this is the main entry for a plot's
     * "heavy lifting".
     * @param canvas
     */
    protected synchronized void renderOnCanvas(Canvas canvas) {
        try {
            // any series interested in synchronizing with plot should
            // implement PlotListener.onBeforeDraw(...) and do a read lock from within its
            // invocation.  This is the entry point into that call:
            notifyListenersBeforeDraw(canvas);
            try {
                // need to completely erase what was on the canvas before redrawing, otherwise
                // some odd aliasing artifacts begin to build up around the edges of aa'd entities
                // over time.
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                if (backgroundPaint != null) {
                    drawBackground(canvas, displayDims.marginatedRect);
                }

                layoutManager.draw(canvas);

                if (getBorderPaint() != null) {
                    drawBorder(canvas, displayDims.marginatedRect);
                }
            } catch (PlotRenderException e) {
                Log.e(TAG, "Exception while rendering Plot.", e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "Exception while rendering Plot.", e);
            }
        } finally {
            isIdle = true;
            // any series interested in synchronizing with plot should
            // implement PlotListener.onAfterDraw(...) and do a read unlock from within that
            // invocation. This is the entry point for that invocation.
            notifyListenersAfterDraw(canvas);
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
    protected void drawBorder(Canvas canvas, RectF dims) {
        switch (borderStyle) {
            case ROUNDED:
                canvas.drawRoundRect(dims, borderRadiusX, borderRadiusY, borderPaint);
                break;
            case SQUARE:
                canvas.drawRect(dims, borderPaint);
                break;
            default:
        }
    }

    protected void drawBackground(Canvas canvas, RectF dims) {
        switch (borderStyle) {
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
        return getTitleWidget().getText();
    }

    /**
     *
     * @param title  The title to display on this Plot.
     */
    public void setTitle(String title) {
        titleWidget.setText(title);
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public TextLabelWidget getTitleWidget() {
        return titleWidget;
    }

    public void setTitleWidget(TextLabelWidget titleWidget) {
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
