// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.*;
import android.graphics.*;
import android.os.*;

import com.androidplot.util.*;
import com.androidplot.xy.*;

import java.text.*;
import java.util.*;

/**
 * Plots f(x) with custom LineLabelRenderers: origin labels in red on the bottom/left edges,
 * every other label in gray on the top/right edges.
 */
public class FXPlotExampleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_plot_example);
        XYPlot plot = findViewById(R.id.plot);

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter);
        plot.addSeries(generateSeries(-5, 5, 100), series1Format);

        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

        plot.centerOnDomainOrigin(0);
        plot.centerOnRangeOrigin(0);

        // LineLabelRenderer is the extension point for drawing tick labels yourself
        // use our custom renderer to make origin labels red
        plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.BOTTOM, new MyLineLabelRenderer());
        plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.LEFT, new MyLineLabelRenderer());

        // skip every other line for top and right edge labels
        plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.RIGHT, new MySecondaryLabelRenderer());
        plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.TOP, new MySecondaryLabelRenderer());

        // don't show decimal places for top and right edge labels
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.TOP).setFormat(new DecimalFormat("0"));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.RIGHT).setFormat(new DecimalFormat("0"));

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        plot.getGraph().getDomainGridLinePaint().setPathEffect(dashFx);
        plot.getGraph().getRangeGridLinePaint().setPathEffect(dashFx);
    }

    /**
     * Custom line label renderer that highlights origin labels
     */
    private static class MyLineLabelRenderer extends XYGraphWidget.LineLabelRenderer {

        @Override
        protected void drawLabel(Canvas canvas, String text, Paint paint,
                float x, float y, boolean isOrigin) {
            if(isOrigin) {
                // make the origin labels red:
                final Paint originPaint = new Paint(paint);
                originPaint.setColor(Color.RED);
                super.drawLabel(canvas, text, originPaint, x, y , isOrigin);
            } else {
                super.drawLabel(canvas, text, paint, x, y , isOrigin);
            }
        }
    }

    /**
     * Draws every other tick label and renders text in gray instead of white.
     */
    private static class MySecondaryLabelRenderer extends MyLineLabelRenderer {

        @Override
        public void drawLabel(Canvas canvas, XYGraphWidget.LineLabelStyle style,
                Number val, float x, float y, boolean isOrigin) {
            if(val.doubleValue() % 2 == 0) {
                final Paint paint = style.getPaint();
                if(!isOrigin) {
                    paint.setColor(Color.GRAY);
                }
                super.drawLabel(canvas, style, val, x, y, isOrigin);
            }
        }
    }

    private XYSeries generateSeries(double minX, double maxX, double resolution) {
        final double step = (maxX - minX) / resolution;
        List<Number> xVals = new ArrayList<>();
        List<Number> yVals = new ArrayList<>();

        double x = minX;
        while (x <= maxX) {
            xVals.add(x);
            yVals.add(fx(x));
            x +=step;
        }

        return new SimpleXYSeries(xVals, yVals, "f(x) = (x^2) - 13");
    }

    private double fx(double x) {
        return Math.abs(x*x) - 13;
    }
}
