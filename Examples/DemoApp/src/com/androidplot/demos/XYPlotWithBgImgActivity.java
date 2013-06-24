package com.androidplot.demos;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.Arrays;

public class XYPlotWithBgImgActivity extends Activity {
    private static final String TAG = XYPlotWithBgImgActivity.class.getName();

	private int SERIES_LEN = 50;
	private Shader WHITE_SHADER = new LinearGradient(1, 1, 1, 1, Color.WHITE, Color.WHITE, Shader.TileMode.REPEAT);

	private XYPlot graphView;
	private SimpleXYSeries series;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_plot_with_bq_img_example);

		graphView = (XYPlot) findViewById(R.id.graph_metrics);

		//For debugging.
        //graphView.setMarkupEnabled(true);

        // Format Graph
        graphView.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        graphView.getGraphWidget().getGridBackgroundPaint().setShader(WHITE_SHADER);
        graphView.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        graphView.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{3,3}, 1));
        graphView.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        graphView.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
        //graphView.getGraphWidget().setMarginTop(10);

        // Customize domain and range labels.
        graphView.setDomainLabel("x");
        graphView.setRangeLabel("y");
        graphView.setRangeValueFormat(new DecimalFormat("0"));

        // Make the domain and range step correctly
        graphView.setRangeBoundaries(40, 160, BoundaryMode.FIXED);
        graphView.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 20);
        graphView.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 60);

        series = (SimpleXYSeries) getSeries();
		LineAndPointFormatter lpFormat = new LineAndPointFormatter(
				Color.BLACK,
				Color.BLACK,
				null, // No fill
				new PointLabelFormatter(Color.TRANSPARENT) // Don't show text at points
		);
        graphView.addSeries(series, lpFormat);
        graphView.redraw();
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

        /*RectF graphRect = graphView.getGraphWidget().getGridRect();
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
        graphView.getGraphWidget().getGridBackgroundPaint().setShader(lg);*/

        RectF rect = graphView.getGraphWidget().getGridRect();
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
	        graphView.getGraphWidget().getGridBackgroundPaint().setShader(
	        		myShader);
		else
			graphView.getGraphWidget().getGridBackgroundPaint().setShader(WHITE_SHADER);

        graphView.redraw();

	}
}
