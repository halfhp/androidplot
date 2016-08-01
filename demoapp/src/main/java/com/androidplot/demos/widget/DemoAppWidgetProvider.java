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

package com.androidplot.demos.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.RemoteViews;
import com.androidplot.demos.R;
import com.androidplot.ui.*;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.Arrays;

public class DemoAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            XYPlot plot = new XYPlot(context, "Widget Example");
            final int h = (int) context.getResources().getDimension(R.dimen.sample_widget_height);
            final int w = (int) context.getResources().getDimension(R.dimen.sample_widget_width);

            plot.getGraph().setMargins(0, 0, 0 , 0);
            plot.getGraph().setPadding(0, 0, 0, 0);
//            plot.getGraphWidget().getDomainLineLabelPaint().setTextSize(PixelUtils.spToPix(10));
//            plot.getGraphWidget().getRangeLineLabelPaint().setTextSize(PixelUtils.spToPix(10));
//            plot.getGraphWidget().getDomainOriginLineLabelPaint().setTextSize(PixelUtils.spToPix(10));
//            plot.getGraphWidget().getRangeOriginLineLabelPaint().setTextSize(PixelUtils.spToPix(10));

            plot.getGraph().position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0,
                    VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);

            plot.getGraph().setSize(Size.FILL);

            plot.getLayoutManager().moveToTop(plot.getTitle());

            plot.measure(w, h);
            plot.layout(0, 0, w, h);

            Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
            Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

            // Turn the above arrays into XYSeries':
            XYSeries series1 = new SimpleXYSeries(
                    Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                    "Series1");                             // Set the display title of the series

            // same as above
            XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

            // Create a formatter to use for drawing a series using LineAndPointRenderer:
            LineAndPointFormatter series1Format = new LineAndPointFormatter(
                    Color.rgb(0, 200, 0),                   // line color
                    Color.rgb(0, 100, 0),                   // point color
                    null, null);                            // fill color (none)

            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format);

            // same as above:
            plot.addSeries(series2,
                    new LineAndPointFormatter(
                            Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null, null));


            // reduce the number of range labels
            plot.setLinesPerRangeLabel(3);
            plot.setLinesPerDomainLabel(2);

            // hide the legend:
            plot.getLegend().setVisible(false);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.demo_app_widget);

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            plot.draw(new Canvas(bitmap));
            rv.setImageViewBitmap(R.id.imgView, bitmap);
            appWidgetManager.updateAppWidget(widgetId, rv);
        }
    }
}
