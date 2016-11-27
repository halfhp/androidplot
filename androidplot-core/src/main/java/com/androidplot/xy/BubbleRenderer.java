package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.Region;
import com.androidplot.exception.*;
import com.androidplot.ui.*;
import com.androidplot.util.*;

/**
 * Renders three dimensional data onto an {@link XYPlot} as bubbles; the x/y values define the position
 * of the bubble and z is uses as a scaling value for the bubble's radius.
 * @since 1.2.2
 */
public class BubbleRenderer<FormatterType extends BubbleFormatter> extends XYSeriesRenderer<BubbleSeries, FormatterType> {

    protected static final float MIN_BUBBLE_RADIUS_DEFAULT_DP = 9;
    protected static final float MAX_BUBBLE_RADIUS_DEFAULT_DP = 25;

    private Region bubbleBounds;

    private BubbleScaleMode bubbleScaleMode = BubbleScaleMode.SQUARE_ROOT;

    public enum BubbleScaleMode {

        /**
         * Bubble radius is scaled directly by {@link BubbleSeries} z-vals
         */
        LINEAR,

        /**
         * Bubble radius is scaled by the square root of {@link BubbleSeries} z-vals.
         * This is the default scaling used.
         */
        SQUARE_ROOT
    }

    public BubbleRenderer(XYPlot plot) {
        super(plot);

        bubbleBounds = new Region(
                PixelUtils.dpToPix(MIN_BUBBLE_RADIUS_DEFAULT_DP),
                PixelUtils.dpToPix(MAX_BUBBLE_RADIUS_DEFAULT_DP));
    }

    @Override
    protected void onRender(Canvas canvas, RectF plotArea, BubbleSeries series,
            FormatterType formatter, RenderStack stack) throws PlotRenderException {

        Region magnitudeBounds = calculateBounds();
        for(int i = 0; i < series.size(); i++) {

            // only render non-null values greater than zero:
            if(series.getY(i) != null && series.getZ(i).doubleValue() > 0) {

                final PointF centerPoint = getPlot().getBounds().
                        transform(series.getX(i), series.getY(i), plotArea, false, true);

                // calculate bubble radius:
                float bubbleRadius = magnitudeBounds.
                        transform(bubbleScaleMode == BubbleScaleMode.SQUARE_ROOT ?
                                  Math.sqrt(series.getZ(i).doubleValue()) :
                                  series.getZ(i).doubleValue(), bubbleBounds).floatValue();
                drawBubble(canvas, formatter, series, i, centerPoint, bubbleRadius);
            }
        }
    }

    /**
     * Render a bubble onto the canvas
     * @param canvas
     * @param formatter
     * @param series
     * @param index
     * @param centerPoint the x/y coords of the center of the bubble
     * @param radius size of the bubble
     */
    protected void drawBubble(Canvas canvas, FormatterType formatter, BubbleSeries series,
            int index, PointF centerPoint, float radius) {
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, formatter.getFillPaint());
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, formatter.getStrokePaint());
        if(series != null && formatter.hasPointLabelFormatter() && formatter.getPointLabeler() != null) {
            FontUtils.drawTextVerticallyCentered(
                    canvas,
                    formatter.getPointLabeler().getLabel(series, index),
                    centerPoint.x,
                    centerPoint.y,
                    formatter.getPointLabelFormatter().getTextPaint());
        }
    }

    @Override
    protected void doDrawLegendIcon(Canvas canvas, RectF rect, FormatterType formatter) {
        drawBubble(canvas, formatter, null, 0,
                new PointF(rect.centerX(), rect.centerY()), (rect.width()/2.5f));
    }

    public float getMinBubbleRadius() {
        return bubbleBounds.getMin().floatValue();
    }

    public void setMinBubbleRadius(float minBubbleRadius) {
        bubbleBounds.setMin(minBubbleRadius);
    }

    public float getMaxBubbleRadius() {
        return bubbleBounds.getMax().floatValue();
    }

    public void setMaxBubbleRadius(float maxBubbleRadius) {
        bubbleBounds.setMax(maxBubbleRadius);
    }

    public BubbleScaleMode getBubbleScaleMode() {
        return bubbleScaleMode;
    }

    public void setBubbleScaleMode(BubbleScaleMode bubbleScaleMode) {
        this.bubbleScaleMode = bubbleScaleMode;
    }

    protected Region calculateBounds() {
        Region bounds = new Region();
        for(SeriesBundle<BubbleSeries, ? extends FormatterType> f : getSeriesAndFormatterList()) {
            SeriesUtils.minMax(bounds, f.getSeries().getZVals());
        }

        if(bounds.getMax() != null && bounds.getMax().doubleValue() > 0) {
            if(bubbleScaleMode == BubbleScaleMode.SQUARE_ROOT) {
                // scale for easier visual interpretation. see:
                // https://en.wikipedia.org/wiki/Bubble_chart#Choosing_bubble_sizes_correctly
                bounds.setMax(Math.sqrt(bounds.getMax().doubleValue()));
            }
        } else {
            // no non-null, greater than zero vals so bounds are undefined
            return null;
        }

        if(bounds.getMin().doubleValue() > 0) {

            if(bubbleScaleMode == BubbleScaleMode.SQUARE_ROOT) {
                // scale for easier visual interpretation. see:
                // https://en.wikipedia.org/wiki/Bubble_chart#Choosing_bubble_sizes_correctly
                bounds.setMin(Math.sqrt(bounds.getMin().doubleValue()));
            }
        } else {
            // if the smallest value is negative, use zero instead since those vals arent visible:
            bounds.setMax(0);
        }
        return bounds;
    }
}
