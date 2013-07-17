/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.demos;
import java.util.Arrays;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYLegendWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.ui.YLayoutStyle;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class DualScaleXYPlotExampleActivity extends Activity implements OnClickListener
{

    private XYPlot myXYPlot_LEFT, myXYPlot_RIGHT; 
    private Boolean series2_onRight = true;
    private LineAndPointFormatter series1Format, series2Format;
    private Button button;
    
    // Declare and enable buttons to toggle whether the 2nd series is on left or right.

    // Create a couple arrays of y-values to plot:
    private Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
    private Number[] series2Numbers = {444, 613, 353, 876, 924, 1004};
    XYSeries series1, series2;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dual_scale_xy_plot_example);

        // Setup the LEFT Plot as normal
        myXYPlot_LEFT = (XYPlot) findViewById(R.id.mySimpleXYPlot_L);
        myXYPlot_RIGHT = (XYPlot) findViewById(R.id.mySimpleXYPlot_R);
        
        // Disable Hardware Acceleration on the xyPlot view object.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	myXYPlot_LEFT.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	myXYPlot_RIGHT.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        XYGraphWidget graphWidget_LEFT = myXYPlot_LEFT.getGraphWidget();
        graphWidget_LEFT.setRangeAxisPosition(true, false, 4, "10");
        graphWidget_LEFT.setMarginRight(0);
        graphWidget_LEFT.setPaddingRight(30);
        graphWidget_LEFT.setRangeLabelVerticalOffset(-3);
        graphWidget_LEFT.setRangeLabelWidth(50);
        
        // Setup the second Plot with Right-hand Scale and otherwise invisible.
        myXYPlot_RIGHT.getDomainLabelWidget().setVisible(false);
        myXYPlot_RIGHT.getRangeLabelWidget().setVisible(false);
        myXYPlot_RIGHT.getTitleWidget().setVisible(false);
        myXYPlot_RIGHT.getBorderPaint().setAlpha(0);
        myXYPlot_RIGHT.getBackgroundPaint().setAlpha(0);
        XYGraphWidget graphWidget_RIGHT = myXYPlot_RIGHT.getGraphWidget();
        graphWidget_RIGHT.getBackgroundPaint().setAlpha(0);
        graphWidget_RIGHT.getDomainLabelPaint().setAlpha(0);
        graphWidget_RIGHT.getGridBackgroundPaint().setAlpha(0);
        graphWidget_RIGHT.getDomainOriginLabelPaint().setAlpha(0);
        graphWidget_RIGHT.getRangeOriginLinePaint().setAlpha(0); 
        graphWidget_RIGHT.getDomainGridLinePaint().setAlpha(0);
        graphWidget_RIGHT.getRangeGridLinePaint().setAlpha(0);
        graphWidget_RIGHT.setRangeAxisPosition(false, false, 4, "10");

        // Copy where possible from the LEFT plot
        graphWidget_RIGHT.setRangeLabelVerticalOffset(graphWidget_LEFT.getRangeLabelVerticalOffset());
        graphWidget_RIGHT.setMarginRight(graphWidget_LEFT.getMarginRight());
        graphWidget_RIGHT.setPaddingRight(graphWidget_LEFT.getPaddingRight());
        graphWidget_RIGHT.setRangeLabelWidth(graphWidget_LEFT.getRangeLabelWidth());

        // Position the Graphs
        myXYPlot_LEFT.getGraphWidget().position(
                0 ,XLayoutStyle.ABSOLUTE_FROM_LEFT,10,YLayoutStyle.ABSOLUTE_FROM_TOP,AnchorPosition.LEFT_TOP);
        myXYPlot_RIGHT.getGraphWidget().position(
                49,XLayoutStyle.ABSOLUTE_FROM_LEFT,10,YLayoutStyle.ABSOLUTE_FROM_TOP,AnchorPosition.LEFT_TOP);

        // Setup and Position the LEFT Legend
        XYLegendWidget legendWidget_LEFT = myXYPlot_LEFT.getLegendWidget();
        legendWidget_LEFT.setTableModel(new DynamicTableModel(1, 3));
        legendWidget_LEFT.getTextPaint().setTextSize(20);
        legendWidget_LEFT.setSize(new SizeMetrics(100, SizeLayoutType.ABSOLUTE, 75, SizeLayoutType.FILL));
        legendWidget_LEFT.setPadding(1, 1, 1, 1);
        myXYPlot_LEFT.getGraphWidget().position(
                55, XLayoutStyle.ABSOLUTE_FROM_LEFT, 15, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.LEFT_TOP);
        
        // Setup and Position the RIGHT Legend
        XYLegendWidget legendWidget_RIGHT = myXYPlot_RIGHT.getLegendWidget();
        legendWidget_RIGHT.setTableModel(new DynamicTableModel(1, 3));
        legendWidget_RIGHT.getTextPaint().setTextSize(20);
        legendWidget_RIGHT.setSize(new SizeMetrics(100, SizeLayoutType.ABSOLUTE, 110, SizeLayoutType.ABSOLUTE));
        legendWidget_RIGHT.setPadding(1, 1, 1, 1);
        myXYPlot_RIGHT.getGraphWidget().position(
                25, XLayoutStyle.ABSOLUTE_FROM_RIGHT, 15, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.RIGHT_TOP);

        // Setup the Series
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");                            
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Setup the formatters
        series1Format = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), null, new PointLabelFormatter(Color.WHITE));
        series2Format = new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null, new PointLabelFormatter(Color.WHITE));
        
        // Setup the Button
        button = (Button)findViewById(R.id.toggleSeries2);
        button.setOnClickListener(this);
    }
    
    @Override    
    protected void onResume() {        
        super.onResume();

        updateView();
    	
    }
    
    private void updateView() {

    	// Remove all current series from each plot
        Iterator<XYSeries> iterator1 = myXYPlot_LEFT.getSeriesSet().iterator();
        while(iterator1.hasNext()) {
        	XYSeries setElement = iterator1.next();
        	myXYPlot_LEFT.removeSeries(setElement);
        }
        Iterator<XYSeries> iterator2 = myXYPlot_RIGHT.getSeriesSet().iterator();
        while(iterator2.hasNext()) {
        	XYSeries setElement = iterator2.next();
        	myXYPlot_RIGHT.removeSeries(setElement);
        }
    	
    	// Add series to each plot as needed.
        myXYPlot_LEFT.addSeries(series1, series1Format);
        if (series2_onRight) {
        	myXYPlot_RIGHT.addSeries(series2, series2Format);
        } else {
        	myXYPlot_LEFT.addSeries(series2, series2Format);
        }

        // Finalise each Plot based on whether they have any series or not.
    	if (! myXYPlot_RIGHT.getSeriesSet().isEmpty()) {
    		myXYPlot_RIGHT.setVisibility(XYPlot.VISIBLE);
    		myXYPlot_RIGHT.redraw();
    	} else {
    		myXYPlot_RIGHT.setVisibility(XYPlot.INVISIBLE);
    	}
    	
    	if (! myXYPlot_LEFT.getSeriesSet().isEmpty()) {
    		myXYPlot_LEFT.setVisibility(XYPlot.VISIBLE);
    		myXYPlot_LEFT.redraw();
    	} else {
    		myXYPlot_LEFT.setVisibility(XYPlot.INVISIBLE);
    	}
    	
    }

	@Override
	public void onClick(View v) {
    	if (series2_onRight) {
    		series2_onRight = false;
    	} else {
    		series2_onRight = true;
    	}
        updateView();
	}
}
