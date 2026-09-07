// SPDX-License-Identifier: Apache-2.0

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
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.Arrays;

public class DemoAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            // 1. build the plot in code -- there is no layout xml for a widget
            XYPlot plot = new XYPlot(context, "Widget Example");
            final int h = (int) context.getResources().getDimension(R.dimen.sample_widget_height);
            final int w = (int) context.getResources().getDimension(R.dimen.sample_widget_width);

            plot.getGraph().setMargins(0, 0, 0 , 0);
            plot.getGraph().setPadding(0, 0, 0, 0);

            plot.getGraph().position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0,
                    VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP);

            plot.getGraph().setSize(Size.FILL);

            plot.getLayoutManager().moveToTop(plot.getTitle());

            plot.getGraph().setLineLabelEdges(XYGraphWidget.Edge.LEFT, XYGraphWidget.Edge.BOTTOM);
            plot.getGraph().getLineLabelInsets().setLeft(PixelUtils.dpToPix(16));
            plot.getGraph().getLineLabelInsets().setBottom(PixelUtils.dpToPix(4));
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.RED);
            plot.getGraph().getGridInsets().setTop(PixelUtils.dpToPix(12));
            plot.getGraph().getGridInsets().setRight(PixelUtils.dpToPix(12));
            plot.getGraph().getGridInsets().setLeft(PixelUtils.dpToPix(36));
            plot.getGraph().getGridInsets().setBottom(PixelUtils.dpToPix(16));

            // 2. size it by hand since it is never attached to a window
            plot.measure(w, h);
            plot.layout(0, 0, w, h);

            // 3. add data, then render into a bitmap for RemoteViews
            Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
            Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

            // Y_VALS_ONLY: x is the element index
            XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
            XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

            // LineAndPointFormatter(lineColor, pointColor, fillColor, pointLabelFormatter):
            LineAndPointFormatter series1Format = new LineAndPointFormatter(
                    Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), null, null);

            plot.addSeries(series1, series1Format);
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
