// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import android.graphics.*;

import com.androidplot.test.*;
import com.androidplot.ui.*;

import org.junit.*;
import org.mockito.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CandlestickRendererTest extends AndroidplotTest {

    XYPlot xyPlot;

    Canvas canvas;

    RectF plotArea = new RectF(0, 0, 100, 100);

    @Mock
    RenderStack renderStack;

    @Before
    public void setUp() throws Exception {
        canvas = new Canvas();
        xyPlot = spy(new XYPlot(getContext(), "My Plot"));
    }

    @Test
    public void testOnRender() throws Exception {
        CandlestickFormatter formatter = spy(new CandlestickFormatter());
        CandlestickRenderer renderer = spy((CandlestickRenderer) formatter.doGetRendererInstance(xyPlot));
        doReturn(renderer.getClass()).when(formatter).getRendererClass();
        doReturn(renderer).when(formatter).doGetRendererInstance(any(XYPlot.class));

        XYSeries openVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries closeVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries highVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        XYSeries lowVals = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "open", 1, 2, 3, 4);
        CandlestickMaker.make(xyPlot, formatter, openVals, closeVals, highVals, lowVals);

        renderer.onRender(canvas, plotArea, openVals, formatter, renderStack);
    }

    // ---- drawing primitives, verified against a mock canvas ----

    /** a plot with fixed bounds x: 0..10, y: 0..100 so pixel positions are deterministic */
    private XYPlot boundedPlot() {
        XYPlot plot = new XYPlot(getContext(), "bounded");
        plot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        return plot;
    }

    @Test
    public void drawBody_square_rising_usesRisingPaints() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setBodyWidth(10);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        // rising: the open pixel is lower on screen (larger y) than the close pixel
        renderer.drawBody(mockCanvas, new PointF(50, 80), new PointF(50, 20), formatter);

        RectF expected = new RectF(45, 80, 55, 20);
        verify(mockCanvas).drawRect(expected, formatter.getRisingBodyFillPaint());
        verify(mockCanvas).drawRect(expected, formatter.getRisingBodyStrokePaint());
        verify(mockCanvas, never()).drawRect(any(RectF.class), eq(formatter.getFallingBodyFillPaint()));
        verify(mockCanvas, never()).drawPath(any(Path.class), any(Paint.class));
    }

    @Test
    public void drawBody_square_falling_usesFallingPaints() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setBodyWidth(10);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        renderer.drawBody(mockCanvas, new PointF(50, 20), new PointF(50, 80), formatter);

        RectF expected = new RectF(45, 20, 55, 80);
        verify(mockCanvas).drawRect(expected, formatter.getFallingBodyFillPaint());
        verify(mockCanvas).drawRect(expected, formatter.getFallingBodyStrokePaint());
        verify(mockCanvas, never()).drawRect(any(RectF.class), eq(formatter.getRisingBodyFillPaint()));
    }

    @Test
    public void drawBody_openEqualsClose_isTreatedAsRising() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickFormatter formatter = new CandlestickFormatter();
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        renderer.drawBody(mockCanvas, new PointF(50, 40), new PointF(50, 40), formatter);

        verify(mockCanvas).drawRect(any(RectF.class), eq(formatter.getRisingBodyFillPaint()));
        verify(mockCanvas).drawRect(any(RectF.class), eq(formatter.getRisingBodyStrokePaint()));
    }

    @Test
    public void drawBody_triangular_drawsFilledAndStrokedPath() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.TRIANGULAR);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        renderer.drawBody(mockCanvas, new PointF(50, 80), new PointF(50, 20), formatter);

        verify(mockCanvas).drawPath(any(Path.class), eq(formatter.getRisingBodyFillPaint()));
        verify(mockCanvas).drawPath(any(Path.class), eq(formatter.getRisingBodyStrokePaint()));
        verify(mockCanvas, never()).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void drawTriangle_drawsSamePathWithBothPaints() {
        Canvas mockCanvas = mock(Canvas.class);
        Paint fill = new Paint();
        Paint stroke = new Paint();
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        renderer.drawTriangle(mockCanvas, new RectF(0, 0, 10, 10), fill, stroke);

        ArgumentCaptor<Path> paths = ArgumentCaptor.forClass(Path.class);
        verify(mockCanvas, times(2)).drawPath(paths.capture(), any(Paint.class));
        assertSame(paths.getAllValues().get(0), paths.getAllValues().get(1));
        verify(mockCanvas).drawPath(any(Path.class), eq(fill));
        verify(mockCanvas).drawPath(any(Path.class), eq(stroke));
    }

    @Test
    public void drawWickAndCaps_useFormatterWidthsAndPaints() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setUpperCapWidth(4);
        formatter.setLowerCapWidth(6);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());

        renderer.drawWick(mockCanvas, new PointF(50, 10), new PointF(50, 90), formatter);
        renderer.drawUpperCap(mockCanvas, new PointF(50, 10), formatter);
        renderer.drawLowerCap(mockCanvas, new PointF(50, 90), formatter);

        verify(mockCanvas).drawLine(50, 10, 50, 90, formatter.getWickPaint());
        verify(mockCanvas).drawLine(46, 10, 54, 10, formatter.getUpperCapPaint());
        verify(mockCanvas).drawLine(44, 90, 56, 90, formatter.getLowerCapPaint());
    }

    @Test
    public void drawTextLabel_nullText_drawsNothing() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());
        PointLabelFormatter plf = new PointLabelFormatter(Color.WHITE, 2, 3);

        renderer.drawTextLabel(mockCanvas, new PointF(10, 20), null, plf);

        verify(mockCanvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void drawTextLabel_offsetsTextByFormatter() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());
        PointLabelFormatter plf = new PointLabelFormatter(Color.WHITE, 2, 3);

        renderer.drawTextLabel(mockCanvas, new PointF(10, 20), "hi", plf);

        verify(mockCanvas).drawText("hi", 12, 23, plf.getTextPaint());
    }

    @Test
    public void onRender_withPointLabels_drawsOneLabelPerValue() {
        Canvas mockCanvas = mock(Canvas.class);
        XYPlot plot = boundedPlot();
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setPointLabelFormatter(new PointLabelFormatter(Color.WHITE));
        formatter.setPointLabeler(new PointLabeler() {
            @Override
            public String getLabel(XYSeries series, int index) {
                return series.getTitle() + index;
            }
        });
        CandlestickSeries series = new CandlestickSeries(
                new CandlestickSeries.Item(10, 40, 20, 30),
                new CandlestickSeries.Item(20, 60, 50, 25));
        series.getHighSeries().setTitle("high");
        series.getLowSeries().setTitle("low");
        series.getOpenSeries().setTitle("open");
        series.getCloseSeries().setTitle("close");
        CandlestickMaker.make(plot, formatter, series);
        CandlestickRenderer renderer = (CandlestickRenderer) plot.getRenderer(CandlestickRenderer.class);

        renderer.onRender(mockCanvas, plotArea, series.getHighSeries(), formatter, renderStack);

        for (String title : new String[] {"high", "low", "open", "close"}) {
            verify(mockCanvas).drawText(eq(title + "0"), anyFloat(), anyFloat(), any(Paint.class));
            verify(mockCanvas).drawText(eq(title + "1"), anyFloat(), anyFloat(), any(Paint.class));
        }
        // two candles: a wick and two caps each, plus a filled and stroked body each:
        verify(mockCanvas, times(6)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
        verify(mockCanvas, times(4)).drawRect(any(RectF.class), any(Paint.class));
        // the first candle is rising (close 30 > open 20), the second falling (25 < 50):
        verify(mockCanvas, times(1)).drawRect(any(RectF.class), eq(formatter.getRisingBodyFillPaint()));
        verify(mockCanvas, times(1)).drawRect(any(RectF.class), eq(formatter.getFallingBodyFillPaint()));
        verify(renderStack).disable(CandlestickRenderer.class);
    }

    @Test
    public void onRender_withoutPointLabelFormatter_drawsNoLabels() {
        Canvas mockCanvas = mock(Canvas.class);
        XYPlot plot = boundedPlot();
        CandlestickFormatter formatter = new CandlestickFormatter();
        assertFalse(formatter.hasPointLabelFormatter());
        CandlestickSeries series = new CandlestickSeries(new CandlestickSeries.Item(10, 40, 20, 30));
        CandlestickMaker.make(plot, formatter, series);
        CandlestickRenderer renderer = (CandlestickRenderer) plot.getRenderer(CandlestickRenderer.class);

        renderer.onRender(mockCanvas, plotArea, series.getHighSeries(), formatter, renderStack);

        verify(mockCanvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void onRender_withNullPointLabeler_drawsNoLabels() {
        Canvas mockCanvas = mock(Canvas.class);
        XYPlot plot = boundedPlot();
        CandlestickFormatter formatter = new CandlestickFormatter();
        formatter.setPointLabelFormatter(new PointLabelFormatter(Color.WHITE));
        formatter.setPointLabeler(null);
        CandlestickSeries series = new CandlestickSeries(new CandlestickSeries.Item(10, 40, 20, 30));
        CandlestickMaker.make(plot, formatter, series);
        CandlestickRenderer renderer = (CandlestickRenderer) plot.getRenderer(CandlestickRenderer.class);

        renderer.onRender(mockCanvas, plotArea, series.getHighSeries(), formatter, renderStack);

        verify(mockCanvas, never()).drawText(any(String.class), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void doDrawLegendIcon_drawsNothing() {
        Canvas mockCanvas = mock(Canvas.class);
        CandlestickRenderer renderer = new CandlestickRenderer(boundedPlot());
        renderer.doDrawLegendIcon(mockCanvas, new RectF(0, 0, 10, 10), new CandlestickFormatter());
        verifyNoInteractions(mockCanvas);
    }
}
