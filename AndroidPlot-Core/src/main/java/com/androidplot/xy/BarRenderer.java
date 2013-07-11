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

package com.androidplot.xy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.androidplot.exception.PlotRenderException;
import com.androidplot.util.ValPixConverter;

/**
 * Renders a point as a Bar
 */
public class BarRenderer<T extends BarFormatter> extends XYSeriesRenderer<T> {

    private BarRenderStyle renderStyle = BarRenderStyle.OVERLAID;  // default Render Style
	private BarWidthStyle widthStyle = BarWidthStyle.FIXED_WIDTH;  // default Width Style
    private float barWidth = 5;
    private float barGap = 1;

    public enum BarRenderStyle {
        OVERLAID,           // bars are overlaid in descending y-val order (largest val in back)
        STACKED,            // bars are drawn stacked vertically on top of each other
        SIDE_BY_SIDE        // bars are drawn horizontally next to each-other
    }

    public enum BarWidthStyle {
        FIXED_WIDTH,        // bar width is always barWidth
        VARIABLE_WIDTH      // bar width is calculated so that there is only barGap between each bar
    }

    public BarRenderer(XYPlot plot) {
        super(plot);
    }

    /**
     * Sets the width of the bars when using the FIXED_WIDTH render style
     * @param barWidth
     */
    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * Sets the size of the gap between the bar (or bar groups) when using the VARIABLE_WIDTH render style
     * @param barGap
     */
    public void setBarGap(float barGap) {
        this.barGap = barGap;
    }

    public void setBarRenderStyle(BarRenderStyle renderStyle) {
        this.renderStyle = renderStyle;
    }
    
    public void setBarWidthStyle(BarWidthStyle widthStyle) {
        this.widthStyle = widthStyle;
    }
    
    public void setBarWidthStyle(BarWidthStyle style, float value) {
    	setBarWidthStyle(style);
        switch (style) {
        	case FIXED_WIDTH:
        		setBarWidth(value);
                break;
        	case VARIABLE_WIDTH:
        		setBarGap(value);
        		break;
		default:
			break;
        }
    }

    @Override
    public void doDrawLegendIcon(Canvas canvas, RectF rect, BarFormatter formatter) {
        canvas.drawRect(rect, formatter.getFillPaint());
        canvas.drawRect(rect, formatter.getBorderPaint());
    }

    /**
     * Retrieves the BarFormatter instance that corresponds with the series passed in.
     * Can be overridden to return other BarFormatters as a result of touch events etc.
     * @param index index of the point being rendered.
     * @param series XYSeries to which the point being rendered belongs.
     * @return
     */
    @SuppressWarnings("UnusedParameters")
    protected T getFormatter(int index, XYSeries series) {
        return getFormatter(series);
    }
    
    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {
        
    	List<XYSeries> sl = getPlot().getSeriesListForRenderer(this.getClass());
    	
    	TreeMap<Number, BarGroup> axisMap = new TreeMap<Number, BarGroup>();
    	
        // dont try to render anything if there's nothing to render.
        if(sl == null) return;

        /* 
         * Build the axisMap (yVal,BarGroup)... a TreeMap of BarGroups
         * BarGroups represent a point on the X axis where a single or group of bars need to be drawn.
         */
        
        // For each Series
        for(XYSeries series : sl) { 
        	BarGroup barGroup;
        	
            // For each value in the series
            for(int i = 0; i < series.size(); i++) {
            	
               	if (series.getX(i) != null) {

            		// get a new bar object
            		Bar b = new Bar(series,i,plotArea);
	            	
            		// Find or create the barGroup
	            	if (axisMap.containsKey(b.intX)) {
	            		barGroup = axisMap.get(b.intX);
	            	} else {
	            		barGroup = new BarGroup(b.intX,plotArea);
	            		axisMap.put(b.intX, barGroup);
	            	}
	            	barGroup.addBar(b);
            	}
            	
            }
        }

		// Loop through the axisMap linking up prev pointers
		BarGroup prev, current;
		prev = null;
		for(Entry<Number, BarGroup> mapEntry : axisMap.entrySet()) {
			current = mapEntry.getValue(); 
    		current.prev = prev;
    		prev = current;
		}

		
		// The default gap between each bar section
		int gap  = (int) barGap;

		// Determine roughly how wide (rough_width) this bar should be. This is then used as a default width
		// when there are gaps in the data or for the first/last bars.
		float f_rough_width = ((plotArea.width() - ((axisMap.size() - 1) * gap)) / (axisMap.size() - 1));
		int rough_width = (int) f_rough_width;
		if (rough_width < 0) rough_width = 0;
		if (gap > rough_width) {
			gap = rough_width / 2;
		}

		//Log.d("PARAMTER","PLOT_WIDTH=" + plotArea.width());
		//Log.d("PARAMTER","BAR_GROUPS=" + axisMap.size());
		//Log.d("PARAMTER","ROUGH_WIDTH=" + rough_width);
		//Log.d("PARAMTER","GAP=" + gap);

		/* 
		 * Calculate the dimensions of each barGroup and then draw each bar within it according to
		 * the Render Style and Width Style. 
		 */
		
		for(Number key : axisMap.keySet()) {

			BarGroup barGroup = axisMap.get(key);

			// Determine the exact left and right X for the Bar Group
			switch (widthStyle) {
			case FIXED_WIDTH:
    			// use intX and go halfwidth either side.
    			barGroup.leftX = barGroup.intX - (int) (barWidth / 2);
    			barGroup.width = (int) barWidth;
    			barGroup.rightX = barGroup.leftX + barGroup.width;
				break;
			case VARIABLE_WIDTH:
	    		if (barGroup.prev != null) {
	    			if (barGroup.intX - barGroup.prev.intX - gap - 1 > (int)(rough_width * 1.5)) {
	    				// use intX and go halfwidth either side.
	        			barGroup.leftX = barGroup.intX - (rough_width / 2);
	        			barGroup.width = rough_width;
	        			barGroup.rightX = barGroup.leftX + barGroup.width;
	    			} else {
	    				// base left off prev right to get the gap correct.
	    				barGroup.leftX = barGroup.prev.rightX + gap + 1;
	    				if (barGroup.leftX > barGroup.intX) barGroup.leftX = barGroup.intX;
	    				// base right off intX + halfwidth.
	    				barGroup.rightX = barGroup.intX + (rough_width / 2);
	    				// calculate the width
	    				barGroup.width = barGroup.rightX - barGroup.leftX;
	    			}
	    		} else {
	    			// use intX and go halfwidth either side.
	    			barGroup.leftX = barGroup.intX - (rough_width / 2);
	    			barGroup.width = rough_width;
	    			barGroup.rightX = barGroup.leftX + barGroup.width;
	    		}
				break;
			default:
				break;
			}
    		
    		//Log.d("BAR_GROUP", "rough_width=" + rough_width + " width=" + barGroup.width + " <" + barGroup.leftX + "|" + barGroup.intX + "|" + barGroup.rightX + ">"); 
    		
    		/*
    		 * Draw the bars within the barGroup area.
    		 */
			switch (renderStyle) {
			case OVERLAID:
				Collections.sort(barGroup.bars, new BarComparator());
				for (Bar b : barGroup.bars) {
					BarFormatter formatter = b.formatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}
	        		//Log.d("BAR", b.series.getTitle() + " <" + b.barGroup.leftX + "|" + b.barGroup.intX + "|" + b.barGroup.rightX + "> " + b.intY); 
	    			if (b.barGroup.width >= 2) {
	        			canvas.drawRect(b.barGroup.leftX, b.intY, b.barGroup.rightX, b.barGroup.plotArea.bottom, formatter.getFillPaint());
	        		}
	        		canvas.drawRect(b.barGroup.leftX, b.intY, b.barGroup.rightX, b.barGroup.plotArea.bottom, formatter.getBorderPaint());
	        		if(plf != null && pointLabeler != null) {
	                    canvas.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), b.intX + plf.hOffset, b.intY + plf.vOffset, plf.getTextPaint());
	                }
	        	}
				break;
			case SIDE_BY_SIDE:
				int width = (int) barGroup.width / barGroup.bars.size();
				int leftX = barGroup.leftX;
				Collections.sort(barGroup.bars, new BarComparator());
				for (Bar b : barGroup.bars) {
					BarFormatter formatter = b.formatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}
	        		//Log.d("BAR", "width=" + width + " <" + leftX + "|" + b.intX + "|" + (leftX + width) + "> " + b.intY); 
	        		if (b.barGroup.width >= 2) {
	        			canvas.drawRect(leftX, b.intY, leftX + width, b.barGroup.plotArea.bottom, formatter.getFillPaint());
	        		}
	        		canvas.drawRect(leftX, b.intY, leftX + width, b.barGroup.plotArea.bottom, formatter.getBorderPaint());
	        		if(plf != null && pointLabeler != null) {
	                    canvas.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), leftX + width/2 + plf.hOffset, b.intY + plf.vOffset, plf.getTextPaint());
	                }
	        		leftX = leftX + width;
	        	}
				break;
			case STACKED:
				int bottom = (int) barGroup.plotArea.bottom;
				Collections.sort(barGroup.bars, new BarComparator());
				for (Bar b : barGroup.bars) {
					BarFormatter formatter = b.formatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}
	        		int height = (int) b.barGroup.plotArea.bottom - b.intY;
	        		int top = bottom - height;
	        		//Log.d("BAR", "top=" + top + " bottom=" + bottom + " height=" + height); 
	    			if (b.barGroup.width >= 2) {
	        			canvas.drawRect(b.barGroup.leftX, top, b.barGroup.rightX, bottom, formatter.getFillPaint());
	        		}
	        		canvas.drawRect(b.barGroup.leftX, top, b.barGroup.rightX, bottom, formatter.getBorderPaint());
	        		if(plf != null && pointLabeler != null) {
	                    canvas.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), b.intX + plf.hOffset, b.intY + plf.vOffset, plf.getTextPaint());
	                }
		        	bottom = top;
	        	}
				break;
			default:
				break;
			}
			
		}
    		
    }
    
    private class Bar {
		public XYSeries series;
		public int seriesIndex;
		public double yVal, xVal;
		public int intX, intY;
		public float pixX, pixY;
		public BarGroup barGroup;
    	
    	public Bar(XYSeries series, int seriesIndex, RectF plotArea) {
			this.series = series;
			this.seriesIndex = seriesIndex;
			
			this.xVal = series.getX(seriesIndex).doubleValue();
			this.pixX = ValPixConverter.valToPix(xVal, getPlot().getCalculatedMinX().doubleValue(), getPlot().getCalculatedMaxX().doubleValue(), plotArea.width(), false) + (plotArea.left);
			this.intX = (int) pixX;
			
			if (series.getY(seriesIndex) != null) {
				this.yVal = series.getY(seriesIndex).doubleValue();
				this.pixY = ValPixConverter.valToPix(yVal, getPlot().getCalculatedMinY().doubleValue(), getPlot().getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
				this.intY = (int) pixY;
			} else {
				this.yVal = 0;
				this.pixY = plotArea.bottom;
				this.intY = (int) pixY;
			}
		}
    	public BarFormatter formatter() {
    		return getFormatter(seriesIndex, series);
    	}
    }
    
    private class BarGroup {
    	public ArrayList<Bar> bars;
    	public int intX;
    	public int width, leftX, rightX;
    	public RectF plotArea;
    	public BarGroup prev;
		
    	public BarGroup(int intX, RectF plotArea) {
    		// Setup the TreeMap with the required comparator
   			this.bars = new ArrayList<Bar>(); // create a comparator that compares series title given the index.
    		this.intX = intX;
			this.plotArea = plotArea;
		}
    	
    	public void addBar(Bar bar) {
    		bar.barGroup = this;
   			this.bars.add(bar);
    	}
    }

    @SuppressWarnings("WeakerAccess")
    public class BarComparator implements Comparator<Bar>{
        @Override
        public int compare(Bar bar1, Bar bar2) {
			switch (renderStyle) {
			case OVERLAID:
				return Integer.valueOf(bar1.intY).compareTo(bar2.intY);
			case SIDE_BY_SIDE:
				return bar1.series.getTitle().compareToIgnoreCase(bar2.series.getTitle());
			case STACKED:
				return bar1.series.getTitle().compareToIgnoreCase(bar2.series.getTitle());
			default:
	            return 0;
			}
        }
    }        
    
}
