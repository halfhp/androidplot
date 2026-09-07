// SPDX-License-Identifier: Apache-2.0
package com.androidplot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.test.AndroidplotTest;
import com.androidplot.xy.AdvancedLineAndPointRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.BubbleFormatter;
import com.androidplot.xy.BubbleRenderer;
import com.androidplot.xy.BubbleSeries;
import com.androidplot.xy.CandlestickFormatter;
import com.androidplot.xy.CandlestickMaker;
import com.androidplot.xy.CandlestickSeries;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.FastLineAndPointRenderer;
import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.RectRegion;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepFormatter;
import com.androidplot.xy.XValueMarker;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYRegionFormatter;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.YValueMarker;

import org.junit.Test;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Renders every plot / renderer type onto a real bitmap and checks that (a) nothing blew up and
 * (b) the series actually contributed pixels.  The same renderers are then driven over a table of
 * degenerate datasets where the only requirement is that rendering does not fail.
 *
 * Native graphics (and therefore an SDK >= 26) are required for {@link Bitmap#getPixel(int, int)}
 * to return anything but 0 under Robolectric; the rest of the suite runs at the default SDK.
 *
 * Note that {@link Plot#renderOnCanvas(Canvas)} catches and logs any exception thrown while
 * drawing, so "no exception" is verified by inspecting the log rather than by catching.
 */
@Config(sdk = 36)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class RenderSmokeTest extends AndroidplotTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    /** A degenerate dataset, applied to an otherwise standard XYPlot. */
    private static final class EdgeCase {
        final String name;
        final Supplier<XYSeries> series;
        final Consumer<XYPlot> configure;

        EdgeCase(String name, Supplier<XYSeries> series, Consumer<XYPlot> configure) {
            this.name = name;
            this.series = series;
            this.configure = configure;
        }
    }

    private static EdgeCase edge(String name, Number... yVals) {
        return new EdgeCase(name, () -> yVals(name, yVals), null);
    }

    private static EdgeCase edge(String name, Consumer<XYPlot> configure, Number... yVals) {
        return new EdgeCase(name, () -> yVals(name, yVals), configure);
    }

    /**
     * Each of these is rendered through each of {@link #LINE_FAMILY_FORMATTERS}.  Adding a row
     * here adds a case for every renderer.
     */
    private static final EdgeCase[] EDGE_CASES = {
            edge("empty"),
            edge("singlePoint", 5),
            edge("flatLine", 3, 3, 3, 3, 3),
            edge("nullInMiddle", 1, 2, null, 4, 5),
            edge("negativeValues", -1, -5, -2, -8, -3),
            edge("allNull", (Number) null, null, null),
            edge("invertedDomain", p -> p.setDomainBoundaries(10, 0, BoundaryMode.FIXED), 1, 2, 3, 4),
            edge("invertedRange", p -> p.setRangeBoundaries(10, 0, BoundaryMode.FIXED), 1, 2, 3, 4),
            edge("widerThanFixedWindow", p -> p.setDomainBoundaries(5, 8, BoundaryMode.FIXED),
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19),
            edge("fixedWindowOutsideData", p -> p.setDomainBoundaries(100, 200, BoundaryMode.FIXED),
                    1, 2, 3, 4),
            edge("zeroWidthFixedDomain", p -> p.setDomainBoundaries(2, 2, BoundaryMode.FIXED), 1, 2, 3, 4),
    };

    /** The renderers that accept a plain {@link XYSeries}; one row per renderer / configuration. */
    private static final Object[][] LINE_FAMILY_FORMATTERS = {
            {"line", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::lineFormatter},
            {"lineCatmullRom", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::catmullRomFormatter},
            {"lineFillTop", (Supplier<XYSeriesFormatter<?>>) () -> lineFormatter(FillDirection.TOP)},
            {"lineFillRangeOrigin", (Supplier<XYSeriesFormatter<?>>) () -> lineFormatter(FillDirection.RANGE_ORIGIN)},
            {"fastLine", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::fastFormatter},
            {"advancedLine", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::advancedFormatter},
            {"bar", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::barFormatter},
            {"step", (Supplier<XYSeriesFormatter<?>>) RenderSmokeTest::stepFormatter},
    };

    // ---------------------------------------------------------------------------------------
    // happy path: every renderer draws something
    // ---------------------------------------------------------------------------------------

    /**
     * The "series contributed pixels" assertions compare two renders with {@link Bitmap#sameAs};
     * this pins the other direction: identical renders compare equal, so a difference is real.
     */
    @Test
    public void render_isDeterministic() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), lineFormatter());
        assertTrue(render(plot).sameAs(render(plot)));
    }

    @Test
    public void lineAndPoint_plain() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), new LineAndPointFormatter(Color.RED, Color.GREEN, null, null));
        assertSeriesDrawn(plot);
    }

    @Test
    public void lineAndPoint_withCatmullRomInterpolation() {
        for (CatmullRomInterpolator.Type type : CatmullRomInterpolator.Type.values()) {
            XYPlot plot = newXYPlot();
            LineAndPointFormatter formatter = lineFormatter();
            formatter.setInterpolationParams(new CatmullRomInterpolator.Params(10, type));
            plot.addSeries(sampleSeries("s"), formatter);
            assertSeriesDrawn(type.name(), plot);
        }
    }

    @Test
    public void lineAndPoint_fillDirections() {
        for (FillDirection direction : Arrays.asList(
                FillDirection.TOP, FillDirection.BOTTOM, FillDirection.RANGE_ORIGIN)) {
            XYPlot plot = newXYPlot();
            plot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);
            plot.setUserRangeOrigin(0);
            plot.addSeries(sampleSeries("s"), lineFormatter(direction));
            assertSeriesDrawn(direction.name(), plot);
        }
    }

    /**
     * LEFT / RIGHT / DOMAIN_ORIGIN are documented as not yet implemented.  The plot must survive
     * them (the failure is logged) rather than take the app down mid-frame.
     */
    @Test
    public void lineAndPoint_unimplementedFillDirections_logRatherThanCrash() {
        for (FillDirection direction : Arrays.asList(
                FillDirection.LEFT, FillDirection.RIGHT, FillDirection.DOMAIN_ORIGIN)) {
            XYPlot plot = newXYPlot();
            plot.addSeries(sampleSeries("s"), lineFormatter(direction));
            ShadowLog.clear();
            render(plot);
            List<Throwable> logged = loggedThrowables();
            assertTrue(direction + ": expected an UnsupportedOperationException to be logged",
                    logged.size() == 1 && logged.get(0) instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void lineAndPoint_withRegionFormatter() {
        XYPlot plot = newXYPlot();
        LineAndPointFormatter formatter = lineFormatter();
        formatter.addRegion(new RectRegion(1, 3, 2, 8), new XYRegionFormatter(Color.MAGENTA));
        plot.addSeries(sampleSeries("s"), formatter);
        assertSeriesDrawn(plot);
    }

    @Test
    public void advancedLineAndPoint() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), advancedFormatter());
        plot.getRenderer(AdvancedLineAndPointRenderer.class).setLatestIndex(2);
        assertSeriesDrawn(plot);
    }

    @Test
    public void fastLineAndPoint() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), fastFormatter());
        assertSeriesDrawn(plot);
    }

    @Test
    public void bar_allOrientationsAndWidthModes() {
        for (BarRenderer.BarOrientation orientation : BarRenderer.BarOrientation.values()) {
            for (BarRenderer.BarGroupWidthMode widthMode : BarRenderer.BarGroupWidthMode.values()) {
                XYPlot plot = newXYPlot();
                plot.addSeries(sampleSeries("s1"), barFormatter());
                plot.addSeries(yVals("s2", 4, 1, 6, 2, 7, 3, 2), new BarFormatter(Color.BLUE, Color.CYAN));
                BarRenderer<?> renderer = plot.getRenderer(BarRenderer.class);
                renderer.setBarOrientation(orientation);
                renderer.setBarGroupWidth(widthMode, 20);
                assertSeriesDrawn(orientation + "/" + widthMode, plot);
            }
        }
    }

    @Test
    public void step() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), stepFormatter());
        assertSeriesDrawn(plot);
    }

    @Test
    public void candlestick() {
        XYPlot plot = newXYPlot();
        plot.setRangeBoundaries(0, 20, BoundaryMode.FIXED);
        CandlestickMaker.make(plot, new CandlestickFormatter(), sampleCandlesticks());
        assertSeriesDrawn(plot);
    }

    @Test
    public void bubble_allScaleModes() {
        for (BubbleRenderer.BubbleScaleMode mode : BubbleRenderer.BubbleScaleMode.values()) {
            XYPlot plot = newXYPlot();
            plot.addSeries(new BubbleSeries(1, 2, 5, 3, 6, 20, 5, 4, 50), new BubbleFormatter(Color.RED, Color.BLUE));
            plot.getRenderer(BubbleRenderer.class).setBubbleScaleMode(mode);
            assertSeriesDrawn(mode.name(), plot);
        }
    }

    @Test
    public void pie_withAndWithoutDonut() {
        for (float donutSize : new float[]{0f, 0.5f}) {
            PieChart chart = newPieChart();
            addSegments(chart, 10, 20, 30, 40);
            chart.getRenderer(PieRenderer.class).setDonutSize(donutSize, PieRenderer.DonutMode.PERCENT);
            assertSeriesDrawn("donut=" + donutSize, chart);
        }
    }

    @Test
    public void valueMarkers() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("s"), lineFormatter());
        Bitmap without = render(plot);

        plot.addMarker(new XValueMarker(2, "x marker"));
        plot.addMarker(new YValueMarker(5, "y marker"));
        Bitmap with = render(plot);
        assertNotUniform(with);
        assertFalse("markers were not drawn", with.sameAs(without));
    }

    @Test
    public void legend() {
        XYPlot plot = newXYPlot();
        plot.addSeries(sampleSeries("first"), lineFormatter());
        plot.addSeries(yVals("second", 4, 1, 6, 2, 7), barFormatter());
        plot.getLegend().setVisible(false);
        Bitmap without = render(plot);

        plot.getLegend().setVisible(true);
        Bitmap with = render(plot);
        assertNotUniform(with);
        assertFalse("legend was not drawn", with.sameAs(without));
    }

    // ---------------------------------------------------------------------------------------
    // edge cases: nothing may fail, drawing may legitimately be blank
    // ---------------------------------------------------------------------------------------

    @Test
    public void edgeCases_lineFamilyRenderers() {
        List<String> failures = new ArrayList<>();
        for (Object[] row : LINE_FAMILY_FORMATTERS) {
            @SuppressWarnings("unchecked")
            Supplier<XYSeriesFormatter<?>> formatter = (Supplier<XYSeriesFormatter<?>>) row[1];
            for (EdgeCase edgeCase : EDGE_CASES) {
                XYPlot plot = newXYPlot();
                plot.setDomainBoundaries(null, null, BoundaryMode.AUTO);
                plot.setRangeBoundaries(null, null, BoundaryMode.AUTO);
                plot.addSeries(edgeCase.series.get(), formatter.get());
                if (edgeCase.configure != null) {
                    edgeCase.configure.accept(plot);
                }
                collectRenderFailure(row[0] + "/" + edgeCase.name, plot, failures);
            }
        }
        assertNoFailures(failures);
    }

    @Test
    public void edgeCases_bubble() {
        List<String> failures = new ArrayList<>();
        Object[][] cases = {
                {"zeroZ", new BubbleSeries(1, 2, 0, 3, 6, 0)},
                {"negativeZ", new BubbleSeries(1, 2, -5, 3, 6, -1)},
                {"mixedZ", new BubbleSeries(1, 2, -5, 3, 6, 0, 5, 4, 50)},
                {"singleBubble", new BubbleSeries(1, 2, 3)},
                {"nullZ", new BubbleSeries(Arrays.asList((Number) 1, 2), Arrays.asList((Number) 3, 4),
                        Arrays.asList((Number) null, 5), "nullZ")},
        };
        for (Object[] row : cases) {
            XYPlot plot = newXYPlot();
            plot.addSeries((BubbleSeries) row[1], new BubbleFormatter(Color.RED, Color.BLUE));
            collectRenderFailure("bubble/" + row[0], plot, failures);
        }
        assertNoFailures(failures);
    }

    @Test
    public void edgeCases_candlestick() {
        List<String> failures = new ArrayList<>();
        Object[][] cases = {
                {"empty", new CandlestickSeries()},
                {"single", new CandlestickSeries(new CandlestickSeries.Item(1, 5, 2, 4))},
                {"flat", new CandlestickSeries(
                        new CandlestickSeries.Item(3, 3, 3, 3), new CandlestickSeries.Item(3, 3, 3, 3))},
                {"negative", new CandlestickSeries(
                        new CandlestickSeries.Item(-9, -1, -5, -2), new CandlestickSeries.Item(-8, -2, -3, -6))},
        };
        for (Object[] row : cases) {
            XYPlot plot = newXYPlot();
            plot.setDomainBoundaries(null, null, BoundaryMode.AUTO);
            plot.setRangeBoundaries(null, null, BoundaryMode.AUTO);
            CandlestickMaker.make(plot, new CandlestickFormatter(), (CandlestickSeries) row[1]);
            collectRenderFailure("candlestick/" + row[0], plot, failures);
        }
        assertNoFailures(failures);
    }

    @Test
    public void edgeCases_pie() {
        List<String> failures = new ArrayList<>();
        Object[][] cases = {
                {"noSegments", new Number[]{}},
                {"zeroValueSegment", new Number[]{10, 0, 30}},
                {"zeroTotal", new Number[]{0, 0, 0}},
                {"singleSegment", new Number[]{42}},
                {"negativeSegment", new Number[]{10, -5, 30}},
        };
        for (Object[] row : cases) {
            for (float donutSize : new float[]{0f, 0.5f}) {
                PieChart chart = newPieChart();
                addSegments(chart, (Number[]) row[1]);
                if (chart.getRegistry().size() > 0) {
                    chart.getRenderer(PieRenderer.class).setDonutSize(donutSize, PieRenderer.DonutMode.PERCENT);
                }
                collectRenderFailure("pie/" + row[0] + "/donut=" + donutSize, chart, failures);
            }
        }
        assertNoFailures(failures);
    }

    // ---------------------------------------------------------------------------------------
    // fixtures
    // ---------------------------------------------------------------------------------------

    private XYPlot newXYPlot() {
        XYPlot plot = new XYPlot(getContext(), "smoke", Plot.RenderMode.USE_MAIN_THREAD);
        // fixed bounds keep the grid and labels identical with and without data, so that the
        // with / without comparison in assertSeriesDrawn isolates the series renderer's output:
        plot.setDomainBoundaries(0, 6, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        plot.getLegend().setVisible(false);
        plot.layout(0, 0, WIDTH, HEIGHT);
        return plot;
    }

    private PieChart newPieChart() {
        PieChart chart = new PieChart(getContext(), "pie", Plot.RenderMode.USE_MAIN_THREAD);
        chart.layout(0, 0, WIDTH, HEIGHT);
        return chart;
    }

    private static void addSegments(PieChart chart, Number... values) {
        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
        for (int i = 0; i < values.length; i++) {
            chart.addSegment(new Segment("seg" + i, values[i]),
                    new SegmentFormatter(colors[i % colors.length], Color.BLACK));
        }
    }

    private static XYSeries yVals(String title, Number... yVals) {
        return new SimpleXYSeries(Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);
    }

    private static XYSeries sampleSeries(String title) {
        return yVals(title, 1, 4, 2, 8, 5, 7, 3);
    }

    private static CandlestickSeries sampleCandlesticks() {
        return new CandlestickSeries(
                new CandlestickSeries.Item(1, 10, 2, 9),
                new CandlestickSeries.Item(4, 18, 6, 17),
                new CandlestickSeries.Item(3, 11, 10, 5),
                new CandlestickSeries.Item(2, 17, 15, 3),
                new CandlestickSeries.Item(6, 16, 15, 5));
    }

    private static LineAndPointFormatter lineFormatter() {
        return lineFormatter(FillDirection.BOTTOM);
    }

    private static LineAndPointFormatter lineFormatter(FillDirection fillDirection) {
        return new LineAndPointFormatter(Color.RED, Color.GREEN, Color.argb(100, 0, 0, 255), null, fillDirection);
    }

    private static LineAndPointFormatter catmullRomFormatter() {
        LineAndPointFormatter formatter = lineFormatter();
        formatter.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        return formatter;
    }

    private static FastLineAndPointRenderer.Formatter fastFormatter() {
        return new FastLineAndPointRenderer.Formatter(Color.RED, Color.GREEN, null);
    }

    private static AdvancedLineAndPointRenderer.Formatter advancedFormatter() {
        return new AdvancedLineAndPointRenderer.Formatter();
    }

    private static BarFormatter barFormatter() {
        return new BarFormatter(Color.RED, Color.GREEN);
    }

    private static StepFormatter stepFormatter() {
        return new StepFormatter(Color.RED, Color.argb(100, 0, 0, 255));
    }

    // ---------------------------------------------------------------------------------------
    // rendering + assertions
    // ---------------------------------------------------------------------------------------

    /**
     * Renders plot onto a fresh bitmap.  Any exception thrown while drawing is swallowed and
     * logged by {@link Plot#renderOnCanvas(Canvas)}; use {@link #loggedThrowables()} to find it.
     */
    private static Bitmap render(Plot plot) {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        plot.renderOnCanvas(new Canvas(bitmap));
        return bitmap;
    }

    private static List<Throwable> loggedThrowables() {
        List<Throwable> result = new ArrayList<>();
        for (ShadowLog.LogItem item : ShadowLog.getLogs()) {
            if (item.throwable != null) {
                result.add(item.throwable);
            }
        }
        return result;
    }

    private void assertSeriesDrawn(Plot plot) {
        assertSeriesDrawn("", plot);
    }

    /**
     * Renders plot, requiring a clean render that is not uniformly one color and that differs
     * from a render of the same plot after all of its series have been removed.
     */
    private void assertSeriesDrawn(String label, Plot plot) {
        ShadowLog.clear();
        Bitmap with = render(plot);
        assertRenderedCleanly(label);
        assertNotUniform(with);

        plot.clear();
        Bitmap without = render(plot);
        assertRenderedCleanly(label);
        assertFalse(label + ": series contributed no pixels", with.sameAs(without));
    }

    private static void assertRenderedCleanly(String label) {
        List<Throwable> logged = loggedThrowables();
        if (!logged.isEmpty()) {
            throw new AssertionError(label + ": exception logged while rendering", logged.get(0));
        }
    }

    private static void assertNotUniform(Bitmap bitmap) {
        Set<Integer> colors = new HashSet<>();
        for (int x = 0; x < bitmap.getWidth(); x += 4) {
            for (int y = 0; y < bitmap.getHeight(); y += 4) {
                colors.add(bitmap.getPixel(x, y));
            }
        }
        assertTrue("nothing was drawn; bitmap is uniformly " + colors, colors.size() > 1);
    }

    private static void collectRenderFailure(String label, Plot plot, List<String> failures) {
        ShadowLog.clear();
        try {
            render(plot);
        } catch (Throwable t) {
            failures.add(label + ": " + t);
            return;
        }
        for (Throwable t : loggedThrowables()) {
            failures.add(label + ": " + t);
        }
    }

    private static void assertNoFailures(List<String> failures) {
        if (!failures.isEmpty()) {
            fail(failures.size() + " case(s) failed to render:\n  " + String.join("\n  ", failures));
        }
    }
}
