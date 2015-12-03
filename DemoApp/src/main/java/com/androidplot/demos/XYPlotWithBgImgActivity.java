package com.androidplot.demos;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.Arrays;

public class XYPlotWithBgImgActivity extends Activity {
    private static final String TAG = XYPlotWithBgImgActivity.class.getName();

	private int SERIES_LEN = 50;
	private Shader WHITE_SHADER = new LinearGradient(1, 1, 1, 1, Color.WHITE, Color.WHITE, Shader.TileMode.REPEAT);

	private XYPlot plot;
	private SimpleXYSeries series;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_plot_with_bq_img_example);

		plot = (XYPlot) findViewById(R.id.graph_metrics);

		//For debugging.
        //plot.setMarkupEnabled(true);

        // Format Graph
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getGridBackgroundPaint().setShader(WHITE_SHADER);
        plot.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{3, 3}, 1));
        plot.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeGridLinePaint().setPathEffect(new DashPathEffect(new float[]{3, 3}, 1));
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
        //plot.getGraphWidget().setMarginTop(10);

        // Customize domain and range labels.
        plot.setDomainLabel("x-vals");
        plot.setRangeLabel("y-vals");
        plot.setRangeValueFormat(new DecimalFormat("0"));

        // Make the domain and range step correctly
        plot.setRangeBoundaries(40, 160, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 20);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 60);
        plot.setTicksPerDomainLabel(2);

        series = (SimpleXYSeries) getSeries();
		LineAndPointFormatter lpFormat = new LineAndPointFormatter(
				Color.BLACK,
				Color.BLACK,
				null, // No fill
				new PointLabelFormatter(Color.TRANSPARENT) // Don't show text at points
		);
        plot.addSeries(series, lpFormat);
        plot.redraw();
	}

	private SimpleXYSeries getSeries() {
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

	public void onGraphStyleToggle(View v) {
		boolean styleOn = ((ToggleButton) v).isChecked();

        /*RectF graphRect = plot.getGraphWidget().getGridRect();
        float segmentSize = 1.0f/6.0f;
        LinearGradient lg = new LinearGradient(
                0,
                graphRect.top,
                0,
                graphRect.bottom,
                new int[]{
                        Color.RED,
                        Color.YELLOW,
                        Color.GREEN,
                        Color.WHITE},
                new float[]{
                        0,
                        segmentSize*2,
                        segmentSize*3,
                        segmentSize*5
                },
                Shader.TileMode.REPEAT
        );
        plot.getGraphWidget().getGridBackgroundPaint().setShader(lg);*/

        RectF rect = plot.getGraphWidget().getGridRect();
        BitmapShader myShader = new BitmapShader(
                Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.graph_background),
                        1,
                        (int) rect.height(),
                        false),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        Matrix m = new Matrix();
        m.setTranslate(rect.left, rect.top);
        myShader.setLocalMatrix(m);
        if (styleOn)
	        plot.getGraphWidget().getGridBackgroundPaint().setShader(
	        		myShader);
		else
			plot.getGraphWidget().getGridBackgroundPaint().setShader(WHITE_SHADER);

        plot.redraw();

	}
}
