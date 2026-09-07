// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class StepChartExampleActivity extends Activity
{
    private static final String[] STATE_NAMES = {"Unknown", "Init", "Idle", "Recv", "Send"};

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_chart_example);
        XYPlot plot = findViewById(R.id.stepChartExamplePlot);

        // y-vals to plot:
        Number[] yVals = {1, 2, 3, 4, 2, 3, 4, 2, 2, 2, 3, 4, 2, 3, 2, 2};
        // create our series from our array of nums:
        XYSeries series = new SimpleXYSeries(
                Arrays.asList(yVals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Thread #1");

        final int screenHeightPx = getResources().getDisplayMetrics().heightPixels;
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, screenHeightPx, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

        StepFormatter stepFormatter  = new StepFormatter(Color.WHITE, Color.BLUE);
        stepFormatter.setVertexPaint(null); // don't draw individual points
        stepFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));

        stepFormatter.getLinePaint().setAntiAlias(false);
        stepFormatter.setFillPaint(lineFill);
        plot.addSeries(series, stepFormatter);

        // adjust the domain/range ticks to make more sense; label per line for range and label per 5 ticks domain:
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.setLinesPerRangeLabel(1);
        plot.setLinesPerDomainLabel(5);

        // get rid of decimal points in our domain labels:
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("0"));

        // create a custom getFormatter to draw our state names as range tick labels:
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo,
                                       @NonNull FieldPosition pos) {
                int i = ((Number) obj).intValue();
                toAppendTo.append(i >= 1 && i <= 4 ? STATE_NAMES[i] : "Unknown");
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }
}
