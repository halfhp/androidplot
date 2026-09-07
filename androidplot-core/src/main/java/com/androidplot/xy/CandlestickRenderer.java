// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;
import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesBundle;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Renders a group of {@link com.androidplot.xy.XYSeries} as a candlestick chart
 * into an {@link com.androidplot.xy.XYPlot}.
 *
 * Constraints:
 * - Exactly four series must be added using the same {@link CandlestickFormatter}.
 * - Each of the four series has the same x(i) value.
 * - Expects that series are added in the order of:
 * high, low, open, close
 *
 * {@link CandlestickSeries} and {@link CandlestickMaker} provide simplified classes and methods
 * for setting up a candlestick chart.
 * @since 0.9.7
 */
public class CandlestickRenderer<FormatterType extends CandlestickFormatter> extends GroupRenderer<FormatterType> {

    protected static final int HIGH_INDEX = 0;
    protected static final int LOW_INDEX = 1;
    protected static final int OPEN_INDEX = 2;
    protected static final int CLOSE_INDEX = 3;

    public CandlestickRenderer(@NonNull XYPlot plot) {
        super(plot);
    }


    @Override
    public void onRender(@NonNull Canvas canvas, @NonNull RectF plotArea, @NonNull List<SeriesBundle<XYSeries,
                ? extends FormatterType>> sfList, int seriesSize,  @NonNull RenderStack stack) {

        final FormatterType formatter = sfList.get(0).getFormatter();
        for(int i = 0; i < seriesSize; i++) {

            final XYSeries highSeries = sfList.get(HIGH_INDEX).getSeries();
            final XYSeries lowSeries = sfList.get(LOW_INDEX).getSeries();
            final XYSeries openSeries = sfList.get(OPEN_INDEX).getSeries();
            final XYSeries closeSeries = sfList.get(CLOSE_INDEX).getSeries();

            // x-val for all series should be identical so just grab x from the first series:
            Number x = highSeries.getX(i);

            Number high = highSeries.getY(i);
            Number low = lowSeries.getY(i);
            Number open = openSeries.getY(i);
            Number close = closeSeries.getY(i);

            // draw the candlestick:
            final PointF highPix = getPlot().getBounds().transformScreen(x, high, plotArea);
            final PointF lowPix = getPlot().getBounds().transformScreen(x, low, plotArea);
            final PointF openPix = getPlot().getBounds().transformScreen(x, open, plotArea);
            final PointF closePix = getPlot().getBounds().transformScreen(x, close, plotArea);

            drawWick(canvas, highPix, lowPix, formatter);
            drawBody(canvas, openPix, closePix, formatter);
            drawUpperCap(canvas, highPix, formatter);
            drawLowerCap(canvas, lowPix, formatter);

            // draw labels, if any:
            final PointLabelFormatter plf = formatter.hasPointLabelFormatter()
                                            ? formatter.getPointLabelFormatter() : null;
            final PointLabeler pointLabeler = formatter.getPointLabeler();
            if(plf != null && pointLabeler != null) {
                drawTextLabel(canvas, highPix, pointLabeler.getLabel(highSeries, i), plf);
                drawTextLabel(canvas, lowPix, pointLabeler.getLabel(lowSeries, i), plf);
                drawTextLabel(canvas, openPix, pointLabeler.getLabel(openSeries, i), plf);
                drawTextLabel(canvas, closePix, pointLabeler.getLabel(closeSeries, i), plf);
            }
        }
    }

    protected void drawTextLabel(@NonNull Canvas canvas, @NonNull PointF coords, @Nullable String text, @NonNull PointLabelFormatter plf) {
        if(text != null) {
            canvas.drawText(text, coords.x + plf.hOffset, coords.y + plf.vOffset, plf.getTextPaint());
        }
    }

    protected void drawWick(@NonNull Canvas canvas, @NonNull PointF min, @NonNull PointF max, @NonNull FormatterType formatter) {
        canvas.drawLine(min.x, min.y, max.x, max.y, formatter.getWickPaint());
    }

    protected void drawBody(@NonNull Canvas canvas, @NonNull PointF open, @NonNull PointF close, @NonNull FormatterType formatter) {
        final float halfWidth = formatter.getBodyWidth() / 2;
        final RectF rect = new RectF(open.x - halfWidth, open.y, close.x + halfWidth, close.y);

        Paint bodyFillPaint = open.y >= close.y ?
                formatter.getRisingBodyFillPaint() : formatter.getFallingBodyFillPaint();

        Paint bodyStrokePaint = open.y >= close.y ?
                formatter.getRisingBodyStrokePaint() : formatter.getFallingBodyStrokePaint();

        switch(formatter.getBodyStyle()) {
            case SQUARE:
                canvas.drawRect(rect, bodyFillPaint);
                canvas.drawRect(rect, bodyStrokePaint);
                break;
            case TRIANGULAR:
                drawTriangle(canvas, rect, bodyFillPaint, bodyStrokePaint);
        }
    }

    protected void drawUpperCap(@NonNull Canvas canvas, @NonNull PointF val, @NonNull FormatterType formatter) {
        final float halfWidth = formatter.getUpperCapWidth();
        canvas.drawLine(val.x - halfWidth, val.y, val.x + halfWidth, val.y, formatter.getUpperCapPaint());
    }

    protected void drawLowerCap(@NonNull Canvas canvas, @NonNull PointF val, @NonNull FormatterType formatter) {
        final float halfWidth = formatter.getLowerCapWidth();
        canvas.drawLine(val.x - halfWidth, val.y, val.x + halfWidth, val.y, formatter.getLowerCapPaint());
    }

    @Override
    protected void doDrawLegendIcon(@NonNull Canvas canvas, @NonNull RectF rect, @NonNull FormatterType formatter) {
        // TODO
    }

    protected void drawTriangle(@NonNull Canvas canvas, @NonNull RectF rect,
                                @NonNull Paint fillPaint, @NonNull Paint strokePaint) {
        Path path = new Path();
        path.moveTo(rect.centerX(), rect.bottom);
        path.lineTo(rect.left,rect.top);
        path.lineTo(rect.right, rect.top);
        path.close();
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);
    }
}
