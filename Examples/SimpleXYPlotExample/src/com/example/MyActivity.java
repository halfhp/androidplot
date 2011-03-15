package com.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.ui.layout.AnchorPosition;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.XYPlot;


public class MyActivity extends Activity
{

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        
        // add a new series
        plot.addSeries(new SimpleXYSeries(), LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0)));
        //plot.addSeries(new SimpleXYSeries(), BarRenderer.class, new BarFormatter(Color.RED, Color.WHITE));

        // reduce the number of range labels
        //plot.getGraphWidget().setRangeTicksPerLabel(4);
        plot.setTicksPerRangeLabel(4);
        plot.setTicksPerDomainLabel(2);

        // reposition the domain label to look a little cleaner:
        Widget domainLabelWidget = plot.getDomainLabelWidget();

        plot.position(domainLabelWidget,                     // the widget to position
                                45,                                    // x position value, in this case 45 pixels
                                XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                                0,                                     // y position value
                                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                                AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned


        //plot.setUserRangeOrigin(44);
        plot.centerOnRangeOrigin(60);
        plot.centerOnDomainOrigin(5);


        plot.setGridPadding(4, 4, 4, 4);
        //plot.setDomainBoundaries(5,7,BoundaryMode.FIXED);

        // get rid of the visual aids for positioning:
        plot.disableAllMarkup();

    }
}
