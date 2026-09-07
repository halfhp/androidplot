// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.Arrays;

public class XYPlotWithBgImgActivity extends Activity {

    private static final int SERIES_LEN = 50;
    private static final Shader WHITE_SHADER = new LinearGradient(1, 1, 1, 1, Color.WHITE, Color.WHITE, Shader.TileMode.REPEAT);

    private XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xy_plot_with_bq_img_example);
        plot = findViewById(R.id.graph_metrics);

        // Format Graph
        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getGridBackgroundPaint().setShader(WHITE_SHADER);
        plot.getGraph().getDomainGridLinePaint().setColor(Color.BLACK);
        plot.getGraph().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{3, 3}, 1));
        plot.getGraph().getRangeGridLinePaint().setColor(Color.BLACK);
        plot.getGraph().getRangeGridLinePaint().setPathEffect(new DashPathEffect(new float[]{3, 3}, 1));
        plot.getGraph().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraph().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Customize domain and range labels.
        plot.setDomainLabel("x-vals");
        plot.setRangeLabel("y-vals");
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("0"));

        // Make the domain and range step correctly
        plot.setRangeBoundaries(40, 160, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 20);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 60);
        plot.setLinesPerDomainLabel(2);

        SimpleXYSeries series = generateSeries();
        LineAndPointFormatter lpFormat = new LineAndPointFormatter(
                Color.BLACK,
                Color.GRAY,
                null, // No fill
                new PointLabelFormatter(Color.TRANSPARENT) // Don't show text at points
        );
        lpFormat.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));
        lpFormat.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(6));
        plot.addSeries(series, lpFormat);
    }

    // wired from android:onClick in xy_plot_with_bq_img_example.xml
    public void onGraphStyleToggle(View v) {
        boolean styleOn = ((ToggleButton) v).isChecked();
        if (styleOn) {
            plot.getGraph().getGridBackgroundPaint().setShader(
                    backgroundImageShader(plot.getGraph().getGridRect()));
        } else {
            plot.getGraph().getGridBackgroundPaint().setShader(WHITE_SHADER);
        }
        plot.redraw();
    }

    private Shader backgroundImageShader(RectF gridRect) {
        BitmapShader shader = new BitmapShader(
                Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.graph_background),
                        1,
                        (int) gridRect.height(),
                        false),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        Matrix m = new Matrix();
        m.setTranslate(gridRect.left, gridRect.top);
        shader.setLocalMatrix(m);
        return shader;
    }

    private SimpleXYSeries generateSeries() {
        Integer[] xVals = new Integer[SERIES_LEN];
        Integer[] yVals = new Integer[SERIES_LEN];

        xVals[0] = 0;
        yVals[0] = 0;

        for (int i = 1; i < SERIES_LEN; i += 1){
            xVals[i] = xVals[i-1] + (int)(Math.random() * i);
            yVals[i] = (int)(Math.random() * 140);
        }

        return new SimpleXYSeries(
                Arrays.asList(xVals),
                Arrays.asList(yVals),
                "Sample Series");
    }
}
