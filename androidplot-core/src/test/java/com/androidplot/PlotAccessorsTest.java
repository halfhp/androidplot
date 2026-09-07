// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.androidplot.PlotTest.MockFormatter1;
import com.androidplot.PlotTest.MockPlot;
import com.androidplot.PlotTest.MockRenderer1;
import com.androidplot.PlotTest.MockRenderer2;
import com.androidplot.PlotTest.MockSeries;
import com.androidplot.PlotTest.MockSeriesBundle;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.ui.Formatter;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.widget.TextLabelWidget;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Accessors and small branches of {@link Plot} not exercised by {@link PlotTest}.
 */
public class PlotAccessorsTest extends AndroidplotTest {

    @Mock
    Canvas canvas;

    MockPlot plot;

    @Before
    public void setUp() {
        plot = new MockPlot("MockPlot");
    }

    @Test
    public void getDisplayDimensions_isNotNull() {
        assertNotNull(plot.getDisplayDimensions());
        assertNotNull(plot.getDisplayDimensions().canvasRect);
    }

    @Test
    public void setTitle_widget_roundTrips() {
        TextLabelWidget title = new TextLabelWidget(plot.getLayoutManager(),
                new Size(10, SizeMode.ABSOLUTE, 10, SizeMode.ABSOLUTE));
        title.setText("custom");
        plot.setTitle(title);
        assertSame(title, plot.getTitle());
        assertEquals("custom", plot.getTitle().getText());
    }

    @Test
    public void setLayoutManager_roundTrips() {
        LayoutManager layoutManager = new LayoutManager();
        plot.setLayoutManager(layoutManager);
        assertSame(layoutManager, plot.getLayoutManager());
    }

    @Test
    public void setBackgroundPaint_roundTrips() {
        Paint paint = new Paint();
        plot.setBackgroundPaint(paint);
        assertSame(paint, plot.getBackgroundPaint());

        plot.setBackgroundPaint(null);
        assertNull(plot.getBackgroundPaint());
    }

    @Test
    public void setBorderPaint_copiesPaintAsStroke() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF123456);

        plot.setBorderPaint(paint);

        assertNotSame(paint, plot.getBorderPaint());
        assertEquals(Paint.Style.STROKE, plot.getBorderPaint().getStyle());
        assertEquals(0xFF123456, plot.getBorderPaint().getColor());
        // the caller's paint is left untouched:
        assertEquals(Paint.Style.FILL, paint.getStyle());
    }

    @Test
    public void setBorderPaint_null_clearsBorderPaint() {
        plot.setBorderPaint(null);
        assertNull(plot.getBorderPaint());
    }

    @Test
    public void setBorderStyle_rounded_drawsRoundRect() {
        plot.setBorderStyle(Plot.BorderStyle.ROUNDED, 3f, 4f);
        RectF dims = new RectF(0, 0, 10, 10);

        plot.drawBorder(canvas, dims);
        verify(canvas).drawRoundRect(eq(dims), eq(3f), eq(4f), eq(plot.getBorderPaint()));

        plot.drawBackground(canvas, dims);
        verify(canvas).drawRoundRect(eq(dims), eq(3f), eq(4f), eq(plot.getBackgroundPaint()));
        verify(canvas, never()).drawRect(any(RectF.class), any(Paint.class));
    }

    @Test
    public void setBorderStyle_square_drawsRect() {
        plot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        RectF dims = new RectF(0, 0, 10, 10);

        plot.drawBorder(canvas, dims);
        verify(canvas).drawRect(eq(dims), eq(plot.getBorderPaint()));
        verify(canvas, never()).drawRoundRect(any(RectF.class), any(Float.class), any(Float.class), any(Paint.class));
    }

    @Test
    public void setBorderStyle_roundedWithoutRadius_throws() {
        try {
            plot.setBorderStyle(Plot.BorderStyle.ROUNDED, null, 1f);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            plot.setBorderStyle(Plot.BorderStyle.ROUNDED, 1f, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void isEmpty_reflectsRegistry() {
        assertTrue(plot.isEmpty());
        plot.addSeries(new MockSeries(), new MockFormatter1());
        assertFalse(plot.isEmpty());
        plot.clear();
        assertTrue(plot.isEmpty());
    }

    @Test
    public void setRegistry_attachesAlreadyRegisteredSeries() {
        MockSeries series = new MockSeries();
        MockFormatter1 formatter = new MockFormatter1();
        SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> registry =
                new SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>() {
                    @Override
                    protected MockSeriesBundle newSeriesBundle(MockSeries s, Formatter f) {
                        return new MockSeriesBundle(s, f);
                    }
                };
        registry.add(series, formatter);
        assertTrue(plot.getRenderers().isEmpty());

        plot.setRegistry(registry);

        assertSame(registry, plot.getRegistry());
        // the renderer for the pre-registered series was created and the series was attached
        // as a plot listener:
        assertNotNull(plot.getRenderer(MockRenderer1.class));
        assertEquals(1, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
        assertTrue(plot.getListeners().contains(series));
    }

    @Test
    public void addSeries_varargs_addsAllSeriesWithSameFormatter() {
        MockSeries s1 = new MockSeries();
        MockSeries s2 = new MockSeries();
        assertTrue(plot.addSeries(new MockFormatter1(), s1, s2));
        assertEquals(2, plot.getRegistry().size());
        assertEquals(2, plot.getRenderer(MockRenderer1.class).getSeriesList().size());
    }

    @Test
    public void addSeries_varargs_stopsAtFirstFailure() {
        MockSeries s1 = new MockSeries();
        MockSeries s2 = new MockSeries();
        MockFormatter1 formatter = new MockFormatter1();
        MockPlot spy = spy(plot);
        doReturn(false).when(spy).addSeries(eq(s1), any(Formatter.class));

        assertFalse(spy.addSeries(formatter, s1, s2));
        verify(spy, never()).addSeries(eq(s2), any(Formatter.class));
    }

    @Test
    public void getSeries_byRendererClass_returnsMatchingBundleOrNull() {
        MockSeries series = new MockSeries();
        MockFormatter1 formatter = new MockFormatter1();
        plot.addSeries(series, formatter);

        SeriesBundle<MockSeries, Formatter> bundle = plot.getSeries(series, MockRenderer1.class);
        assertNotNull(bundle);
        assertSame(series, bundle.getSeries());
        assertSame(formatter, bundle.getFormatter());

        assertNull(plot.getSeries(series, MockRenderer2.class));
        assertNull(plot.getSeries(new MockSeries(), MockRenderer1.class));
    }

    @Test
    public void onPreInit_defaultImplementation_isANoOp() {
        // a plot that does not override the onPreInit hook:
        Plot<MockSeries, Formatter, SeriesRenderer, MockSeriesBundle,
                SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>> bare =
                new Plot<MockSeries, Formatter, SeriesRenderer, MockSeriesBundle,
                        SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>>(
                        RuntimeEnvironment.application, "bare") {
                    @Override
                    protected SeriesRegistry<MockSeriesBundle, MockSeries, Formatter> getRegistryInstance() {
                        return new SeriesRegistry<MockSeriesBundle, MockSeries, Formatter>() {
                            @Override
                            protected MockSeriesBundle newSeriesBundle(MockSeries s, Formatter f) {
                                return new MockSeriesBundle(s, f);
                            }
                        };
                    }

                    @Override
                    protected void processAttrs(android.content.res.TypedArray attrs) {
                    }
                };
        assertNotNull(bare.getTitle());
        assertEquals("bare", bare.getTitle().getText());
        assertTrue(bare.isEmpty());
    }
}
