// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.HorizontalPosition;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPosition;
import com.androidplot.ui.VerticalPositioning;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ValueMarker}, {@link XValueMarker} and {@link YValueMarker}: construction, label
 * positioning and the canvas operations performed by draw().
 */
public class ValueMarkerTest extends AndroidplotTest {

    /** grid rect the markers are drawn into; 100 wide x 200 tall */
    private static final RectF GRID = new RectF(0, 0, 100, 200);

    /** text bounds reported by the mocked text paint: 60 wide, 10 above the baseline */
    private static final int TEXT_WIDTH = 60;
    private static final float ASCENT = -10;
    private static final float DESCENT = 2;
    private static final float TEXT_HEIGHT = -ASCENT + DESCENT; // 12

    @Mock
    Canvas canvas;

    XYPlot plot;
    Paint textPaint;
    Paint linePaint;

    @Before
    public void setUp() {
        plot = new XYPlot(getContext(), "markers");
        // x: 0..10, y: 0..100
        plot.addSeries(new SimpleXYSeries(Arrays.asList(0, 100), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "s"),
                new LineAndPointFormatter());
        plot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.calculateMinMaxVals();
        assertEquals(0.0, plot.getBounds().getMinX().doubleValue(), 0);
        assertEquals(10.0, plot.getBounds().getMaxX().doubleValue(), 0);
        assertEquals(100.0, plot.getBounds().getMaxY().doubleValue(), 0);

        linePaint = new Paint();
        textPaint = mock(Paint.class);
        final Paint.FontMetrics metrics = new Paint.FontMetrics();
        metrics.ascent = ASCENT;
        metrics.descent = DESCENT;
        when(textPaint.getFontMetrics()).thenReturn(metrics);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                final Rect rect = invocation.getArgument(3);
                rect.set(0, (int) ASCENT, TEXT_WIDTH, 0);
                return null;
            }
        }).when(textPaint).getTextBounds(anyString(), anyInt(), anyInt(), any(Rect.class));
    }

    // ---- construction ----

    @Test
    public void defaultConstructors_useRedPaintsAndDefaultTextPosition() {
        final XValueMarker x = new XValueMarker(1, "x");
        assertEquals(1, x.getValue());
        assertEquals("x", x.getText());
        assertEquals(Color.RED, x.getLinePaint().getColor());
        assertEquals(Color.RED, x.getTextPaint().getColor());
        assertEquals(Paint.Style.STROKE, x.getLinePaint().getStyle());
        assertEquals(3f, x.getTextPosition().getValue(), 0);
        assertEquals(VerticalPositioning.ABSOLUTE_FROM_TOP, x.getTextPosition().getLayoutType());
        assertEquals(2, x.getTextMargin());
        assertNull(x.getTextOrientation());

        final YValueMarker y = new YValueMarker(2, "y");
        assertEquals(2, y.getValue());
        assertEquals("y", y.getText());
        assertEquals(3f, y.getTextPosition().getValue(), 0);
        assertEquals(HorizontalPositioning.ABSOLUTE_FROM_LEFT, y.getTextPosition().getLayoutType());
        assertNotSame(x.getLinePaint(), y.getLinePaint());
    }

    @Test
    public void paintConstructors_usePaintsAsGiven() {
        final Paint line = new Paint();
        final Paint text = new Paint();
        final VerticalPosition vpos = new VerticalPosition(0.5f, VerticalPositioning.RELATIVE_TO_TOP);
        final XValueMarker x = new XValueMarker(1, "x", vpos, line, text);
        assertSame(line, x.getLinePaint());
        assertSame(text, x.getTextPaint());
        assertSame(vpos, x.getTextPosition());

        final HorizontalPosition hpos = new HorizontalPosition(5, HorizontalPositioning.ABSOLUTE_FROM_RIGHT);
        final YValueMarker y = new YValueMarker(2, "y", hpos, line, text);
        assertSame(line, y.getLinePaint());
        assertSame(text, y.getTextPaint());
        assertSame(hpos, y.getTextPosition());
    }

    @Test
    public void colorConstructors_colorDefaultPaints() {
        final XValueMarker x = new XValueMarker(1, "x",
                new VerticalPosition(1, VerticalPositioning.ABSOLUTE_FROM_BOTTOM), Color.BLUE, Color.GREEN);
        assertEquals(Color.BLUE, x.getLinePaint().getColor());
        assertEquals(Color.GREEN, x.getTextPaint().getColor());
        assertEquals(Paint.Style.STROKE, x.getLinePaint().getStyle());

        final YValueMarker y = new YValueMarker(2, "y",
                new HorizontalPosition(1, HorizontalPositioning.ABSOLUTE_FROM_LEFT), Color.CYAN, Color.MAGENTA);
        assertEquals(Color.CYAN, y.getLinePaint().getColor());
        assertEquals(Color.MAGENTA, y.getTextPaint().getColor());
    }

    @Test
    public void setters_updateState() {
        final XValueMarker x = new XValueMarker(1, "x");
        final Paint line = new Paint();
        final Paint text = new Paint();
        final VerticalPosition vpos = new VerticalPosition(9, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        x.setValue(7.5);
        x.setText("seven");
        x.setLinePaint(line);
        x.setTextPaint(text);
        x.setTextPosition(vpos);
        x.setTextMargin(4);
        x.setTextOrientation(TextOrientation.VERTICAL_ASCENDING);
        assertEquals(7.5, x.getValue());
        assertEquals("seven", x.getText());
        assertSame(line, x.getLinePaint());
        assertSame(text, x.getTextPaint());
        assertSame(vpos, x.getTextPosition());
        assertEquals(4, x.getTextMargin());
        assertEquals(TextOrientation.VERTICAL_ASCENDING, x.getTextOrientation());
    }

    // ---- XValueMarker.draw ----

    @Test
    public void xMarker_draw_drawsVerticalLineAtTransformedXAndLabelBelowTextPosition() {
        // text is positioned 20px from the top of the grid
        final XValueMarker marker = new XValueMarker(5, "five",
                new VerticalPosition(20, VerticalPositioning.ABSOLUTE_FROM_TOP), linePaint, textPaint);

        marker.draw(canvas, plot, GRID);

        // x = 5 of 0..10 across 0..100 -> 50
        verify(canvas).drawLine(50, GRID.top, 50, GRID.bottom, linePaint);
        // label origin: x + 2 = 52, baseline y - 2 = 18; the 60px wide label overflows the right
        // edge by 12 and is shifted left to end at 100 (top = 18 - 12 = 6 is inside the grid)
        verify(canvas).drawText("five", 40, 18, textPaint);
    }

    @Test
    public void xMarker_draw_clampsLabelInsideGrid() {
        // x near the right edge and text at the very top: label would overflow right and top
        final XValueMarker marker = new XValueMarker(9, "nine",
                new VerticalPosition(0, VerticalPositioning.ABSOLUTE_FROM_TOP), linePaint, textPaint);

        marker.draw(canvas, plot, GRID);

        verify(canvas).drawLine(90, 0, 90, 200, linePaint);
        // unclamped rect: left 92, right 152, top -2 - 12 = -14, bottom -2
        // shifted left by 52 to right edge 100 and down by 14 to top 0:
        verify(canvas).drawText("nine", 100 - TEXT_WIDTH, TEXT_HEIGHT, textPaint);
    }

    @Test
    public void xMarker_draw_relativeTextPosition() {
        final XValueMarker marker = new XValueMarker(2, "two",
                new VerticalPosition(0.5f, VerticalPositioning.RELATIVE_TO_TOP), linePaint, textPaint);
        marker.draw(canvas, plot, new RectF(10, 20, 110, 220));

        // x = 2 of 0..10 across 10..110 -> 30; y = 50% of 200 + top 20 = 120
        verify(canvas).drawLine(30, 20, 30, 220, linePaint);
        verify(canvas).drawText("two", 32, 118, textPaint);
    }

    @Test
    public void xMarker_draw_nullText_drawsLineOnly() {
        final XValueMarker marker = new XValueMarker(5, null,
                new VerticalPosition(0, VerticalPositioning.ABSOLUTE_FROM_TOP), linePaint, textPaint);
        marker.draw(canvas, plot, GRID);
        verify(canvas).drawLine(50, 0, 50, 200, linePaint);
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void xMarker_draw_nullValue_drawsNothing() {
        final XValueMarker marker = new XValueMarker(null, "text");
        marker.draw(canvas, plot, GRID);
        verify(canvas, never()).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    // ---- YValueMarker.draw ----

    @Test
    public void yMarker_draw_drawsHorizontalLineAtFlippedYAndLabelRightOfTextPosition() {
        final YValueMarker marker = new YValueMarker(25, "quarter",
                new HorizontalPosition(10, HorizontalPositioning.ABSOLUTE_FROM_LEFT), linePaint, textPaint);

        marker.draw(canvas, plot, GRID);

        // y = 25 of 0..100 flipped across 0..200 -> 150
        verify(canvas).drawLine(GRID.left, 150, GRID.right, 150, linePaint);
        // label: x = 10 + 2 = 12, baseline y = 150 - 2 = 148 (top 136, not clamped)
        verify(canvas).drawText("quarter", 12, 148, textPaint);
    }

    @Test
    public void yMarker_draw_clampsLabelInsideGrid() {
        // text anchored 10px from the right edge with a 60px wide label: overflows right;
        // value at the top of the range: overflows top
        final YValueMarker marker = new YValueMarker(100, "top",
                new HorizontalPosition(10, HorizontalPositioning.ABSOLUTE_FROM_RIGHT), linePaint, textPaint);

        marker.draw(canvas, plot, GRID);

        verify(canvas).drawLine(0, 0, 100, 0, linePaint);
        verify(canvas).drawText("top", 100 - TEXT_WIDTH, TEXT_HEIGHT, textPaint);
    }

    @Test
    public void yMarker_draw_nullText_drawsLineOnly() {
        final YValueMarker marker = new YValueMarker(50, null,
                new HorizontalPosition(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT), linePaint, textPaint);
        marker.draw(canvas, plot, GRID);
        verify(canvas).drawLine(0, 100, 100, 100, linePaint);
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void yMarker_draw_nullValue_drawsNothing() {
        final YValueMarker marker = new YValueMarker(null, "text");
        marker.draw(canvas, plot, GRID);
        verify(canvas, never()).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));
    }

    @Test
    public void draw_withRealTextPaint_doesNotThrow() {
        final XValueMarker x = new XValueMarker(5, "x");
        final YValueMarker y = new YValueMarker(50, "y");
        x.draw(canvas, plot, GRID);
        y.draw(canvas, plot, GRID);
        verify(canvas).drawText(eq("x"), anyFloat(), anyFloat(), eq(x.getTextPaint()));
        verify(canvas).drawText(eq("y"), anyFloat(), anyFloat(), eq(y.getTextPaint()));
        assertNotNull(x.getTextPaint());
    }
}
