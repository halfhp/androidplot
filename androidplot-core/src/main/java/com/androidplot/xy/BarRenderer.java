/*
 * Copyright 2015 AndroidPlot.com
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

import com.androidplot.ui.RenderStack;
import com.androidplot.ui.SeriesAndFormatter;
import com.androidplot.util.ValPixConverter;

/**
 * Renders the points in an XYSeries as bars.
 */
public class BarRenderer<FormatterType extends BarFormatter> extends GroupRenderer<FormatterType> {

    private BarRenderStyle renderStyle = BarRenderStyle.OVERLAID;  // default Render Style
	private BarWidthStyle widthStyle = BarWidthStyle.FIXED_WIDTH;  // default Width Style
    private float barWidth = 5;
    private float barGap = 1;
    private Comparator<Bar> barComparator = new BarComparator();

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
    
    /**
     * Sets a {@link Comparator} used for sorting bars.
     */
    public void setBarComparator(Comparator<Bar> barComparator) {
      this.barComparator = barComparator;
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
     * @return The desired getFormatter or null to use the default.
     */
    @SuppressWarnings("UnusedParameters")
    public FormatterType getFormatter(int index, XYSeries series) {
        return null;
    }


	@Override
	public void onRender(Canvas canvas, RectF plotArea, List<SeriesAndFormatter<XYSeries,
			? extends FormatterType>> sfList, int seriesSize, RenderStack stack) {

    	TreeMap<Number, BarGroup> axisMap = new TreeMap<Number, BarGroup>();

        /*
         * Build the axisMap (yVal,BarGroup)... a TreeMap of BarGroups
         * BarGroups represent a point on the X axis where a single or group of bars need to be drawn.
         */
        for(SeriesAndFormatter<XYSeries, ? extends FormatterType> thisPair : sfList) {
        	BarGroup barGroup;

            // For each value in the series
            for(int i = 0; i < seriesSize; i++) {

               	if (thisPair.getSeries().getX(i) != null) {

            		// get a new bar object
            		Bar bar = new Bar(thisPair.getSeries(), thisPair.getFormatter(),i,plotArea);

            		// Find or create the barGroup
	            	if (axisMap.containsKey(bar.intX)) {
	            		barGroup = axisMap.get(bar.intX);
	            	} else {
	            		barGroup = new BarGroup(bar.intX,plotArea);
	            		axisMap.put(bar.intX, barGroup);
	            	}
	            	barGroup.addBar(bar);
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

    		/*
    		 * Draw the bars within the barGroup area.
    		 */
            double rangeOrigin = getPlot().getRangeOrigin().doubleValue();
            float basePositionY = (float) ValPixConverter.valToPix(rangeOrigin,
                    getPlot().getCalculatedMinY().doubleValue(),
                    getPlot().getCalculatedMaxY().doubleValue(),
                    plotArea.height(), true) + plotArea.top;

			switch (renderStyle) {
			case OVERLAID:
				Collections.sort(barGroup.bars, barComparator);
				for (Bar bar : barGroup.bars) {
					BarFormatter formatter = bar.getFormatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}

                	if (bar.yVal<rangeOrigin){ // falling bar
                		if (bar.barGroup.width >= 2) {
                			canvas.drawRect(bar.barGroup.leftX, basePositionY, bar.barGroup.rightX, bar.intY, formatter.getFillPaint());
                		}
                		canvas.drawRect(bar.barGroup.leftX, basePositionY, bar.barGroup.rightX, bar.intY, formatter.getBorderPaint());
                	} else { // rising bar
                		if (bar.barGroup.width >= 2) {
                			canvas.drawRect(bar.barGroup.leftX, bar.intY, bar.barGroup.rightX, basePositionY, formatter.getFillPaint());
                		}
                		canvas.drawRect(bar.barGroup.leftX, bar.intY, bar.barGroup.rightX, basePositionY, formatter.getBorderPaint());
                	}
	        		if(plf != null && pointLabeler != null) {
	                    canvas.drawText(pointLabeler.getLabel(bar.series, bar.seriesIndex), bar.intX + plf.hOffset, bar.intY + plf.vOffset, plf.getTextPaint());
	                }
	        	}
				break;
			case SIDE_BY_SIDE:
				int width = barGroup.width / barGroup.bars.size();
				int leftX = barGroup.leftX;
				Collections.sort(barGroup.bars, barComparator);
				for (Bar bar : barGroup.bars) {
					BarFormatter formatter = bar.getFormatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}

                	if (bar.yVal<rangeOrigin){ // falling value
                		if (bar.barGroup.width >= 2) {
                			canvas.drawRect(leftX, basePositionY, leftX + width, bar.intY, formatter.getFillPaint());
                		}
                		canvas.drawRect(leftX, basePositionY, leftX + width, bar.intY, formatter.getBorderPaint());
                	} else { // rising bar
                		if (bar.barGroup.width >= 2) {
                			canvas.drawRect(leftX, bar.intY, leftX + width, basePositionY, formatter.getFillPaint());
                		}
                		canvas.drawRect(leftX, bar.intY, leftX + width, basePositionY, formatter.getBorderPaint());
                	}
	        		if(plf != null && pointLabeler != null) {
	                    canvas.drawText(pointLabeler.getLabel(bar.series, bar.seriesIndex), leftX + width/2 + plf.hOffset, bar.intY + plf.vOffset, plf.getTextPaint());
	                }
	        		leftX = leftX + width;
	        	}
				break;
			case STACKED:
				int bottom = (int) barGroup.plotArea.bottom;
				Collections.sort(barGroup.bars, barComparator);
				for (Bar b : barGroup.bars) {
					BarFormatter formatter = b.getFormatter();
			        PointLabelFormatter plf = formatter.getPointLabelFormatter();
			        PointLabeler pointLabeler = null;
                	if (formatter != null) {
                		pointLabeler = formatter.getPointLabeler();
                	}
	        		int height = (int) b.barGroup.plotArea.bottom - b.intY;
	        		int top = bottom - height;
	    			if (b.barGroup.width >= 2) {
	        			canvas.drawRect(b.barGroup.leftX, top, b.barGroup.rightX, bottom, formatter.getFillPaint());
	        		}

	        		canvas.drawRect(b.barGroup.leftX, top, b.barGroup.rightX, bottom, formatter.getBorderPaint());
	        		if(plf != null && pointLabeler != null) {
	                    //canvas.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), b.intX + plf.hOffset, b.intY + plf.vOffset, plf.getTextPaint());
                        // b.intY should be replaced by top as Text label should be drawn on top of each bar
                        canvas.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), b.intX + plf.hOffset, top + plf.vOffset, plf.getTextPaint());
	                }
		        	bottom = top;
	        	}
				break;
			default:
				break;
			}
		}
    }

	public class Bar {
        public final XYSeries series;
        private final FormatterType formatter;
		public final int seriesIndex;
		public final double yVal, xVal;
		public final int intX, intY;
		public final float pixX, pixY;
		protected BarGroup barGroup;
    	
    	public Bar(XYSeries series, FormatterType formatter, int seriesIndex, RectF plotArea) {
			this.series = series;
            this.formatter = formatter;
			this.seriesIndex = seriesIndex;
			
			this.xVal = series.getX(seriesIndex).doubleValue();
			this.pixX = (float) ValPixConverter.valToPix(xVal, getPlot().getCalculatedMinX().doubleValue(),
					getPlot().getCalculatedMaxX().doubleValue(), plotArea.width(), false) + (plotArea.left);
			this.intX = (int) pixX;
			
			if (series.getY(seriesIndex) != null) {
				this.yVal = series.getY(seriesIndex).doubleValue();
				this.pixY = (float) ValPixConverter.valToPix(yVal, getPlot().getCalculatedMinY().doubleValue(),
						getPlot().getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
				this.intY = (int) pixY;
			} else {
				this.yVal = 0;
				this.pixY = plotArea.bottom;
				this.intY = (int) pixY;
			}
		}

        public FormatterType getFormatter() {
            FormatterType f =  BarRenderer.this.getFormatter(seriesIndex, series);
            return f != null ? f : formatter;
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
