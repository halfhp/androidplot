// SPDX-License-Identifier: Apache-2.0

package com.androidplot.demos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidplot.ui.SeriesBundle;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListViewActivity extends Activity {
    private static final int NUM_PLOTS = 10;
    private static final int NUM_POINTS_PER_SERIES = 10;
    private static final int NUM_SERIES_PER_PLOT = 5;
    private static final Random RANDOM = new Random();

    private List<List<SeriesBundle<XYSeries, LineAndPointFormatter>>> seriesData = new ArrayList<>(NUM_PLOTS);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_example);

        // formatters below are built before any Plot view exists, so init PixelUtils by hand
        PixelUtils.init(this);
        generateData();
        ListView lv = findViewById(R.id.listView1);
        lv.setAdapter(new MyViewAdapter(this, R.layout.listview_example_item, null));
    }

    class MyViewAdapter extends ArrayAdapter<View> {
        public MyViewAdapter(Context context, int resId, List<View> views) {
            super(context, resId, views);
        }

        @Override
        public int getCount() {
            return NUM_PLOTS;
        }

        // recycling: clear the plot, re-add this row's series, redraw
        @NonNull
        @Override
        public View getView(int pos, View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.listview_example_item, parent, false);
            }

            XYPlot p = v.findViewById(R.id.xyplot);
            p.clear();
            p.getTitle().setText("plot" + pos);

            List<SeriesBundle<XYSeries, LineAndPointFormatter>> thisSeriesList = seriesData.get(pos);
            for(SeriesBundle<XYSeries, LineAndPointFormatter> sf : thisSeriesList) {
                p.addSeries(sf.getSeries(), sf.getFormatter());
            }
            p.redraw();
            return v;
        }
    }

    private void generateData() {
        for(int i = 0; i < NUM_PLOTS; i++) {
            List<SeriesBundle<XYSeries, LineAndPointFormatter>> seriesList
                    = new ArrayList<>(NUM_SERIES_PER_PLOT);

            for (int k = 0; k < NUM_SERIES_PER_PLOT; k++) {
                ArrayList<Number> nums = new ArrayList<>();
                for (int j = 0; j < NUM_POINTS_PER_SERIES; j++) {
                    nums.add(RANDOM.nextFloat());
                }

                LineAndPointFormatter lpf = new LineAndPointFormatter(
                        randomColor(), randomColor(), null, null);

                // for fun, configure interpolation on the formatter:
                lpf.setInterpolationParams(
                        new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

                seriesList.add(new SeriesBundle<>(
                        new SimpleXYSeries(nums, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "S" + k),
                        lpf));
            }
            seriesData.add(seriesList);
        }
    }

    private static int randomColor() {
        return Color.rgb(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
    }
}
