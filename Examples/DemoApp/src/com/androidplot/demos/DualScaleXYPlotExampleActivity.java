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
import android.graphics.Paint.Align;
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
import com.androidplot.util.PixelUtils;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class DualScaleXYPlotExampleActivity extends Activity implements OnClickListener
{

    private XYPlot myXYPlot_L, myXYPlot_R; 
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

        final float f26 = PixelUtils.dpToPix(26);
        final float f10 = PixelUtils.dpToPix(10);
        final SizeMetrics sm = new SizeMetrics(0, SizeLayoutType.FILL, 0, SizeLayoutType.FILL);
        
        myXYPlot_L = (XYPlot) findViewById(R.id.mySimpleXYPlot_L);
        myXYPlot_R = (XYPlot) findViewById(R.id.mySimpleXYPlot_R);
        
        // Disable Hardware Acceleration on the xyPlot view object.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	myXYPlot_L.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	myXYPlot_R.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        /*
         * Setup the Plots
         */
        myXYPlot_L.setPlotMargins(0, 0, 0, 0);
        myXYPlot_R.setPlotMargins(0, 0, 0, 0);

        myXYPlot_L.setPlotPadding(0, 0, 0, 0);
        myXYPlot_R.setPlotPadding(0, 0, 0, 0);

        myXYPlot_R.getDomainLabelWidget().setVisible(false);
        myXYPlot_R.getRangeLabelWidget().setVisible(false);
        myXYPlot_R.getTitleWidget().setVisible(false);
        myXYPlot_R.setBorderPaint(null);
        myXYPlot_R.setBackgroundPaint(null);

        /* 
         * Setup the Graph Widgets
         */
        XYGraphWidget graphWidget_L = myXYPlot_L.getGraphWidget();
        XYGraphWidget graphWidget_R = myXYPlot_R.getGraphWidget();
        
        graphWidget_L.setSize(sm);
        graphWidget_R.setSize(sm);
        
        graphWidget_L.setMargins(0, 0, 0, 0);
        graphWidget_R.setMargins(0, 0, 0, 0);
        
        graphWidget_L.setPadding(f26, f10, f26, f26);
        graphWidget_R.setPadding(f26, f10, f26, f26);
        
        graphWidget_L.setRangeAxisPosition(true, false, 4, "10");
        graphWidget_R.setRangeAxisPosition(false, false, 4, "10");
        
        graphWidget_L.setRangeLabelVerticalOffset(-3);
        graphWidget_R.setRangeLabelVerticalOffset(-3);
        
        graphWidget_L.setRangeOriginLabelPaint(null);
        graphWidget_R.setRangeOriginLabelPaint(null);

        graphWidget_L.setRangeLabelWidth(0);
        graphWidget_R.setRangeLabelWidth(0);
        
        graphWidget_L.setDomainLabelWidth(0);
        graphWidget_R.setDomainLabelWidth(0);

        graphWidget_R.setBackgroundPaint(null);
        graphWidget_R.setDomainLabelPaint(null);
        graphWidget_R.setGridBackgroundPaint(null);
        graphWidget_R.setDomainOriginLabelPaint(null);
        graphWidget_R.setRangeOriginLinePaint(null);
        graphWidget_R.setDomainGridLinePaint(null);
        graphWidget_R.setRangeGridLinePaint(null);
        
        graphWidget_L.getRangeLabelPaint().setTextSize(PixelUtils.dpToPix(8));
        graphWidget_R.getRangeLabelPaint().setTextSize(PixelUtils.dpToPix(8));

        graphWidget_L.getDomainOriginLabelPaint().setTextSize(PixelUtils.dpToPix(8));
        graphWidget_L.getDomainLabelPaint().setTextSize(PixelUtils.dpToPix(8));
        
        float textSize = graphWidget_L.getRangeLabelPaint().getTextSize();
        graphWidget_L.setRangeLabelVerticalOffset((textSize / 2) * -1);
        graphWidget_R.setRangeLabelVerticalOffset(graphWidget_L.getRangeLabelVerticalOffset());

        /*
         * Position the Graph Widgets in the Centre
         */
        graphWidget_L.position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.CENTER);
        graphWidget_R.position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.CENTER);

        /* 
         * Position the Label Widgets
         */
        myXYPlot_L.getDomainLabelWidget().setWidth(100);
        myXYPlot_L.getRangeLabelWidget().setWidth(100);
        myXYPlot_L.getDomainLabelWidget().position(0, XLayoutStyle.RELATIVE_TO_CENTER, 1, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);
        myXYPlot_L.getRangeLabelWidget().position(1, XLayoutStyle.ABSOLUTE_FROM_LEFT,  -20, YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.LEFT_BOTTOM);

        /*
         *  Setup and Position the LEFT Legend
         */
        XYLegendWidget legendWidget_LEFT = myXYPlot_L.getLegendWidget();
        legendWidget_LEFT.setSize(new SizeMetrics(100, SizeLayoutType.ABSOLUTE, 200, SizeLayoutType.ABSOLUTE));
        legendWidget_LEFT.setPadding(1, 1, 1, 1);
        legendWidget_LEFT.setTableModel(new DynamicTableModel(1, 3));
        legendWidget_LEFT.setIconSizeMetrics(new SizeMetrics(PixelUtils.dpToPix(10), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(10), SizeLayoutType.ABSOLUTE));
        legendWidget_LEFT.getTextPaint().setTextSize(PixelUtils.dpToPix(9));
        legendWidget_LEFT.position(PixelUtils.dpToPix(30), XLayoutStyle.ABSOLUTE_FROM_LEFT, f10+2, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.LEFT_TOP); 

       
        /*
         *  Setup and Position the RIGHT Legend
         */
        XYLegendWidget legendWidget_RIGHT = myXYPlot_R.getLegendWidget();
        legendWidget_RIGHT.setSize(new SizeMetrics(100, SizeLayoutType.ABSOLUTE, 200, SizeLayoutType.ABSOLUTE));
        legendWidget_RIGHT.setPadding(1, 1, 1, 1);
        legendWidget_RIGHT.setTableModel(new DynamicTableModel(1, 3));
        legendWidget_RIGHT.setIconSizeMetrics(new SizeMetrics(PixelUtils.dpToPix(10), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(10), SizeLayoutType.ABSOLUTE));
        legendWidget_RIGHT.getTextPaint().setTextSize(PixelUtils.dpToPix(9));
        legendWidget_RIGHT.getTextPaint().setTextAlign(Align.RIGHT);
        legendWidget_RIGHT.setMarginLeft(185);
        legendWidget_RIGHT.position(PixelUtils.dpToPix(30), XLayoutStyle.ABSOLUTE_FROM_RIGHT, f10+2, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.RIGHT_TOP);

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
        Iterator<XYSeries> iterator1 = myXYPlot_L.getSeriesSet().iterator();
        while(iterator1.hasNext()) {
        	XYSeries setElement = iterator1.next();
        	myXYPlot_L.removeSeries(setElement);
        }
        Iterator<XYSeries> iterator2 = myXYPlot_R.getSeriesSet().iterator();
        while(iterator2.hasNext()) {
        	XYSeries setElement = iterator2.next();
        	myXYPlot_R.removeSeries(setElement);
        }
    	
    	// Add series to each plot as needed.
        myXYPlot_L.addSeries(series1, series1Format);
        if (series2_onRight) {
        	myXYPlot_R.addSeries(series2, series2Format);
        } else {
        	myXYPlot_L.addSeries(series2, series2Format);
        }

        // Finalise each Plot based on whether they have any series or not.
    	if (! myXYPlot_R.getSeriesSet().isEmpty()) {
    		myXYPlot_R.setVisibility(XYPlot.VISIBLE);
    		myXYPlot_R.redraw();
    	} else {
    		myXYPlot_R.setVisibility(XYPlot.INVISIBLE);
    	}
    	
    	if (! myXYPlot_L.getSeriesSet().isEmpty()) {
    		myXYPlot_L.setVisibility(XYPlot.VISIBLE);
    		myXYPlot_L.redraw();
    	} else {
    		myXYPlot_L.setVisibility(XYPlot.INVISIBLE);
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
